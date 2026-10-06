package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name; // ROLE_ADMIN, ROLE_ORGANIZER, ROLE_REFEREE, ROLE_PLAYER, ROLE_VIEWER

    @Column(length = 255)
    private String description;
}
