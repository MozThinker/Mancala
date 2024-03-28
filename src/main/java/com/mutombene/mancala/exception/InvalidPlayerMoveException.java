package com.mutombene.mancala.exception;

import lombok.Getter;

/**
 * @author mutombene
 */
@Getter
public class InvalidPlayerMoveException extends RuntimeException{

    private final String message;

    public InvalidPlayerMoveException (String message) {

        this.message = message;
    }

}
