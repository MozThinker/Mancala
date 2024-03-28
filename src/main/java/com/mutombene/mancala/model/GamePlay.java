package com.mutombene.mancala.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author mutombene
 */

@Data
public class GamePlay {

    private Boolean southTurn;

    @NotNull(message = "chosenIndex cannot be null")
    private Integer chosenIndex;

    @NotNull(message = "Game Id is required")
    private String gameId;
}
