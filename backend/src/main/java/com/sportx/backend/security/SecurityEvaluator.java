package com.sportx.backend.security;

import com.sportx.backend.entity.TournamentEntity;
import com.sportx.backend.entity.TeamEntity;
import com.sportx.backend.entity.FixtureEntity;
import com.sportx.backend.repository.TournamentRepository;
import com.sportx.backend.repository.TeamRepository;
import com.sportx.backend.repository.FixtureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("securityEvaluator")
@RequiredArgsConstructor
public class SecurityEvaluator {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final FixtureRepository fixtureRepository;

    public boolean isTournamentOwner(Authentication authentication, Long tournamentId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        if (hasRole(authentication, "ROLE_ADMIN")) return true;

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return tournamentRepository.findById(tournamentId)
                .map(TournamentEntity::getOrganizer)
                .filter(org -> Objects.equals(org.getId(), principal.getId()))
                .isPresent();
    }

    public boolean isTeamManager(Authentication authentication, Long teamId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        if (hasRole(authentication, "ROLE_ADMIN")) return true;

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return teamRepository.findById(teamId)
                .map(TeamEntity::getManager)
                .filter(mgr -> mgr != null && Objects.equals(mgr.getId(), principal.getId()))
                .isPresent();
    }

    public boolean isAssignedRefereeOrAdmin(Authentication authentication, Long fixtureId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        if (hasRole(authentication, "ROLE_ADMIN") || hasRole(authentication, "ROLE_ORGANIZER")) return true;

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return fixtureRepository.findById(fixtureId)
                .map(FixtureEntity::getReferee)
                .filter(ref -> ref != null && Objects.equals(ref.getId(), principal.getId()))
                .isPresent();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }
}
