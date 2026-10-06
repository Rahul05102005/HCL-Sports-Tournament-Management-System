package com.sportx.core.domain;

/**
 * Concrete Player domain class extending Person.
 * Demonstrates: Inheritance (IS-A relationship), Encapsulation, Business Validation.
 */
public class Player extends Person {
    private final Long teamId;
    private final int jerseyNumber;
    private final String rollNumber;
    private final int age;
    private final String position;
    private boolean eligible;

    public Player(Long id, String name, String email, String phone,
                  Long teamId, int jerseyNumber, String rollNumber,
                  int age, String position, boolean eligible) {
        super(id, name, email, phone);
        if (jerseyNumber <= 0 || jerseyNumber > 99) {
            throw new IllegalArgumentException("Jersey number must be between 1 and 99");
        }
        if (rollNumber == null || rollNumber.isBlank()) {
            throw new IllegalArgumentException("Roll number is required for student player");
        }
        if (age < 16 || age > 35) {
            throw new IllegalArgumentException("Player age must be between 16 and 35 for collegiate eligibility");
        }
        this.teamId = teamId;
        this.jerseyNumber = jerseyNumber;
        this.rollNumber = rollNumber.trim().toUpperCase();
        this.age = age;
        this.position = position != null ? position.trim() : "Player";
        this.eligible = eligible;
    }

    public Long getTeamId() {
        return teamId;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public int getAge() {
        return age;
    }

    public String getPosition() {
        return position;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    @Override
    public String getRoleTitle() {
        return "Student Athlete";
    }

    @Override
    public String toString() {
        return "%s (#%d %s, Roll: %s, Eligible: %b)".formatted(
                getName(), jerseyNumber, position, rollNumber, eligible
        );
    }
}
