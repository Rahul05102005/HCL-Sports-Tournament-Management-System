package com.sportx.core.domain;

/**
 * Domain entity representing a match venue / ground / arena.
 */
public class Venue extends BaseEntity {
    private final Long tournamentId;
    private final String name;
    private final String location;
    private final int fieldNumber;
    private final int capacity;
    private boolean active;

    public Venue(Long id, Long tournamentId, String name, String location, int fieldNumber, int capacity) {
        super(id);
        this.tournamentId = tournamentId;
        this.name = name;
        this.location = location;
        this.fieldNumber = fieldNumber;
        this.capacity = capacity;
        this.active = true;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "%s - Field #%d (%s, Cap: %d)".formatted(name, fieldNumber, location, capacity);
    }
}
