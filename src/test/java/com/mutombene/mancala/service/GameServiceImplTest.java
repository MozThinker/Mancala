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

import static com.mutombene.mancala.model.GameStatus.IN_PROGRESS;
import static com.mutombene.mancala.model.GameStatus.NEW;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author mutombene
 */
public class GameServiceImplTest {

    GameServiceImpl gameService;
    Player player1, player2;
    Game game;
    GamePlay gamePlayInt;
    List<Integer> expectedList = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
    List<Integer> expectedListGamePlay = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 0, 7, 7, 7, 7, 7, 1);

    @BeforeEach
    void init() {
        gameService = new GameServiceImpl();

        player1 = new Player();
        player1.setLogin("Eddy");

        player2 = new Player();
        player2.setLogin("Tulipa");

        gamePlayInt = new GamePlay();
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
}
