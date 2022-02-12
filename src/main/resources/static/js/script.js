var boardPits = [[6,6,6,6,6,6],[6,6,6,6,6,6]];
var kalahaSouth = 0;
var kalahaNorth = 0;
var playerSouthTurn;
var boardRawSouth;
var boardRawNorth;
var activePlayer = 0;
var pits;

var winner;

var p1Score = document.getElementsByClassName("Player1Score")[0];
var p2Score = document.getElementsByClassName("Player2Score")[0];
var TurnUI = document.getElementsByClassName("Turn")[0];
var spawner = document.getElementsByClassName("Spawner")[0];

SetupGame();
function SetupGame(){
    PreparePitFunctionality();
}

function PreparePitFunctionality(){
    // Convert pit dom elements to easily useable array
    pits = Array.prototype.slice.call(document.getElementsByClassName("Pit"));

    for(var i = 0; i < pits.length; i++){

        if(i < 8)
            pits[i].value = 13 - i;

        else
            pits[i].value = i - 8;

        pits[i].onclick = OnPitClick;
    }
}

function OnPitClick(){
    if(this.classList.contains("Valid")){

        if(this.classList.contains("P2")){
            playerSouthTurn = true;
        }else {
            playerSouthTurn =false;
        }
        makeAmove(playerSouthTurn,this.value,gameId);
        StartDropDownAnimation(this, this.innerHTML, 0, -100, null);
    }
    else{
        console.log("Invalid Move");
        console.log(this.value);
    }
}

function StartDropDownAnimation(el, amount, startOffset, goalOffset, onAnimationEnd){
    var spawn = document.createElement('div');
    spawn.innerHTML = amount;

    spawn.style.position = "absolute";
    spawn.style.width = "40px";
    spawn.style.top = (startOffset + el.offsetTop).toString() + "px";
    spawn.style.left = el.offsetLeft.toString() + "px";
    spawn.className = "Pit DropDown";
    spawner.appendChild(spawn);

    // Remove the spawned object when animation is finished
    setTimeout(() => spawn.style.top = (goalOffset + el.offsetTop).toString() + "px", 10);
    spawn.addEventListener('transitionend', ()=>spawn.remove());
    if(onAnimationEnd != null){
        spawn.addEventListener('transitionend', onAnimationEnd);
        lastSpawn = spawn;
    }
}

var lastSpawn;

function DisableAllPits(){
    pits.forEach(element => {
        IfTrueAddElseRemoveClass(element, "Valid",false);
    });
}

function SetActivePlayerAndPitsToValid(gameState){
    console.log("SetActivePlayerAndPitsToValid");
    // Check for a win gameState/whos turn it is
    if(gameState.NextTurnState == "WinP1" || gameState.NextTurnState == "WinP2")
        activePlayer = gameState.nextPlayerTurn == "WinP1" ? -1 : -2;
    else
        activePlayer = gameState.nextPlayerTurn == "TurnP1" ? 0 : 1;

    TurnUI.innerHTML = "Player " + (activePlayer == 0? "1" : "2") + " Turn" ;

    pits.forEach(element => {
        IfTrueAddElseRemoveClass(element, "Valid",
            element.classList.contains("P1") && activePlayer == 0 ||
            element.classList.contains("P2") && activePlayer == 1 );
    });

    // Set the right player active
    IfTrueAddElseRemoveClass(p1Score, "Active", activePlayer == 0);
    IfTrueAddElseRemoveClass(p2Score, "Active", activePlayer == 1);
}

function IfTrueAddElseRemoveClass(element, className, condition){
    if(condition){
        element.classList.add(className);
    }else{
        element.classList.remove(className);
    }
}

function makeAmove(southTurn,index, gameId) {
    if(index>5){
        index = index - 7;
    }

    $.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "chosenIndex": index,
            "southTurn": southTurn,
            "gameId": gameId
        }),
        success: function (data) {
            gameId = data.gameId;
            southTurn = data.southTurn;
            displayResponse(data);
            connectToSocket(gameId);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function displayResponse(data){
    boardRawSouth = data.rowSouth;
    boardRawNorth = data.rowNorth;
    kalahaSouth = data.stonesKalahaSouth;
    kalahaNorth = data.stonesKalahaNorth;

    for(let i = 0; i<boardPits.length;i++){
        for(let j = 0; j < boardPits[i].length; j++ ){

            let id = i + "_" +j;
            if(i==0){
                $("#" +id).text(boardRawNorth[j]);
            }else{
                $("#" +id).text(boardRawSouth[5-j]);
            }
        }
    }

    $("#13").text(kalahaNorth);
    $("#6").text(kalahaSouth);

    SetActivePlayerAndPitsToValid(data);

    winner = data.winnerMessage;
    if(winner != null){
        DisableAllPits();
        alert("Winner is "+data.winnerMessage);
        winner = null;
    }
}