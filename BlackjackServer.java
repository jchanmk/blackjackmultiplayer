package blackjackServer;

import com.corundumstudio.socketio.listener.DisconnectListener;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.io.UnsupportedEncodingException;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.listener.DataListener;
import org.springframework.core.env.SystemEnvironmentPropertySource;



@SpringBootApplication
public class BlackjackServer {
    public static HashMap<String, String> userIdentities = new HashMap<>(); // key = userID, value = randomToken
    public static HashMap<String, User> userHands = new HashMap<>(); // key = random generated string


    public static int dealerHand = 0;
    public static boolean blackjack = false;
    public static int[] deckValues = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
                                        2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
                                        2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
                                        2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};
    public static List<Integer> dealerCards = new ArrayList<>();
    public static List<Integer> dealerRandomNums = new ArrayList<>();

    public static PlayersAtTable[] connectedClients = {null, null, null};

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/Blackjack?useUnicode=true&characterEncoding=UTF-8";
    static final String USER = "root";
    static final String PASSWORD = "p56230101P";

    static int interval = 10;
    static int interval2 = 4;
    static Timer timer;
    static Timer timer2;
    static Timer timer3;


    public static void main(String[] args) {
        SpringApplication.run(BlackjackServer.class, args);

        Configuration config = new Configuration();
        config.setHostname("localhost");
//        config.setHostname("0.0.0.0");
//        config.setHostname("3.14.151.219");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);

        server.addDisconnectListener(new DisconnectListener() {      // if a user leaves the table
            public void onDisconnect(SocketIOClient client) {
                JSONObject responseObj2 = new JSONObject();
                responseObj2.put("message", "true");
                server.getBroadcastOperations().sendEvent("playerLeft", responseObj2);

                for (int i = 0; i < connectedClients.length; i++) {       // removing user from table
                    if (connectedClients[i] == null) {

                    } else if (connectedClients[i].clientID.equals(client.toString())) {
                        connectedClients[i] = null;
                    } else {
                        JSONObject responseObj = new JSONObject();
                        connectedClients[i].isHost = true;
                        connectedClients[i].isTurn = true;
                        responseObj.put("userID", connectedClients[i].userID);
                        responseObj.put("randomToken", connectedClients[i].randomToken);
                        responseObj.put("seat", connectedClients[i].seat);
                        responseObj.put("tableFull", "false");
                        responseObj.put("isHost", connectedClients[i].isHost);
                        responseObj.put("isTurn", connectedClients[i].isTurn);
                        server.getBroadcastOperations().sendEvent("seatAssignment", responseObj.toString());
                    }
                }
            }
        });

        server.addEventListener("notifySocketServer", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String payload, AckRequest ackRequest) throws Exception {
                JSONObject payloadObj = new JSONObject(payload);
                //   System.out.println(payloadObj);
                String userID = payloadObj.getString("userID");
                String randomToken = payloadObj.getString("randomToken");
                int seat = 0;
                PlayersAtTable player = new PlayersAtTable(client.toString(), userID, randomToken, seat, new ArrayList(), false, false, false);

                User user = new User(0, new ArrayList<Integer>(), 0, false); // new stuff trying
                userHands.put(randomToken, user);                                       // new stuff trying
                server.getBroadcastOperations().sendEvent("inSession", "true");

                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] == null) {
                        if (connectedClients[0] == null && connectedClients[1] == null) {     // hard coded, can change for scability later
                            player.isHost = true;
                        }
                        if (i == 0) {
                            player.isTurn = true;
                        }

                        player.seat = i;
                        connectedClients[i] = player;
                        System.out.println(connectedClients[i].userID + " " + player.isHost);
                        System.out.println(connectedClients[i].userID + " turn " + player.isTurn);

                        if (i == 0 && connectedClients[i + 1] != null) {
                            if (connectedClients[i + 1].clientID.equals(client.toString())) {
                                connectedClients[i] = null;
                                connectedClients[i + 1].isTurn = true;
                            } else if(!connectedClients[i+1].isTurn){
                                connectedClients[i].isTurn = true;
                                connectedClients[i + 1].isTurn = false;
                            } else if(userHands.get(connectedClients[i+1].randomToken).handValue == 0){
                                connectedClients[i + 1].isTurn = false;
                            }
                        }

                        break;
                    } else if (connectedClients[i].clientID.equals(client.toString())) {
                        break;
                    }
                    else if (i == 3 && connectedClients[i] != null) {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("userID", userID);
                        responseObj.put("randomToken", randomToken);
                        responseObj.put("tableFull", "true");
                    }
                }

                System.out.println("seat number: " + seat);
                System.out.println("number of connected clients: " + server.getAllClients().size());
                System.out.println("this client is id: " + client.toString());

                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] == null) {

                    } else {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("userID", connectedClients[i].userID);
                        responseObj.put("randomToken", connectedClients[i].randomToken);
                        responseObj.put("seat", connectedClients[i].seat);
                        responseObj.put("tableFull", "false");
                        responseObj.put("isHost", connectedClients[i].isHost);
                        responseObj.put("isTurn", connectedClients[i].isTurn);
                        server.getBroadcastOperations().sendEvent("seatAssignment", responseObj.toString());
                    }
                }

                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {
                        if (connectedClients[i].clientID.equals(client.toString()) && connectedClients[i].isHost) {
                            // timer for game to start
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                public void run() {
                                    //System.out.println(interval);
                                    server.getBroadcastOperations().sendEvent("serverTimer", interval);
                                    if (interval == 0) {
                                        timer.cancel();
                                        server.getBroadcastOperations().sendEvent("timerDone", "start");
                                        interval = 10;
                                    }
                                    interval--;

                                }
                            }, 1000, 1000);
                        } else {
                            timer2 = new Timer();
                            //  interval = 5;
                            timer2.scheduleAtFixedRate(new TimerTask() {
                                public void run() {
                                    server.getBroadcastOperations().sendEvent("serverTimer", interval);
                                    if (interval == 0) {
                                        timer2.cancel();
                                    }
                                }
                            }, 1000, 1000);

                        }

                    }
                }

            }
        });

        server.addEventListener("restartGame", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String payload, AckRequest ackRequest) throws Exception {
                server.getBroadcastOperations().sendEvent("restartConfirm", payload);
            }
        });

/// Server socket version of start game
        server.addEventListener("startGame", String.class, new DataListener<String>() {

            public void onData(SocketIOClient client, String payload, AckRequest ackRequest) throws Exception {
                JSONObject payloadObj = new JSONObject(payload);
//                String participant = payloadObj.getString("userID");
                String randomToken = payloadObj.getString("randomToken");
                int bet = payloadObj.getInt("bet");

                blackjack = false;

                // game reset
                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {
                        User user = new User(0, new ArrayList<Integer>(), bet, false);
                        userHands.put(connectedClients[i].randomToken, user);
                        connectedClients[i].randomNums.clear();
                    }
                }

                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {
                        if (connectedClients[i].blackjack) {
                            connectedClients[i].blackjack = false;
                        }
                    }
                }

                dealerHand = 0;
                dealerCards.clear();
                dealerRandomNums.clear();

                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {

                        Random rand = new Random();
                        int randomNum = rand.nextInt(52);
                        Random rand2 = new Random();
                        int randomNum2 = rand2.nextInt(52);

                        User user = userHands.get(connectedClients[i].randomToken);    // new stuff
                        int hand = user.handValue;                                                                      // new stuff

                        hand += deckValues[randomNum];
                        hand += deckValues[randomNum2];

                        user.handValue = hand;  // new stuff
                        user.handCards.add(deckValues[randomNum]);  // new stuff
                        user.handCards.add(deckValues[randomNum2]);  // new stuff

                        userHands.put(connectedClients[i].randomToken, user);

                        connectedClients[i].randomNums.add(randomNum);
                        connectedClients[i].randomNums.add(randomNum2);


                        if (hand == 21 && user.handCards.size() == 2) {
                            JSONObject responseObj = new JSONObject();
                            responseObj.put("randomNumber1", randomNum);
                            responseObj.put("randomNumber2", randomNum2);

                            responseObj.put("blackjack", "true");
                            responseObj.put("participant", connectedClients[i].userID);
                            recordResult(true, connectedClients[i].userID);
                            calculateBalance(true, connectedClients[i].userID, user.bet, true);

                            connectedClients[i].blackjack = true;

                            user.isPlaying = false;                                                 // really new stuff, after sockets
                            userHands.put(connectedClients[i].randomToken, user);       // really new stuff, after sockets
                            if (i == 0) {
                                connectedClients[i].isTurn = false;
                                if (connectedClients[1] != null) {
                                    connectedClients[i + 1].isTurn = true;
                                }
                                else {
                                    connectedClients[i].isTurn = true;
                                }
                            } else if (i == 1) {
                                connectedClients[i].isTurn = false;
                            }
                            server.getBroadcastOperations().sendEvent("startGameServer", responseObj.toString());

                        } else if (hand >= 21 && user.handCards.size() == 2) {              // new stuff
                            hand -= 10;
                            user.handValue = hand;
                            user.handCards.set(0, 1);
                            userHands.put(connectedClients[i].randomToken, user);
                        }
                    }

                }


                if (dealerCards.size() == 0 && dealerHand == 0) {
                    Random rand3 = new Random();
                    int randomNum3 = rand3.nextInt(52);
                    dealerHand += deckValues[randomNum3];
                    dealerCards.add(deckValues[randomNum3]);
                    dealerRandomNums.add(randomNum3);
                }

                if (connectedClients[0] != null && !connectedClients[0].blackjack) {
                    JSONObject responseObj2 = new JSONObject();
                    responseObj2.put("randomNumber1", connectedClients[0].randomNums.get(0));
                    responseObj2.put("randomNumber2", connectedClients[0].randomNums.get(1));
                    responseObj2.put("blackjack", "false");
                    responseObj2.put("participant", connectedClients[0].userID);
                    responseObj2.put("isTurn", connectedClients[0].isTurn);

                    System.out.println("sending clients hands from players");

                    server.getBroadcastOperations().sendEvent("startGameServer", responseObj2.toString());
                }

                //Thread.sleep(500);
                if (connectedClients[1] != null && !connectedClients[1].blackjack) {
                    JSONObject responseObj3 = new JSONObject();
                    responseObj3.put("randomNumber1", connectedClients[1].randomNums.get(0));
                    responseObj3.put("randomNumber2", connectedClients[1].randomNums.get(1));
                    responseObj3.put("blackjack", "false");
                    responseObj3.put("participant", connectedClients[1].userID);
                    responseObj3.put("isTurn", connectedClients[1].isTurn);
                    System.out.println("sending clients hands from players");


                    server.getBroadcastOperations().sendEvent("startGameServer", responseObj3.toString());

                }

                JSONObject responseObj = new JSONObject();
                responseObj.put("randomNumber", dealerRandomNums.get(0));
                responseObj.put("blackjack", "false");
                responseObj.put("participant", "dealer");

                System.out.println("sending clients hands from dealer");

                server.getBroadcastOperations().sendEvent("startGameServer", responseObj.toString());


                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {
                        System.out.println(connectedClients[i].userID + " " + connectedClients[i].randomNums.get(0) + " " + connectedClients[i].randomNums.get(1));
                        System.out.println(userHands.get(connectedClients[i].randomToken).handValue);
                    }
                }

                // BOTH PLAYERS GETTING BLACKJACK, AUTOMATICALLY START DEALER
                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {
                        if (i == 0 && connectedClients[i].blackjack && connectedClients[i+1] == null) {
                            boolean dealerBust = false;
                            while (!dealerBust) {
                                Random rand = new Random();
                                int randomNumDealer = rand.nextInt(52);
                                dealerHand += deckValues[randomNumDealer];
                                dealerCards.add(deckValues[randomNumDealer]);

                                if (dealerHand > 21) {      // check for aces
                                    for (int j = 0; j < dealerCards.size(); j++) {
                                        if (dealerCards.get(j).equals(11)) {
                                            dealerHand -= 10;
                                            dealerCards.set(j, 1);
                                            break;
                                        }
                                    }
                                    if (dealerHand > 16) {
                                        dealerBust = true;
                                    }
                                } else if (dealerHand > 16) {
                                    dealerBust = true;
                                }
                                JSONObject responseObjDealer = new JSONObject();
                                responseObjDealer.put("randomNum", randomNumDealer);
                                responseObjDealer.put("participant", "dealer");
                                server.getBroadcastOperations().sendEvent("serverHit", responseObjDealer.toString());
                            }
                            timer3 = new Timer();
                            timer3.scheduleAtFixedRate(new TimerTask() {
                                public void run() {
                                    server.getBroadcastOperations().sendEvent("serverTimerRestart", interval2);
                                    if (interval2 == 0) {
                                        timer3.cancel();
                                        server.getBroadcastOperations().sendEvent("timerDoneRestart", "start");
                                        interval2 = 5;
                                    }
                                    interval2--;
                                }
                            }, 1000, 1000);
                        } else if (i == 1 && (connectedClients[i - 1] == null || connectedClients[i - 1].blackjack) && connectedClients[i].blackjack) {
                            boolean dealerBust = false;
                            while (!dealerBust) {
                                Random rand = new Random();
                                int randomNumDealer = rand.nextInt(52);
                                dealerHand += deckValues[randomNumDealer];
                                dealerCards.add(deckValues[randomNumDealer]);

                                if (dealerHand > 21) {      // check for aces
                                    for (int j = 0; j < dealerCards.size(); j++) {
                                        if (dealerCards.get(j).equals(11)) {
                                            dealerHand -= 10;
                                            dealerCards.set(j, 1);
                                            break;
                                        }
                                    }
                                    if (dealerHand > 16) {
                                        dealerBust = true;
                                    }
                                } else if (dealerHand > 16) {
                                    dealerBust = true;
                                }
                                JSONObject responseObjDealer = new JSONObject();
                                responseObjDealer.put("randomNum", randomNumDealer);
                                responseObjDealer.put("participant", "dealer");
                                server.getBroadcastOperations().sendEvent("serverHit", responseObjDealer.toString());
                                timer3 = new Timer();
                                timer3.scheduleAtFixedRate(new TimerTask() {
                                    public void run() {
                                        server.getBroadcastOperations().sendEvent("serverTimerRestart", interval2);
                                        if (interval2 == 0) {
                                            timer3.cancel();
                                            server.getBroadcastOperations().sendEvent("timerDoneRestart", "start");
                                            interval2 = 5;
                                        }
                                        interval2--;
                                    }
                                }, 1000, 1000);
                            }
                        }
                    }
                }
//                System.out.println("dealer hand: " + dealerHand);

            }
        });


        // SOCKET VERSION OF HIT
        server.addEventListener("hit", String.class, new DataListener<String>() {

            public void onData(SocketIOClient client, String payload, AckRequest ackRequest) throws Exception {
                JSONObject payloadObj = new JSONObject(payload);
                String participant = payloadObj.getString("userID");
                String randomToken = payloadObj.getString("randomToken");

                boolean bust = false;
                boolean playerExists = false;
                String status = "noBust";


                if (!participant.equals("dealer")) {
                    if (!blackjackServer.BlackjackServer.userIdentities.get(participant).equals(randomToken)) {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("message", "token doesn't match");
                        server.getBroadcastOperations().sendEvent("startGameServer", responseObj.toString());
                    }
                }

                Random rand = new Random();
                int randomNum = rand.nextInt(52);
                User user = userHands.get(randomToken);

                for (int i = 0; i < connectedClients.length; i++) {             // this for loop checks to see whos turn it is
                    if (connectedClients[i] == null) {

                    } else if (connectedClients[i].clientID.equals(client.toString())) {
                        if (!userIdentities.get(connectedClients[i].userID).equals(connectedClients[i].randomToken)) {
                            return;
                        }
                        if (!connectedClients[i].isTurn) {
                            System.out.println("not your turn");
                            return;
                        }
                    }
                }

                if (user.handValue > 21 || blackjack) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("message", "busted");
                    server.getBroadcastOperations().sendEvent("startGameServer", responseObj.toString());
                    return;
                }

                for (String key : userIdentities.keySet()) {
                    if (key.equals(participant)) {
                        if (userIdentities.get(key).equals(randomToken)) {
                            playerExists = true;
                        }
                    }
                }

                if (playerExists) {
                    int hand = user.handValue;                                          // new stuff
                    hand += deckValues[randomNum];
                    user.handValue = hand;                                              // new stuff
                    user.handCards.add(deckValues[randomNum]); // new stuff
                    userHands.put(randomToken, user);           // new stuff
                    for (int i = 0; i < connectedClients.length; i++) {
                        if (connectedClients[i] == null) {

                        } else if (connectedClients[i].clientID.equals(client.toString())) {
                            connectedClients[i].randomNums.add(randomNum);
                        }
                    }


                    if (user.handValue > 21) {      // new stuff
                        bust = true;
                        status = "bust";
                        for (int i = 0; i < user.handCards.size(); i++) {   // new stuff
                            if (user.handCards.get(i).equals(11)) { // new stuff
                                int hand2 = user.handValue; // new stuff
                                hand2 -= 10;
                                user.handCards.set(i, 1);   // newstuff
                                user.handValue = hand2;

                                blackjackServer.BlackjackServer.userHands.put(randomToken, user);
                                if (user.handValue < 22) {
                                    bust = false;
                                    break;
                                } else {
                                    bust = true;
                                    break;
                                }

                            }
                        }
                        if (bust) {
                            user.isPlaying = false;                                                 // really new stuff, after sockets
                            userHands.put(randomToken, user);       // really new stuff, after sockets
                            recordResult(false, participant);
                            calculateBalance(false, participant, user.bet, false);
                        }
                    }
                } else if (participant.equals("dealer")) {
                    dealerHand += deckValues[randomNum];
                    dealerCards.add(deckValues[randomNum]);

                    if (dealerHand > 21) {
                        for (int i = 0; i < dealerCards.size(); i++) {
                            if (dealerCards.get(i).equals(11)) {
                                dealerHand -= 10;
                                dealerCards.set(i, 1);
                                break;
                            }
                        }
                        if (dealerHand < 17) {
                            bust = false;
                        } else {
                            user.isPlaying = false;                                                 // really new stuff, after sockets
                            userHands.put(randomToken, user);       // really new stuff, after sockets
                            bust = true;
                        }
                    } else if (dealerHand > 16) {
                        bust = true;
                    }
                }
                if (bust) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("randomNum", randomNum);
                    responseObj.put("participant", participant);
                    responseObj.put("status", "bust");
                    server.getBroadcastOperations().sendEvent("serverHit", responseObj.toString());

                    for (int i = 0; i < connectedClients.length; i++) {             // if player busted, wait token is passed on
                        if (connectedClients[i] != null) {
                            if (connectedClients[i].clientID.equals(client.toString()) && connectedClients[i + 1] != null) {
                                connectedClients[i].isTurn = false;
                                if (connectedClients[i + 1].blackjack) {
                                    boolean dealerBust = false;
                                    while (!dealerBust) {
                                        //Random randDealer = new Random();
                                        int randomNumDealer = rand.nextInt(52);
                                        dealerHand += deckValues[randomNumDealer];
                                        dealerCards.add(deckValues[randomNumDealer]);

                                        if (dealerHand > 21) {      // check for aces
                                            for (int j = 0; j < dealerCards.size(); j++) {
                                                if (dealerCards.get(j).equals(11)) {
                                                    dealerHand -= 10;
                                                    dealerCards.set(j, 1);
                                                    break;
                                                }
                                            }
                                            if (dealerHand > 16) {
                                                dealerBust = true;
                                            }
                                        } else if (dealerHand > 16) {
                                            dealerBust = true;
                                        }
                                        JSONObject responseObjDealer = new JSONObject();
                                        responseObjDealer.put("randomNum", randomNumDealer);
                                        responseObjDealer.put("participant", "dealer");
                                        server.getBroadcastOperations().sendEvent("serverHit", responseObjDealer.toString());
                                    }
                                } else {
                                    connectedClients[i + 1].isTurn = true;
                                }
                            } else if (connectedClients[i].clientID.equals(client.toString())) {    // dealers turn
                                boolean dealerBust = false;
                                while (!dealerBust) {
                                    int randomNumDealer = rand.nextInt(52);
                                    dealerHand += deckValues[randomNumDealer];
                                    dealerCards.add(deckValues[randomNumDealer]);

                                    if (dealerHand > 21) {      // check for aces
                                        for (int j = 0; j < dealerCards.size(); j++) {
                                            if (dealerCards.get(j).equals(11)) {
                                                dealerHand -= 10;
                                                dealerCards.set(j, 1);
                                                break;
                                            }
                                        }
                                        if (dealerHand > 16) {
                                            dealerBust = true;
                                        }
                                    } else if (dealerHand > 16) {
                                        dealerBust = true;
                                    }
                                    JSONObject responseObjDealer = new JSONObject();
                                    responseObjDealer.put("randomNum", randomNumDealer);
                                    responseObjDealer.put("participant", "dealer");
                                    server.getBroadcastOperations().sendEvent("serverHit", responseObjDealer.toString());
                                }
                                for (int k = 0; k < connectedClients.length; k++) {

                                    if (connectedClients[k] != null) {
                                        JSONObject responseObj2 = new JSONObject();
                                        responseObj2.put("participant", connectedClients[k].userID);
                                        if (userHands.get(connectedClients[k].randomToken).handValue > 21) {
                                            responseObj2.put("result", "lose");
                                            //  recordResult(false, connectedClients[k].userID);
                                        } else if (dealerHand > 21 && userHands.get(connectedClients[k].randomToken).handValue < 21) {   // if dealer bust and player didnt
                                            responseObj2.put("result", "win");
                                            recordResult(true, connectedClients[k].userID);
                                            calculateBalance(true, connectedClients[k].userID, userHands.get(connectedClients[k].randomToken).bet, false);
                                        } else if (dealerHand > userHands.get(connectedClients[k].randomToken).handValue) {
                                            responseObj2.put("result", "lose");
                                            recordResult(false, connectedClients[k].userID);
                                            calculateBalance(false, connectedClients[k].userID, userHands.get(connectedClients[k].randomToken).bet, false);
                                        } else if (dealerHand < userHands.get(connectedClients[k].randomToken).handValue) {
                                            responseObj2.put("result", "win");
                                            recordResult(true, connectedClients[k].userID);
                                            calculateBalance(true, connectedClients[k].userID, userHands.get(connectedClients[k].randomToken).bet, false);
                                        } else {
                                            responseObj2.put("result", "push");
                                        }
                                        server.getBroadcastOperations().sendEvent("results", responseObj2.toString());
                                    }
                                }

                                for (int k = 0; k < connectedClients.length; k++) {
                                    if (connectedClients[k] != null) {
                                        if (k == 0) {
                                            connectedClients[k].isTurn = true;
                                        } else if (k == 1 && connectedClients[0] != null) {
                                            connectedClients[k].isTurn = false;
                                        }
                                    }
                                }


                                timer3 = new Timer();
                                timer3.scheduleAtFixedRate(new TimerTask() {
                                    public void run() {
                                        server.getBroadcastOperations().sendEvent("serverTimerRestart", interval2);
                                        if (interval2 == 0) {
                                            timer3.cancel();
                                            server.getBroadcastOperations().sendEvent("timerDoneRestart", "start");
                                            interval2 = 5;
                                        }
                                        interval2--;
                                    }
                                }, 1000, 1000);

                            }
                        }
                    }
                } else {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("randomNum", randomNum);
                    responseObj.put("participant", participant);
                    responseObj.put("status", "notBust");
                    server.getBroadcastOperations().sendEvent("serverHit", responseObj.toString());
                }
            }
        });

        server.addEventListener("stand", String.class, new DataListener<String>() {

            public void onData(SocketIOClient client, String payload, AckRequest ackRequest) throws Exception {
                JSONObject payloadObj = new JSONObject(payload);
                String participant = payloadObj.getString("userID");
                String randomToken = payloadObj.getString("randomToken");

                for (int i = 0; i < connectedClients.length; i++) {         // check to see if random token matches
                    if (connectedClients[i] != null) {
                        if (!userIdentities.get(participant).equals(randomToken)) {
                            return;
                        }
                    }
                }

                for (int i = 0; i < connectedClients.length; i++) {             // this for loop checks to see whos turn it is
                    if (connectedClients[i] == null) {

                    } else if (connectedClients[i].clientID.equals(client.toString())) {
                        if (!userIdentities.get(connectedClients[i].userID).equals(connectedClients[i].randomToken)) {
                            return;
                        }
                        if (!connectedClients[i].isTurn) {
                            System.out.println("not your turn");
                            return;
                        }
                    }
                }

                for (int i = 0; i < connectedClients.length; i++) {
                    if (connectedClients[i] != null) {
                        if (i == 0 && connectedClients[i].clientID.equals(client.toString()) && connectedClients[i + 1] != null) {
                            connectedClients[i].isTurn = false;
                            connectedClients[i + 1].isTurn = true;
                            if (connectedClients[i + 1].blackjack) {
                                break;
                            }

                            JSONObject responseObj = new JSONObject();
                            responseObj.put("participant", participant);
                            responseObj.put("isTurn", connectedClients[i].isTurn);
                            server.getBroadcastOperations().sendEvent("playerStands", responseObj.toString());

                            return;
                        } else if (i == 1 && !connectedClients[i].blackjack && connectedClients[0] != null) {
                            connectedClients[i].isTurn = false;
                            connectedClients[i - 1].isTurn = true;
                        }
                    }
                }

                boolean dealerBust = false;
                while (!dealerBust) {
                    Random rand = new Random();
                    int randomNumDealer = rand.nextInt(52);
                    dealerHand += deckValues[randomNumDealer];
                    dealerCards.add(deckValues[randomNumDealer]);

                    if (dealerHand > 21) {      // check for aces
                        for (int j = 0; j < dealerCards.size(); j++) {
                            if (dealerCards.get(j).equals(11)) {
                                dealerHand -= 10;
                                dealerCards.set(j, 1);
                                break;
                            }
                        }
                        if (dealerHand > 16) {
                            dealerBust = true;
                        }
                    } else if (dealerHand > 16) {
                        dealerBust = true;
                    }
                    JSONObject responseObjDealer = new JSONObject();
                    responseObjDealer.put("randomNum", randomNumDealer);
                    responseObjDealer.put("participant", "dealer");
                    server.getBroadcastOperations().sendEvent("serverHit", responseObjDealer.toString());

                }
                if (dealerBust) {
                    System.out.println("inside the result calculation statement");
                    for (int i = 0; i < connectedClients.length; i++) {

                        if (connectedClients[i] != null) {
                            JSONObject responseObj = new JSONObject();
                            responseObj.put("participant", connectedClients[i].userID);
                            if (userHands.get(connectedClients[i].randomToken).handValue > 21) {
                                responseObj.put("result", "lose");
                            } else if (dealerHand > 21 && userHands.get(connectedClients[i].randomToken).handValue <= 21 && !connectedClients[i].blackjack) {   // if dealer bust and player didnt
                                responseObj.put("result", "win");
                                recordResult(true, connectedClients[i].userID);
                                calculateBalance(true, connectedClients[i].userID, userHands.get(connectedClients[i].randomToken).bet, false);
                            } else if (dealerHand > userHands.get(connectedClients[i].randomToken).handValue) {
                                responseObj.put("result", "lose");
                                recordResult(false, connectedClients[i].userID);
                                calculateBalance(false, connectedClients[i].userID, userHands.get(connectedClients[i].randomToken).bet, false);
                            } else if (dealerHand < userHands.get(connectedClients[i].randomToken).handValue) {
                                responseObj.put("result", "win");
                                recordResult(true, connectedClients[i].userID);
                                calculateBalance(true, connectedClients[i].userID, userHands.get(connectedClients[i].randomToken).bet, false);
                            } else {
                                responseObj.put("result", "push");
                            }
                            server.getBroadcastOperations().sendEvent("results", responseObj.toString());
                        }
                    }
                    timer3 = new Timer();
                    //  interval = 5;
                    timer3.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            server.getBroadcastOperations().sendEvent("serverTimerRestart", interval2);
                            if (interval2 == 0) {
                                timer3.cancel();
                                server.getBroadcastOperations().sendEvent("timerDoneRestart", "start");
                                interval2 = 5;
                            }
                            interval2--;
                        }
                    }, 1000, 1000);
                }
            }
        });


        server.start();


        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();


    }

    public static void recordResult(boolean win, String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        String result;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String querySQL;
            if (win) {
                querySQL = "UPDATE Users SET wins = wins + 1 WHERE username = ?";
            } else {
                querySQL = "UPDATE Users SET losses = losses + 1 WHERE username = ?";
            }
            ps = conn.prepareStatement(querySQL);
            ps.setString(1, username);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void calculateBalance(boolean win, String username, int bet, boolean blackjack) {
        Connection conn = null;
        PreparedStatement ps = null;
        double blackjackBet = bet * 1.5;
        System.out.println("inside the calculate method: username = " + username + " bet = " + bet + " result = " + win);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String querySQL;
            if (win) {
                querySQL = "UPDATE Users SET wallet = wallet + ? WHERE username = ?";
            } else {
                querySQL = "UPDATE Users SET wallet = wallet - ? WHERE username = ?";
            }
            ps = conn.prepareStatement(querySQL);
            if (blackjack) {
                ps.setDouble(1, blackjackBet);
            } else {
                ps.setInt(1, bet);
            }
            ps.setString(2, username);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class User {
    int handValue;
    List<Integer> handCards = new ArrayList<>();
    int bet;
    boolean isPlaying;

    User(int handValue, ArrayList handCards, int bet, boolean isPlaying) {
        this.handValue = handValue;
        this.handCards = handCards;
        this.bet = bet;
        this.isPlaying = isPlaying;
    }
}

class PlayersAtTable {
    String clientID;
    String userID;
    String randomToken;
    int seat;
    List<Integer> randomNums = new ArrayList<>();
    boolean isHost;
    boolean isTurn;
    boolean blackjack;

    PlayersAtTable(String clientID, String userID, String randomToken, int seat, ArrayList randomNums, boolean isHost, boolean isTurn, boolean blackjack) {
        this.clientID = clientID;
        this.userID = userID;
        this.randomToken = randomToken;
        this.seat = seat;
        this.randomNums = randomNums;
        this.isHost = isHost;
        this.isTurn = isTurn;
        this.blackjack = blackjack;
    }
}

