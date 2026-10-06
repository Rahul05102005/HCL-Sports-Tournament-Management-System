-- ============================================================================
-- SportX - Smart Sports Tournament Management Platform
-- Performance Indexes (HCL Rubric)
-- Target: MySQL 8.0+
-- ============================================================================

USE sportx_db;

-- ----------------------------------------------------------------------------
-- Index 1: Composite Index on Fixture tournament and scheduled_time
-- Purpose: Accelerates fixture filtering by tournament ordered chronologically
-- Used by: Public tournament calendar, Organiser schedule dashboard
-- ----------------------------------------------------------------------------
CREATE INDEX idx_fixture_tournament_date 
ON fixtures (tournament_id, scheduled_time);

-- ----------------------------------------------------------------------------
-- Index 2: Match Status Filter Index
-- Purpose: Quick lookup for 'LIVE', 'UPCOMING', and 'COMPLETED' matches
-- Used by: Tournament Pulse, Live scoring dash, referee assignments
-- ----------------------------------------------------------------------------
CREATE INDEX idx_match_status 
ON matches (status);

-- ----------------------------------------------------------------------------
-- Index 3: Player Team Membership Index
-- Purpose: Fast squad lookup, eligibility validation, team roster generation
-- Used by: Team Manager dashboard, player verification wizard
-- ----------------------------------------------------------------------------
CREATE INDEX idx_player_team 
ON players (team_id, jersey_number);

-- ----------------------------------------------------------------------------
-- Index 4: Score Events by Match and Minute
-- Purpose: Instant chronological timeline rendering and Match Momentum chart
-- Used by: Live match timeline, Smart Match Insights calculation
-- ----------------------------------------------------------------------------
CREATE INDEX idx_score_event_match 
ON score_events (match_id, minute_offset);

-- ----------------------------------------------------------------------------
-- Index 5: Standings Ranking Composite Index
-- Purpose: Rapid sorting and tie-breaker retrieval for leaderboards
-- Used by: Standings table, Tournament Pulse leader badge
-- ----------------------------------------------------------------------------
CREATE INDEX idx_standings_tournament_rank 
ON standings (tournament_id, points DESC, goal_difference DESC, goals_for DESC);

-- ----------------------------------------------------------------------------
-- Index 6: User Email and Role Index
-- Purpose: Sub-millisecond JWT authentication and RBAC lookup
-- Used by: Spring Security UserDetailsService during every authenticated API call
-- ----------------------------------------------------------------------------
CREATE INDEX idx_users_email_role 
ON users (email, role);
