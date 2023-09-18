package com.architecture.api.exception;

public class ConcurrentDataModificationException extends RuntimeException {

    public ConcurrentDataModificationException(String message) {
        super(message);
    }
}
