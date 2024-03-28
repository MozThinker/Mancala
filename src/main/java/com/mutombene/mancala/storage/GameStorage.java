package com.mutombene.mancala.storage;

import com.mutombene.mancala.model.Game;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mutombene
 */
public class GameStorage {

    private final Map<String, Game> games;
    private static volatile GameStorage instance;

    private GameStorage() {
        games = new ConcurrentHashMap<>();
    }

    public static GameStorage getInstance() {
        if (instance == null) {
            synchronized (GameStorage.class) {
                if (instance == null) {
                    instance = new GameStorage();
                }
            }
        }
        return instance;
    }

    public Map<String, Game> getGames() {
        return Collections.unmodifiableMap(games);
    }

    public void setGame(Game game) {
        games.put(game.getGameId(), game);
    }
}