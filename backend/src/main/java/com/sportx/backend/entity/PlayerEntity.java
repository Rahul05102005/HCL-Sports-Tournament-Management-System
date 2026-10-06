package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Column(length = 50)
    private String position;

    @Column(name = "student_roll_no", length = 50)
    private String studentRollNo;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "is_eligible", nullable = false)
    @Builder.Default
    private Boolean isEligible = true;

    @Column(name = "is_captain", nullable = false)
    @Builder.Default
    private Boolean isCaptain = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
