package com.sportx.core.exceptions;

/**
 * Unchecked exception thrown during team registration errors (e.g. duplicate jersey, team name collision).
 */
public class TeamRegistrationException extends RuntimeException {
    public TeamRegistrationException(String message) {
        super(message);
    }
}
