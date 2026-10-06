package com.sportx.core.exceptions;

/**
 * Unchecked exception thrown when an actor attempts an unauthorized match operation (IDOR prevention).
 */
public class MatchAccessDeniedException extends RuntimeException {
    public MatchAccessDeniedException(String message) {
        super(message);
    }
}
