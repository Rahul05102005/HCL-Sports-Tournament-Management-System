package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 60)
    private String name; // Football, Cricket, Basketball, Badminton, Volleyball, Kabaddi

    @Column(nullable = false, length = 40)
    private String category; // TEAM, DUAL, INDIVIDUAL

    @Column(name = "min_squad_size", nullable = false)
    private Integer minSquadSize;

    @Column(name = "max_squad_size", nullable = false)
    private Integer maxSquadSize;

    @Column(name = "match_duration_minutes")
    private Integer matchDurationMinutes;

    @Column(name = "rules_config", columnDefinition = "TEXT")
    private String rulesConfig;
}
