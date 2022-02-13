package com.mutombene.mancala.model;

import lombok.Data;

import java.util.List;

/**
 * @author mutombene
 */
@Data
public class Game {

    private String gameId;
    private Player playerSouth;
    private Player playerNorth;
    private GameStatus status;
    public int [] rowSouth;
    public int [] rowNorth;
    private List<Integer> pitList;
    private boolean southTurn;

    private String winnerMessage;
    private int stonesKalahaSouth;
    private int stonesKalahaNorth;

    String nextPlayerTurn = "TurnP1";
}