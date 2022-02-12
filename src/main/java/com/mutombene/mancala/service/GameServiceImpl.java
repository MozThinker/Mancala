package com.mutombene.mancala.service;

import com.mutombene.mancala.exception.InvalidGameException;
import com.mutombene.mancala.exception.InvalidParamException;
import com.mutombene.mancala.exception.InvalidPlayerMoveException;
import com.mutombene.mancala.exception.NotFoundException;
import com.mutombene.mancala.model.Game;
import com.mutombene.mancala.model.GamePlay;
import com.mutombene.mancala.model.Player;
import com.mutombene.mancala.storage.GameStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mutombene.mancala.model.GameStatus.FINISHED;
import static com.mutombene.mancala.model.GameStatus.IN_PROGRESS;
import static com.mutombene.mancala.model.GameStatus.NEW;

/**
 * @author mutombene
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {

    private static final int KALAHA_SOUTH_INDEX = 6;
    private static final int KALAHA_NORTH_INDEX = 13;
    private static final int PIT_DEFAULT_VALUE = 6;
    private static final int KALAHA_DEFAULT_VALUE = 0;
    private static final String PLAYER_1_TURN = "TurnP1";
    private static final String PLAYER_2_TURN = "TurnP2";
    private static final String PLAYER_1_WINNER = "WinP1";
    private static final String PLAYER_2_WINNER = "WinP2";

    public Game createGame(Player player1) {
        List<Integer> pitList = new ArrayList<>();
        Game game = new Game();

        for(int i = 0; i<=13; i++){
            if(i==6 || i==13){
                pitList.add(KALAHA_DEFAULT_VALUE);
            }else{
                pitList.add(PIT_DEFAULT_VALUE);
            }
        }

        game.setPitList(pitList);

        game.setRowSouth(new int[]{0, 6, 6, 6, 6, 6, 6});
        game.setRowNorth(new int[]{6, 6, 6, 6, 6, 6, 0});


        game.setGameId(UUID.randomUUID().toString());
        game.setPlayerSouth(player1);
        game.setStatus(NEW);
        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGameException {

        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided ID doesn't exist");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);

        if (game.getPlayerNorth() != null) {
            throw new InvalidGameException("Game is not valid anymore!");
        }

        game.setPlayerNorth(player2);
        game.setSouthTurn(false);//
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game connectToRandomGame(Player player2) throws NotFoundException {

        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW))
                .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
        game.setPlayerNorth(player2);
        game.setSouthTurn(false);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException, InvalidPlayerMoveException {

        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId()))
            throw new NotFoundException("Game not found");

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
        if (game.getStatus().equals(FINISHED)) throw new InvalidGameException("Game is finished");

        int chosenIndex = gamePlay.getChosenIndex();
        boolean isSouthTurn = gamePlay.getSouthTurn();

        if(game.isSouthTurn()!=isSouthTurn) throw new InvalidPlayerMoveException("Invalid player move");

        int pitListIndex = isSouthTurn ? chosenIndex : chosenIndex + getOffsetPlayerNorth(game);

        if (isPitEmpty(pitListIndex, game))
            throw new InvalidPlayerMoveException("Empty pit");
        else
            play(pitListIndex, isSouthTurn, game);

        game.setStonesKalahaSouth(game.getPitList().get(KALAHA_SOUTH_INDEX));
        game.setStonesKalahaNorth(game.getPitList().get(KALAHA_NORTH_INDEX));

        int[] intSouthArray = new int[6];
        int[] intNorthArray = new int[6];
        for (int i = 0; i < 6; i++) {
            intSouthArray[i] = game.getPitList().get(i);
        }
        game.setRowSouth(intSouthArray);

        int indexNorth = 0;
        for (int i = game.getPitList().size() - 2; i >= 7; i--) {
            intNorthArray[indexNorth] = game.getPitList().get(i);
            indexNorth++;
        }
        game.setRowNorth(intNorthArray);

        return game;
    }

    public void makeMove(final int index, Game game) {
        int lastPit = allocateStonesAndGetLastPit(index, game);

        captureIfLastPitIsOwnEmptyPit(lastPit, game);
        collectLastStonesIfGameIsOver(game);
        switchTurnsIfLastPitIsNotOwnKalaha(lastPit, game);
    }

    private int allocateStonesAndGetLastPit(final int index, Game game) {
        int stones = getStonesInPit(index, game);
        int lastPit = index;
        while (stones > 0) {
            lastPit = nextPit(lastPit, game);
            incrementStonesInPit(lastPit, game);
            --stones;
        }
        emptyPit(index, game);
        return lastPit;
    }

    private void captureIfLastPitIsOwnEmptyPit(final int index, Game game) {
        if (pitContainsOneStone(index, game) && isARegularPit(index, game) && landsInPlayersOwnPit(index, game)) {
            int kalaha = game.isSouthTurn() ? getIndexKalahaSouth(game) : getIndexKalahaNorth(game);
            int capturedStones = getStonesInPit(index, game) + getStonesInPit(oppositePit(index, game), game);

            setStonesInPit(kalaha, getStonesInPit(kalaha, game) + capturedStones, game);
            emptyPit(index, game);
            emptyPit(oppositePit(index, game), game);
        }
    }

    private int getStonesInPit(final int index, Game game) {
        return game.getPitList().get(index);
    }

    private int nextPit(final int index, Game game) {
        int nextIndex = isLastIndex(index, game) ? 0 : index + 1;
        return skipKalahaOpponent(nextIndex, game);
    }

    private boolean isLastIndex(final int index, Game game) {
        return index == getIndexKalahaNorth(game);
    }

    private void incrementStonesInPit(final int index, Game game) {
        setStonesInPit(index, getStonesInPit(index, game) + 1, game);
    }

    public int getIndexKalahaSouth(Game game) {
        return game.getPitList().size() / 2 - 1;
    }

    public int getIndexKalahaNorth(Game game) {
        return game.getPitList().size() - 1;
    }

    private void emptyPit(final int index, Game game) {
        game.getPitList().set(index, 0);
    }

    private void setStonesInPit(final int index, final int value, Game game) {
        game.getPitList().set(index, value);
    }

    private int skipKalahaOpponent(final int index, Game game) {
        if (game.isSouthTurn() && index == getIndexKalahaNorth(game))
            return 0;
        if (!game.isSouthTurn() && index == getIndexKalahaSouth(game))
            return index + 1;
        return index;
    }

    private void collectLastStonesIfGameIsOver(Game game) {
        if (isGameOver(game)) {
            int lastStonesSouth = getTotalStonesInPitsSouth(game);
            int lastStonesNorth = getTotalStonesInPitsNorth(game);

            collectStones(lastStonesSouth, lastStonesNorth, game);
            emptyRegularPits(game);
        }
    }

    public boolean isGameOver(Game game) {
        return getTotalStonesInPitsSouth(game) == 0 || getTotalStonesInPitsNorth(game) == 0;
    }

    private int getTotalStonesInPitsSouth(Game game) {
        int sum = 0;
        int bound = getIndexKalahaSouth(game);
        for (int i = 0; i < bound; i++) {
            int stonesInPit = getStonesInPit(i, game);
            sum += stonesInPit;
        }
        return sum;
    }

    private int getTotalStonesInPitsNorth(Game game) {
        int sum = 0;
        int bound = getIndexKalahaNorth(game);
        for (int i = getIndexKalahaSouth(game) + 1; i < bound; i++) {
            int stonesInPit = getStonesInPit(i, game);
            sum += stonesInPit;
        }
        return sum;
    }

    private void collectStones(final int lastStonesSouth, final int lastStonesNorth, Game game) {
        int newAmountSouth = getStonesInPit(getIndexKalahaSouth(game), game) + lastStonesSouth;
        int newAmountNorth = getStonesInPit(getIndexKalahaNorth(game), game) + lastStonesNorth;

        setStonesInPit(getIndexKalahaSouth(game), newAmountSouth, game);
        setStonesInPit(getIndexKalahaNorth(game), newAmountNorth, game);
    }

    private void emptyRegularPits(Game game) {
        int bound1 = getIndexKalahaSouth(game);
        for (int i1 = 0; i1 < bound1; i1++) {
            emptyPit(i1, game);
        }

        int bound = getIndexKalahaNorth(game);
        for (int i = getIndexKalahaSouth(game) + 1; i < bound; i++) {
            emptyPit(i, game);
        }
    }

    private void switchTurnsIfLastPitIsNotOwnKalaha(final int lastPit, Game game) {
        if (isNotOwnKalaha(lastPit, game)) {
            game.setSouthTurn(isNotSouthTurn(game));

           String turn = game.isSouthTurn() ? PLAYER_2_TURN : PLAYER_1_TURN;
           game.setNextPlayerTurn(turn);

        }
    }

    private boolean isNotOwnKalaha(final int index, Game game) {
        return game.isSouthTurn() && index != getIndexKalahaSouth(game)
                || isNotSouthTurn(game) && index != getIndexKalahaNorth(game);
    }

    private boolean isNotSouthTurn(Game game) {
        return !game.isSouthTurn();
    }



    private boolean pitContainsOneStone(final int index, Game game) {
        return getStonesInPit(index, game) == 1;
    }

    private int oppositePit(final int index, Game game) {
        return 2 * getIndexKalahaSouth(game) - index;
    }

    private boolean isARegularPit(final int index, Game game) {
        return index != getIndexKalahaSouth(game) && index != getIndexKalahaNorth(game);
    }

    private boolean landsInPlayersOwnPit(final int index, Game game) {
        return game.isSouthTurn() && index < getIndexKalahaSouth(game)
                || isNotSouthTurn(game) && index > getIndexKalahaSouth(game);
    }

    private void play(final int pitListIndex, final boolean southTurn, Game game) {
        game.setSouthTurn(southTurn);
        makeMove(pitListIndex, game);
    }

    public int getOffsetPlayerNorth(Game game) {
        return game.getPitList().size() / 2;
    }

    public boolean isPitEmpty(final int index, Game game) {
        return isEmpty(index, game);
    }

    public boolean isEmpty(final int index, Game game) {
        return getStonesInPit(index, game) == 0;
    }

    public String getWinnerMessage(Game game) {
        int exceedingStonesSouth = game.getStonesKalahaSouth() - game.getStonesKalahaNorth();

        if (exceedingStonesSouth == 0)
            return "It's a tie!";

        return exceedingStonesSouth > 0 ? PLAYER_2_WINNER : PLAYER_1_WINNER;
    }
}