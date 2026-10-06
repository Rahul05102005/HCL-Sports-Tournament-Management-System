package com.sportx.backend.service;

import com.sportx.backend.dto.match.*;
import com.sportx.backend.entity.*;
import com.sportx.backend.exception.BadRequestException;
import com.sportx.backend.exception.ConflictException;
import com.sportx.backend.exception.ResourceNotFoundException;
import com.sportx.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final FixtureRepository fixtureRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final ScoreEventRepository scoreEventRepository;
    private final StandingService standingService;

    @Transactional
    public FixtureResponseDto createFixture(FixtureCreateRequest request) {
        TournamentEntity tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + request.getTournamentId()));

        TeamEntity homeTeam = teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Home team not found: " + request.getHomeTeamId()));

        TeamEntity awayTeam = teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Away team not found: " + request.getAwayTeamId()));

        if (Objects.equals(homeTeam.getId(), awayTeam.getId())) {
            throw new BadRequestException("Home team and away team cannot be the same");
        }

        VenueEntity venue = null;
        if (request.getVenueId() != null) {
            venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + request.getVenueId()));

            // Check venue clash (within 2 hours)
            LocalDateTime startWindow = request.getScheduledTime().minusHours(2);
            LocalDateTime endWindow = request.getScheduledTime().plusHours(2);
            List<FixtureEntity> venueConflicts = fixtureRepository.findVenueConflicts(venue.getId(), startWindow, endWindow);
            if (!venueConflicts.isEmpty()) {
                throw new ConflictException("Venue conflict: " + venue.getName() + " is already booked within 2 hours of this slot.");
            }
        }

        // Check team rest clash (within 3 hours)
        LocalDateTime teamStartWindow = request.getScheduledTime().minusHours(3);
        LocalDateTime teamEndWindow = request.getScheduledTime().plusHours(3);
        if (!fixtureRepository.findTeamConflicts(homeTeam.getId(), teamStartWindow, teamEndWindow).isEmpty()) {
            throw new ConflictException("Team schedule conflict: " + homeTeam.getName() + " has a match within 3 hours of this slot.");
        }
        if (!fixtureRepository.findTeamConflicts(awayTeam.getId(), teamStartWindow, teamEndWindow).isEmpty()) {
            throw new ConflictException("Team schedule conflict: " + awayTeam.getName() + " has a match within 3 hours of this slot.");
        }

        UserEntity referee = null;
        if (request.getRefereeId() != null) {
            referee = userRepository.findById(request.getRefereeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Referee user not found: " + request.getRefereeId()));
        }

        FixtureEntity fixture = FixtureEntity.builder()
                .tournament(tournament)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .venue(venue)
                .referee(referee)
                .scheduledTime(request.getScheduledTime())
                .roundName(request.getRoundName() != null ? request.getRoundName() : "Regular Round")
                .status("SCHEDULED")
                .build();

        fixture = fixtureRepository.save(fixture);

        // Pre-create match record
        MatchEntity match = MatchEntity.builder()
                .fixture(fixture)
                .homeScore(0)
                .awayScore(0)
                .matchStatus("SCHEDULED")
                .build();
        matchRepository.save(match);

        return mapToFixtureResponseDto(fixture);
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDto> getMatchesByTournament(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToMatchResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatchResponseDto getMatchById(Long matchId) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));
        return mapToMatchResponseDto(match);
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDto> getLiveMatches() {
        return matchRepository.findLiveMatches().stream()
                .map(this::mapToMatchResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScoreEventResponseDto addScoreEvent(Long matchId, ScoreEventRequest request) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));

        TeamEntity team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + request.getTeamId()));

        PlayerEntity player = null;
        if (request.getPlayerId() != null) {
            player = playerRepository.findById(request.getPlayerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + request.getPlayerId()));
        }

        ScoreEventEntity scoreEvent = ScoreEventEntity.builder()
                .match(match)
                .team(team)
                .player(player)
                .eventType(request.getEventType().toUpperCase())
                .minuteOccurred(request.getMinuteOccurred() != null ? request.getMinuteOccurred() : 0)
                .extraInfo(request.getExtraInfo())
                .build();

        scoreEvent = scoreEventRepository.save(scoreEvent);

        // Update match running score and status
        if ("GOAL".equalsIgnoreCase(request.getEventType()) || "POINT".equalsIgnoreCase(request.getEventType())) {
            if (Objects.equals(match.getFixture().getHomeTeam().getId(), team.getId())) {
                match.setHomeScore(match.getHomeScore() + 1);
            } else if (Objects.equals(match.getFixture().getAwayTeam().getId(), team.getId())) {
                match.setAwayScore(match.getAwayScore() + 1);
            }
        }

        match.setMatchStatus("LIVE");
        if (request.getMinuteOccurred() != null) {
            match.setCurrentMinute(request.getMinuteOccurred());
        }
        matchRepository.save(match);

        return ScoreEventResponseDto.builder()
                .id(scoreEvent.getId())
                .matchId(match.getId())
                .teamId(team.getId())
                .teamName(team.getName())
                .playerId(player != null ? player.getId() : null)
                .playerName(player != null ? player.getFullName() : null)
                .jerseyNumber(player != null ? player.getJerseyNumber() : null)
                .eventType(scoreEvent.getEventType())
                .minuteOccurred(scoreEvent.getMinuteOccurred())
                .extraInfo(scoreEvent.getExtraInfo())
                .createdAt(scoreEvent.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ScoreEventResponseDto> getScoreEvents(Long matchId) {
        return scoreEventRepository.findByMatchIdOrderByMinuteOccurredAsc(matchId).stream()
                .map(event -> ScoreEventResponseDto.builder()
                        .id(event.getId())
                        .matchId(event.getMatch().getId())
                        .teamId(event.getTeam().getId())
                        .teamName(event.getTeam().getName())
                        .playerId(event.getPlayer() != null ? event.getPlayer().getId() : null)
                        .playerName(event.getPlayer() != null ? event.getPlayer().getFullName() : null)
                        .jerseyNumber(event.getPlayer() != null ? event.getPlayer().getJerseyNumber() : null)
                        .eventType(event.getEventType())
                        .minuteOccurred(event.getMinuteOccurred())
                        .extraInfo(event.getExtraInfo())
                        .createdAt(event.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchResponseDto finalizeMatch(Long matchId, FinalizeMatchRequest request) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));

        if ("COMPLETED".equals(match.getMatchStatus())) {
            throw new BadRequestException("Match is already finalized and completed");
        }

        match.setHomeScore(request.getHomeScore());
        match.setAwayScore(request.getAwayScore());
        match.setMatchStatus("COMPLETED");
        match.setEndedAt(LocalDateTime.now());
        match.setNotes(request.getNotes());

        TeamEntity homeTeam = match.getFixture().getHomeTeam();
        TeamEntity awayTeam = match.getFixture().getAwayTeam();

        if (request.getHomeScore() > request.getAwayScore()) {
            match.setWinnerTeam(homeTeam);
            match.setIsDraw(false);
        } else if (request.getAwayScore() > request.getHomeScore()) {
            match.setWinnerTeam(awayTeam);
            match.setIsDraw(false);
        } else {
            match.setWinnerTeam(null);
            match.setIsDraw(true);
        }

        // Update fixture status
        match.getFixture().setStatus("COMPLETED");
        fixtureRepository.save(match.getFixture());

        match = matchRepository.save(match);

        // Update tournament standings
        standingService.recordMatchResult(
                match.getFixture().getTournament(),
                homeTeam,
                awayTeam,
                request.getHomeScore(),
                request.getAwayScore()
        );

        return mapToMatchResponseDto(match);
    }

    public MatchResponseDto mapToMatchResponseDto(MatchEntity match) {
        FixtureEntity f = match.getFixture();
        return MatchResponseDto.builder()
                .id(match.getId())
                .fixtureId(f.getId())
                .tournamentId(f.getTournament().getId())
                .tournamentName(f.getTournament().getName())
                .homeTeamId(f.getHomeTeam().getId())
                .homeTeamName(f.getHomeTeam().getName())
                .homeCollegeName(f.getHomeTeam().getCollegeName())
                .awayTeamId(f.getAwayTeam().getId())
                .awayTeamName(f.getAwayTeam().getName())
                .awayCollegeName(f.getAwayTeam().getCollegeName())
                .homeScore(match.getHomeScore())
                .awayScore(match.getAwayScore())
                .matchStatus(match.getMatchStatus())
                .currentMinute(match.getCurrentMinute())
                .scheduledTime(f.getScheduledTime())
                .venueName(f.getVenue() != null ? f.getVenue().getName() : "TBD")
                .venueLocation(f.getVenue() != null ? f.getVenue().getLocation() : "TBD")
                .refereeName(f.getReferee() != null ? f.getReferee().getFullName() : "Unassigned")
                .roundName(f.getRoundName())
                .winnerTeamId(match.getWinnerTeam() != null ? match.getWinnerTeam().getId() : null)
                .winnerTeamName(match.getWinnerTeam() != null ? match.getWinnerTeam().getName() : null)
                .isDraw(match.getIsDraw())
                .notes(match.getNotes())
                .build();
    }

    public FixtureResponseDto mapToFixtureResponseDto(FixtureEntity f) {
        return FixtureResponseDto.builder()
                .id(f.getId())
                .tournamentId(f.getTournament().getId())
                .tournamentName(f.getTournament().getName())
                .homeTeamId(f.getHomeTeam().getId())
                .homeTeamName(f.getHomeTeam().getName())
                .awayTeamId(f.getAwayTeam().getId())
                .awayTeamName(f.getAwayTeam().getName())
                .venueId(f.getVenue() != null ? f.getVenue().getId() : null)
                .venueName(f.getVenue() != null ? f.getVenue().getName() : "TBD")
                .refereeId(f.getReferee() != null ? f.getReferee().getId() : null)
                .refereeName(f.getReferee() != null ? f.getReferee().getFullName() : "Unassigned")
                .scheduledTime(f.getScheduledTime())
                .roundName(f.getRoundName())
                .status(f.getStatus())
                .build();
    }
}
