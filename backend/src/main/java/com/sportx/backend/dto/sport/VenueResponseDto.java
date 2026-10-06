package com.sportx.backend.dto.sport;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueResponseDto {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private String status;
    private String facilities;
}
