var url = "http://localhost:8080";

// var url = "http://3.14.148.190:8080";

$(document).ready(function () {
    profileData();
});
function profileData() {       // returns a random number from server and starts the game
    var obj = {}
    obj.userID = cookieValues("userID");
    obj.randomToken = cookieValues("randomToken");
    $.ajax({
        type: 'POST',
        url: 'http://localhost:8080/profile',
        // url: 'http://3.14.151.219:8080/profile',
        url: url + "/profile",
        data: JSON.stringify(obj),
        contentType: "application/json",
        crossDomain: true
    })
        .done(function (data) {
            response = data;
            var wins = parseInt(response.wins);
            var losses = parseInt(response.losses);
            var winRate;
            if (wins === 0 && losses === 0) {
                winRate = 0;
            } else {
                winRate = Math.round(100 * (wins / (wins + losses)));
            }
            $("#username").text(response.username);
            $("#wins").text(response.wins);
            $("#losses").text(response.losses);

            $("#winRate").text(winRate + "%");
            $("#balanceValue").text("$" + response.wallet + "0");
            $("#walletBalance").text("$" + response.wallet + "0");
        })
        .fail(function (data) {
            console.log(data + "failed somewhere went wrong");
        });
}




// bonus countdown (once a day)
var tomorrow = new Date(new Date().getTime() + (24 * 60 * 60 * 1000));

$("#bonusButton").on("click", function () {
    dailyBonus();
    $(this).prop('disabled', true);
    $(this).css("background", "#5f6268");
})

var distance = 10000;

function bonusCountdownStart() {
    var bonusCountdown = setInterval(function () {
        var today = new Date().getTime();
        // var distance = tomorrow - today - 1;
        var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);
        $("#bonusTimer").text(hours + "h " + minutes + "m " + seconds + "s ");

        if (distance < 0) {
            clearInterval(bonusCountdown);
            $("#bonusTimer").text("");
            $("#bonusButton").prop('disabled', false);
            $("#bonusButton").css("background", "#0E9A09");
        }
        distance-= 1000;
    }, 1000);
}

function dailyBonus() {       // returns a random number from server and starts the game
    var obj = {}
    obj.userID = cookieValues("userID");
    obj.randomToken = cookieValues("randomToken");
    $.ajax({
        type: 'POST',
        // url: 'http://localhost:8080/bonus',
        url: url + "/bonus",
        data: JSON.stringify(obj),
        contentType: "application/json",
        crossDomain: true
    })
        .done(function (data) {
            response = data;
            if(response.bonus === "allowed"){
                bonusCountdownStart();
            }
        })
        .fail(function (data) {
            console.log(data + "failed somewhere went wrong");
        });
}