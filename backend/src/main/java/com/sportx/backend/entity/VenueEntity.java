package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String location;

    private Integer capacity;

    @Column(length = 30)
    @Builder.Default
    private String status = "AVAILABLE"; // AVAILABLE, UNDER_MAINTENANCE, BOOKED

    @Column(length = 255)
    private String facilities;
}
