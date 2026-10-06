package com.sportx.core.repository;

import com.sportx.core.domain.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic In-Memory Repository Interface demonstrating HCL Generics Rubric.
 * Covers:
 *  - Bounded Type Parameter (T extends BaseEntity)
 *  - Wildcards (? extends T, ? super T)
 *  - Generic Methods
 *  - Type safety without raw types
 *
 * @param <T>  Entity type bounded by BaseEntity
 * @param <ID> Identity type
 */
public interface Repository<T extends BaseEntity, ID> {

    T save(T entity);

    void saveAll(Iterable<? extends T> entities);

    Optional<T> findById(ID id);

    List<T> findAll();

    List<T> findAll(Predicate<? super T> filter);

    boolean existsById(ID id);

    void deleteById(ID id);

    long count();

    /**
     * Generic method demonstrating transformation across types with wildcard support.
     */
    <R> List<R> mapAll(Function<? super T, R> mapper);
}
