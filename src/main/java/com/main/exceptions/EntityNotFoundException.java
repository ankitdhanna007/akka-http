package com.main.exceptions;

public class EntityNotFoundException extends Exception{
    public EntityNotFoundException(final String errorMessage) {
        super(errorMessage);
    }
}
