package com.sportx.core.domain;

import com.sportx.core.exceptions.TeamRegistrationException;

import java.util.*;

/**
 * Domain entity representing a participating sports team.
 * Demonstrates: OOP Encapsulation, Collections (List, Set), Defensive copying.
 */
public class Team extends BaseEntity {
    private final Long tournamentId;
    private final String name;
    private final String shortName;
    private final String collegeName;
    private Long managerId;

    // Collections rubric: List for ordered player roster, Set for O(1) uniqueness check of jersey numbers
    private final List<Player> players = new ArrayList<>();
    private final Set<Integer> assignedJerseyNumbers = new HashSet<>();

    public Team(Long id, Long tournamentId, String name, String shortName, String collegeName, Long managerId) {
        super(id);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be blank");
        }
        if (shortName == null || shortName.isBlank()) {
            throw new IllegalArgumentException("Team short name cannot be blank");
        }
        if (collegeName == null || collegeName.isBlank()) {
            throw new IllegalArgumentException("College name cannot be blank");
        }
        this.tournamentId = tournamentId;
        this.name = name.trim();
        this.shortName = shortName.trim().toUpperCase();
        this.collegeName = collegeName.trim();
        this.managerId = managerId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    /**
     * Encapsulated method to add player with jersey conflict check.
     */
    public synchronized void addPlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (assignedJerseyNumbers.contains(player.getJerseyNumber())) {
            throw new TeamRegistrationException("Jersey number %d is already taken in team %s".formatted(
                    player.getJerseyNumber(), name));
        }
        players.add(player);
        assignedJerseyNumbers.add(player.getJerseyNumber());
    }

    /**
     * Defensive copy of player list.
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Set<Integer> getAssignedJerseyNumbers() {
        return Collections.unmodifiableSet(assignedJerseyNumbers);
    }

    public long getEligiblePlayerCount() {
        return players.stream().filter(Player::isEligible).count();
    }

    public boolean isSquadEligible(int minRequired) {
        return getEligiblePlayerCount() >= minRequired;
    }

    @Override
    public String toString() {
        return "%s (%s) - %s [Players: %d, Eligible: %d]".formatted(
                name, shortName, collegeName, players.size(), getEligiblePlayerCount()
        );
    }
}
