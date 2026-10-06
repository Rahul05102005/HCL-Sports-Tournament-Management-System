-- ============================================================================
-- SportX - Smart Sports Tournament Management Platform
-- Database Schema (3NF Normalization, Constraints, Foreign Keys)
-- Target: MySQL 8.0+
-- ============================================================================

CREATE DATABASE IF NOT EXISTS sportx_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sportx_db;

-- Drop tables in reverse dependency order if recreating
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS referee_assignments;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS standings;
DROP TABLE IF EXISTS score_events;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS fixtures;
DROP TABLE IF EXISTS venues;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS teams;
DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS sports;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- Roles: ADMIN, ORGANISER, TEAM_MANAGER, REFEREE, VIEWER
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'VIEWER',
    phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'ORGANISER', 'TEAM_MANAGER', 'REFEREE', 'VIEWER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. SPORTS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE sports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    min_players_per_team INT NOT NULL DEFAULT 5,
    max_players_per_team INT NOT NULL DEFAULT 16,
    match_duration_minutes INT NOT NULL DEFAULT 90,
    scoring_type VARCHAR(30) NOT NULL DEFAULT 'GOALS',
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_sports_name UNIQUE (name),
    CONSTRAINT uq_sports_code UNIQUE (code),
    CONSTRAINT chk_sport_players CHECK (min_players_per_team > 0 AND max_players_per_team >= min_players_per_team)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. TOURNAMENTS TABLE
-- Formats: ROUND_ROBIN, KNOCKOUT, LEAGUE_AND_KNOCKOUT
-- Statuses: DRAFT, UPCOMING, ONGOING, COMPLETED, CANCELLED
-- ----------------------------------------------------------------------------
CREATE TABLE tournaments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    sport_id BIGINT NOT NULL,
    organiser_id BIGINT NOT NULL,
    format VARCHAR(30) NOT NULL DEFAULT 'ROUND_ROBIN',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    venue_city VARCHAR(100) NOT NULL,
    max_teams INT NOT NULL DEFAULT 8,
    points_for_win INT NOT NULL DEFAULT 3,
    points_for_draw INT NOT NULL DEFAULT 1,
    points_for_loss INT NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_tournaments_code UNIQUE (code),
    CONSTRAINT fk_tournaments_sport FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE RESTRICT,
    CONSTRAINT fk_tournaments_organiser FOREIGN KEY (organiser_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_tournaments_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_tournaments_format CHECK (format IN ('ROUND_ROBIN', 'KNOCKOUT', 'LEAGUE_AND_KNOCKOUT')),
    CONSTRAINT chk_tournaments_status CHECK (status IN ('DRAFT', 'UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 4. TEAMS TABLE
-- Registration Statuses: PENDING, APPROVED, REJECTED
-- ----------------------------------------------------------------------------
CREATE TABLE teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    short_name VARCHAR(10) NOT NULL,
    college_name VARCHAR(120) NOT NULL,
    manager_id BIGINT,
    logo_url VARCHAR(255),
    registration_status VARCHAR(20) NOT NULL DEFAULT 'APPROVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_teams_tournament_name UNIQUE (tournament_id, name),
    CONSTRAINT uq_teams_tournament_short_name UNIQUE (tournament_id, short_name),
    CONSTRAINT fk_teams_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    CONSTRAINT fk_teams_manager FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_teams_status CHECK (registration_status IN ('PENDING', 'APPROVED', 'REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 5. PLAYERS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    jersey_number INT NOT NULL,
    roll_number VARCHAR(30) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    age INT NOT NULL,
    position VARCHAR(30) NOT NULL,
    is_eligible BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_players_team_jersey UNIQUE (team_id, jersey_number),
    CONSTRAINT uq_players_roll_number UNIQUE (roll_number),
    CONSTRAINT fk_players_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT chk_players_age CHECK (age >= 16 AND age <= 35),
    CONSTRAINT chk_players_jersey CHECK (jersey_number > 0 AND jersey_number < 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 6. VENUES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE venues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    location VARCHAR(120) NOT NULL,
    field_number INT NOT NULL DEFAULT 1,
    capacity INT NOT NULL DEFAULT 500,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_venues_tournament_field UNIQUE (tournament_id, name, field_number),
    CONSTRAINT fk_venues_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 7. FIXTURES TABLE
-- Status: SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED
-- ----------------------------------------------------------------------------
CREATE TABLE fixtures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    round_number INT NOT NULL DEFAULT 1,
    home_team_id BIGINT NOT NULL,
    away_team_id BIGINT NOT NULL,
    venue_id BIGINT,
    referee_id BIGINT,
    scheduled_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fixtures_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    CONSTRAINT fk_fixtures_home_team FOREIGN KEY (home_team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_fixtures_away_team FOREIGN KEY (away_team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_fixtures_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE SET NULL,
    CONSTRAINT fk_fixtures_referee FOREIGN KEY (referee_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_fixtures_different_teams CHECK (home_team_id <> away_team_id),
    CONSTRAINT chk_fixtures_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'POSTPONED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 8. MATCHES TABLE
-- 1-to-1 relationship with Fixture
-- Status: UPCOMING, LIVE, COMPLETED, ABANDONED
-- ----------------------------------------------------------------------------
CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fixture_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING',
    home_score INT NOT NULL DEFAULT 0,
    away_score INT NOT NULL DEFAULT 0,
    current_minute INT NOT NULL DEFAULT 0,
    started_at DATETIME,
    finished_at DATETIME,
    winner_team_id BIGINT,
    referee_id BIGINT,
    notes TEXT,
    CONSTRAINT uq_matches_fixture UNIQUE (fixture_id),
    CONSTRAINT fk_matches_fixture FOREIGN KEY (fixture_id) REFERENCES fixtures(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_winner FOREIGN KEY (winner_team_id) REFERENCES teams(id) ON DELETE SET NULL,
    CONSTRAINT fk_matches_referee FOREIGN KEY (referee_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_matches_status CHECK (status IN ('UPCOMING', 'LIVE', 'COMPLETED', 'ABANDONED')),
    CONSTRAINT chk_matches_scores CHECK (home_score >= 0 AND away_score >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 9. SCORE_EVENTS TABLE
-- Event Types: GOAL, POINT, RUN, WICKET, FOUL, YELLOW_CARD, RED_CARD, SUBSTITUTION
-- ----------------------------------------------------------------------------
CREATE TABLE score_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    player_id BIGINT,
    event_type VARCHAR(30) NOT NULL,
    minute_offset INT NOT NULL DEFAULT 0,
    points_awarded INT NOT NULL DEFAULT 1,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_score_events_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    CONSTRAINT fk_score_events_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_score_events_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE SET NULL,
    CONSTRAINT chk_score_events_type CHECK (event_type IN ('GOAL', 'POINT', 'RUN', 'WICKET', 'FOUL', 'YELLOW_CARD', 'RED_CARD', 'SUBSTITUTION'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 10. STANDINGS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE standings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    played INT NOT NULL DEFAULT 0,
    won INT NOT NULL DEFAULT 0,
    drawn INT NOT NULL DEFAULT 0,
    lost INT NOT NULL DEFAULT 0,
    points INT NOT NULL DEFAULT 0,
    goals_for INT NOT NULL DEFAULT 0,
    goals_against INT NOT NULL DEFAULT 0,
    goal_difference INT NOT NULL DEFAULT 0,
    fair_play_points INT NOT NULL DEFAULT 100,
    ranking INT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_standings_tournament_team UNIQUE (tournament_id, team_id),
    CONSTRAINT fk_standings_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    CONSTRAINT fk_standings_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT chk_standings_played CHECK (played = (won + drawn + lost)),
    CONSTRAINT chk_standings_gd CHECK (goal_difference = (goals_for - goals_against))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 11. ANNOUNCEMENTS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    content TEXT NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_announcements_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    CONSTRAINT fk_announcements_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 12. NOTIFICATIONS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(30) NOT NULL DEFAULT 'SYSTEM',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 13. REFEREE_ASSIGNMENTS TABLE
-- Status: ASSIGNED, ACCEPTED, DECLINED, COMPLETED
-- ----------------------------------------------------------------------------
CREATE TABLE referee_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    referee_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED',
    notes VARCHAR(255),
    CONSTRAINT uq_referee_assignments_match_ref UNIQUE (match_id, referee_id),
    CONSTRAINT fk_ref_assign_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    CONSTRAINT fk_ref_assign_referee FOREIGN KEY (referee_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_ref_assign_status CHECK (status IN ('ASSIGNED', 'ACCEPTED', 'DECLINED', 'COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
