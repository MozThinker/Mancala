package com.mutombene.mancala.service;

import com.mutombene.mancala.exception.InvalidGameException;
import com.mutombene.mancala.exception.InvalidParamException;
import com.mutombene.mancala.exception.InvalidPlayerMoveException;
import com.mutombene.mancala.exception.NotFoundException;
import com.mutombene.mancala.model.Game;
import com.mutombene.mancala.model.GamePlay;
import com.mutombene.mancala.model.Player;

/**
 * @author mutombene
 */
public interface GameService {
    Game createGame (Player player);
    Game connectToGame(Player north, String gameId) throws InvalidParamException, InvalidGameException, NotFoundException;
    Game connectToRandomGame(Player north) throws NotFoundException;
    Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException, InvalidPlayerMoveException;
}