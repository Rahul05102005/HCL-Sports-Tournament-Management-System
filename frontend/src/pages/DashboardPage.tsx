import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { fetchTournamentPulse, fetchTournaments } from '../store/tournamentSlice';
import { fetchTournamentMatches, fetchLiveMatches, addScoreEvent, finalizeMatch } from '../store/matchSlice';
import { Trophy, Calendar, Users, Radio, CheckCircle, Flame, ArrowUpRight, Zap, Play, Bell } from 'lucide-react';
import { Match, Player } from '../types';
import { ScoreModal } from '../components/ScoreModal';
import { FinalizeModal } from '../components/FinalizeModal';
import api from '../api/client';

export const DashboardPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { currentTournament, pulse, isLoading: tourneyLoading } = useSelector((state: RootState) => state.tournaments);
  const { liveMatches, matches } = useSelector((state: RootState) => state.matches);
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);

  const [activeScoreMatch, setActiveScoreMatch] = useState<Match | null>(null);
  const [activeFinalizeMatch, setActiveFinalizeMatch] = useState<Match | null>(null);
  const [matchPlayers, setMatchPlayers] = useState<Player[]>([]);
  const [generatingFixtures, setGeneratingFixtures] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  useEffect(() => {
    dispatch(fetchTournaments());
    dispatch(fetchLiveMatches());
  }, [dispatch]);

  useEffect(() => {
    if (currentTournament) {
      dispatch(fetchTournamentPulse(currentTournament.id));
      dispatch(fetchTournamentMatches(currentTournament.id));
    }
  }, [currentTournament, dispatch]);

  const handleOpenScoreModal = async (match: Match) => {
    setActiveScoreMatch(match);
    try {
      const [resHome, resAway] = await Promise.all([
        api.get(`/teams/${match.homeTeamId}/players`),
        api.get(`/teams/${match.awayTeamId}/players`),
      ]);
      setMatchPlayers([...(resHome.data.data || []), ...(resAway.data.data || [])]);
    } catch (err) {
      setMatchPlayers([]);
    }
  };

  const handleGenerateFixtures = async () => {
    if (!currentTournament) return;
    try {
      setGeneratingFixtures(true);
      setStatusMessage(null);
      await api.post(`/tournaments/${currentTournament.id}/fixtures/generate`);
      dispatch(fetchTournamentPulse(currentTournament.id));
      dispatch(fetchTournamentMatches(currentTournament.id));
      setStatusMessage('Smart Round-Robin fixtures generated successfully with clash buffers!');
    } catch (err: any) {
      setStatusMessage(err.response?.data?.message || 'Failed to generate fixtures');
    } finally {
      setGeneratingFixtures(false);
    }
  };

  const isRefereeOrAdmin =
    isAuthenticated && (user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_ORGANIZER' || user?.role === 'ROLE_REFEREE');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Status banner */}
      {statusMessage && (
        <div className="p-4 rounded-xl bg-brand-500/10 border border-brand-500/30 text-brand-300 text-sm flex items-center justify-between">
          <span>{statusMessage}</span>
          <button onClick={() => setStatusMessage(null)} className="text-brand-400 hover:text-white font-bold ml-4">
            ✕
          </button>
        </div>
      )}

      {/* Hero Tournament Overview */}
      <div className="relative overflow-hidden rounded-3xl p-8 glass-panel-glow border border-slate-700/80 bg-gradient-to-br from-slate-900 via-slate-900 to-[#0c1a2e]">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold tracking-wider uppercase bg-brand-500/20 text-brand-400 border border-brand-500/30">
                {currentTournament?.sportName || 'Collegiate Tournament'}
              </span>
              <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold tracking-wider uppercase bg-sky-500/20 text-sky-400 border border-sky-500/30">
                {currentTournament?.format?.replace('_', ' ') || 'Round Robin'}
              </span>
              <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold tracking-wider uppercase bg-amber-500/20 text-amber-400 border border-amber-500/30">
                {currentTournament?.status || 'UPCOMING'}
              </span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
              {currentTournament?.name || 'Tournament Hub'}
            </h1>
            <p className="text-sm text-slate-300 max-w-2xl leading-relaxed">
              {currentTournament?.description ||
                'Real-time tournament tracking with conflict-free smart scheduling, live scoring, and automatic standings.'}
            </p>
          </div>

          {/* Action Button for Organizer / Admin */}
          {isRefereeOrAdmin && (
            <div className="flex items-center gap-3">
              <button
                onClick={handleGenerateFixtures}
                disabled={generatingFixtures}
                className="flex items-center gap-2 px-5 py-3 rounded-xl font-bold text-sm bg-gradient-to-r from-brand-500 to-emerald-600 hover:from-brand-600 hover:to-emerald-700 text-white shadow-lg shadow-brand-500/25 transition-all transform hover:-translate-y-0.5 active:translate-y-0 disabled:opacity-50"
              >
                <Zap className="w-4 h-4" />
                {generatingFixtures ? 'Generating...' : 'Auto-Generate Fixtures'}
              </button>
            </div>
          )}
        </div>

        {/* Pulse Stat Cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-8 pt-8 border-t border-slate-800">
          <div className="bg-slate-900/60 p-4 rounded-2xl border border-slate-800">
            <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase">
              <span>Teams Enrolled</span>
              <Users className="w-4 h-4 text-sky-400" />
            </div>
            <div className="text-2xl font-bold text-white mt-1 font-mono">{pulse?.totalTeams ?? 0}</div>
            <div className="text-[11px] text-slate-500 mt-0.5">Approved squads</div>
          </div>

          <div className="bg-slate-900/60 p-4 rounded-2xl border border-slate-800">
            <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase">
              <span>Total Fixtures</span>
              <Calendar className="w-4 h-4 text-purple-400" />
            </div>
            <div className="text-2xl font-bold text-white mt-1 font-mono">{pulse?.totalFixtures ?? 0}</div>
            <div className="text-[11px] text-slate-500 mt-0.5">Conflict-free scheduled</div>
          </div>

          <div className="bg-slate-900/60 p-4 rounded-2xl border border-slate-800">
            <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase">
              <span>Live Matches</span>
              <Radio className="w-4 h-4 text-red-400 animate-pulse" />
            </div>
            <div className="text-2xl font-bold text-red-400 mt-1 font-mono">{pulse?.liveMatchesCount ?? 0}</div>
            <div className="text-[11px] text-slate-500 mt-0.5">In progress right now</div>
          </div>

          <div className="bg-slate-900/60 p-4 rounded-2xl border border-slate-800">
            <div className="flex items-center justify-between text-slate-400 text-xs font-semibold uppercase">
              <span>Completion</span>
              <CheckCircle className="w-4 h-4 text-brand-400" />
            </div>
            <div className="text-2xl font-bold text-brand-400 mt-1 font-mono">
              {pulse?.completionPercentage ?? 0}%
            </div>
            <div className="text-[11px] text-slate-500 mt-0.5">
              {pulse?.completedMatches ?? 0} / {pulse?.totalFixtures ?? 0} matches
            </div>
          </div>
        </div>
      </div>

      {/* Grid: Live Spotlight + Standings Leader + Announcements */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column (2 spans): Live Match Spotlight or Recent Results */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-white flex items-center gap-2">
              <Flame className="w-5 h-5 text-red-500" />
              Live &amp; Featured Matches
            </h2>
            <span className="text-xs text-slate-400">Real-time score updates</span>
          </div>

          {liveMatches.length > 0 ? (
            <div className="space-y-4">
              {liveMatches.map((match) => (
                <div
                  key={match.id}
                  className="p-6 rounded-2xl bg-gradient-to-r from-slate-900 via-slate-850 to-slate-900 border border-emerald-500/30 shadow-xl relative overflow-hidden"
                >
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center gap-2">
                      <span className="flex h-2.5 w-2.5 relative">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-red-500"></span>
                      </span>
                      <span className="text-xs font-bold font-mono uppercase text-red-400">
                        {match.matchStatus} {match.currentMinute ? `• ${match.currentMinute}'` : ''}
                      </span>
                      <span className="text-xs text-slate-400">• {match.roundName}</span>
                    </div>
                    <span className="text-xs text-slate-400">{match.venueName}</span>
                  </div>

                  <div className="flex items-center justify-between py-4">
                    <div className="flex-1 text-center md:text-left">
                      <h4 className="text-lg font-bold text-white">{match.homeTeamName}</h4>
                      <p className="text-xs text-slate-400">{match.homeCollegeName}</p>
                    </div>

                    <div className="px-6 py-3 rounded-2xl bg-slate-950 border border-slate-800 text-center font-mono">
                      <span className="text-3xl font-extrabold text-brand-400 tracking-wider">
                        {match.homeScore} : {match.awayScore}
                      </span>
                    </div>

                    <div className="flex-1 text-center md:text-right">
                      <h4 className="text-lg font-bold text-white">{match.awayTeamName}</h4>
                      <p className="text-xs text-slate-400">{match.awayCollegeName}</p>
                    </div>
                  </div>

                  {/* Actions for Referee/Admin */}
                  {isRefereeOrAdmin && (
                    <div className="mt-4 pt-4 border-t border-slate-800 flex justify-end gap-3">
                      <button
                        onClick={() => handleOpenScoreModal(match)}
                        className="px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/40 hover:bg-brand-500/30 transition-colors"
                      >
                        + Record Event
                      </button>
                      <button
                        onClick={() => setActiveFinalizeMatch(match)}
                        className="px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-emerald-500 text-white hover:bg-emerald-600 shadow-sm"
                      >
                        Finalize Match
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div className="p-8 rounded-2xl bg-slate-900/40 border border-slate-800 text-center space-y-3">
              <Play className="w-10 h-10 text-slate-600 mx-auto" />
              <h3 className="text-base font-semibold text-slate-300">No matches currently in progress</h3>
              <p className="text-xs text-slate-500">Upcoming matches are scheduled and ready to kick off.</p>
            </div>
          )}

          {/* Upcoming Fixtures List */}
          <div className="space-y-4 pt-4">
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <Calendar className="w-5 h-5 text-sky-400" />
              Upcoming Fixtures
            </h3>
            <div className="space-y-3">
              {matches
                .filter((m) => m.matchStatus === 'SCHEDULED')
                .slice(0, 4)
                .map((m) => (
                  <div
                    key={m.id}
                    className="p-4 rounded-xl bg-slate-900/60 border border-slate-800 flex items-center justify-between hover:border-slate-700 transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div className="text-center font-mono text-xs text-slate-400 bg-slate-950 px-3 py-1.5 rounded-lg border border-slate-800">
                        {new Date(m.scheduledTime).toLocaleDateString([], { month: 'short', day: 'numeric' })}
                        <div className="text-brand-400 font-bold">
                          {new Date(m.scheduledTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </div>
                      </div>
                      <div>
                        <div className="text-sm font-bold text-white flex items-center gap-2">
                          <span>{m.homeTeamName}</span>
                          <span className="text-xs text-slate-500 font-normal">vs</span>
                          <span>{m.awayTeamName}</span>
                        </div>
                        <div className="text-xs text-slate-400">{m.venueName} • {m.roundName}</div>
                      </div>
                    </div>

                    {isRefereeOrAdmin && (
                      <button
                        onClick={() => handleOpenScoreModal(m)}
                        className="px-3 py-1 rounded-lg text-xs font-semibold bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700"
                      >
                        Start Scoring
                      </button>
                    )}
                  </div>
                ))}
            </div>
          </div>
        </div>

        {/* Right Column: Leaderboard preview + Announcements */}
        <div className="space-y-6">
          {/* Tournament Leader Card */}
          <div className="p-6 rounded-2xl glass-panel border border-brand-500/20 relative overflow-hidden bg-gradient-to-b from-slate-900 to-slate-900/90">
            <div className="flex items-center justify-between mb-4">
              <span className="text-xs font-bold uppercase tracking-wider text-brand-400 font-mono flex items-center gap-1.5">
                <Trophy className="w-4 h-4 text-amber-400" />
                Current Leader
              </span>
              <span className="text-xs text-slate-400">Rank #1</span>
            </div>

            {pulse?.leaderTeam ? (
              <div className="space-y-3">
                <h3 className="text-2xl font-black text-white">{pulse.leaderTeam.teamName}</h3>
                <p className="text-xs text-slate-400">{pulse.leaderTeam.collegeName}</p>

                <div className="grid grid-cols-3 gap-2 pt-3 border-t border-slate-800 text-center font-mono">
                  <div className="bg-slate-950 p-2 rounded-lg">
                    <span className="text-[10px] text-slate-400 block">PTS</span>
                    <span className="text-base font-bold text-brand-400">{pulse.leaderTeam.points}</span>
                  </div>
                  <div className="bg-slate-950 p-2 rounded-lg">
                    <span className="text-[10px] text-slate-400 block">GD</span>
                    <span className="text-base font-bold text-white">
                      {pulse.leaderTeam.goalDifference > 0 ? `+${pulse.leaderTeam.goalDifference}` : pulse.leaderTeam.goalDifference}
                    </span>
                  </div>
                  <div className="bg-slate-950 p-2 rounded-lg">
                    <span className="text-[10px] text-slate-400 block">FORM</span>
                    <span className="text-xs font-bold text-emerald-400">{pulse.leaderTeam.formGuide || 'W-W'}</span>
                  </div>
                </div>
              </div>
            ) : (
              <p className="text-xs text-slate-400">Leaderboard will appear after matches are finalized.</p>
            )}
          </div>

          {/* Announcements Card */}
          <div className="p-6 rounded-2xl glass-panel border border-slate-800 space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
                <Bell className="w-4 h-4 text-amber-400" />
                Tournament Alerts
              </h3>
            </div>

            <div className="space-y-3">
              {pulse?.recentAnnouncements && pulse.recentAnnouncements.length > 0 ? (
                pulse.recentAnnouncements.map((ann, idx) => (
                  <div key={idx} className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 text-xs text-slate-300 leading-relaxed">
                    {ann}
                  </div>
                ))
              ) : (
                <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 text-xs text-slate-400">
                  No active announcements for this tournament.
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      {activeScoreMatch && (
        <ScoreModal
          match={activeScoreMatch}
          players={matchPlayers}
          isOpen={!!activeScoreMatch}
          onClose={() => setActiveScoreMatch(null)}
          onSubmit={(data) => {
            dispatch(addScoreEvent({ matchId: activeScoreMatch.id, data }));
            if (currentTournament) {
              dispatch(fetchTournamentPulse(currentTournament.id));
              dispatch(fetchTournamentMatches(currentTournament.id));
            }
          }}
        />
      )}

      {activeFinalizeMatch && (
        <FinalizeModal
          match={activeFinalizeMatch}
          isOpen={!!activeFinalizeMatch}
          onClose={() => setActiveFinalizeMatch(null)}
          onSubmit={(data) => {
            dispatch(finalizeMatch({ matchId: activeFinalizeMatch.id, data }));
            if (currentTournament) {
              dispatch(fetchTournamentPulse(currentTournament.id));
              dispatch(fetchTournamentMatches(currentTournament.id));
            }
          }}
        />
      )}
    </div>
  );
};
