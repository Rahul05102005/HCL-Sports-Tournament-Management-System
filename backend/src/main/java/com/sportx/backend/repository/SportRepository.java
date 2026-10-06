package com.sportx.backend.repository;

import com.sportx.backend.entity.SportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportRepository extends JpaRepository<SportEntity, Long> {
    Optional<SportEntity> findByNameIgnoreCase(String name);
}
