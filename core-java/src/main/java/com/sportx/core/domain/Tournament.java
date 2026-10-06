package com.sportx.core.domain;

import com.sportx.core.domain.enums.TournamentFormat;
import com.sportx.core.exceptions.InvalidTournamentException;
import com.sportx.core.exceptions.TeamRegistrationException;

import java.time.LocalDate;
import java.util.*;

/**
 * Domain entity representing a Sports Tournament.
 * Demonstrates: Encapsulation, Collections (List, Map), Checked Exception handling.
 */
public class Tournament extends BaseEntity {
    private final String name;
    private final String code;
    private final Sport sport;
    private final Long organiserId;
    private final TournamentFormat format;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int maxTeams;
    private final int pointsForWin;
    private final int pointsForDraw;
    private final int pointsForLoss;

    // Collections rubric: List for ordered entity collections, Map for key-value standings lookup
    private final List<Team> registeredTeams = new ArrayList<>();
    private final List<Fixture> fixtures = new ArrayList<>();
    private final Map<Long, Standing> standingsMap = new HashMap<>();

    public Tournament(Long id, String name, String code, Sport sport, Long organiserId,
                      TournamentFormat format, LocalDate startDate, LocalDate endDate,
                      int maxTeams, int pointsForWin, int pointsForDraw, int pointsForLoss)
            throws InvalidTournamentException {
        super(id);
        if (name == null || name.isBlank()) {
            throw new InvalidTournamentException("Tournament name cannot be empty");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new InvalidTournamentException("End date cannot be earlier than start date");
        }
        if (maxTeams < 2) {
            throw new InvalidTournamentException("A tournament requires at least 2 teams");
        }
        this.name = name.trim();
        this.code = code != null ? code.trim().toUpperCase() : UUID.randomUUID().toString().substring(0, 8);
        this.sport = Objects.requireNonNull(sport, "Sport cannot be null");
        this.organiserId = organiserId;
        this.format = format != null ? format : TournamentFormat.ROUND_ROBIN;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxTeams = maxTeams;
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
        this.pointsForLoss = pointsForLoss;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Sport getSport() {
        return sport;
    }

    public Long getOrganiserId() {
        return organiserId;
    }

    public TournamentFormat getFormat() {
        return format;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public int getPointsForWin() {
        return pointsForWin;
    }

    public int getPointsForDraw() {
        return pointsForDraw;
    }

    public int getPointsForLoss() {
        return pointsForLoss;
    }

    public synchronized void registerTeam(Team team) {
        Objects.requireNonNull(team, "Team cannot be null");
        if (registeredTeams.size() >= maxTeams) {
            throw new TeamRegistrationException("Tournament has reached maximum capacity of %d teams".formatted(maxTeams));
        }
        boolean exists = registeredTeams.stream().anyMatch(t -> t.getName().equalsIgnoreCase(team.getName()));
        if (exists) {
            throw new TeamRegistrationException("Team with name '%s' is already registered".formatted(team.getName()));
        }
        registeredTeams.add(team);
        standingsMap.put(team.getId(), new Standing((long) standingsMap.size() + 1, this.getId(), team));
    }

    public List<Team> getRegisteredTeams() {
        return Collections.unmodifiableList(registeredTeams);
    }

    public List<Fixture> getFixtures() {
        return Collections.unmodifiableList(fixtures);
    }

    public void setFixtures(List<Fixture> generatedFixtures) {
        this.fixtures.clear();
        this.fixtures.addAll(generatedFixtures);
    }

    public Map<Long, Standing> getStandingsMap() {
        return Collections.unmodifiableMap(standingsMap);
    }

    public Optional<Standing> getStandingForTeam(Long teamId) {
        return Optional.ofNullable(standingsMap.get(teamId));
    }

    @Override
    public String toString() {
        return "%s [%s - %s] (Teams: %d/%d, Dates: %s to %s)".formatted(
                name, sport.getName(), format, registeredTeams.size(), maxTeams, startDate, endDate
        );
    }
}
