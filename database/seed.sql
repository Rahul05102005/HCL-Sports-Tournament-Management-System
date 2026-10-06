-- ============================================================================
-- SportX - Smart Sports Tournament Management Platform
-- Realistic Indian Inter-Collegiate Seed Data (20+ Rows per Main Table)
-- Target: MySQL 8.0+
-- ============================================================================

USE sportx_db;

-- BCrypt hash for "password123": $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG

-- ----------------------------------------------------------------------------
-- 1. USERS (22 rows: 2 Admins, 4 Organisers, 10 Team Managers, 4 Referees, 2 Viewers)
-- ----------------------------------------------------------------------------
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, phone) VALUES
(1, 'admin', 'admin@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Vikram', 'Aditya', 'ADMIN', '+919876543210'),
(2, 'admin_suresh', 'suresh.admin@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Suresh', 'Raina', 'ADMIN', '+919876543211'),
(3, 'organiser_rahul', 'rahul.organiser@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Rahul', 'Dravid', 'ORGANISER', '+919876543212'),
(4, 'organiser_priya', 'priya.k@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Priya', 'Krishnan', 'ORGANISER', '+919876543213'),
(5, 'organiser_anand', 'anand.v@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Anand', 'Viswanathan', 'ORGANISER', '+919876543214'),
(6, 'organiser_deepa', 'deepa.n@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Deepa', 'Nair', 'ORGANISER', '+919876543215'),
(7, 'mgr_bit', 'manager.bit@bitsathy.ac.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Karthik', 'Raja', 'TEAM_MANAGER', '+919876543216'),
(8, 'mgr_psg', 'manager.psg@psgtech.edu', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Murugan', 'Vel', 'TEAM_MANAGER', '+919876543217'),
(9, 'mgr_kec', 'manager.kec@kongu.ac.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Prakash', 'Chandran', 'TEAM_MANAGER', '+919876543218'),
(10, 'mgr_cit', 'manager.cit@cit.edu.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Saravanan', 'G', 'TEAM_MANAGER', '+919876543219'),
(11, 'mgr_ssn', 'manager.ssn@ssn.edu.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Gopinath', 'M', 'TEAM_MANAGER', '+919876543220'),
(12, 'mgr_ceg', 'manager.ceg@annauniv.edu', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Muthukumar', 'P', 'TEAM_MANAGER', '+919876543221'),
(13, 'mgr_gct', 'manager.gct@gct.ac.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Venkatesh', 'K', 'TEAM_MANAGER', '+919876543222'),
(14, 'mgr_tce', 'manager.tce@tce.edu', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Dinesh', 'Kumar', 'TEAM_MANAGER', '+919876543223'),
(15, 'mgr_kct', 'manager.kct@kct.ac.in', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Ashok', 'Varma', 'TEAM_MANAGER', '+919876543224'),
(16, 'mgr_sastra', 'manager.sastra@sastra.edu', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Balaji', 'Raman', 'TEAM_MANAGER', '+919876543225'),
(17, 'referee_arun', 'arun.ref@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Arun', 'Prasath', 'REFEREE', '+919876543226'),
(18, 'referee_shankar', 'shankar.ref@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Shankar', 'Mahadevan', 'REFEREE', '+919876543227'),
(19, 'referee_fathima', 'fathima.ref@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Fathima', 'Zehra', 'REFEREE', '+919876543228'),
(20, 'referee_john', 'john.ref@sportx.io', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'John', 'Paul', 'REFEREE', '+919876543229'),
(21, 'viewer_kavitha', 'kavitha@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Kavitha', 'Sundar', 'VIEWER', '+919876543230'),
(22, 'viewer_manoj', 'manoj.k@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Manoj', 'Kumar', 'VIEWER', '+919876543231');

-- ----------------------------------------------------------------------------
-- 2. SPORTS (8 rows: Football, Basketball, Cricket, Volleyball, Badminton, Table Tennis, Kabaddi, Hockey)
-- ----------------------------------------------------------------------------
INSERT INTO sports (id, name, code, min_players_per_team, max_players_per_team, match_duration_minutes, scoring_type, description) VALUES
(1, 'Football', 'FB-11', 7, 18, 90, 'GOALS', 'Association Football - 11 aside collegiate standard match with 45 min halves.'),
(2, 'Basketball', 'BB-5', 5, 12, 40, 'POINTS', 'Standard FIBA collegiate basketball with four 10-minute quarters.'),
(3, 'Cricket T20', 'CRIC-T20', 11, 16, 180, 'RUNS_WICKETS', 'Twenty20 Inter-University Cricket Championship match format.'),
(4, 'Volleyball', 'VB-6', 6, 12, 60, 'SETS_POINTS', 'Best of 3 or 5 sets FIVB collegiate tournament format.'),
(5, 'Badminton (Team)', 'BADM-5', 3, 7, 75, 'SETS_POINTS', 'Collegiate team tie badminton championship format (singles and doubles).'),
(6, 'Kabaddi', 'KBD-7', 7, 14, 40, 'POINTS', 'Pro Kabaddi collegiate format with two 20-minute halves and raid clock.'),
(7, 'Hockey', 'HCK-11', 11, 18, 60, 'GOALS', 'Field Hockey four 15-minute quarters collegiate championship.'),
(8, 'Table Tennis', 'TT-3', 3, 6, 45, 'SETS_POINTS', 'Team Championship collegiate table tennis best of 5 singles/doubles.');

-- ----------------------------------------------------------------------------
-- 3. TOURNAMENTS (6 rows: 2 Ongoing, 2 Upcoming, 1 Completed, 1 Draft)
-- ----------------------------------------------------------------------------
INSERT INTO tournaments (id, name, code, sport_id, organiser_id, format, status, start_date, end_date, venue_city, max_teams, points_for_win, points_for_draw, points_for_loss, description) VALUES
(1, 'All India Inter-Collegiate Football Cup 2026', 'AICF-2026', 1, 3, 'ROUND_ROBIN', 'ONGOING', '2026-10-01', '2026-10-15', 'Coimbatore', 8, 3, 1, 0, 'Premier south-zone inter-collegiate football league featuring top autonomous institutions.'),
(2, 'State Basketball Super League 2026', 'SBSL-2026', 2, 4, 'KNOCKOUT', 'ONGOING', '2026-10-03', '2026-10-12', 'Chennai', 8, 2, 0, 0, 'High-intensity knockout basketball championship sanctioned by Collegiate Sports Board.'),
(3, 'Tamil Nadu Inter-University Cricket Trophy', 'TNICT-2026', 3, 5, 'ROUND_ROBIN', 'UPCOMING', '2026-10-20', '2026-11-05', 'Madurai', 8, 2, 1, 0, 'Flagship T20 tournament on red-soil turf wickets with floodlight evening finals.'),
(4, 'Inter-Engineering Volleyball Championship', 'IEVC-2026', 4, 3, 'LEAGUE_AND_KNOCKOUT', 'UPCOMING', '2026-11-10', '2026-11-18', 'Tiruchirappalli', 8, 3, 0, 0, 'League-cum-knockout volleyball fest organized under the State Technical Universities banner.'),
(5, 'Spring Kabaddi Inter-Zonal Fest 2026', 'SKIZ-2026', 6, 6, 'ROUND_ROBIN', 'COMPLETED', '2026-08-10', '2026-08-20', 'Salem', 6, 2, 1, 0, 'Annual inter-zonal kabaddi league wrapped up with record collegiate viewership.'),
(6, 'National Invitational Football Trophy 2027', 'NIFT-2027', 1, 3, 'ROUND_ROBIN', 'DRAFT', '2027-01-15', '2027-01-28', 'Bengaluru', 10, 3, 1, 0, 'Upcoming national invitational football summit for top 10 technological universities.');

-- ----------------------------------------------------------------------------
-- 4. TEAMS (8 teams for Tournament 1 + 6 teams for Tournament 2)
-- ----------------------------------------------------------------------------
INSERT INTO teams (id, tournament_id, name, short_name, college_name, manager_id, logo_url, registration_status) VALUES
(1, 1, 'BIT Sparks', 'BIT', 'Bannari Amman Institute of Technology', 7, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2', 'APPROVED'),
(2, 1, 'PSG Strikers', 'PSG', 'PSG College of Technology', 8, 'https://images.unsplash.com/photo-1517466787929-bc90951d0974', 'APPROVED'),
(3, 1, 'KEC Warriors', 'KEC', 'Kongu Engineering College', 9, 'https://images.unsplash.com/photo-1543326727-cf6c39e8f84c', 'APPROVED'),
(4, 1, 'CIT Dynamos', 'CIT', 'Coimbatore Institute of Technology', 10, 'https://images.unsplash.com/photo-1522778119026-d647f0596c20', 'APPROVED'),
(5, 1, 'SSN Titans', 'SSN', 'SSN College of Engineering', 11, 'https://images.unsplash.com/photo-1574629810360-7efbbe195018', 'APPROVED'),
(6, 1, 'CEG Blasters', 'CEG', 'College of Engineering Guindy Anna Univ', 12, 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55', 'APPROVED'),
(7, 1, 'GCT Panthers', 'GCT', 'Government College of Technology', 13, 'https://images.unsplash.com/photo-1560272564-c83b66b1ad12', 'APPROVED'),
(8, 1, 'TCE Lions', 'TCE', 'Thiagarajar College of Engineering', 14, 'https://images.unsplash.com/photo-1489944445391-11dd35574549', 'APPROVED'),
-- Basketball Tournament 2 teams
(9, 2, 'KCT Hawks', 'KCT', 'Kumaraguru College of Technology', 15, 'https://images.unsplash.com/photo-1546519638-68e109498ffc', 'APPROVED'),
(10, 2, 'SASTRA Bulls', 'SAS', 'SASTRA Deemed University', 16, 'https://images.unsplash.com/photo-1519861531473-9200262188bf', 'APPROVED'),
(11, 2, 'BIT Hoopers', 'BITH', 'Bannari Amman Institute of Technology', 7, 'https://images.unsplash.com/photo-1574629810360-7efbbe195018', 'APPROVED'),
(12, 2, 'PSG Cagers', 'PSGC', 'PSG College of Technology', 8, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2', 'APPROVED'),
(13, 2, 'SSN Knights', 'SSNK', 'SSN College of Engineering', 11, 'https://images.unsplash.com/photo-1522778119026-d647f0596c20', 'APPROVED'),
(14, 2, 'CEG Eagles', 'CEGE', 'College of Engineering Guindy Anna Univ', 12, 'https://images.unsplash.com/photo-1543326727-cf6c39e8f84c', 'APPROVED');

-- ----------------------------------------------------------------------------
-- 5. PLAYERS (24 players across teams with realistic college profiles)
-- ----------------------------------------------------------------------------
INSERT INTO players (id, team_id, first_name, last_name, jersey_number, roll_number, email, phone, age, position, is_eligible) VALUES
(1, 1, 'Rahul', 'Prasanna', 10, '22CS0101', 'rahul.p@bitsathy.ac.in', '+919443100001', 20, 'Forward', TRUE),
(2, 1, 'Dinesh', 'Karthik', 7, '22CS0102', 'dinesh.k@bitsathy.ac.in', '+919443100002', 21, 'Midfielder', TRUE),
(3, 1, 'Ashwin', 'Murthy', 4, '22EC0201', 'ashwin.m@bitsathy.ac.in', '+919443100003', 20, 'Defender', TRUE),
(4, 1, 'Hari', 'Prasad', 1, '22ME0301', 'hari.p@bitsathy.ac.in', '+919443100004', 21, 'Goalkeeper', TRUE),
(5, 2, 'Manoj', 'Venkatesh', 9, '22PSG0401', 'manoj.v@psgtech.edu', '+919443100005', 21, 'Forward', TRUE),
(6, 2, 'Surya', 'Prakash', 8, '22PSG0402', 'surya.p@psgtech.edu', '+919443100006', 20, 'Midfielder', TRUE),
(7, 2, 'Vijay', 'Narayanan', 5, '22PSG0403', 'vijay.n@psgtech.edu', '+919443100007', 22, 'Defender', TRUE),
(8, 2, 'Kiran', 'Raj', 1, '22PSG0404', 'kiran.r@psgtech.edu', '+919443100008', 20, 'Goalkeeper', TRUE),
(9, 3, 'Gowtham', 'Shanmugam', 11, '22KEC0501', 'gowtham.s@kongu.ac.in', '+919443100009', 20, 'Forward', TRUE),
(10, 3, 'Arvind', 'Swamy', 6, '22KEC0502', 'arvind.s@kongu.ac.in', '+919443100010', 21, 'Midfielder', TRUE),
(11, 3, 'Deepak', 'Kumar', 3, '22KEC0503', 'deepak.k@kongu.ac.in', '+919443100011', 21, 'Defender', TRUE),
(12, 4, 'Naveen', 'Chander', 10, '22CIT0601', 'naveen.c@cit.edu.in', '+919443100012', 20, 'Forward', TRUE),
(13, 4, 'Sanjay', 'Dutt', 14, '22CIT0602', 'sanjay.d@cit.edu.in', '+919443100013', 21, 'Midfielder', TRUE),
(14, 5, 'Siddharth', 'Balaji', 9, '22SSN0701', 'siddharth.b@ssn.edu.in', '+919443100014', 21, 'Forward', TRUE),
(15, 5, 'Pranav', 'Mohan', 2, '22SSN0702', 'pranav.m@ssn.edu.in', '+919443100015', 20, 'Defender', TRUE),
(16, 6, 'Karthik', 'Sivakumar', 7, '22CEG0801', 'karthik.s@annauniv.edu', '+919443100016', 22, 'Midfielder', TRUE),
(17, 6, 'Vishal', 'Dev', 11, '22CEG0802', 'vishal.d@annauniv.edu', '+919443100017', 20, 'Forward', TRUE),
(18, 7, 'Ajith', 'Kumar', 10, '22GCT0901', 'ajith.k@gct.ac.in', '+919443100018', 21, 'Forward', TRUE),
(19, 8, 'Madhavan', 'Rangan', 8, '22TCE1001', 'madhavan.r@tce.edu', '+919443100019', 21, 'Midfielder', TRUE),
(20, 9, 'Aakash', 'Varun', 23, '22KCT1101', 'aakash.v@kct.ac.in', '+919443100020', 21, 'Point Guard', TRUE),
(21, 9, 'Rohit', 'Sharma', 30, '22KCT1102', 'rohit.s@kct.ac.in', '+919443100021', 20, 'Shooting Guard', TRUE),
(22, 10, 'Siva', 'Kartik', 15, '22SAS1201', 'siva.k@sastra.edu', '+919443100022', 22, 'Center', TRUE),
(23, 11, 'Tarun', 'Tej', 12, '22BIT1301', 'tarun.t@bitsathy.ac.in', '+919443100023', 20, 'Small Forward', TRUE),
(24, 12, 'Vinay', 'Ramesh', 21, '22PSG1401', 'vinay.r@psgtech.edu', '+919443100024', 21, 'Power Forward', TRUE);

-- ----------------------------------------------------------------------------
-- 6. VENUES (6 rows)
-- ----------------------------------------------------------------------------
INSERT INTO venues (id, tournament_id, name, location, field_number, capacity, is_active) VALUES
(1, 1, 'Kovai Sports Arena - Main Ground', 'Coimbatore North Campus', 1, 2500, TRUE),
(2, 1, 'Kovai Sports Arena - Stadium 2', 'Coimbatore North Campus', 2, 1200, TRUE),
(3, 1, 'Periyar Stadium Complex', 'Sathyamangalam Sports Complex', 1, 3000, TRUE),
(4, 2, 'Anna Indoor Basketball Pavilion', 'Guindy Sports Complex', 1, 1500, TRUE),
(5, 2, 'Nehru Indoor Stadium Hall A', 'Periamet Chennai', 1, 4000, TRUE),
(6, 3, 'Meenakshi Turf Grounds', 'Madurai Sports Enclave', 1, 5000, TRUE);

-- ----------------------------------------------------------------------------
-- 7. FIXTURES (20 fixtures across tournament rounds)
-- ----------------------------------------------------------------------------
INSERT INTO fixtures (id, tournament_id, round_number, home_team_id, away_team_id, venue_id, referee_id, scheduled_time, status) VALUES
(1, 1, 1, 1, 2, 1, 17, '2026-10-02 09:00:00', 'COMPLETED'),
(2, 1, 1, 3, 4, 2, 18, '2026-10-02 11:30:00', 'COMPLETED'),
(3, 1, 1, 5, 6, 1, 19, '2026-10-02 14:30:00', 'COMPLETED'),
(4, 1, 1, 7, 8, 3, 20, '2026-10-02 16:30:00', 'COMPLETED'),
(5, 1, 2, 1, 3, 1, 17, '2026-10-04 09:00:00', 'COMPLETED'),
(6, 1, 2, 2, 5, 2, 18, '2026-10-04 11:30:00', 'COMPLETED'),
(7, 1, 2, 4, 7, 1, 19, '2026-10-04 14:30:00', 'COMPLETED'),
(8, 1, 2, 6, 8, 3, 20, '2026-10-04 16:30:00', 'COMPLETED'),
(9, 1, 3, 1, 4, 1, 17, '2026-10-05 09:00:00', 'IN_PROGRESS'),
(10, 1, 3, 2, 6, 2, 18, '2026-10-05 11:30:00', 'SCHEDULED'),
(11, 1, 3, 3, 8, 1, 19, '2026-10-05 14:30:00', 'SCHEDULED'),
(12, 1, 3, 5, 7, 3, 20, '2026-10-05 16:30:00', 'SCHEDULED'),
(13, 1, 4, 1, 5, 1, 17, '2026-10-07 09:00:00', 'SCHEDULED'),
(14, 1, 4, 2, 7, 2, 18, '2026-10-07 11:30:00', 'SCHEDULED'),
(15, 1, 4, 3, 6, 1, 19, '2026-10-07 14:30:00', 'SCHEDULED'),
(16, 1, 4, 4, 8, 3, 20, '2026-10-07 16:30:00', 'SCHEDULED'),
(17, 2, 1, 9, 10, 4, 18, '2026-10-03 10:00:00', 'COMPLETED'),
(18, 2, 1, 11, 12, 4, 19, '2026-10-03 14:00:00', 'COMPLETED'),
(19, 2, 1, 13, 14, 5, 20, '2026-10-03 16:30:00', 'COMPLETED'),
(20, 2, 2, 9, 11, 5, 17, '2026-10-06 15:00:00', 'SCHEDULED');

-- ----------------------------------------------------------------------------
-- 8. MATCHES (20 matches corresponding to fixtures)
-- ----------------------------------------------------------------------------
INSERT INTO matches (id, fixture_id, status, home_score, away_score, current_minute, started_at, finished_at, winner_team_id, referee_id, notes) VALUES
(1, 1, 'COMPLETED', 2, 1, 90, '2026-10-02 09:00:00', '2026-10-02 10:55:00', 1, 17, 'Thrilling opener. BIT scored winning goal in 82nd minute.'),
(2, 2, 'COMPLETED', 1, 1, 90, '2026-10-02 11:30:00', '2026-10-02 13:20:00', NULL, 18, 'Hard fought draw between KEC and CIT in mid-day sun.'),
(3, 3, 'COMPLETED', 3, 0, 90, '2026-10-02 14:30:00', '2026-10-02 16:20:00', 5, 19, 'SSN dominated midfield control against CEG.'),
(4, 4, 'COMPLETED', 0, 2, 90, '2026-10-02 16:30:00', '2026-10-02 18:25:00', 8, 20, 'TCE secured away victory with disciplined counter attacks.'),
(5, 5, 'COMPLETED', 3, 1, 90, '2026-10-04 09:00:00', '2026-10-04 10:50:00', 1, 17, 'Rahul Prasanna scored brace for BIT.'),
(6, 6, 'COMPLETED', 2, 2, 90, '2026-10-04 11:30:00', '2026-10-04 13:25:00', NULL, 18, 'Late equalizer by PSG kept the match level at 2-2.'),
(7, 7, 'COMPLETED', 1, 0, 90, '2026-10-04 14:30:00', '2026-10-04 16:20:00', 4, 19, 'Single goal penalty decisive for CIT Dynamos.'),
(8, 8, 'COMPLETED', 1, 2, 90, '2026-10-04 16:30:00', '2026-10-04 18:25:00', 8, 20, 'TCE Lions continued their winning run.'),
(9, 9, 'LIVE', 2, 1, 72, '2026-10-05 09:00:00', NULL, NULL, 17, 'Match in second half. High tempo with BIT leading 2-1.'),
(10, 10, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 18, 'Awaiting kick-off at Field 2.'),
(11, 11, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 19, 'Scheduled afternoon match.'),
(12, 12, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 20, 'Scheduled late afternoon match.'),
(13, 13, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 17, 'Round 4 clash.'),
(14, 14, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 18, 'Round 4 clash.'),
(15, 15, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 19, 'Round 4 clash.'),
(16, 16, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 20, 'Round 4 clash.'),
(17, 17, 'COMPLETED', 68, 62, 40, '2026-10-03 10:00:00', '2026-10-03 11:45:00', 9, 18, 'KCT Hawks advanced with superior perimeter shooting.'),
(18, 18, 'COMPLETED', 54, 58, 40, '2026-10-03 14:00:00', '2026-10-03 15:40:00', 12, 19, 'PSG Cagers clinched victory in final seconds.'),
(19, 19, 'COMPLETED', 71, 65, 40, '2026-10-03 16:30:00', '2026-10-03 18:15:00', 13, 20, 'SSN Knights held off CEG rally in Q4.'),
(20, 20, 'UPCOMING', 0, 0, 0, NULL, NULL, NULL, 17, 'Quarter-final basketball matchup.');

-- ----------------------------------------------------------------------------
-- 9. SCORE_EVENTS (25 real chronological events)
-- ----------------------------------------------------------------------------
INSERT INTO score_events (id, match_id, team_id, player_id, event_type, minute_offset, points_awarded, description) VALUES
-- Match 1 events (BIT 2 - 1 PSG)
(1, 1, 1, 1, 'GOAL', 18, 1, 'Rahul opened the scoring with a clinical finish from 12 yards.'),
(2, 1, 2, 5, 'GOAL', 54, 1, 'Manoj equalized for PSG with a header from corner kick.'),
(3, 1, 1, 2, 'GOAL', 82, 1, 'Dinesh struck a long range curl into the top corner.'),
(4, 1, 2, 7, 'YELLOW_CARD', 74, 0, 'Tactical foul pulling jersey.'),
-- Match 2 events (KEC 1 - 1 CIT)
(5, 2, 3, 9, 'GOAL', 32, 1, 'Gowtham scored on counter-attack.'),
(6, 2, 4, 12, 'GOAL', 68, 1, 'Naveen converted penalty kick after handball.'),
-- Match 3 events (SSN 3 - 0 CEG)
(7, 3, 5, 14, 'GOAL', 12, 1, 'Siddharth scored inside penalty box.'),
(8, 3, 5, 14, 'GOAL', 44, 1, 'Siddharth scored brace before half time whistle.'),
(9, 3, 5, 15, 'GOAL', 79, 1, 'Pranav scored from set-piece corner.'),
-- Match 4 events (GCT 0 - 2 TCE)
(10, 4, 8, 19, 'GOAL', 25, 1, 'Madhavan curled free-kick past goalkeeper.'),
(11, 4, 8, 19, 'GOAL', 64, 1, 'Madhavan scored penalty kick.'),
(12, 4, 7, 18, 'YELLOW_CARD', 85, 0, 'Late sliding tackle.'),
-- Match 5 events (BIT 3 - 1 KEC)
(13, 5, 1, 1, 'GOAL', 14, 1, 'Rahul tap-in after cross from Dinesh.'),
(14, 5, 3, 9, 'GOAL', 38, 1, 'Gowtham volleyed from edge of the box.'),
(15, 5, 1, 1, 'GOAL', 61, 1, 'Rahul scored second goal of the match.'),
(16, 5, 1, 2, 'GOAL', 88, 1, 'Dinesh finished solo run on right wing.'),
-- Match 6 events (PSG 2 - 2 SSN)
(17, 6, 2, 5, 'GOAL', 22, 1, 'Manoj header from cross.'),
(18, 6, 5, 14, 'GOAL', 31, 1, 'Siddharth volley equalizer.'),
(19, 6, 5, 14, 'GOAL', 59, 1, 'Siddharth penalty to take lead.'),
(20, 6, 2, 6, 'GOAL', 87, 1, 'Surya deflected strike to tie the game.'),
-- Match 7 events (CIT 1 - 0 GCT)
(21, 7, 4, 12, 'GOAL', 52, 1, 'Naveen scored from deflection.'),
-- Match 8 events (CEG 1 - 2 TCE)
(22, 8, 6, 17, 'GOAL', 16, 1, 'Vishal early goal for CEG.'),
(23, 8, 8, 19, 'GOAL', 42, 1, 'Madhavan equalizer.'),
(24, 8, 8, 19, 'GOAL', 77, 1, 'Madhavan completed comeback with powerful header.'),
-- Match 9 Live events (BIT 2 - 1 CIT)
(25, 9, 1, 1, 'GOAL', 28, 1, 'Rahul opened score in live game.');

-- ----------------------------------------------------------------------------
-- 10. STANDINGS (8 rows for Tournament 1)
-- ----------------------------------------------------------------------------
INSERT INTO standings (id, tournament_id, team_id, played, won, drawn, lost, points, goals_for, goals_against, goal_difference, fair_play_points, ranking) VALUES
(1, 1, 1, 2, 2, 0, 0, 6, 5, 2, 3, 98, 1),
(2, 1, 8, 2, 2, 0, 0, 6, 4, 1, 3, 96, 2),
(3, 1, 5, 2, 1, 1, 0, 4, 5, 2, 3, 94, 3),
(4, 1, 4, 2, 1, 1, 0, 4, 2, 1, 1, 95, 4),
(5, 1, 2, 2, 0, 1, 1, 1, 3, 4, -1, 92, 5),
(6, 1, 3, 2, 0, 1, 1, 1, 2, 4, -2, 95, 6),
(7, 1, 6, 2, 0, 0, 2, 0, 1, 5, -4, 97, 7),
(8, 1, 7, 2, 0, 0, 2, 0, 0, 3, -3, 90, 8);

-- ----------------------------------------------------------------------------
-- 11. ANNOUNCEMENTS (5 rows)
-- ----------------------------------------------------------------------------
INSERT INTO announcements (id, tournament_id, title, content, author_id, is_pinned) VALUES
(1, 1, 'Welcome to All India Inter-Collegiate Football Cup 2026', 'All teams are requested to verify their squads and submit jersey allocations to the organizing desk by 8:00 AM daily.', 3, TRUE),
(2, 1, 'Weather Update - Pitch 1 & 2', 'Due to clear weather forecasts, all fixtures will proceed as per original schedule.', 3, FALSE),
(3, 1, 'Live Streaming Link Enabled', 'Live match timelines and real-time pulse scoring are now active on SportX portal.', 3, TRUE),
(4, 2, 'Basketball Semi-Finals Schedule Finalized', 'The top 4 advancing institutions will play on October 8th under floodlights at Nehru Indoor Stadium.', 4, TRUE),
(5, 1, 'Fair Play Points Review Post Round 2', 'A reminder to all team managers that disciplinary cards will impact the tie-breaker standings.', 3, FALSE);

-- ----------------------------------------------------------------------------
-- 12. NOTIFICATIONS (6 rows)
-- ----------------------------------------------------------------------------
INSERT INTO notifications (id, user_id, title, message, notification_type, is_read) VALUES
(1, 7, 'Match 9 Started', 'Your team BIT Sparks is currently playing against CIT Dynamos at Kovai Main Ground.', 'MATCH_UPDATE', FALSE),
(2, 8, 'Post-Match Report Available', 'Official referee match card for Match 6 (PSG vs SSN) has been confirmed.', 'STANDINGS_UPDATE', TRUE),
(3, 17, 'Referee Assignment', 'You have been assigned to referee Match 9 (BIT vs CIT) scheduled for 9:00 AM.', 'REFEREE_DUTY', TRUE),
(4, 18, 'Referee Assignment', 'You have been assigned to referee Match 10 (PSG vs CEG) scheduled for 11:30 AM.', 'REFEREE_DUTY', FALSE),
(5, 3, 'Standings Automatically Updated', 'Round 2 standings recalculated successfully with tie-breaker criteria applied.', 'SYSTEM', TRUE),
(6, 11, 'Fair Play Notice', 'SSN Titans has accumulated 94 Fair Play points after Round 2.', 'ALERT', FALSE);

-- ----------------------------------------------------------------------------
-- 13. REFEREE ASSIGNMENTS (8 rows)
-- ----------------------------------------------------------------------------
INSERT INTO referee_assignments (id, match_id, referee_id, assigned_at, status, notes) VALUES
(1, 1, 17, '2026-10-01 10:00:00', 'COMPLETED', 'Handled Round 1 opener without disputes.'),
(2, 2, 18, '2026-10-01 10:00:00', 'COMPLETED', 'Fair match conducted.'),
(3, 3, 19, '2026-10-01 10:00:00', 'COMPLETED', 'Accurate decision making on offsides.'),
(4, 4, 20, '2026-10-01 10:00:00', 'COMPLETED', 'Disciplinary cards issued properly.'),
(5, 5, 17, '2026-10-03 10:00:00', 'COMPLETED', 'Round 2 match concluded.'),
(6, 6, 18, '2026-10-03 10:00:00', 'COMPLETED', 'Good advantage play applied.'),
(7, 9, 17, '2026-10-04 18:00:00', 'ACCEPTED', 'Currently officiating live match.'),
(8, 10, 18, '2026-10-04 18:00:00', 'ACCEPTED', 'Ready for upcoming match.');
