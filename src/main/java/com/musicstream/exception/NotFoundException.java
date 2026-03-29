package com.musicstream.exception;

/** Thrown when a requested entity (song, user, artist, etc.) cannot be found. */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
