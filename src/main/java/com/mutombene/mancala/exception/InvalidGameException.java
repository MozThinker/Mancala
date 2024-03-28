package com.mutombene.mancala.exception;

import lombok.Getter;

/**
 * @author mutombene
 */
@Getter
public class InvalidGameException extends RuntimeException{

    private final String message;

    public InvalidGameException (String message) {

        this.message = message;
    }

}
