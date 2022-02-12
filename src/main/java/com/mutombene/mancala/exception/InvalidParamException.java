package com.mutombene.mancala.exception;

/**
 * @author mutombene
 */
public class InvalidParamException extends Exception{
    private String message;

    public InvalidParamException (String message) {

        this.message = message;
    }

    public String getMessage() {

        return message;
    }
}
