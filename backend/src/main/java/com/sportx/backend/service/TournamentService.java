package com.sportx.backend.service;

import com.sportx.backend.dto.match.MatchResponseDto;
import com.sportx.backend.dto.standing.StandingResponseDto;
import com.sportx.backend.dto.tournament.*;
import com.sportx.backend.entity.*;
import com.sportx.backend.exception.BadRequestException;
import com.sportx.backend.exception.ResourceNotFoundException;
import com.sportx.backend.repository.*;
import com.sportx.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final SportRepository sportRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final FixtureRepository fixtureRepository;
    private final MatchRepository matchRepository;
    private final VenueRepository venueRepository;
    private final AnnouncementRepository announcementRepository;
    private final StandingService standingService;
    private final MatchService matchService;

    @Transactional
    public TournamentResponseDto createTournament(TournamentCreateRequest request, Authentication authentication) {
        SportEntity sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found: " + request.getSportId()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserEntity organizer = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Tournament end date cannot be before start date");
        }

        TournamentEntity tournament = TournamentEntity.builder()
                .name(request.getName())
                .sport(sport)
                .organizer(organizer)
                .format(request.getFormat().toUpperCase())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .minTeams(request.getMinTeams() != null ? request.getMinTeams() : 4)
                .maxTeams(request.getMaxTeams() != null ? request.getMaxTeams() : 16)
                .description(request.getDescription())
                .status("UPCOMING")
                .build();

        tournament = tournamentRepository.save(tournament);
        return mapToTournamentResponseDto(tournament);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDto> getAllTournaments(String status, String search) {
        List<TournamentEntity> list;
        if (search != null && !search.trim().isEmpty()) {
            list = tournamentRepository.searchTournaments(search.trim());
        } else if (status != null && !status.trim().isEmpty()) {
            list = tournamentRepository.findByStatus(status.toUpperCase());
        } else {
            list = tournamentRepository.findAll();
        }

        return list.stream()
                .map(this::mapToTournamentResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TournamentResponseDto getTournamentById(Long id) {
        TournamentEntity tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + id));
        return mapToTournamentResponseDto(tournament);
    }

    @Transactional
    public List<MatchResponseDto> generateFixtures(Long tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + tournamentId));

        List<TeamEntity> approvedTeams = teamRepository.findApprovedTeamsByTournamentId(tournamentId);
        if (approvedTeams.size() < 2) {
            throw new BadRequestException("Need at least 2 approved teams to generate fixtures. Currently: " + approvedTeams.size());
        }

        // Get available venues
        List<VenueEntity> venues = venueRepository.findByStatus("AVAILABLE");
        if (venues.isEmpty()) {
            venues = venueRepository.findAll();
        }

        LocalDateTime matchTime = tournament.getStartDate().atTime(9, 30);
        int venueIndex = 0;
        int roundNumber = 1;

        List<MatchResponseDto> generatedMatches = new ArrayList<>();

        // Generate Round Robin pairings
        int n = approvedTeams.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                TeamEntity home = approvedTeams.get(i);
                TeamEntity away = approvedTeams.get(j);

                VenueEntity venue = venues.isEmpty() ? null : venues.get(venueIndex % venues.size());
                venueIndex++;

                FixtureEntity fixture = FixtureEntity.builder()
                        .tournament(tournament)
                        .homeTeam(home)
                        .awayTeam(away)
                        .venue(venue)
                        .scheduledTime(matchTime)
                        .roundName("Round " + roundNumber)
                        .status("SCHEDULED")
                        .build();

                fixture = fixtureRepository.save(fixture);

                MatchEntity match = MatchEntity.builder()
                        .fixture(fixture)
                        .homeScore(0)
                        .awayScore(0)
                        .matchStatus("SCHEDULED")
                        .build();
                match = matchRepository.save(match);

                generatedMatches.add(matchService.mapToMatchResponseDto(match));

                // Advance match time by 3.5 hours for realistic team rest and venue turnover
                matchTime = matchTime.plusHours(3).plusMinutes(30);
                if (matchTime.getHour() >= 19) {
                    matchTime = matchTime.plusDays(1).withHour(9).withMinute(30);
                    roundNumber++;
                }
            }
        }

        tournament.setStatus("ONGOING");
        tournamentRepository.save(tournament);

        return generatedMatches;
    }

    @Transactional(readOnly = true)
    public TournamentPulseDto getTournamentPulse(Long tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + tournamentId));

        int totalTeams = (int) teamRepository.countByTournamentId(tournamentId);
        List<MatchEntity> allMatches = matchRepository.findByTournamentId(tournamentId);

        int totalFixtures = allMatches.size();
        int completed = (int) allMatches.stream().filter(m -> "COMPLETED".equals(m.getMatchStatus())).count();
        int live = (int) allMatches.stream().filter(m -> "LIVE".equals(m.getMatchStatus())).count();

        double completion = totalFixtures > 0 ? (double) completed / totalFixtures * 100.0 : 0.0;

        List<StandingResponseDto> standings = standingService.getStandingsByTournament(tournamentId);
        StandingResponseDto leader = standings.isEmpty() ? null : standings.get(0);

        List<MatchResponseDto> liveMatches = allMatches.stream()
                .filter(m -> "LIVE".equals(m.getMatchStatus()))
                .map(matchService::mapToMatchResponseDto)
                .collect(Collectors.toList());

        List<MatchResponseDto> upcomingMatches = allMatches.stream()
                .filter(m -> "SCHEDULED".equals(m.getMatchStatus()))
                .limit(5)
                .map(matchService::mapToMatchResponseDto)
                .collect(Collectors.toList());

        List<MatchResponseDto> recentResults = allMatches.stream()
                .filter(m -> "COMPLETED".equals(m.getMatchStatus()))
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(5)
                .map(matchService::mapToMatchResponseDto)
                .collect(Collectors.toList());

        List<String> announcements = announcementRepository.findByTournamentIdOrderByCreatedAtDesc(tournamentId)
                .stream()
                .map(a -> "[" + a.getPriority() + "] " + a.getTitle() + " - " + a.getContent())
                .limit(4)
                .collect(Collectors.toList());

        return TournamentPulseDto.builder()
                .tournamentId(tournament.getId())
                .tournamentName(tournament.getName())
                .sportName(tournament.getSport().getName())
                .status(tournament.getStatus())
                .totalTeams(totalTeams)
                .totalFixtures(totalFixtures)
                .completedMatches(completed)
                .liveMatchesCount(live)
                .completionPercentage(Math.round(completion * 10.0) / 10.0)
                .leaderTeam(leader)
                .liveMatches(liveMatches)
                .upcomingMatches(upcomingMatches)
                .recentResults(recentResults)
                .recentAnnouncements(announcements)
                .build();
    }

    private TournamentResponseDto mapToTournamentResponseDto(TournamentEntity t) {
        long currentTeamsCount = teamRepository.countByTournamentId(t.getId());
        return TournamentResponseDto.builder()
                .id(t.getId())
                .name(t.getName())
                .sportId(t.getSport().getId())
                .sportName(t.getSport().getName())
                .organizerId(t.getOrganizer().getId())
                .organizerName(t.getOrganizer().getFullName())
                .format(t.getFormat())
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .status(t.getStatus())
                .minTeams(t.getMinTeams())
                .maxTeams(t.getMaxTeams())
                .currentTeamsCount((int) currentTeamsCount)
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
