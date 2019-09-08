let deck = [
    { src: "Assets/PlayingCards/2_of_clubs.svg", value: 2 },
    { src: "Assets/PlayingCards/3_of_clubs.svg", value: 3 },
    { src: "Assets/PlayingCards/4_of_clubs.svg", value: 4 },
    { src: "Assets/PlayingCards/5_of_clubs.svg", value: 5 },
    { src: "Assets/PlayingCards/6_of_clubs.svg", value: 6 },
    { src: "Assets/PlayingCards/7_of_clubs.svg", value: 7 },
    { src: "Assets/PlayingCards/8_of_clubs.svg", value: 8 },
    { src: "Assets/PlayingCards/9_of_clubs.svg", value: 9 },
    { src: "Assets/PlayingCards/10_of_clubs.svg", value: 10 },
    { src: "Assets/PlayingCards/jack_of_clubs.svg", value: 10 },
    { src: "Assets/PlayingCards/queen_of_clubs.svg", value: 10 },
    { src: "Assets/PlayingCards/king_of_clubs.svg", value: 10 },
    { src: "Assets/PlayingCards/ace_of_clubs.svg", value: 11 },

    { src: "Assets/PlayingCards/2_of_diamonds.svg", value: 2 },
    { src: "Assets/PlayingCards/3_of_diamonds.svg", value: 3 },
    { src: "Assets/PlayingCards/4_of_diamonds.svg", value: 4 },
    { src: "Assets/PlayingCards/5_of_diamonds.svg", value: 5 },
    { src: "Assets/PlayingCards/6_of_diamonds.svg", value: 6 },
    { src: "Assets/PlayingCards/7_of_diamonds.svg", value: 7 },
    { src: "Assets/PlayingCards/8_of_diamonds.svg", value: 8 },
    { src: "Assets/PlayingCards/9_of_diamonds.svg", value: 9 },
    { src: "Assets/PlayingCards/10_of_diamonds.svg", value: 10 },
    { src: "Assets/PlayingCards/jack_of_diamonds.svg", value: 10 },
    { src: "Assets/PlayingCards/queen_of_diamonds.svg", value: 10 },
    { src: "Assets/PlayingCards/king_of_diamonds.svg", value: 10 },
    { src: "Assets/PlayingCards/ace_of_diamonds.svg", value: 11 },

    { src: "Assets/PlayingCards/3_of_spades.svg", value: 3 },
    { src: "Assets/PlayingCards/2_of_spades.svg", value: 2 },
    { src: "Assets/PlayingCards/4_of_spades.svg", value: 4 },
    { src: "Assets/PlayingCards/5_of_spades.svg", value: 5 },
    { src: "Assets/PlayingCards/6_of_spades.svg", value: 6 },
    { src: "Assets/PlayingCards/7_of_spades.svg", value: 7 },
    { src: "Assets/PlayingCards/8_of_spades.svg", value: 8 },
    { src: "Assets/PlayingCards/9_of_spades.svg", value: 9 },
    { src: "Assets/PlayingCards/10_of_spades.svg", value: 10 },
    { src: "Assets/PlayingCards/jack_of_spades.svg", value: 10 },
    { src: "Assets/PlayingCards/queen_of_spades.svg", value: 10 },
    { src: "Assets/PlayingCards/king_of_spades.svg", value: 10 },
    { src: "Assets/PlayingCards/ace_of_spades.svg", value: 11 },

    { src: "Assets/PlayingCards/2_of_hearts.svg", value: 2 },
    { src: "Assets/PlayingCards/3_of_hearts.svg", value: 3 },
    { src: "Assets/PlayingCards/4_of_hearts.svg", value: 4 },
    { src: "Assets/PlayingCards/5_of_hearts.svg", value: 5 },
    { src: "Assets/PlayingCards/6_of_hearts.svg", value: 6 },
    { src: "Assets/PlayingCards/7_of_hearts.svg", value: 7 },
    { src: "Assets/PlayingCards/8_of_hearts.svg", value: 8 },
    { src: "Assets/PlayingCards/9_of_hearts.svg", value: 9 },
    { src: "Assets/PlayingCards/10_of_hearts.svg", value: 10 },
    { src: "Assets/PlayingCards/jack_of_hearts.svg", value: 10 },
    { src: "Assets/PlayingCards/queen_of_hearts.svg", value: 10 },
    { src: "Assets/PlayingCards/king_of_hearts.svg", value: 10 },
    { src: "Assets/PlayingCards/ace_of_hearts.svg", value: 11 }
];
var player1 = { name: "player1", x: 650, y: 360 };
var player2 = { name: "player2", x: 950, y: 330 };
var dealer = { name: "dealer", x: 650, y: 65 };

var seat = [{ name: "player2", x: 950, y: 330, usernameX: 935, usernameY: 500, cards: [] },
{ name: "player1", x: 650, y: 360, usernameX: 635, usernameY: 520, cards: [] }]; // seat at the table

var responseArray = [];
var seatNumber;
var seatNumberOpponent1;
var username = cookieValues("userID");
var usernameOpponent1;
var isHost = false;
var dealerFirstCard;
var playerCards = [];
var dealerCards = [];
var isTurn = false;
var response;
var handDealt = false;
var tableDone = false;
var functionRunning = false;
var playersDone = false;
var playerBusted = false;
var userRandomString;
var betTime;
var resetTime;
var tempResponseRandomNum;
var tempResponseRandomNum2;
var tempParticipant;
var tempParticipant2;
var canvas = document.querySelector('canvas');
var c = canvas.getContext('2d');
var table = new Image();
var betValue = 0;
canvas.width = window.innerWidth;
canvas.height = window.innerHeight - 95;
table.src = 'Assets/blackjackTable.png';

var hitButton = new Image();
hitButton.src = 'Assets/hitButton@2x.png';

var standButton = new Image();
standButton.src = 'Assets/standButton@2x.png';

var redChipButton = new Image();
redChipButton.src = 'Assets/5chip@2x.png';

var greenChipButton = new Image();
greenChipButton.src = 'Assets/25chip@2x.png';

var blackChipButton = new Image();
blackChipButton.src = 'Assets/100chip@2x.png';
var url = "http://localhost:8080";
// var url = "http://3.14.151.219:8080";



var redChip = { name: "redChip", x: 610, y: 530, value: 5 };
var greenChip = { name: "greenChip", x: 612, y: 530, value: 25 };
var blackChip = { name: "blackChip", x: 613, y: 530, value: 100 };

var redChip2 = { name: "redChip", x: 965, y: 500, value: 5 };
var greenChip2 = { name: "greenChip", x: 967, y: 500, value: 25 };
var blackChip2 = { name: "blackChip", x: 968, y: 500, value: 100 };
// var socket = io.connect("http://localhost:9092");   // connect to socket server
 var socket = io.connect("http://3.14.148.190:9092");   // connect to socket server



$(document).ready(function () {
    console.log("document . ready function");
    resetTable();
    $("#sitDown").css("display", "block");

    // connectToSocket();
});
$(canvas).on("click", clickButtonOnCanvas);

socket.on("inSession", function(){
    $("#sitDown").css("display", "none");
})

function sit(){
    connectToSocket();
    $("#sitDown").css("display", "none");
}

function restart() {
    resetTable();
    connectToSocket();
}

function sendRestartRequest() {
    socket.emit("restartGame", "restart");
}
socket.on("restartConfirm", function () {
    console.log("restart confirmed");
    seat[0].x = 950;
    seat[1].x = 650;
    dealer.x = 650;
    seat[0].cards = [];
    seat[1].cards = [];
    dealerCards = [];
    responseArray = [];
    dealerFirstCard = false;
    $("#userBet").text(betValue);


    restart();
});

function resetTable() {
    c.clearRect(0, 0, canvas.width, canvas.height);
    handDealt = false;

    $("#bust").css("display", "none");
    $("#win").css("display", "none");
    $("#lose").css("display", "none");
    $("#tie").css("display", "none");
    $("#wait").css("display", "none");
    $("#yourTurn").css("display", "none");
    $("#blackjack").css("display", "none");
    $('#hitButton').prop('disabled', false);
    // c.clearRect(343,0, 50, 70);

    drawTable(c, table);    // draw the table for the game
    setTimeout(function () {
        drawHitButton(c, hitButton);
        drawStandButton(c, standButton);
        drawRedChipButton(c, redChipButton);
        drawGreenChipButton(c, greenChipButton);
        drawBlackChipButton(c, blackChipButton);
        $("#gameResetTimer").css("display", "none");
        $("#placeBetTimer").css("display", "block");
        $("#betCountdown").css("display", "inline-block");
        $("#userBet").text(betValue);

        profileData();
        redChip.y = 530;
        greenChip.y = 530;
        blackChip.y = 530;

        redChip2.y = 500;
        greenChip2.y = 500;
        blackChip2.y = 500;

    }, 500);
}

function sendBet() {
    console.log("sending bet to server...")
    var obj = {}
    obj.userID = cookieValues("userID");
    obj.randomToken = cookieValues("randomToken");
    obj.userBet = betValue;
    // if (isHost) {       // need to send bet through sockets
    //     startGame();
    // }
    $.ajax({
        type: 'POST',
        // url: 'http://localhost:8080/bet',
        url: url + "/bet",
        data: JSON.stringify(obj),
        contentType: "application/json",
        crossDomain: true
    })
        .done(function (data) {
            response = data;
            if (response.enoughFunds === "true") {
                handDealt = true;
                betValue = 0;
                
            } else {
                alert("not enough funds!");
                // window.location.href = "http://localhost:8080/profile.html";
                window.location.href = url + "/profile.html";
                seat[0].x = 950;
                seat[1].x = 650;
                dealer.x = 650;
                betValue = 0;
                playerCards = [];
                dealerCards = [];
                seat[0].cards = [];
                seat[1].cards = [];
                dealerFirstCard = false;

                setTimeout(function(){
                    restart();
                }, 500);
            }
        });
}

function startGame() {       // returns a random number from server and starts the game
    var obj = {}
    obj.randomToken = cookieValues("randomToken");
    obj.bet = betValue;
    socket.emit("startGame", JSON.stringify(obj));
}

socket.on("startGameServer", function (data) {
    $("#placeBetTimer").css("display", "none")
    $("#betCountdown").css("display", "none");
    console.log("receiving input from server..." + data);
    responseArray.push(JSON.parse(data));
  
    if (responseArray.length === 3 || (responseArray.length === 2 && seatNumberOpponent1 == undefined)) {
        for (var i = 0; i < responseArray.length; i++) {
            response = responseArray[i];
            if (response.message === "token doesn't match") {
                alert("hacker!");
            } else if (response.blackjack === "true" && response.participant === cookieValues("userID")) {
                $("#blackjack").css("display", "block");
            }

            drawHitButton(c, hitButton);
            drawStandButton(c, standButton);
            drawRedChipButton(c, redChipButton);
            drawGreenChipButton(c, greenChipButton);
            drawBlackChipButton(c, blackChipButton);
            if (response.participant === "dealer" && !dealerFirstCard) {
                randomCard(dealer, response.randomNumber, 0);
            } else if (response.participant === cookieValues("userID")) {
                console.log("drawing your cards")
                randomCard(response.participant, response.randomNumber1, 20);
                randomCard(response.participant, response.randomNumber2, 0);
                if(response.isTurn){
                    $("#yourTurn").css("display", "block");
                } else{
                    $("#wait").css("display", "block");
                }
            } else {
                randomCard(response.participant, response.randomNumber1, 20);
                randomCard(response.participant, response.randomNumber2, 0);
            }
        }
        sendBet();
    }
});

function hit() {       // returns a random number from server and starts the game
    var obj = {}
    obj.userID = cookieValues("userID");
    obj.randomToken = cookieValues("randomToken");
    console.log("send hit request");
    socket.emit("hit", JSON.stringify(obj));
}
socket.on("serverHit", function (data) {
    response = JSON.parse(data);
    console.log("server gives the ok to hit" + response);
    if (response.participant === "dealer") {
        participant = dealer;
        console.log("changed dealer to dealer object");
    } else {
        console.log("hit is for the user" + response);

        participant = response.participant;
    }
    randomCard(participant, response.randomNum, 0);
    if (response.status === "bust" && response.participant === cookieValues("userID")) {
        $("#bust").css("display", "block");
        if(seatNumber === 0){
            $("#yourTurn").css("display", "none");
            $("#wait").css("display", "block");
        }
    } else if(response.status === "bust" && participant != dealer){
        $("#yourTurn").css("display", "block");
        $("#wait").css("display", "none");
    }
})

socket.on("results", function (data) {
    console.log("results are in!");
    response = JSON.parse(data);
    if (response.participant === cookieValues("userID")) {
        if (response.result === "win") {
            $("#win").css("display", "block");
        } else if (response.result === "lose") {
            $("#lose").css("display", "block");
        } else {
            $("#tie").css("display", "block");
        }
    }
})

function randomCard(participant, randomNum, x) {
    var card = new Image();                     // creating a new image with source
    console.log(participant.name);
    card.src = deck[randomNum].src;
    drawCards(card, participant, randomNum, x); // calling drawCard method 
}

function drawCards(image, participant, num, x) {
    if (participant === cookieValues("userID") || participant === seat[seatNumber]) {
        participant = seat[seatNumber];
    } else if (participant.name != "dealer") {
        participant = seat[seatNumberOpponent1];
    }
    if (!image.complete) {                      // if image hasn't rendered, try again
        setTimeout(function () {
            drawCards(image, participant, num, x);
        }, 50);
        return;
    }
    c.drawImage(image, participant.x - x, participant.y, 100, 140);

    if (image.complete) {                       // after image has rendered, increment coordinates for next card
        if (participant.y === dealer.y) {
            dealer.x += 20;
            dealerCards.push(deck[num].value);
            dealerFirstCard = true;
            
        } else if (participant === seat[seatNumber]) {
            if (seat[seatNumber].cards.length >= 1) {
                seat[seatNumber].x += 20;
            }
            seat[seatNumber].cards.push(deck[num].value);

        } else if (participant === seat[seatNumberOpponent1]) {
            if (seat[seatNumberOpponent1].cards.length >= 1) {
                seat[seatNumberOpponent1].x += 20;
            }
            seat[seatNumberOpponent1].cards.push(deck[num].value);
        }
    }
}

function playerStand() {
    var obj = {}
    obj.userID = cookieValues("userID");
    obj.randomToken = cookieValues("randomToken");
    socket.emit("stand", JSON.stringify(obj));
}


function drawTable(c, image) {      // draw the table/background
    if (!image.complete) {
        setTimeout(function () {
            drawTable(c, image);
        }, 50);
        return;
    }
    c.drawImage(image, 0, -40, window.innerWidth, window.innerHeight);
}

function cookieValues(param) {
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(";");
    if (param === "randomToken") {
        param = " randomToken";
    } else if(param === "userID" && ca.length > 2){
        if(ca[0].substring(0,6) != "userID"){
            param = " userID";
        }
    }
    for (var i = 0; i < ca.length; i++) {
        for (var y = 0; y < ca[i].length; y++) {
            if (ca[i].charAt(y) === "=" && ca[i].substring(0, y) === param) {
                // console.log(ca[i].substring(y+1, ca[i].length));
                return (ca[i].substring(y + 1, ca[i].length));
            }
        }
    }
}

function deleteAllCookies() {
    var c = document.cookie.split("; ");
    for (i in c)
        document.cookie = /^[^=]+/.exec(c[i])[0] + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
}

function clickButtonOnCanvas(event) {

    var totalOffsetX = 0;
    var totalOffsetY = 0;
    var canvasX = 0;
    var canvasY = 0;
    var currentElement = this;

    do {
        totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
        totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
    }
    while (currentElement = currentElement.offsetParent)

    canvasX = event.pageX - totalOffsetX;
    canvasY = event.pageY - totalOffsetY;

    //Image x y cordinates adding width and height

    if ((canvasX >= window.innerWidth - 380 && canvasX <= window.innerWidth - 230) && (canvasY >= 20 && canvasY <= 90) && handDealt) {
        hit();
    } else if ((canvasX >= window.innerWidth - 200 && canvasX <= window.innerWidth - 50) && (canvasY >= 20 && canvasY <= 90) && handDealt) {
        playerStand();
    } else if ((canvasX >= window.innerWidth - 320 && canvasX <= window.innerWidth - 270) && (canvasY >= 120 && canvasY <= 170) && !handDealt) {
        if (seatNumber === 1) {
            redChip.y -= 7;
            blackChip.y -= 7;
            greenChip.y -= 7;
            placeChips(redChip);
        } else if (seatNumber == 0) {
            redChip2.y -= 7;
            blackChip2.y -= 7;
            greenChip2.y -= 7;
            placeChips(redChip2);
        }
    } else if ((canvasX >= window.innerWidth - 240 && canvasX <= window.innerWidth - 190) && (canvasY >= 120 && canvasY <= 170) && !handDealt) {
        if (seatNumber === 1) {
            redChip.y -= 7;
            blackChip.y -= 7;
            greenChip.y -= 7;
            placeChips(greenChip);
        } else if (seatNumber == 0) {
            redChip2.y -= 7;
            blackChip2.y -= 7;
            greenChip2.y -= 7;
            placeChips(greenChip2);
        }
    } else if ((canvasX >= window.innerWidth - 160 && canvasX <= window.innerWidth - 110) && (canvasY >= 120 && canvasY <= 170) && !handDealt) {
        if (seatNumber === 1) {
            redChip.y -= 7;
            blackChip.y -= 7;
            greenChip.y -= 7;
            placeChips(blackChip);
        } else if (seatNumber == 0) {
            redChip2.y -= 7;
            blackChip2.y -= 7;
            greenChip2.y -= 7;
            placeChips(blackChip2);
        }
    }
}

function drawHitButton(c, image) {      // draw the hit button
    if (!image.complete) {
        setTimeout(function () {
            drawHitButton(c, image);
        }, 50);
        return;
    }
    c.font = "25px Open Sans";
    c.fillStyle = "white";
    c.fillText(username, seat[seatNumber].usernameX, seat[seatNumber].usernameY);
    c.drawImage(image, window.innerWidth - 380, 20, 150, 70);
}

function drawStandButton(c, image) {      // draw the stand button
    if (!image.complete) {
        setTimeout(function () {
            drawStandButton(c, image);
        }, 50);
        return;
    }
    c.drawImage(image, window.innerWidth - 200, 20, 150, 70);
}

function drawRedChipButton(c, image) {      // draw the red chip button
    if (!image.complete) {
        setTimeout(function () {
            drawRedChipButton(c, image);
        }, 50);
        return;
    }
    c.drawImage(image, window.innerWidth - 320, 120, 50, 50);
}

function drawGreenChipButton(c, image) {      // draw the red chip button
    if (!image.complete) {
        setTimeout(function () {
            drawGreenChipButton(c, image);
        }, 50);
        return;
    }
    c.drawImage(image, window.innerWidth - 240, 120, 50, 50);
}

function drawBlackChipButton(c, image) {      // draw the red chip button
    if (!image.complete) {
        setTimeout(function () {
            drawBlackChipButton(c, image);
        }, 50);
        return;
    }
    c.drawImage(image, window.innerWidth - 160, 120, 50, 50);
}

function placeChips(obj) {
    var chipImage = new Image();
    if (obj.name === "redChip") {
        chipImage.src = 'Assets/redChip.png';
    } else if (obj.name === "greenChip") {
        chipImage.src = 'Assets/greenChip.png';
    } else if (obj.name === "blackChip") {
        chipImage.src = 'Assets/blackChip.png';
    }
    betValue += obj.value;
    drawChip(c, chipImage, obj.x, obj.y);
    $("#userBet").text(betValue);
}

function drawChip(c, image, chipCoordinateX, chipCoordinateY) {      // draw the red chip button
    if (!image.complete) {
        setTimeout(function () {
            drawChip(c, image, chipCoordinateX, chipCoordinateY);
        }, 50);
        return;
    }
    c.drawImage(image, chipCoordinateX, chipCoordinateY, 150, 150);
}

function drawOpponentName(c) {      // draw the hit button
    c.font = "25px Open Sans";
    c.fillStyle = "white";
    c.fillText(usernameOpponent1, seat[seatNumberOpponent1].usernameX, seat[seatNumberOpponent1].usernameY);
}

// Socket connect

socket.on("connect", function () {
    //alert("connected");
})

function connectToSocket() {
    var user = cookieValues("userID");
    var obj = {};
    obj.userID = user;
    obj.randomToken = cookieValues("randomToken");
    socket.emit("notifySocketServer", JSON.stringify(obj));
}
socket.on("playerLeft", function () {
    seatNumber = null;
    seatNumberOpponent1 = null;
})

socket.on("seatAssignment", function (data) {
    response = JSON.parse(data);
    if (response.tableFull === "true") {
        alert("table is full!");
    } else if (response.userID !== cookieValues("userID")) {
        // alert("another player has joined! " + response.userID);
        seatNumberOpponent1 = response.seat;
        usernameOpponent1 = response.userID;
        // connectToSocket();
        setTimeout(function () {
            drawOpponentName(c);
        }, 300);
    } else if (response.userID === cookieValues("userID") && response.randomToken === cookieValues("randomToken")) {
        console.log("in the else if");
        seatNumber = response.seat;
        isHost = response.isHost;
        isTurn = response.isTurn;
    }
})

socket.on("playerStands", function(data){
    response = JSON.parse(data);
    if(response.participant === cookieValues("userID") && seatNumber === 0){
        $("#yourTurn").css("display", "none");
        $("#wait").css("display", "block");
    } else{
        $("#yourTurn").css("display", "block");
        $("#wait").css("display", "none");
    }
})

socket.on("serverTimer", function(data){
    betTime = data;
    $("#betCountdown").text(betTime);
})
socket.on("timerDone", function(){
    if(isHost){
        startGame();
    }
})


socket.on("serverTimerRestart", function(data){
    console.log(data + " servertime function");
    $("#gameResetTimer").css("display", "block");
    $("#resetCountdown").css("display", "inline-block");
    resetTime = data;
    $("#resetCountdown").text(resetTime);
})
socket.on("timerDoneRestart", function(){
    if(isHost){
        sendRestartRequest();
    }
})

socket.on("disconnect", function () {
    alert("disconnected");
})



