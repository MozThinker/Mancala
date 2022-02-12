const url = 'http://localhost:8080';
let stompClient;
var gameId;
let southTurn;

function connectToSocket(gameId){
    console.log("Connection to game")
    let socket = new SockJS(url + "/gameplay");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("conneted to frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" +gameId, function (response){
            let data = JSON.parse(response.body);
            console.log(data);
            displayResponse(data);
        })

    })
}

function create_game() {
    let login = document.getElementById("login").value;
    if(login==null || login === ''){
        alert("Please enter login");
    } else {
        $.ajax({
            url: url + "/game/start",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": login
            }),
            success: function (data) {
                gameId = data.gameId;
                southTurn = data.southTurn;
                PreparePitFunctionality();
                connectToSocket(gameId)
                alert("You created a game. Game id is: " + data.gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function connectToRandom(){

    let login = document.getElementById("login").value;
    if(login==null || login === ''){
        alert("Please enter login");
    } else {
        $.ajax({
            url: url + "/game/connect/random",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": login
            }),
            success: function (data) {
                gameId = data.gameId;
                southTurn = data.southTurn;
                PreparePitFunctionality();
                connectToSocket(gameId)
                alert("Congrats you're playing with: " + data.playerSouth.login);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }

}


function connectToSpecificGame(){

    let login = document.getElementById("login").value;
    if(login==null || login === ''){
        alert("Please enter login");
    } else {
        gameId = document.getElementById("game_id").value;//
        if(gameId == null || gameId === '' ){
            alert("Please enter game id");
        }
        $.ajax({
            url: url + "/game/connect",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "player": {
                    "login": login
                },
                "gameId": gameId
            }),
            success: function (data) {
                gameId = data.gameId;
                southTurn = data.southTurn;
                PreparePitFunctionality();
                connectToSocket(gameId)
                alert("Congrats you're playing with: " + data.playerSouth.login);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }

}