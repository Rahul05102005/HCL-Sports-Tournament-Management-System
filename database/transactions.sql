-- ============================================================================
-- SportX - Smart Sports Tournament Management Platform
-- ACID Multi-Table Transaction Demonstration
-- Scenario: Finalizing Match #9 (BIT 2 - 1 CIT)
-- Flow:
--   1. Update Match status to 'COMPLETED' with final score & timestamp
--   2. Update Fixture status to 'COMPLETED'
--   3. Insert final match winning Score Event
--   4. Update Standings for Winner (BIT Sparks: +3 pts, +1 won, +2 GF, +1 GA, +1 GD)
--   5. Update Standings for Loser (CIT Dynamos: +0 pts, +1 lost, +1 GF, +2 GA, -1 GD)
--   6. Re-calculate tournament rank order
-- ============================================================================

USE sportx_db;

-- ----------------------------------------------------------------------------
-- SUCCESSFUL TRANSACTION (COMMIT SCENARIO)
-- Isolation Level: REPEATABLE READ (Default for InnoDB)
-- ----------------------------------------------------------------------------
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

START TRANSACTION;

-- Step 1: Update Match #9 to COMPLETED with winner BIT Sparks (id: 1)
UPDATE matches 
SET 
    status = 'COMPLETED',
    home_score = 2,
    away_score = 1,
    current_minute = 90,
    finished_at = '2026-10-05 10:55:00',
    winner_team_id = 1,
    notes = 'Match completed successfully. BIT secured 3 points.'
WHERE id = 9 AND status = 'LIVE';

-- Step 2: Update corresponding Fixture #9 status
UPDATE fixtures 
SET status = 'COMPLETED' 
WHERE id = 9;

-- Step 3: Insert final stoppage-time event
INSERT INTO score_events (match_id, team_id, player_id, event_type, minute_offset, points_awarded, description)
VALUES (9, 1, 2, 'GOAL', 89, 1, 'Dinesh sealed victory in the 89th minute.');

-- Step 4: Atomic Update of Standings for Home Team (BIT Sparks - Won)
UPDATE standings 
SET 
    played = played + 1,
    won = won + 1,
    points = points + 3,
    goals_for = goals_for + 2,
    goals_against = goals_against + 1,
    goal_difference = (goals_for + 2) - (goals_against + 1)
WHERE tournament_id = 1 AND team_id = 1;

-- Step 5: Atomic Update of Standings for Away Team (CIT Dynamos - Lost)
UPDATE standings 
SET 
    played = played + 1,
    lost = lost + 1,
    goals_for = goals_for + 1,
    goals_against = goals_against + 2,
    goal_difference = (goals_for + 1) - (goals_against + 2)
WHERE tournament_id = 1 AND team_id = 4;

-- Step 6: Commit all changes atomically
COMMIT;


-- ----------------------------------------------------------------------------
-- FAILURE & ROLLBACK DEMONSTRATION (SIMULATED BUSINESS EXCEPTION)
-- Scenario: Attempting to finalize an already completed match or invalid team ID
-- ----------------------------------------------------------------------------
START TRANSACTION;

-- Step 1: Attempt update
UPDATE matches 
SET status = 'COMPLETED' 
WHERE id = 9;

-- Step 2: Intentional constraint violation or business rule check
-- Example: Verifying match hasn't already been processed or scores are valid
-- If an anomaly is detected, rollback immediately:
ROLLBACK;
-- At this point, neither Match nor Standings changes are persisted.
