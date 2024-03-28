package com.mutombene.mancala.exception;

import lombok.Getter;

/**
 * @author mutombene
 */
@Getter
public class NotFoundException extends RuntimeException{

    private final String message;

    public NotFoundException (String message) {

        this.message = message;
    }

}

