import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { fetchTournamentMatches, addScoreEvent, finalizeMatch } from '../store/matchSlice';
import { Match, Player, ScoreEvent } from '../types';
import { ScoreModal } from '../components/ScoreModal';
import { FinalizeModal } from '../components/FinalizeModal';
import api from '../api/client';
import { Calendar, Filter, Clock, MapPin, User, CheckCircle2, ChevronDown, ChevronUp } from 'lucide-react';

export const MatchesPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { currentTournament } = useSelector((state: RootState) => state.tournaments);
  const { matches } = useSelector((state: RootState) => state.matches);
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);

  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [activeScoreMatch, setActiveScoreMatch] = useState<Match | null>(null);
  const [activeFinalizeMatch, setActiveFinalizeMatch] = useState<Match | null>(null);
  const [matchPlayers, setMatchPlayers] = useState<Player[]>([]);
  const [expandedMatchId, setExpandedMatchId] = useState<number | null>(null);
  const [matchEvents, setMatchEvents] = useState<Record<number, ScoreEvent[]>>({});

  useEffect(() => {
    if (currentTournament) {
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

  const toggleExpandMatch = async (matchId: number) => {
    if (expandedMatchId === matchId) {
      setExpandedMatchId(null);
    } else {
      setExpandedMatchId(matchId);
      if (!matchEvents[matchId]) {
        try {
          const res = await api.get(`/matches/${matchId}/score-events`);
          setMatchEvents((prev) => ({ ...prev, [matchId]: res.data.data || [] }));
        } catch (err) {
          // ignore
        }
      }
    }
  };

  const filteredMatches = matches.filter((m) => {
    if (statusFilter === 'ALL') return true;
    return m.matchStatus === statusFilter;
  });

  const isRefereeOrAdmin =
    isAuthenticated && (user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_ORGANIZER' || user?.role === 'ROLE_REFEREE');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header and Filter */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Calendar className="w-6 h-6 text-brand-400" />
            Fixtures &amp; Match Center
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            {currentTournament ? currentTournament.name : 'Select a tournament to view schedule'}
          </p>
        </div>

        {/* Filter Pills */}
        <div className="flex items-center gap-1.5 bg-slate-900/90 p-1.5 rounded-xl border border-slate-800 text-xs">
          {['ALL', 'LIVE', 'SCHEDULED', 'COMPLETED'].map((status) => (
            <button
              key={status}
              onClick={() => setStatusFilter(status)}
              className={`px-3 py-1.5 rounded-lg font-semibold transition-all ${
                statusFilter === status
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
              }`}
            >
              {status}
            </button>
          ))}
        </div>
      </div>

      {/* Match Cards List */}
      <div className="space-y-4">
        {filteredMatches.length > 0 ? (
          filteredMatches.map((m) => {
            const isLive = m.matchStatus === 'LIVE';
            const isCompleted = m.matchStatus === 'COMPLETED';
            const isExpanded = expandedMatchId === m.id;
            const events = matchEvents[m.id] || [];

            return (
              <div
                key={m.id}
                className={`rounded-2xl border transition-all ${
                  isLive
                    ? 'bg-slate-900/90 border-emerald-500/40 shadow-lg shadow-emerald-500/5'
                    : 'bg-slate-900/60 border-slate-800 hover:border-slate-700'
                }`}
              >
                <div className="p-6">
                  {/* Top Bar */}
                  <div className="flex items-center justify-between text-xs text-slate-400 mb-4">
                    <div className="flex items-center gap-2 font-mono">
                      <span className="font-bold text-slate-300">{m.roundName}</span>
                      <span>•</span>
                      <span className="flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-slate-500" />
                        {new Date(m.scheduledTime).toLocaleString([], {
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </span>
                    </div>

                    <div className="flex items-center gap-2">
                      {isLive && (
                        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold bg-red-500/20 text-red-400 border border-red-500/30 animate-pulse">
                          LIVE {m.currentMinute ? `${m.currentMinute}'` : ''}
                        </span>
                      )}
                      {isCompleted && (
                        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold bg-slate-800 text-slate-300 border border-slate-700">
                          FINAL
                        </span>
                      )}
                      {!isLive && !isCompleted && (
                        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold bg-sky-500/20 text-sky-400 border border-sky-500/30">
                          SCHEDULED
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Main Match Scoreboard */}
                  <div className="grid grid-cols-1 md:grid-cols-7 items-center gap-4 py-2">
                    {/* Home Team */}
                    <div className="md:col-span-3 text-center md:text-right">
                      <h3 className="text-lg font-bold text-white tracking-tight">{m.homeTeamName}</h3>
                      <p className="text-xs text-slate-400">{m.homeCollegeName}</p>
                    </div>

                    {/* Score Center */}
                    <div className="md:col-span-1 text-center font-mono">
                      <div className="inline-block px-5 py-2 rounded-xl bg-slate-950 border border-slate-800">
                        <span className="text-2xl font-black text-brand-400">
                          {m.matchStatus === 'SCHEDULED' ? 'VS' : `${m.homeScore} : ${m.awayScore}`}
                        </span>
                      </div>
                    </div>

                    {/* Away Team */}
                    <div className="md:col-span-3 text-center md:text-left">
                      <h3 className="text-lg font-bold text-white tracking-tight">{m.awayTeamName}</h3>
                      <p className="text-xs text-slate-400">{m.awayCollegeName}</p>
                    </div>
                  </div>

                  {/* Footer Info: Venue & Referee & Actions */}
                  <div className="mt-4 pt-4 border-t border-slate-800/80 flex flex-wrap items-center justify-between gap-3 text-xs text-slate-400">
                    <div className="flex items-center gap-4">
                      <span className="flex items-center gap-1">
                        <MapPin className="w-3.5 h-3.5 text-slate-500" />
                        {m.venueName}
                      </span>
                      <span className="flex items-center gap-1">
                        <User className="w-3.5 h-3.5 text-slate-500" />
                        Referee: {m.refereeName}
                      </span>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => toggleExpandMatch(m.id)}
                        className="px-2.5 py-1 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 flex items-center gap-1 transition-colors"
                      >
                        Timeline {isExpanded ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
                      </button>

                      {isRefereeOrAdmin && (
                        <>
                          <button
                            onClick={() => handleOpenScoreModal(m)}
                            className="px-3 py-1 rounded-lg font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/40 hover:bg-brand-500/30 transition-colors"
                          >
                            + Event
                          </button>
                          {!isCompleted && (
                            <button
                              onClick={() => setActiveFinalizeMatch(m)}
                              className="px-3 py-1 rounded-lg font-semibold bg-emerald-500 text-white hover:bg-emerald-600 transition-colors shadow-sm"
                            >
                              Finalize
                            </button>
                          )}
                        </>
                      )}
                    </div>
                  </div>

                  {/* Expandable Match Timeline */}
                  {isExpanded && (
                    <div className="mt-4 pt-4 border-t border-slate-800 space-y-2">
                      <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider">Match Events Timeline</h4>
                      {events.length > 0 ? (
                        <div className="space-y-1.5">
                          {events.map((ev) => (
                            <div
                              key={ev.id}
                              className="flex items-center gap-3 text-xs p-2 rounded-lg bg-slate-950/60 border border-slate-800/60"
                            >
                              <span className="font-mono font-bold text-brand-400 w-8 text-right">
                                {ev.minuteOccurred}'
                              </span>
                              <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-slate-800 text-slate-300">
                                {ev.eventType}
                              </span>
                              <span className="font-semibold text-white">{ev.teamName}</span>
                              {ev.playerName && <span className="text-slate-400">({ev.playerName})</span>}
                              {ev.extraInfo && <span className="text-slate-500 italic">— {ev.extraInfo}</span>}
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-xs text-slate-500 italic">No score events logged for this fixture yet.</p>
                      )}
                    </div>
                  )}
                </div>
              </div>
            );
          })
        ) : (
          <div className="p-12 text-center rounded-2xl bg-slate-900/40 border border-slate-800 space-y-2">
            <Calendar className="w-10 h-10 text-slate-600 mx-auto" />
            <h3 className="text-base font-semibold text-slate-300">No matches found for filter: {statusFilter}</h3>
            <p className="text-xs text-slate-500">Generate or schedule fixtures from the Dashboard.</p>
          </div>
        )}
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
            if (currentTournament) dispatch(fetchTournamentMatches(currentTournament.id));
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
            if (currentTournament) dispatch(fetchTournamentMatches(currentTournament.id));
          }}
        />
      )}
    </div>
  );
};
