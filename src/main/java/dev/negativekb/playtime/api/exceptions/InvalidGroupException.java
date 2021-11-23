package dev.negativekb.playtime.api.exceptions;

/**
 * Called when the provided group does not exist.
 */
public class InvalidGroupException extends RuntimeException {

    public InvalidGroupException(String message) {
        super(message);
    }
}
