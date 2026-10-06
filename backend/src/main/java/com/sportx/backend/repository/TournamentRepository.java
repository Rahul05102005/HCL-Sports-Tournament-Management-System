package com.sportx.backend.repository;

import com.sportx.backend.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

    List<TournamentEntity> findByOrganizerId(Long organizerId);

    List<TournamentEntity> findByStatus(String status);

    List<TournamentEntity> findBySportId(Long sportId);

    @Query("SELECT t FROM TournamentEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.sport.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TournamentEntity> searchTournaments(@Param("query") String query);

    @Query("SELECT COUNT(t) FROM TournamentEntity t WHERE t.status = 'ONGOING'")
    long countOngoingTournaments();

    @Query("SELECT t FROM TournamentEntity t WHERE t.status IN ('UPCOMING', 'ONGOING') ORDER BY t.startDate ASC")
    List<TournamentEntity> findActiveAndUpcomingTournaments();
}
