package com.aurahealth.hms.exception;

/** The request is valid but clashes with the current state (e.g. the bed is already occupied). Maps to HTTP 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
