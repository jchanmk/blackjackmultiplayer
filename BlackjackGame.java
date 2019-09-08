package blackjackServer;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

// Note: The entire game functionality has been refactored to work with sockets instead of http requests

@RestController
public class BlackjackGame {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/Blackjack?useUnicode=true&characterEncoding=UTF-8";
    static final String USER = "root";
    static final String PASSWORD = "p56230101P";

//    @RequestMapping(value = "/renew", method = RequestMethod.POST)
//    // <-- setup the endpoint URL at /hello with the HTTP POST method
//    public ResponseEntity<String> renew(@RequestBody String payload, HttpServletRequest request) {
//        JSONObject payloadObj = new JSONObject(payload);
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Content-Type", "application/json");
//
//        String userID = payloadObj.getString("userID");
//        String randomToken = payloadObj.getString("randomToken");
//
//        if (!blackjackServer.BlackjackServer.userIdentities.get(userID).equals(randomToken)) {
//            JSONObject responseObj = new JSONObject();
//            responseObj.put("message", "token doesn't match");
//            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
//        }
////        System.out.println("userHands Hashmap" + blackjackServer.BlackjackServer.userHands);
////        System.out.println("userIdentities Hashmap" + blackjackServer.BlackjackServer.userIdentities);
//
//
//        blackjackServer.User user = new blackjackServer.User(0, new ArrayList<Integer>(), 0, false); // new stuff trying
//        blackjackServer.BlackjackServer.userHands.put(randomToken, user);                                       // new stuff trying
//
//        blackjackServer.BlackjackServer.dealerHand = 0;
//        blackjackServer.BlackjackServer.dealerCards.clear();
//        BlackjackServer.dealerRandomNums.clear();
////
////        for(int i = 0; i < BlackjackServer.connectedClients.length; i++){
////            if (BlackjackServer.connectedClients[i] == null) {
////
////            } else {
////                BlackjackServer.connectedClients[i].randomNums.clear();
////            }
////        }
//
//        blackjackServer.BlackjackServer.blackjack = false;
//
//        return new ResponseEntity(1, responseHeaders, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/start", method = RequestMethod.POST)
//    // <-- setup the endpoint URL at /hello with the HTTP POST method
//    public ResponseEntity<String> startGame(@RequestBody String payload, HttpServletRequest request) {
//        JSONObject payloadObj = new JSONObject(payload);
//        String participant = payloadObj.getString("userID");
//        String randomToken = payloadObj.getString("randomToken");
////        System.out.println(participant + " " + randomToken);
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Content-Type", "application/json");
//
//        Random rand = new Random();
//        int randomNum = rand.nextInt(13);
//        if (!participant.equals("dealer")) {
//            if (!blackjackServer.BlackjackServer.userIdentities.get(participant).equals(randomToken)) {
//                JSONObject responseObj = new JSONObject();
//                responseObj.put("message", "token doesn't match");
//                return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
//            }
//        }
//        for (String key : blackjackServer.BlackjackServer.userIdentities.keySet()) {
//            if (key.equals(participant)) {
//                blackjackServer.User user = blackjackServer.BlackjackServer.userHands.get(randomToken);    // new stuff
//                int hand = user.handValue;                                                                      // new stuff
//
//                hand += blackjackServer.BlackjackServer.deckValues[randomNum];
//
//                user.handValue = hand;  // new stuff
//                user.handCards.add(blackjackServer.BlackjackServer.deckValues[randomNum]);  // new stuff
//                blackjackServer.BlackjackServer.userHands.put(randomToken, user);           // new stuff
//
//
//                if (hand == 21 && user.handCards.size() == 2) {        // new stuff
//
//                    JSONObject responseObj = new JSONObject();
//                    responseObj.put("randomNumber", randomNum);
//                    responseObj.put("blackjack", "true");
//                    blackjackServer.BlackjackServer.blackjack = true;
//                    recordResult(true, participant);
//                    calculateBalance(true, participant, user.bet, true);
//
//                    user.isPlaying = false;                                                 // really new stuff, after sockets
//                    blackjackServer.BlackjackServer.userHands.put(randomToken, user);       // really new stuff, after sockets
//
//                    return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
//                } else if (hand >= 21 && user.handCards.size() == 2) {              // new stuff
//                    hand -= 10;
//                    user.handValue = hand;
//                    user.handCards.set(0, 1);
//                    blackjackServer.BlackjackServer.userHands.put(randomToken, user);
//                }
//            }
//        }
//        if (participant.equals("dealer")) {
//            blackjackServer.BlackjackServer.dealerHand += blackjackServer.BlackjackServer.deckValues[randomNum];
//            blackjackServer.BlackjackServer.dealerCards.add(blackjackServer.BlackjackServer.deckValues[randomNum]);
//        }
//        JSONObject responseObj = new JSONObject();
//        responseObj.put("randomNumber", randomNum);
//        responseObj.put("blackjack", "false");
//        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/hit", method = RequestMethod.POST)
//    // <-- setup the endpoint URL at /hello with the HTTP POST method
//    public ResponseEntity<String> hit(@RequestBody String payload, HttpServletRequest request) {
//        JSONObject payloadObj = new JSONObject(payload);
//        String participant = payloadObj.getString("userID");
//        String randomToken = payloadObj.getString("randomToken");
//
////        System.out.println("player hand = "+ blackjackServer.BlackjackServer.userHands.get(randomToken));
////        System.out.println("player cards = " + blackjackServer.BlackjackServer.userCards.get(randomToken));
////        System.out.println("dealer hand = "+ blackjackServer.BlackjackServer.dealerHand);
////        System.out.println("dealer cards = " + blackjackServer.BlackjackServer.dealerCards);
//
//        boolean bust = false;
//        boolean playerExists = false;
//        String status = "noBust";
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Content-Type", "application/json");
//
//        if (!participant.equals("dealer")) {
//            if (!blackjackServer.BlackjackServer.userIdentities.get(participant).equals(randomToken)) {
//                JSONObject responseObj = new JSONObject();
//                responseObj.put("message", "token doesn't match");
//                return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
//            }
//        }
//
//        Random rand = new Random();
//        int randomNum = rand.nextInt(13);
//        blackjackServer.User user = blackjackServer.BlackjackServer.userHands.get(randomToken);        // new stuff
//
//    //    System.out.println(user.handValue + " " + user.handCards + " " + user.bet);
//
//        if (user.handValue > 21 || blackjackServer.BlackjackServer.blackjack == true) { // new stuff
//
//            return new ResponseEntity(1, responseHeaders, HttpStatus.BAD_REQUEST);
//        }
//
//        for (String key : blackjackServer.BlackjackServer.userIdentities.keySet()) {
//            if (key.equals(participant)) {
//                if (blackjackServer.BlackjackServer.userIdentities.get(key).equals(randomToken)) {
//                    playerExists = true;
//                } else {
//
//                }
//            }
//        }
//
//        if (playerExists) {
//            int hand = user.handValue;                                          // new stuff
//            hand += blackjackServer.BlackjackServer.deckValues[randomNum];
//            user.handValue = hand;                                              // new stuff
//            user.handCards.add(blackjackServer.BlackjackServer.deckValues[randomNum]); // new stuff
//            blackjackServer.BlackjackServer.userHands.put(randomToken, user);           // new stuff
//
//            if (user.handValue > 21) {      // new stuff
//
//                bust = true;
//                status = "bust";
//                for (int i = 0; i < user.handCards.size(); i++) {   // new stuff
//                    if (user.handCards.get(i).equals(11)) { // new stuff
//                        int hand2 = user.handValue; // new stuff
//                        hand2 -= 10;
//                        user.handCards.set(i, 1);   // newstuff
//                        user.handValue = hand2;
//
//                        blackjackServer.BlackjackServer.userHands.put(randomToken, user);
//                        if(user.handValue < 22){
//                            bust = false;
//                            break;
//                        }else{
//                            bust = true;
//                            break;
//                        }
//
//                    }
//                }
//                if (bust) {
//                    user.isPlaying = false;                                                 // really new stuff, after sockets
//                    blackjackServer.BlackjackServer.userHands.put(randomToken, user);       // really new stuff, after sockets
//                    recordResult(false, participant);
//                    calculateBalance(false, participant, user.bet, false);
//                }
//            }
//        } else if (participant.equals("dealer")) {
//            blackjackServer.BlackjackServer.dealerHand += blackjackServer.BlackjackServer.deckValues[randomNum];
//            blackjackServer.BlackjackServer.dealerCards.add(blackjackServer.BlackjackServer.deckValues[randomNum]);
//
//            if (blackjackServer.BlackjackServer.dealerHand > 21) {
//                for (int i = 0; i < blackjackServer.BlackjackServer.dealerCards.size(); i++) {
//                    if (blackjackServer.BlackjackServer.dealerCards.get(i).equals(11)) {
//                        blackjackServer.BlackjackServer.dealerHand -= 10;
//                        blackjackServer.BlackjackServer.dealerCards.set(i, 1);
//                        break;
//                    }
//                }
//                if (blackjackServer.BlackjackServer.dealerHand < 17) {
//                    bust = false;
//                } else {
//                    user.isPlaying = false;                                                 // really new stuff, after sockets
//                    blackjackServer.BlackjackServer.userHands.put(randomToken, user);       // really new stuff, after sockets
//                    bust = true;
//                }
//            } else if (blackjackServer.BlackjackServer.dealerHand > 16) {
//                bust = true;
//            }
//        }
//        if (bust) {
//            JSONObject responseObj = new JSONObject();
//            responseObj.put("status", "bust");
//            responseObj.put("randomNumber", randomNum);
//            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
//        } else {
//            JSONObject responseObj = new JSONObject();
//            responseObj.put("status", "notBust");
//            responseObj.put("randomNumber", randomNum);
//            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
//        }
//    }
//
//    @RequestMapping(value = "/compare", method = RequestMethod.POST)
//    // <-- setup the endpoint URL at /hello with the HTTP POST method
//    public ResponseEntity<String> compare(@RequestBody String payload, HttpServletRequest request) {
//        HttpHeaders responseHeaders = new HttpHeaders();
//        JSONObject payloadObj = new JSONObject(payload);
//        responseHeaders.set("Content-Type", "application/json");
//
//        String participant = payloadObj.getString("userID");
//        String randomToken = payloadObj.getString("randomToken");
//        JSONObject responseObj = new JSONObject();
//        blackjackServer.User user = blackjackServer.BlackjackServer.userHands.get(randomToken);
//
//        if (blackjackServer.BlackjackServer.dealerHand > 21) {
//            recordResult(true, participant);
//            calculateBalance(true, participant, user.bet, false);
//
//            responseObj.put("winner", "player");
//        } else if (blackjackServer.BlackjackServer.dealerHand > user.handValue) {   // new stuff
//            responseObj.put("winner", "dealer");
//            recordResult(false, participant);
//            calculateBalance(false, participant, user.bet, false);
//        } else if (blackjackServer.BlackjackServer.dealerHand < user.handValue) {   // new stuff
//
//            responseObj.put("winner", "player");
//            recordResult(true, participant);
//            calculateBalance(true, participant, user.bet, false);
//
//
//        } else {
//            responseObj.put("winner", "tie");
//        }
//
//        user.isPlaying = false;                                                 // really new stuff, after sockets
//        blackjackServer.BlackjackServer.userHands.put(randomToken, user);       // really new stuff, after sockets
//
//        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
//    }
//
//
//
//    public static void recordResult(boolean win, String username) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        String result;
//
//        try {
//            Class.forName(JDBC_DRIVER);
//            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
//            String querySQL;
//            if (win) {
//                querySQL = "UPDATE Users SET wins = wins + 1 WHERE username = ?";
//            } else {
//                querySQL = "UPDATE Users SET losses = losses + 1 WHERE username = ?";
//            }
//            ps = conn.prepareStatement(querySQL);
//            ps.setString(1, username);
//            ps.executeUpdate();
//            ps.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void calculateBalance(boolean win, String username, int bet, boolean blackjack) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        double blackjackBet = bet * 1.5;
//
//        try {
//            Class.forName(JDBC_DRIVER);
//            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
//            String querySQL;
//            if (win) {
//                querySQL = "UPDATE Users SET wallet = wallet + ? WHERE username = ?";
//            } else {
//                querySQL = "UPDATE Users SET wallet = wallet - ? WHERE username = ?";
//            }
//            ps = conn.prepareStatement(querySQL);
//            if(blackjack){
//                ps.setDouble(1, blackjackBet);
//            } else{
//                ps.setInt(1, bet);
//            }
//            ps.setString(2, username);
//            ps.executeUpdate();
//            ps.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}