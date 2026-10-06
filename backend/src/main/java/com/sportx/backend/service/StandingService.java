package com.sportx.backend.service;

import com.sportx.backend.dto.standing.StandingResponseDto;
import com.sportx.backend.entity.StandingEntity;
import com.sportx.backend.entity.TeamEntity;
import com.sportx.backend.entity.TournamentEntity;
import com.sportx.backend.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingService {

    private final StandingRepository standingRepository;

    @Transactional(readOnly = true)
    public List<StandingResponseDto> getStandingsByTournament(Long tournamentId) {
        List<StandingEntity> standings = standingRepository.findRankedStandingsByTournamentId(tournamentId);
        AtomicInteger rankCounter = new AtomicInteger(1);

        return standings.stream()
                .map(s -> StandingResponseDto.builder()
                        .id(s.getId())
                        .tournamentId(s.getTournament().getId())
                        .teamId(s.getTeam().getId())
                        .teamName(s.getTeam().getName())
                        .shortName(s.getTeam().getShortName())
                        .collegeName(s.getTeam().getCollegeName())
                        .played(s.getPlayed())
                        .won(s.getWon())
                        .drawn(s.getDrawn())
                        .lost(s.getLost())
                        .scoreFor(s.getScoreFor())
                        .scoreAgainst(s.getScoreAgainst())
                        .goalDifference(s.getGoalDifference())
                        .points(s.getPoints())
                        .formGuide(s.getFormGuide())
                        .rank(rankCounter.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void recordMatchResult(TournamentEntity tournament, TeamEntity homeTeam, TeamEntity awayTeam, int homeScore, int awayScore) {
        StandingEntity homeStanding = standingRepository.findByTournamentIdAndTeamId(tournament.getId(), homeTeam.getId())
                .orElseGet(() -> standingRepository.save(StandingEntity.builder()
                        .tournament(tournament)
                        .team(homeTeam)
                        .played(0).won(0).drawn(0).lost(0).points(0).scoreFor(0).scoreAgainst(0).goalDifference(0).formGuide("")
                        .build()));

        StandingEntity awayStanding = standingRepository.findByTournamentIdAndTeamId(tournament.getId(), awayTeam.getId())
                .orElseGet(() -> standingRepository.save(StandingEntity.builder()
                        .tournament(tournament)
                        .team(awayTeam)
                        .played(0).won(0).drawn(0).lost(0).points(0).scoreFor(0).scoreAgainst(0).goalDifference(0).formGuide("")
                        .build()));

        homeStanding.setPlayed(homeStanding.getPlayed() + 1);
        awayStanding.setPlayed(awayStanding.getPlayed() + 1);

        homeStanding.setScoreFor(homeStanding.getScoreFor() + homeScore);
        homeStanding.setScoreAgainst(homeStanding.getScoreAgainst() + awayScore);
        awayStanding.setScoreFor(awayStanding.getScoreFor() + awayScore);
        awayStanding.setScoreAgainst(awayStanding.getScoreAgainst() + homeScore);

        String homeForm = homeStanding.getFormGuide() != null ? homeStanding.getFormGuide() : "";
        String awayForm = awayStanding.getFormGuide() != null ? awayStanding.getFormGuide() : "";

        if (homeScore > awayScore) {
            homeStanding.setWon(homeStanding.getWon() + 1);
            homeStanding.setPoints(homeStanding.getPoints() + 3);
            awayStanding.setLost(awayStanding.getLost() + 1);

            homeStanding.setFormGuide(appendForm(homeForm, "W"));
            awayStanding.setFormGuide(appendForm(awayForm, "L"));
        } else if (awayScore > homeScore) {
            awayStanding.setWon(awayStanding.getWon() + 1);
            awayStanding.setPoints(awayStanding.getPoints() + 3);
            homeStanding.setLost(homeStanding.getLost() + 1);

            homeStanding.setFormGuide(appendForm(homeForm, "L"));
            awayStanding.setFormGuide(appendForm(awayForm, "W"));
        } else {
            homeStanding.setDrawn(homeStanding.getDrawn() + 1);
            homeStanding.setPoints(homeStanding.getPoints() + 1);
            awayStanding.setDrawn(awayStanding.getDrawn() + 1);
            awayStanding.setPoints(awayStanding.getPoints() + 1);

            homeStanding.setFormGuide(appendForm(homeForm, "D"));
            awayStanding.setFormGuide(appendForm(awayForm, "D"));
        }

        standingRepository.save(homeStanding);
        standingRepository.save(awayStanding);
    }

    private String appendForm(String currentForm, String outcome) {
        String updated = currentForm + outcome;
        if (updated.length() > 5) {
            return updated.substring(updated.length() - 5);
        }
        return updated;
    }
}
