package com.musicstream.exception;

/** Thrown when an attempt is made to create more instances than the allowed maximum. */
public class InstanceLimitException extends Exception {
    public InstanceLimitException(String message) {
        super(message);
    }
}
