package com.sportx.core.domain;

/**
 * Concrete Referee domain class extending Person.
 * Demonstrates: Inheritance, Polymorphism, Stateful tracking.
 */
public class Referee extends Person {
    private final String certificationLevel;
    private int matchesOfficiated;

    public Referee(Long id, String name, String email, String phone,
                   String certificationLevel, int matchesOfficiated) {
        super(id, name, email, phone);
        this.certificationLevel = (certificationLevel != null && !certificationLevel.isBlank())
                ? certificationLevel.trim()
                : "National Grade A";
        this.matchesOfficiated = Math.max(0, matchesOfficiated);
    }

    public String getCertificationLevel() {
        return certificationLevel;
    }

    public int getMatchesOfficiated() {
        return matchesOfficiated;
    }

    public void incrementMatchesOfficiated() {
        this.matchesOfficiated++;
    }

    @Override
    public String getRoleTitle() {
        return "Official Referee";
    }

    @Override
    public String toString() {
        return "%s (Level: %s, Matches: %d)".formatted(getName(), certificationLevel, matchesOfficiated);
    }
}
