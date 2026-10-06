package com.sportx.backend.config;

import com.sportx.backend.entity.*;
import com.sportx.backend.repository.*;
import com.sportx.backend.service.StandingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SportRepository sportRepository;
    private final VenueRepository venueRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FixtureRepository fixtureRepository;
    private final MatchRepository matchRepository;
    private final ScoreEventRepository scoreEventRepository;
    private final AnnouncementRepository announcementRepository;
    private final StandingService standingService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0 && userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data seed.");
            return;
        }

        log.info("Starting SportX Database Seeding with realistic Indian Collegiate data...");

        // 1. Roles
        RoleEntity roleAdmin = roleRepository.save(new RoleEntity(null, "ROLE_ADMIN", "Full system administrator"));
        RoleEntity roleOrganizer = roleRepository.save(new RoleEntity(null, "ROLE_ORGANIZER", "Tournament creator and manager"));
        RoleEntity roleReferee = roleRepository.save(new RoleEntity(null, "ROLE_REFEREE", "Official referee for live scoring"));
        RoleEntity rolePlayer = roleRepository.save(new RoleEntity(null, "ROLE_PLAYER", "Team player and student athlete"));
        RoleEntity roleViewer = roleRepository.save(new RoleEntity(null, "ROLE_VIEWER", "Public spectator"));

        // 2. Users
        String defaultHash = passwordEncoder.encode("password123");

        UserEntity adminUser = userRepository.save(UserEntity.builder()
                .username("admin")
                .email("admin@sportx.dev")
                .password(defaultHash)
                .fullName("System Administrator")
                .phone("+91 98765 00001")
                .role(roleAdmin)
                .active(true)
                .build());

        UserEntity organizerUser = userRepository.save(UserEntity.builder()
                .username("organizer1")
                .email("organizer@bit.ac.in")
                .password(defaultHash)
                .fullName("Prof. K. Rajasekaran")
                .phone("+91 98765 00002")
                .role(roleOrganizer)
                .active(true)
                .build());

        UserEntity refereeUser = userRepository.save(UserEntity.builder()
                .username("referee1")
                .email("referee1@tnsports.org")
                .password(defaultHash)
                .fullName("M. Senthil Kumar (FIFA Certified)")
                .phone("+91 98765 00003")
                .role(roleReferee)
                .active(true)
                .build());

        UserEntity playerUser = userRepository.save(UserEntity.builder()
                .username("player1")
                .email("karthik.s@student.bit.ac.in")
                .password(defaultHash)
                .fullName("Karthik Subramanian")
                .phone("+91 98765 00004")
                .role(rolePlayer)
                .active(true)
                .build());

        // 3. Sports
        SportEntity football = sportRepository.save(SportEntity.builder()
                .name("Football")
                .category("TEAM")
                .minSquadSize(11)
                .maxSquadSize(18)
                .matchDurationMinutes(90)
                .rulesConfig("{\"periods\": 2, \"periodDuration\": 45, \"pointsForWin\": 3, \"pointsForDraw\": 1, \"pointsForLoss\": 0}")
                .build());

        SportEntity cricket = sportRepository.save(SportEntity.builder()
                .name("Cricket")
                .category("TEAM")
                .minSquadSize(11)
                .maxSquadSize(16)
                .matchDurationMinutes(180)
                .rulesConfig("{\"overs\": 20, \"pointsForWin\": 2, \"pointsForTie\": 1, \"pointsForLoss\": 0}")
                .build());

        SportEntity basketball = sportRepository.save(SportEntity.builder()
                .name("Basketball")
                .category("TEAM")
                .minSquadSize(5)
                .maxSquadSize(12)
                .matchDurationMinutes(40)
                .rulesConfig("{\"quarters\": 4, \"quarterDuration\": 10, \"pointsForWin\": 2, \"pointsForLoss\": 0}")
                .build());

        SportEntity badminton = sportRepository.save(SportEntity.builder()
                .name("Badminton")
                .category("DUAL")
                .minSquadSize(2)
                .maxSquadSize(4)
                .matchDurationMinutes(45)
                .rulesConfig("{\"sets\": 3, \"pointsPerSet\": 21}")
                .build());

        // 4. Venues
        VenueEntity venueBit = venueRepository.save(VenueEntity.builder()
                .name("BIT Main Football Arena")
                .location("BIT Campus, Sathyamangalam")
                .capacity(5000)
                .status("AVAILABLE")
                .facilities("Floodlights, Electronic Scoreboard, Medical Bay, Player Dugouts")
                .build());

        VenueEntity venuePsg = venueRepository.save(VenueEntity.builder()
                .name("PSG Tech Turf Ground")
                .location("PSG Tech, Peelamedu, Coimbatore")
                .capacity(3500)
                .status("AVAILABLE")
                .facilities("FIFA 2-Star Turf, Night Lights, Covered Pavilion")
                .build());

        VenueEntity venueCit = venueRepository.save(VenueEntity.builder()
                .name("CIT Sports Complex Ground")
                .location("CIT Campus, Civil Aerodrome, Coimbatore")
                .capacity(4000)
                .status("AVAILABLE")
                .facilities("Natural Grass, Athlete Pavilion, Commentary Box")
                .build());

        VenueEntity venueKec = venueRepository.save(VenueEntity.builder()
                .name("KEC Sports Pavilion")
                .location("Kongu Engineering College, Perundurai")
                .capacity(3000)
                .status("AVAILABLE")
                .facilities("Turf pitch, Live Broadcast Booth")
                .build());

        // 5. Tournaments
        TournamentEntity tournament1 = tournamentRepository.save(TournamentEntity.builder()
                .name("Tamil Nadu Inter-Engineering Super Cup 2026")
                .sport(football)
                .organizer(organizerUser)
                .format("ROUND_ROBIN")
                .startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now().plusDays(10))
                .status("ONGOING")
                .minTeams(4)
                .maxTeams(8)
                .description("Premier collegiate football championship featuring top engineering colleges across Tamil Nadu.")
                .build());

        TournamentEntity tournament2 = tournamentRepository.save(TournamentEntity.builder()
                .name("Kongu Region Collegiate Cricket Trophy 2026")
                .sport(cricket)
                .organizer(organizerUser)
                .format("ROUND_ROBIN")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(20))
                .status("UPCOMING")
                .minTeams(4)
                .maxTeams(6)
                .description("T20 inter-collegiate cricket tournament organized by Kongu region sports council.")
                .build());

        // 6. Teams for Tournament 1 (Football)
        TeamEntity bitTeam = teamRepository.save(TeamEntity.builder()
                .name("BIT Thunderbolts")
                .shortName("BIT")
                .collegeName("Bannari Amman Institute of Technology")
                .tournament(tournament1)
                .manager(organizerUser)
                .logoUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=100")
                .registrationStatus("APPROVED")
                .build());

        TeamEntity psgTeam = teamRepository.save(TeamEntity.builder()
                .name("PSG Strikers")
                .shortName("PSG")
                .collegeName("PSG College of Technology")
                .tournament(tournament1)
                .manager(adminUser)
                .logoUrl("https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=100")
                .registrationStatus("APPROVED")
                .build());

        TeamEntity citTeam = teamRepository.save(TeamEntity.builder()
                .name("CIT Spartans")
                .shortName("CIT")
                .collegeName("Coimbatore Institute of Technology")
                .tournament(tournament1)
                .manager(null)
                .logoUrl("https://images.unsplash.com/photo-1551958219-acbc608c6377?w=100")
                .registrationStatus("APPROVED")
                .build());

        TeamEntity kecTeam = teamRepository.save(TeamEntity.builder()
                .name("KEC Warriors")
                .shortName("KEC")
                .collegeName("Kongu Engineering College")
                .tournament(tournament1)
                .manager(null)
                .logoUrl("https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=100")
                .registrationStatus("APPROVED")
                .build());

        // 7. Players for BIT
        PlayerEntity p1 = playerRepository.save(PlayerEntity.builder()
                .team(bitTeam)
                .fullName("Karthik Subramanian")
                .jerseyNumber(10)
                .position("Forward (Striker)")
                .studentRollNo("22CS101")
                .contactEmail("karthik.s@student.bit.ac.in")
                .isCaptain(true)
                .isEligible(true)
                .build());

        PlayerEntity p2 = playerRepository.save(PlayerEntity.builder()
                .team(bitTeam)
                .fullName("Vigneshwaran Ramasamy")
                .jerseyNumber(7)
                .position("Right Winger")
                .studentRollNo("22CS102")
                .contactEmail("vignesh.r@student.bit.ac.in")
                .isCaptain(false)
                .isEligible(true)
                .build());

        PlayerEntity p3 = playerRepository.save(PlayerEntity.builder()
                .team(bitTeam)
                .fullName("Pranav Sundar")
                .jerseyNumber(1)
                .position("Goalkeeper")
                .studentRollNo("22EC105")
                .contactEmail("pranav.s@student.bit.ac.in")
                .isCaptain(false)
                .isEligible(true)
                .build());

        // Players for PSG
        PlayerEntity p4 = playerRepository.save(PlayerEntity.builder()
                .team(psgTeam)
                .fullName("Aditya Narayanan")
                .jerseyNumber(9)
                .position("Centre Forward")
                .studentRollNo("22ME201")
                .contactEmail("aditya.n@psgtech.edu")
                .isCaptain(true)
                .isEligible(true)
                .build());

        PlayerEntity p5 = playerRepository.save(PlayerEntity.builder()
                .team(psgTeam)
                .fullName("Gokulnath Selvam")
                .jerseyNumber(4)
                .position("Centre Back")
                .studentRollNo("22IT205")
                .contactEmail("gokul.s@psgtech.edu")
                .isCaptain(false)
                .isEligible(true)
                .build());

        // Players for CIT & KEC
        PlayerEntity p6 = playerRepository.save(PlayerEntity.builder()
                .team(citTeam)
                .fullName("Dinesh Kumar")
                .jerseyNumber(11)
                .position("Left Winger")
                .studentRollNo("22EE301")
                .contactEmail("dinesh.k@cit.edu.in")
                .isCaptain(true)
                .isEligible(true)
                .build());

        PlayerEntity p7 = playerRepository.save(PlayerEntity.builder()
                .team(kecTeam)
                .fullName("Saravanan Murugan")
                .jerseyNumber(8)
                .position("Central Midfielder")
                .studentRollNo("22AU401")
                .contactEmail("saravanan.m@kongu.ac.in")
                .isCaptain(true)
                .isEligible(true)
                .build());

        // 8. Completed Match: BIT vs CIT (2 - 1)
        FixtureEntity f1 = fixtureRepository.save(FixtureEntity.builder()
                .tournament(tournament1)
                .homeTeam(bitTeam)
                .awayTeam(citTeam)
                .venue(venueBit)
                .referee(refereeUser)
                .scheduledTime(LocalDateTime.now().minusDays(1).withHour(15).withMinute(30))
                .roundName("Round 1")
                .status("COMPLETED")
                .build());

        MatchEntity m1 = matchRepository.save(MatchEntity.builder()
                .fixture(f1)
                .homeScore(2)
                .awayScore(1)
                .matchStatus("COMPLETED")
                .startedAt(LocalDateTime.now().minusDays(1).withHour(15).withMinute(30))
                .endedAt(LocalDateTime.now().minusDays(1).withHour(17).withMinute(20))
                .winnerTeam(bitTeam)
                .isDraw(false)
                .notes("Thrilling finish! BIT scored a stoppage time header.")
                .build());

        scoreEventRepository.save(ScoreEventEntity.builder()
                .match(m1)
                .team(citTeam)
                .player(p6)
                .eventType("GOAL")
                .minuteOccurred(28)
                .extraInfo("Counter-attack bottom left strike")
                .build());

        scoreEventRepository.save(ScoreEventEntity.builder()
                .match(m1)
                .team(bitTeam)
                .player(p1)
                .eventType("GOAL")
                .minuteOccurred(62)
                .extraInfo("Curling free-kick into top corner")
                .build());

        scoreEventRepository.save(ScoreEventEntity.builder()
                .match(m1)
                .team(bitTeam)
                .player(p2)
                .eventType("GOAL")
                .minuteOccurred(91)
                .extraInfo("Header from corner kick")
                .build());

        standingService.recordMatchResult(tournament1, bitTeam, citTeam, 2, 1);

        // 9. Live Match: PSG vs KEC (Currently 1 - 0, 72nd minute)
        FixtureEntity f2 = fixtureRepository.save(FixtureEntity.builder()
                .tournament(tournament1)
                .homeTeam(psgTeam)
                .awayTeam(kecTeam)
                .venue(venuePsg)
                .referee(refereeUser)
                .scheduledTime(LocalDateTime.now().minusHours(1))
                .roundName("Round 1")
                .status("IN_PROGRESS")
                .build());

        MatchEntity m2 = matchRepository.save(MatchEntity.builder()
                .fixture(f2)
                .homeScore(1)
                .awayScore(0)
                .matchStatus("LIVE")
                .currentMinute(72)
                .startedAt(LocalDateTime.now().minusHours(1))
                .notes("High tempo match. PSG dominating possession.")
                .build());

        scoreEventRepository.save(ScoreEventEntity.builder()
                .match(m2)
                .team(psgTeam)
                .player(p4)
                .eventType("GOAL")
                .minuteOccurred(34)
                .extraInfo("Clinical low drive past keeper")
                .build());

        // 10. Upcoming Match: BIT vs PSG
        FixtureEntity f3 = fixtureRepository.save(FixtureEntity.builder()
                .tournament(tournament1)
                .homeTeam(bitTeam)
                .awayTeam(psgTeam)
                .venue(venueBit)
                .referee(refereeUser)
                .scheduledTime(LocalDateTime.now().plusDays(1).withHour(16).withMinute(0))
                .roundName("Round 2 - Derby")
                .status("SCHEDULED")
                .build());

        matchRepository.save(MatchEntity.builder()
                .fixture(f3)
                .homeScore(0)
                .awayScore(0)
                .matchStatus("SCHEDULED")
                .build());

        // 11. Announcements
        announcementRepository.save(AnnouncementEntity.builder()
                .tournament(tournament1)
                .title("Matchday 2 Schedule Updated")
                .content("BIT vs PSG Derby match moved to 4:00 PM for live broadcast on college sports portal.")
                .priority("HIGH")
                .author(organizerUser)
                .build());

        announcementRepository.save(AnnouncementEntity.builder()
                .tournament(tournament1)
                .title("Medical & Physiotherapy Facilities")
                .content("Free physio support available for all squads at BIT Sports Pavilion.")
                .priority("NORMAL")
                .author(adminUser)
                .build());

        log.info("SportX Database Seeding Completed Successfully! Demo credentials: admin / organizer1 / referee1 / player1 (Password: password123)");
    }
}
