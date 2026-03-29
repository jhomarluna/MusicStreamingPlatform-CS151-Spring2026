package com.musicstream.exception;

/** Thrown when a user attempts to stream content they are not permitted to access. */
public class StreamingPermissionException extends Exception {
    public StreamingPermissionException(String message) {
        super(message);
    }
}
