package com.sportx.backend.service;

import com.sportx.backend.dto.team.*;
import com.sportx.backend.entity.PlayerEntity;
import com.sportx.backend.entity.TeamEntity;
import com.sportx.backend.entity.TournamentEntity;
import com.sportx.backend.entity.UserEntity;
import com.sportx.backend.exception.BadRequestException;
import com.sportx.backend.exception.ResourceNotFoundException;
import com.sportx.backend.repository.PlayerRepository;
import com.sportx.backend.repository.TeamRepository;
import com.sportx.backend.repository.TournamentRepository;
import com.sportx.backend.repository.UserRepository;
import com.sportx.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    @Transactional
    public TeamResponseDto createTeam(TeamCreateRequest request, Authentication authentication) {
        TournamentEntity tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + request.getTournamentId()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserEntity manager = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        long teamCount = teamRepository.countByTournamentId(tournament.getId());
        if (tournament.getMaxTeams() != null && teamCount >= tournament.getMaxTeams()) {
            throw new BadRequestException("Tournament has reached maximum capacity of " + tournament.getMaxTeams() + " teams.");
        }

        TeamEntity team = TeamEntity.builder()
                .name(request.getName())
                .shortName(request.getShortName() != null ? request.getShortName() : request.getName().substring(0, Math.min(3, request.getName().length())).toUpperCase())
                .collegeName(request.getCollegeName())
                .tournament(tournament)
                .manager(manager)
                .logoUrl(request.getLogoUrl())
                .registrationStatus("APPROVED")
                .build();

        team = teamRepository.save(team);
        return mapToTeamResponseDto(team);
    }

    @Transactional(readOnly = true)
    public List<TeamResponseDto> getTeamsByTournament(Long tournamentId) {
        return teamRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToTeamResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamResponseDto getTeamById(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        return mapToTeamResponseDto(team);
    }

    @Transactional
    public PlayerResponseDto addPlayerToTeam(Long teamId, PlayerCreateRequest request) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        int maxSquad = team.getTournament().getSport().getMaxSquadSize();
        long currentSquadCount = playerRepository.countByTeamId(teamId);

        if (currentSquadCount >= maxSquad) {
            throw new BadRequestException("Cannot add player. Maximum squad limit of " + maxSquad + " reached for " + team.getTournament().getSport().getName());
        }

        PlayerEntity player = PlayerEntity.builder()
                .team(team)
                .fullName(request.getFullName())
                .jerseyNumber(request.getJerseyNumber())
                .position(request.getPosition())
                .studentRollNo(request.getStudentRollNo())
                .contactEmail(request.getContactEmail())
                .isEligible(true)
                .isCaptain(Boolean.TRUE.equals(request.getIsCaptain()))
                .build();

        player = playerRepository.save(player);
        return mapToPlayerResponseDto(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponseDto> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId).stream()
                .map(this::mapToPlayerResponseDto)
                .collect(Collectors.toList());
    }

    private TeamResponseDto mapToTeamResponseDto(TeamEntity team) {
        long count = playerRepository.countByTeamId(team.getId());
        int minSquad = team.getTournament().getSport().getMinSquadSize();
        boolean valid = count >= minSquad;

        return TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .shortName(team.getShortName())
                .collegeName(team.getCollegeName())
                .tournamentId(team.getTournament().getId())
                .tournamentName(team.getTournament().getName())
                .managerId(team.getManager() != null ? team.getManager().getId() : null)
                .managerName(team.getManager() != null ? team.getManager().getFullName() : null)
                .logoUrl(team.getLogoUrl())
                .registrationStatus(team.getRegistrationStatus())
                .playerCount((int) count)
                .squadValid(valid)
                .build();
    }

    private PlayerResponseDto mapToPlayerResponseDto(PlayerEntity player) {
        return PlayerResponseDto.builder()
                .id(player.getId())
                .teamId(player.getTeam().getId())
                .teamName(player.getTeam().getName())
                .fullName(player.getFullName())
                .jerseyNumber(player.getJerseyNumber())
                .position(player.getPosition())
                .studentRollNo(player.getStudentRollNo())
                .contactEmail(player.getContactEmail())
                .isEligible(player.getIsEligible())
                .isCaptain(player.getIsCaptain())
                .build();
    }
}
