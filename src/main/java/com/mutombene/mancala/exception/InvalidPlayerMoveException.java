package com.mutombene.mancala.exception;

/**
 * @author mutombene
 */
public class InvalidPlayerMoveException extends Exception{

    private String message;

    public InvalidPlayerMoveException (String message) {

        this.message = message;
    }

    public String getMessage() {

        return message;
    }
}
