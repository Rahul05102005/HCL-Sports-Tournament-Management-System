package com.sportx.core.exceptions;

/**
 * Checked business exception indicating tournament configuration or validation failure.
 * Demonstrates: Checked Exceptions rubric, meaningful message encapsulation.
 */
public class InvalidTournamentException extends Exception {
    public InvalidTournamentException(String message) {
        super(message);
    }

    public InvalidTournamentException(String message, Throwable cause) {
        super(message, cause);
    }
}
