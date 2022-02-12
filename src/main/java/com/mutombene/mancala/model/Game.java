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
    private int offsetPlayerPlayer2;
    private List<Integer> pitListPlayer1;
    private List<Integer> pitListPlayer2;
    private int stonesKalahaSouth;
    private int stonesKalahaNorth;

    String nextPlayerTurn = "TurnP1";
}