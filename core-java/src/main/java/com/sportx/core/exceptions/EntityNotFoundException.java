package com.sportx.core.exceptions;

/**
 * Unchecked exception thrown when a requested entity cannot be found by ID.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Object id) {
        super("%s with identifier '%s' not found".formatted(entityType, id));
    }
}
