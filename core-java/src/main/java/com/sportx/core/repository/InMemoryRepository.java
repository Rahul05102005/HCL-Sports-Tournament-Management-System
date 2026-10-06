package com.sportx.core.repository;

import com.sportx.core.domain.BaseEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Thread-safe In-Memory Generic Repository implementation.
 * Uses ConcurrentHashMap and AtomicLong for lock-free identity generation.
 */
public class InMemoryRepository<T extends BaseEntity> implements Repository<T, Long> {

    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Entity to persist cannot be null");
        if (entity.getId() == null) {
            entity.setId(idSequence.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void saveAll(Iterable<? extends T> entities) {
        Objects.requireNonNull(entities, "Entities collection cannot be null");
        for (T entity : entities) {
            save(entity);
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<T> findAll(Predicate<? super T> filter) {
        Objects.requireNonNull(filter, "Filter predicate cannot be null");
        return storage.values().stream()
                .filter(filter)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            storage.remove(id);
        }
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public <R> List<R> mapAll(Function<? super T, R> mapper) {
        Objects.requireNonNull(mapper, "Mapper function cannot be null");
        return storage.values().stream()
                .map(mapper)
                .toList();
    }

    public void clear() {
        storage.clear();
        idSequence.set(1);
    }
}
