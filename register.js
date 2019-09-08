var url = "http://localhost:8080";

// var url = "http://3.14.148.190:8080";

function register(){
    var obj = {};
    obj.username = document.getElementById("usernameSignup").value;
    obj.password = document.getElementById("passwordSignup").value;
    $.ajax({
        type: "POST",
        url: url + "/register",
        // url: "http://localhost:8080/register",
        data: JSON.stringify(obj),
        contentType: "application/json",
        crossDomain: true
    })
    .done(function(data){
        console.log(data);
        response = data;
        var userID = response.userID;
        var randomToken = response.randomToken;
        document.cookie = "userID = " + userID;
        document.cookie = "randomToken = " + randomToken;
        // alert( data + "success");
        // window.location.href = 'http://localhost:8080/profile.html';
        window.location.href = url + "/profile.html";

    })
    .fail(function(data){
        console.log(data);
        alert("Username already exists");
    });
}

$("#usernameSignup").keyup(function(event) {
    if (event.keyCode === 13) {
        // $("#id_of_button").click();
        register();
    }
});
$("#passwordSignup").keyup(function(event) {
    if (event.keyCode === 13) {
        // $("#id_of_button").click();
        register();
    }
});