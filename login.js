
var url = "http://localhost:8080";
//  var url = "http://3.14.148.190:8080";

function login() {
    var obj = {};
    obj.username = document.getElementById("usernameLogin").value;
    obj.password = document.getElementById("passwordLogin").value;
    $.ajax({
        type: "POST",
        url: url + "/login",
        data: JSON.stringify(obj),
        contentType: "application/json",
        crossDomain: true
    })
        .done(function (data) {
            console.log("in here the done");
            //  alert( data + "success");
            response = data;
            var userID = response.userID;
            var randomToken = response.randomToken;
            document.cookie = "userID = " + userID;
            document.cookie = "randomToken = " + randomToken;
        //    window.location.href = 'http://localhost:8080/profile.html';
            window.location.href = url + "/profile.html";

            // window.location.href = 'http://3.14.151.219:8080/profile.html';
        })
        .fail(function (data) {
            console.log("in here the fail");
            alert(data + "fail");
            console.log(data);
        });
} 
$("#usernameLogin").keyup(function(event) {
    if (event.keyCode === 13) {
        // $("#id_of_button").click();
        login();
    }
});
$("#passwordLogin").keyup(function(event) {
    if (event.keyCode === 13) {
        // $("#id_of_button").click();
        login();
    }
});