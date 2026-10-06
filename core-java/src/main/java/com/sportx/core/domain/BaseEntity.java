package com.sportx.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Generic Base Entity providing identity and timestamp encapsulation.
 * Demonstrates: OOP Encapsulation, Generics foundation, java.time.
 */
public abstract class BaseEntity implements Serializable {
    private Long id;
    private final LocalDateTime createdAt;

    protected BaseEntity(Long id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }

    protected BaseEntity() {
        this(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
