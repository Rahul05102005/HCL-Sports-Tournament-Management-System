package com.sportx.backend.dto.team;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponseDto {
    private Long id;
    private String name;
    private String shortName;
    private String collegeName;
    private Long tournamentId;
    private String tournamentName;
    private Long managerId;
    private String managerName;
    private String logoUrl;
    private String registrationStatus;
    private int playerCount;
    private boolean squadValid;
}
