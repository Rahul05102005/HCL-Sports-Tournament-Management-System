package com.sportx.backend.repository;

import com.sportx.backend.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByTournamentId(Long tournamentId);

    List<TeamEntity> findByManagerId(Long managerId);

    long countByTournamentId(Long tournamentId);

    @Query("SELECT t FROM TeamEntity t WHERE t.tournament.id = :tournamentId AND t.registrationStatus = 'APPROVED'")
    List<TeamEntity> findApprovedTeamsByTournamentId(@Param("tournamentId") Long tournamentId);
}
