

var animSpeedInMS = 200;
var activePlayer = 0;
var pits;

var p1Score = document.getElementsByClassName("Player1Score")[0];
var p2Score = document.getElementsByClassName("Player2Score")[0];
var TurnUI = document.getElementsByClassName("Turn")[0];
var spawner = document.getElementsByClassName("Spawner")[0];
var board =  document.getElementsByClassName("Board")[0];
var log =  document.getElementsByClassName("Log")[0];

SetupGame();
function SetupGame(){
    PreparePitFunctionality();
    GetGame();

}

function PreparePitFunctionality(){
    // Convert pit dom elements to easily useable array
    pits = Array.prototype.slice.call(document.getElementsByClassName("Pit"));

    for(var i = 0; i < pits.length; i++){
        // Add corrected index
        if(i < 8)
            pits[i].value = i;
        else
            pits[i].value = 13 - i + 8;

        // Add the onclick functionality
        pits[i].onclick = OnPitClick;
    }

    // For ease of use sort the array
    pits.sort((a,b) =>{
        return a.value < b.value ? -1 : 1;
    })
}

function OnPitClick(){
    if(this.classList.contains("Valid")){
        PostGame(this.value, activePlayer);
        //StartDropDownAnimation(this, this.innerHTML, 0, -100, null);
    }
    else{
        console.log("Invalid Move");
        console.log(this.value);
    }
}

function StartDropDownAnimationSimple(el, amount, onAnimationEnd){
    if(amount > 0){
        StartDropDownAnimation(el, amount, -100, 0, onAnimationEnd);
    }else{
        StartDropDownAnimation(el, Math.abs(amount), 0, -100, onAnimationEnd);
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

function SetPitValue(Index, Amount){
    pits[Index].innerHTML = Amount;
}

// Bug fix comments
// The problem occurs when +1 with a delayed setPitValue gets in conflict with the later -1
var lastSpawn; // This reference is the solution to that.

function SetGameViewFromJSONViaTransforms(gameState){
    // Do animation per transform with a delay
    console.log(gameState.Log.length);
    DisableAllPits();

    var animDelay = 0;
    gameState.Log.forEach((e) => {
        console.log(e);

        switch(e.Type){
            case "PitLog":
                if(e.AmountAdded > 0){
                    setTimeout(
                        () => StartDropDownAnimationSimple(pits[e.Index],e.AmountAdded,
                            SetPitValue(e.Index,e.FinalAmount) // Delayed
                        ),
                        animDelay
                    );}
                else{
                    setTimeout(() => StartDropDownAnimationSimple(pits[e.Index], e.AmountAdded,null), animDelay);
                    setTimeout(() => SetPitValue(e.Index,e.FinalAmount), animDelay);
                    setTimeout(() => {
                        if(lastSpawn != null)
                            lastSpawn.removeEventListener('transitionend', SetPitValue);
                    }, animDelay); // Instant - Later - Can remove
                }
                break;
            case "TextLog":
                setTimeout(
                    () => log.insertAdjacentHTML('afterbegin',e.Log + "<br\>")//.innerHTML = e.Log + "<br\>"+ log.innerHTML
                );
                break;
            default:
                console.log("Text Log type not found");
                break;
        }

        animDelay += animSpeedInMS;
    });
    // Finished all set valid
    setTimeout(() => SetActivePlayerAndPitsToValid(gameState),animDelay);

}

function SetGameViewFromJSON(gameState){
    console.log("SetGameViewFromJSON");
    SetGameViewFromJSONViaTransforms(gameState);
    return;


    // Change this when received transforms
    pits.forEach(element => {
        var curStones = gameState.Pits[element.value].stones
        element.innerHTML = curStones;

        IfTrueAddElseRemoveClass(element, "Valid",
            element.classList.contains("P1") && activePlayer == 0 && curStones > 0||
            element.classList.contains("P2") && activePlayer == 1 && curStones > 0);
    });

    p1Score.innerHTML = "P1 Score: " + gameState.Player1Score;
    p2Score.innerHTML = "P2 Score: " + gameState.Player2Score;

    // Set the right player active
    IfTrueAddElseRemoveClass(p1Score, "Active", activePlayer == 0);
    IfTrueAddElseRemoveClass(p2Score, "Active", activePlayer == 1);

    // Remove this tbh
    if(activePlayer == -1)
        alert("Congratulations Player1!");
    if(activePlayer == -2)
        alert("Congratulations Player2!");
}

function DisableAllPits(){
    pits.forEach(element => {
        IfTrueAddElseRemoveClass(element, "Valid",false);
    });
}

function SetActivePlayerAndPitsToValid(gameState){
    console.log("SetActivePlayerAndPitsToValid");
    // Check for a win gameState/whos turn it is
    if(gameState.NextTurnState == "WinP1" || gameState.NextTurnState == "WinP2")
        activePlayer = gameState.NextTurnState == "WinP1" ? -1 : -2;
    else
        activePlayer = gameState.NextTurnState == "TurnP1" ? 0 : 1;

    TurnUI.innerHTML = "Player " + (activePlayer == 0? "1" : "2") + " Turn: " + gameState.Turn;

    pits.forEach(element => {
        var curStones = gameState.Pits[element.value].stones;
        IfTrueAddElseRemoveClass(element, "Valid",
            element.classList.contains("P1") && activePlayer == 0 && curStones > 0||
            element.classList.contains("P2") && activePlayer == 1 && curStones > 0);
    });

    p1Score.innerHTML = "P1 Score: " + gameState.Player1Score;
    p2Score.innerHTML = "P2 Score: " + gameState.Player2Score;

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

function GetGame(){
    console.log("GetGame");
    var result;
    fetch('/game')
        .then(response => response.json())
        .then(data =>{
            console.log(data);
            SetGameViewFromJSON(data);
        }).catch(error => console.log(error))
    return result;
}

function PostGame(Index, Player){
    console.log("PostGame");
    var result;
    postRequest('/game',{
        "SelectedBucket": Index,
        "PlayerID": Player,
        "ClientHash": 0
    }).then(data => {
        console.log(data);
        SetGameViewFromJSON(data);
    }) .catch(error => console.log(error));
}

function postRequest(url, data){
    return fetch(url, {
        credential: 'same-origin',
        method: 'POST',
        body: JSON.stringify(data),
        headers: new Headers({
            'Content-Type':'application/json'
        }),
    }).then(response => response.json())
}
