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

// Questions
// how to upload images for users

// Things need to do
// for daily bonus, need to store date collected in database, then send that date over to client,
// client will then use that as "today" in order to calculate the time countdown


@RestController
public class BlackjackUsers {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/Blackjack?useUnicode=true&characterEncoding=UTF-8";
    static final String USER = "root";
    static final String PASSWORD = "p56230101P";

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ResponseEntity<String> profile(@RequestBody String payload, HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        JSONObject payloadObj = new JSONObject(payload);
        String userID = payloadObj.getString("userID");
        String randomToken = payloadObj.getString("randomToken");

        if(!blackjackServer.BlackjackServer.userIdentities.get(userID).equals(randomToken)){
            return new ResponseEntity("bad request", responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Connection conn = null;
        PreparedStatement ps = null;

        ResultSet myQuery = null;
        String username = null;
        String wallet = null;
        String wins = null;
        String losses = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String querySQL = "SELECT username, wallet, wins, losses FROM Users WHERE username = ?";
            ps = conn.prepareStatement(querySQL);
            ps.setString(1, userID);
            myQuery = ps.executeQuery();

            while (myQuery.next()) {
                username = myQuery.getString("username");
                wallet = myQuery.getString("wallet");
                wins = myQuery.getString("wins");
                losses = myQuery.getString("losses");
            }


            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject responseObj = new JSONObject();
        responseObj.put("username", username);
        responseObj.put("wallet", wallet);
        responseObj.put("wins", wins);
        responseObj.put("losses", losses);
        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);

    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody String payload, HttpServletRequest request) {
        JSONObject payloadObj = new JSONObject(payload);
        String username = payloadObj.getString("username");
        String password = payloadObj.getString("password");
        System.out.println(username + "in the register");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        MessageDigest digest = null;
        String hashedKey = null;
        try {
            digest = MessageDigest.getInstance("SHA-256"); //digest algorithm set to SHA-256
            hashedKey = bytesToHex(digest.digest(password.getBytes("UTF-8")));
        } catch (Exception e) {
        }

        boolean uniqueUsername = registerPlayer(username, hashedKey);
//        System.out.println("/register post request");
        if(uniqueUsername){
            String randomToken = randomToken();

            JSONObject responseObj = new JSONObject();
            responseObj.put("userID", username);
            responseObj.put("randomToken", randomToken);
//            System.out.println(responseObj.toString());
            blackjackServer.BlackjackServer.userIdentities.put(username, randomToken);  // adds randomToken with corresponding user to map

            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
        } else{
            JSONObject responseObj2 = new JSONObject();
            responseObj2.put("Everything", "not ok");
            return new ResponseEntity(responseObj2.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody String payload, HttpServletRequest request) {
        JSONObject payloadObj = new JSONObject(payload);
        String username = payloadObj.getString("username");
        String password = payloadObj.getString("password");
        boolean isLoggedIn = false;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
//        System.out.println(payloadObj.toString());
        MessageDigest digest = null;
        String hashedKey = null;
        try {
            digest = MessageDigest.getInstance("SHA-256"); //digest algorithm set to SHA-256
            hashedKey = bytesToHex(digest.digest(password.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        isLoggedIn = loginPlayer(username, hashedKey);

        if (isLoggedIn) {
            String randomToken = randomToken();

            // This loop checks to see if the user has been on the site before, if they have
            // It removes their previous randomToken from the hashtable
            for (String key : blackjackServer.BlackjackServer.userIdentities.keySet()){
                if(key.equals(username)){   // they have been here before
                    String previousToken = blackjackServer.BlackjackServer.userIdentities.get(key);
                    for(String key2 : blackjackServer.BlackjackServer.userHands.keySet()){
                        if(key2.equals(previousToken)){
                            blackjackServer.BlackjackServer.userHands.remove(previousToken);
//                            blackjackServer.BlackjackServer.userCards.remove(randomToken);
                        }
                    }
                }
            }

            blackjackServer.BlackjackServer.userIdentities.put(username, randomToken);  // adds randomToken with corresponding user to map

            JSONObject responseObj = new JSONObject();
            responseObj.put("userID", username);
            responseObj.put("randomToken", randomToken);
            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
        } else {
            JSONObject responseObj = new JSONObject();
            responseObj.put("user", "is not logged in");
            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/bet", method = RequestMethod.POST)
    // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> bet(@RequestBody String payload, HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        JSONObject payloadObj = new JSONObject(payload);
        responseHeaders.set("Content-Type", "application/json");

        String participant = payloadObj.getString("userID");
        String randomToken = payloadObj.getString("randomToken");

        // include check for random token


        int bet = payloadObj.getInt("userBet");
        System.out.println("user bets " + bet);

        JSONObject responseObj = new JSONObject();
        ///     NEED TO DO IMPLEMENTATION WITH HASHMAP
        blackjackServer.User user = blackjackServer.BlackjackServer.userHands.get(randomToken);
        boolean hasEnoughMoney = checkFunds(participant, bet);

        if(hasEnoughMoney){
            user.bet = bet;
            System.out.println("had enough money, placing bet of" + bet);
            BlackjackServer.userHands.put(randomToken, user);
//            System.out.println("bet is " + user.bet);
            responseObj.put("enoughFunds", "true");

        } else{
            responseObj.put("enoughFunds", "false");
        }

        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);

    }
    @RequestMapping(value = "/bonus", method = RequestMethod.POST)
    // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> bonus(@RequestBody String payload, HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        JSONObject payloadObj = new JSONObject(payload);
        responseHeaders.set("Content-Type", "application/json");

        String participant = payloadObj.getString("userID");
        String randomToken = payloadObj.getString("randomToken");
        JSONObject responseObj = new JSONObject();

        // include check for random token

        boolean bonusAllowed = dailyBonus(participant);
        if(bonusAllowed){
            responseObj.put("bonus", "allowed");
        }

        return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);

    }
    public static boolean dailyBonus(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        String result;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String querySQL;
            querySQL = "UPDATE Users SET wallet = wallet + 100.00 WHERE username = ?";

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
        return true;
    }

    public static boolean loginPlayer(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;

        ResultSet myQuery = null;
        ResultSet myQuery2 = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String querySQL = "SELECT username FROM Users";
            ps = conn.prepareStatement(querySQL);
            myQuery = ps.executeQuery();

            while (myQuery.next()) {
                String existingUser = myQuery.getString("username");
                if (existingUser.equals(username)) {
                    String findPassword = "SELECT hashedPassword FROM Users WHERE username = ?";
                    ps2 = conn.prepareStatement(findPassword);
                    ps2.setString(1, username);
                    myQuery2 = ps2.executeQuery();
                    while (myQuery2.next()) {
                        String existingPassword = myQuery2.getString("hashedPassword");
                        if (existingPassword.equals(password)) {
                            return true;
                        }
                    }
                }
            }

            ps.close();
            ps2.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean registerPlayer(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet myQuery = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String queryCheckIfExists = "SELECT username FROM Users";
            ps2 = conn.prepareStatement(queryCheckIfExists);
            myQuery = ps2.executeQuery();
            while(myQuery.next()){
                String existingUser = myQuery.getString("username");
                if(existingUser.equalsIgnoreCase(username)){
                    return false;
                }
            }


            String querySQL = "INSERT INTO Users (username, hashedPassword, wallet) VALUES(" +
                    "?, ?, 300)";   // start with $300
            ps = conn.prepareStatement(querySQL);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String randomToken() {
        String text = "";
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        int randomNum = rand.nextInt(possible.length());
        for (int i = 0; i < 10; i++) {
            randomNum = rand.nextInt(possible.length());
            text += possible.charAt(randomNum);
        }
        return text;
    }

    public static boolean checkFunds(String username, int bet){
        Connection conn = null;
        PreparedStatement ps = null;

        ResultSet myQuery = null;
        String wallet = null;
        double balance;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String querySQL = "SELECT wallet FROM Users WHERE username = ?";
            ps = conn.prepareStatement(querySQL);
            ps.setString(1, username);
            myQuery = ps.executeQuery();

            while (myQuery.next()) {
                wallet = myQuery.getString("wallet");
                balance = Double.parseDouble(wallet);
                if(balance < bet){
                    return false;
                }
            }


            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }


    //Helper method to convert bytes into hexadecimal
    public static String bytesToHex(byte[] in) {
        StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}