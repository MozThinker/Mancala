package com.mutombene.mancala.service;

import com.mutombene.mancala.exception.InvalidGameException;
import com.mutombene.mancala.exception.InvalidParamException;
import com.mutombene.mancala.exception.InvalidPlayerMoveException;
import com.mutombene.mancala.exception.NotFoundException;
import com.mutombene.mancala.model.Game;
import com.mutombene.mancala.model.GamePlay;
import com.mutombene.mancala.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    String gameId;
    GamePlay gamePlayInt, gamePlay;
    List<Integer> expectedList = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
    List<Integer> expectedListGamePlay = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 0, 7, 7, 7, 7, 7, 1);

    @BeforeEach
    void init() throws InvalidGameException, NotFoundException {
        gameService = new GameServiceImpl();

        player1 = new Player();
        player1.setLogin("Eddy");

        player2 = new Player();
        player2.setLogin("Tulipa");

        gamePlayInt = new GamePlay();
        gamePlay = new GamePlay();

        game = gameService.createGame(player1);
        gameId = game.getGameId();

        game = gameService.connectToGame(player2, gameId);
    }

    @Test
    @Order(1)
    void createGame() {
        game = new Game();
        game = gameService.createGame(player1);
        assertThat(game.getGameId()).isNotNull();
        assertThat(game.getPlayerSouth()).isEqualTo(player1);
        assertThat(game.getStatus()).isEqualTo(NEW);
        assertThat(game.getPitList()).isEqualTo(expectedList);
    }

    @Test
    @Order(2)
    void connectToGame() throws InvalidGameException, NotFoundException {
        game = new Game();
        game = gameService.createGame(player1);

        Game gameInstance2 = gameService.connectToGame(player2, game.getGameId());

        assertThat(gameInstance2.getPlayerNorth()).isEqualTo(player2);
        assertThat(gameInstance2.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(gameInstance2.isSouthTurn()).isFalse();
    }

    @Test
    @Order(3)
    void failedConnectToGameIdNotExist() {
        assertThatThrownBy(() -> gameService.connectToGame(player2, "invalidddd"))
                .isInstanceOf(NotFoundException.class).hasMessage("Game with provided ID doesn't exist");
    }

    @Test
    @Order(4)
    void failedConnectToGameInvalidId() throws NotFoundException, InvalidGameException {

        String invalidGameId = game.getGameId();
        game.setStatus(FINISHED);
        assertThatThrownBy(() -> gameService.connectToGame(player2, invalidGameId))
                .isInstanceOf(InvalidGameException.class).hasMessage("This game is not valid for join anymore, please try other!");
    }

    @Test
    @Order(5)
    void connectToRandomGame() throws NotFoundException {
        game = new Game();
        game = gameService.createGame(player1);

        Game gameInstance2 = gameService.connectToRandomGame(player2);

        assertThat(gameInstance2.getPlayerNorth()).isEqualTo(player2);
        assertThat(gameInstance2.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(gameInstance2.isSouthTurn()).isFalse();

    }

    @Test
    @Order(6)
    void gamePlay() throws NotFoundException, InvalidGameException, InvalidPlayerMoveException {
        game = new Game();
        game = gameService.createGame(player1);
        String gameId = game.getGameId();

        gamePlayInt.setGameId(gameId);
        gamePlayInt.setSouthTurn(false);
        gamePlayInt.setChosenIndex(0);

        assertThat(game.getPitList()).isEqualTo(expectedList);

        gameService.gamePlay(gamePlayInt);

        assertThat(game.getPitList()).isEqualTo(expectedListGamePlay);
        assertThat(game.isSouthTurn()).isFalse();

    }

    @Test
    @Order(7)
    void gameInProgress() {
        assertThat(game.getStatus()).isEqualTo(IN_PROGRESS);
    }

    @Test
    @Order(8)
    void amakingMoveGameOver() throws InvalidPlayerMoveException, NotFoundException, InvalidGameException {
        game.setPitList(PIT_LIST_BEFORE_GAMEOVER);
        game.setSouthTurn(true);
        gamePlay.setChosenIndex(0);
        gamePlay.setGameId(gameId);
        gamePlay.setSouthTurn(true);
        gameService.gamePlay(gamePlay);

        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_GAMEOVER);
        assertThat(game.getWinnerMessage()).isNotNull();
        assertThat(game.getStatus()).isEqualTo(FINISHED);
    }

    @Test
    @Order(9)
    void makeSingleMovePlayer1() {
        gameService.makeMove(7, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SINGLE_MOVE_P1);
    }

    @Test
    @Order(10)
    void makeMultipleMovesPlayer1() {
        gameService.makeMove(7, game);
        gameService.makeMove(8, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_MULTIPLE_MOVES_P1);
    }

    @Test
    @Order(11)
    void makeSingleMovePlayer2() {
        gameService.makeMove(0, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SINGLE_MOVE_P2);
    }

    @Test
    @Order(12)
    void makeMultipleMovesPlayer2() {
        gameService.makeMove(0, game);
        gameService.makeMove(1, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_MULTIPLE_MOVES_P2);
    }

    @Test
    @Order(13)
    void getIndexKalahaSouth() {
        assertThat(gameService.getIndexKalahaSouth(game)).isEqualTo(6);
    }

    @Test
    @Order(14)
    void getIndexKalahaNorth() {
        assertThat(gameService.getIndexKalahaNorth(game)).isEqualTo(13);
    }

    @Test
    @Order(15)
    void isGameOver() {
        game.setPitList(modifyPitList(game.getPitList(), 0, 6, 0));
        assertThat(gameService.isGameOver(game));
    }

    @Test
    @Order(16)
    void getOffsetPlayerNorth() {
        assertThat(gameService.getOffsetPlayerNorth(game)).isEqualTo(7);
    }

    @Test
    @Order(17)
    void isPitEmpty() {
        gameService.makeMove(0, game);
        assertThat(gameService.isPitEmpty(0, game));
    }

    @Test
    @Order(18)
    void isEmpty() {
        gameService.makeMove(0, game);
        assertThat(gameService.isEmpty(0, game));
    }

    @Test
    @Order(19)
    void getWinnerMessage() {
        game.setStonesKalahaNorth(36);
        game.setStonesKalahaSouth(36);

        assertThat(gameService.getWinnerMessage(game)).isEqualTo("It's a tie!");
    }

    @Test
    @Order(20)
    void isPlayer1Winner() {
        game.setStonesKalahaNorth(42);
        game.setStonesKalahaSouth(30);

        assertThat(gameService.getWinnerMessage(game)).isEqualTo("WinP1");
    }

    @Test
    @Order(21)
    void isPlayer2Winner() {
        game.setStonesKalahaNorth(22);
        game.setStonesKalahaSouth(50);

        assertThat(gameService.getWinnerMessage(game)).isEqualTo("WinP2");
    }

    @Test
    @Order(22)
    void captureIfLastPitIsOwnEmptyPit() {
        game.setPitList(PIT_LIST_BEFORE_CAPTURE);
        gameService.makeMove(9, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_CAPTURE);
    }

    @Test
    @Order(23)
    void collectLastStonesIfGameIsOver() {
        game.setPitList(PIT_LIST_BEFORE_GAMEOVER);
        game.setSouthTurn(true);
        gameService.makeMove(0, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_GAMEOVER);
    }


    @Test
    @Order(24)
    void skipKalahaOpponent() {
        game.setPitList(PIT_LIST_BEFORE_SKIP_OPPONENT_KALAHA);
        game.setSouthTurn(true);
        gameService.makeMove(5, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SKIP_OPPONENT_KALAHA);
    }

    @Test
    @Order(25)
    void skipKalahaOpponentNorth() {
        game.setPitList(PIT_LIST_BEFORE_SKIP_OPPONENT_KALAHA_NORTH);
        game.setSouthTurn(true);
        gameService.makeMove(5, game);
        assertThat(game.getPitList()).isEqualTo(PIT_LIST_AFTER_SKIP_OPPONENT_KALAHA_NORTH);
    }

    @Test
    @Order(26)
    void throwExceptionMovingEmptyPit() {

        gamePlayInt.setGameId(game.getGameId());
        gamePlayInt.setSouthTurn(true);
        game.setSouthTurn(true);
        gamePlayInt.setChosenIndex(1);
        game.setPitList(PIT_LIST_AFTER_GAMEOVER);
        assertThatThrownBy(() -> gameService.gamePlay(gamePlayInt))
                .isInstanceOf(InvalidPlayerMoveException.class).hasMessage("Empty pit");
    }

    @Test
    @Order(27)
    void failedToPlayToGameNotFound() {
        gamePlayInt.setGameId("hbshbhsbhs");
        gamePlayInt.setSouthTurn(true);
        gamePlayInt.setChosenIndex(4);
        gamePlayInt.setChosenIndex(1);
        assertThatThrownBy(() -> gameService.gamePlay(gamePlayInt))
                .isInstanceOf(NotFoundException.class).hasMessage("Game not found");
    }

    @Test
    @Order(28)
    void getTotalStonesInPitsNorth() {
        assertThat(gameService.getTotalStonesInPitsNorth(game)).isEqualTo(36);
    }

    @Test
    @Order(29)
    void getTotalStonesInPitsSouth() {
        assertThat(gameService.getTotalStonesInPitsSouth(game)).isEqualTo(36);
    }

    List<Integer> modifyPitList(List<Integer> pitList, int indexStart, int indexEnd, int value) {
        for (int i = indexStart; i < indexEnd; i++) {
            pitList.set(i, value);
        }
        return pitList;
    }
}