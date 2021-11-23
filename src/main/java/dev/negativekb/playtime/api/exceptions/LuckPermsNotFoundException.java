package dev.negativekb.playtime.api.exceptions;

/**
 * Called when the LuckPerms API is not found.
 */
public class LuckPermsNotFoundException extends RuntimeException {

    public LuckPermsNotFoundException(String message) {
        super(message);
    }
}
