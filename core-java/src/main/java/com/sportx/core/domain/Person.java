package com.sportx.core.domain;

/**
 * Abstract domain abstraction representing a person in the sports tournament.
 * Demonstrates: OOP Abstraction, Inheritance, Encapsulation.
 */
public abstract class Person extends BaseEntity {
    private final String name;
    private final String email;
    private final String phone;

    protected Person(Long id, String name, String email, String phone) {
        super(id);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Person name cannot be null or empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format for person");
        }
        this.name = name.trim();
        this.email = email.trim();
        this.phone = phone != null ? phone.trim() : "";
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Polymorphic method implemented by each specialized role.
     */
    public abstract String getRoleTitle();

    @Override
    public String toString() {
        return "%s [%s, email=%s]".formatted(getRoleTitle(), name, email);
    }
}
