package com.sportx.backend.dto.sport;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportResponseDto {
    private Long id;
    private String name;
    private String category;
    private int minSquadSize;
    private int maxSquadSize;
    private Integer matchDurationMinutes;
    private String rulesConfig;
}
