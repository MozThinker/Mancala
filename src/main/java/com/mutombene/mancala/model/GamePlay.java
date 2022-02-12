package com.mutombene.mancala.model;

import lombok.Data;

/**
 * @author mutombene
 */

@Data
public class GamePlay {

    private Boolean southTurn;
    private Integer chosenIndex;
    private String gameId;
}
