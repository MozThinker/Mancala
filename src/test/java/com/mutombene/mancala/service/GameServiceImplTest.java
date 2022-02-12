package com.mutombene.mancala.service;

import com.mutombene.mancala.exception.InvalidGameException;
import com.mutombene.mancala.exception.InvalidParamException;
import com.mutombene.mancala.exception.InvalidPlayerMoveException;
import com.mutombene.mancala.exception.NotFoundException;
import com.mutombene.mancala.model.Game;
import com.mutombene.mancala.model.GamePlay;
import com.mutombene.mancala.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.mutombene.mancala.model.GameStatus.FINISHED;
import static com.mutombene.mancala.model.GameStatus.IN_PROGRESS;
import static com.mutombene.mancala.model.GameStatus.NEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author mutombene
 */
public class GameServiceImplTest {

    final static List<Integer> PIT_LIST_AFTER_SINGLE_MOVE_P1 = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 0, 7, 7, 7, 7, 7, 1);
    final static List<Integer> PIT_LIST_AFTER_MULTIPLE_MOVES_P1 = Arrays.asList(7, 7, 6, 6, 6, 6, 0, 0, 0, 8, 8, 8, 8, 2);
    final static List<Integer> PIT_LIST_AFTER_SINGLE_MOVE_P2 = Arrays.asList(0, 7, 7, 7, 7, 7, 0, 7, 6, 6, 6, 6, 6, 0);
    final static List<Integer> PIT_LIST_AFTER_MULTIPLE_MOVES_P2 = Arrays.asList(0, 0, 8, 8, 8, 8, 1, 8, 7, 6, 6, 6, 6, 0);
    final static List<Integer> PIT_LIST_BEFORE_CAPTURE = Arrays.asList(9, 0, 2, 0, 0, 13, 9, 4, 5, 1, 0, 13, 12, 4);
    final static List<Integer> PIT_LIST_AFTER_CAPTURE = Arrays.asList(9, 0, 0, 0, 0, 13, 9, 4, 5, 0, 0, 13, 12, 7);
    final static List<Integer> PIT_LIST_BEFORE_GAMEOVER = Arrays.asList(1, 0, 0, 0, 0, 0, 31, 0, 5, 0, 5, 0, 0, 10);
    final static List<Integer> PIT_LIST_AFTER_GAMEOVER = Arrays.asList(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 20);
    final static List<Integer> PIT_LIST_BEFORE_SKIP_OPPONENT_KALAHA = Arrays.asList(7, 6, 6, 6, 6, 6, 0, 6, 0, 7, 7, 7, 7, 1);
    final static List<Integer> PIT_LIST_AFTER_SKIP_OPPONENT_KALAHA = Arrays.asList(7, 6, 6, 6, 6, 0, 1, 7, 1, 8, 8, 8, 7, 1);
    final static List<Integer> PIT_LIST_BEFORE_SKIP_OPPONENT_KALAHA_NORTH = Arrays.asList(9, 8, 2, 1, 9, 9, 2, 8, 2, 0, 9, 9, 1, 3);
    final static List<Integer> PIT_LIST_AFTER_SKIP_OPPONENT_KALAHA_NORTH = Arrays.asList(10, 9, 2, 1, 9, 0, 3, 9, 3, 1, 10, 10, 2, 3);
    GameServiceImpl gameService;
    Player player1, player2;
    Game game;
    GamePlay gamePlayInt, gamePlay;
    List<Integer> expectedList = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
    List<Integer> expectedListGamePlay = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 0, 7, 7, 7, 7, 7, 1);

    @BeforeEach
    void init() throws InvalidParamException, InvalidGameException {
        gameService = new GameServiceImpl();

        player1 = new Player();
        player1.setLogin("Eddy");

        player2 = new Player();
        player2.setLogin("Tulipa");

        gamePlayInt = new GamePlay();
        gamePlay = new GamePlay();

        game = gameService.createGame(player1);
        String gameId = game.getGameId();

        game = gameService.connectToGame(player2, gameId);
    }

    @Test
    void createGame() {
        game = new Game();
        game = gameService.createGame(player1);
        assertThat(game.getGameId()).isNotNull();
        assertThat(game.getPlayerSouth()).isEqualTo(player1);
        assertThat(game.getStatus()).isEqualTo(NEW);
        assertThat(game.getPitList()).isEqualTo(expectedList);
    }

   @Test
    void connectToGame() throws InvalidParamException, InvalidGameException {
       game = new Game();
       game = gameService.createGame(player1);

       Game gameInstance2 = gameService.connectToGame(player2, game.getGameId());

        assertThat(gameInstance2.getPlayerNorth()).isEqualTo(player2);
        assertThat(gameInstance2.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(gameInstance2.isSouthTurn()).isFalse();
    }

    @Test
    void failedConnectToGameIdNotExist() {
        assertThatThrownBy(()->gameService.connectToGame(player2,"invalidddd"))
                .isInstanceOf(InvalidParamException.class).hasMessage("Game with provided ID doesn't exist");
    }

    @Test
    void failedConnectToGameInvalidId() throws NotFoundException {

        String invalidGameId = game.getGameId();
        game.setStatus(FINISHED);
        assertThatThrownBy(()->gameService.connectToGame(player2,invalidGameId))
                .isInstanceOf(InvalidGameException.class).hasMessage("Game is not valid anymore!");
    }

    @Test
    void connectToRandomGame() throws NotFoundException {
        game = new Game();
        game = gameService.createGame(player1);

        Game gameInstance2 = gameService.connectToRandomGame(player2);

        assertThat(gameInstance2.getPlayerNorth()).isEqualTo(player2);
        assertThat(gameInstance2.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(gameInstance2.isSouthTurn()).isFalse();

    }

    @Test
    void gamePlay() throws NotFoundException, InvalidGameException, InvalidPlayerMoveException {
        game = new Game();
        game = gameService.createGame(player1);
        String gameId = game.getGameId();
        Game gameInstance2 = gameService.connectToRandomGame(player2);

        gamePlayInt.setGameId(gameId);
        gamePlayInt.setSouthTurn(false);
        gamePlayInt.setChosenIndex(0);

        //Before GamePlay
        assertThat(game.getPitList()).isEqualTo(expectedList);

        Game gamePlay = gameService.gamePlay(gamePlayInt);

        //After GamePlay
        assertThat(game.getPitList()).isEqualTo(expectedListGamePlay);
        assertThat(game.isSouthTurn()).isFalse();

        System.out.println("Pit List: "+gamePlay.getPitList());

    }

    @Test
    void gameInProgress(){
        assertThat(game.getStatus()).isEqualTo(IN_PROGRESS);
    }

    @Test
    void makeSingleMovePlayer1(){
        gameService.makeMove(7,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SINGLE_MOVE_P1);
    }

    @Test
    void  makeMultipleMovesPlayer1(){
        gameService.makeMove(7,game);
        gameService.makeMove(8,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_MULTIPLE_MOVES_P1);
    }

    @Test
    void makeSingleMovePlayer2(){
        gameService.makeMove(0,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SINGLE_MOVE_P2);
    }

    @Test
    void  makeMultipleMovesPlayer2(){
        gameService.makeMove(0,game);
        gameService.makeMove(1,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_MULTIPLE_MOVES_P2);
    }

    @Test
    void getIndexKalahaSouth() {
        assertThat(gameService.getIndexKalahaSouth(game)).isEqualTo(6);
    }

    @Test
    void getIndexKalahaNorth() {
        assertThat(gameService.getIndexKalahaNorth(game)).isEqualTo(13);
    }

    @Test
    void isGameOver() {
        game.setPitList(modifyPitList(game.getPitList(),0,6,0));
        assertThat(gameService.isGameOver(game));
    }

    @Test
    void getOffsetPlayerNorth() {
        assertThat(gameService.getOffsetPlayerNorth(game)).isEqualTo(7);
    }

    @Test
    void isPitEmpty() {
        gameService.makeMove(0,game);
        assertThat(gameService.isPitEmpty(0,game));
    }

    @Test
    void isEmpty() {
        gameService.makeMove(0,game);
        assertThat(gameService.isEmpty(0,game));
    }

    @Test
    void getWinnerMessage() {
        game.setStonesKalahaNorth(36);
        game.setStonesKalahaSouth(36);

        assertThat(gameService.getWinnerMessage(game)).isEqualTo("It's a tie!");
    }

    @Test
    void isPlayer1Winner(){

        game.setStonesKalahaNorth(42);
        game.setStonesKalahaSouth(30);

        assertThat(gameService.getWinnerMessage(game)).isEqualTo("WinP1");
    }

    @Test
    void isPlayer2Winner(){

        game.setStonesKalahaNorth(22);
        game.setStonesKalahaSouth(50);

        assertThat(gameService.getWinnerMessage(game)).isEqualTo("WinP2");
    }

    @Test
    void captureIfLastPitIsOwnEmptyPit(){
       // gameService
        game.setPitList(PIT_LIST_BEFORE_CAPTURE);

        gameService.makeMove(9,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_CAPTURE);
    }

    @Test
    void collectLastStonesIfGameIsOver(){
        // gameService
        game.setPitList(PIT_LIST_BEFORE_GAMEOVER);

        System.out.println("Turn: "+game.isSouthTurn());
        game.setSouthTurn(true);
        System.out.println("Turn: "+game.isSouthTurn());

        gameService.makeMove(0,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_GAMEOVER);
    }


    @Test
    void skipKalahaOpponent(){
        // gameService
        game.setPitList(PIT_LIST_BEFORE_SKIP_OPPONENT_KALAHA);

        System.out.println("Turn: "+game.isSouthTurn());
        game.setSouthTurn(true);
        System.out.println("Turn: "+game.isSouthTurn());

        gameService.makeMove(5,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SKIP_OPPONENT_KALAHA);
    }

    @Test
    void skipKalahaOpponentNorth(){
        game.setPitList(PIT_LIST_BEFORE_SKIP_OPPONENT_KALAHA_NORTH);

        System.out.println("Turn: "+game.isSouthTurn());
        game.setSouthTurn(true);
        System.out.println("Turn: "+game.isSouthTurn());

        gameService.makeMove(5,game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SKIP_OPPONENT_KALAHA_NORTH);
    }

    @Test
    void throwExceptionMovingEmptyPit(){

        gamePlayInt.setGameId(game.getGameId());
        gamePlayInt.setSouthTurn(true);
        game.setSouthTurn(true);
        gamePlayInt.setChosenIndex(1);


        game.setPitList(PIT_LIST_AFTER_GAMEOVER);
        assertThatThrownBy(()->gameService.gamePlay(gamePlayInt))
                .isInstanceOf(InvalidPlayerMoveException.class).hasMessage("Empty pit");
    }

    @Test
    void failedToPlayToGameNotFound() {
        gamePlayInt.setGameId("hbshbhsbhs");
        gamePlayInt.setSouthTurn(true);
        gamePlayInt.setChosenIndex(4);

        gamePlayInt.setChosenIndex(1);
        assertThatThrownBy(()->gameService.gamePlay(gamePlayInt))
                .isInstanceOf(NotFoundException.class).hasMessage("Game not found");
    }

    List <Integer> modifyPitList(List<Integer> pitList, int indexStart, int indexEnd, int value){
        for(int i = indexStart; i<indexEnd; i++){
            pitList.set(i,value);
        }
        return pitList;
    }
}