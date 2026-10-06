package com.sportx.core.domain;

/**
 * Concrete TeamManager domain class extending Person.
 * Demonstrates: Inheritance, Ownership association.
 */
public class TeamManager extends Person {
    private final String collegeName;
    private Long managedTeamId;

    public TeamManager(Long id, String name, String email, String phone,
                       String collegeName, Long managedTeamId) {
        super(id, name, email, phone);
        if (collegeName == null || collegeName.isBlank()) {
            throw new IllegalArgumentException("College name is required for Team Manager");
        }
        this.collegeName = collegeName.trim();
        this.managedTeamId = managedTeamId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public Long getManagedTeamId() {
        return managedTeamId;
    }

    public void setManagedTeamId(Long managedTeamId) {
        this.managedTeamId = managedTeamId;
    }

    @Override
    public String getRoleTitle() {
        return "Team Manager / Coach";
    }

    @Override
    public String toString() {
        return "%s (College: %s, Team ID: %s)".formatted(getName(), collegeName, managedTeamId);
    }
}
