package com.sportx.backend.dto.team;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerResponseDto {
    private Long id;
    private Long teamId;
    private String teamName;
    private String fullName;
    private Integer jerseyNumber;
    private String position;
    private String studentRollNo;
    private String contactEmail;
    private Boolean isEligible;
    private Boolean isCaptain;
}
