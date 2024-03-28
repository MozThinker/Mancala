package com.mutombene.mancala.exception;

import lombok.Getter;

/**
 * @author mutombene
 */
@Getter
public class InvalidParamException extends RuntimeException{
    private final String message;

    public InvalidParamException (String message) {

        this.message = message;
    }

}
