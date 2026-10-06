package com.sportx.backend.repository;

import com.sportx.backend.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    List<PlayerEntity> findByTeamId(Long teamId);

    long countByTeamIdAndIsEligibleTrue(Long teamId);

    long countByTeamId(Long teamId);
}
