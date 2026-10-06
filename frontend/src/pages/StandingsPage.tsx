import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { Standing } from '../types';
import api from '../api/client';
import { Trophy, Shield, RefreshCw } from 'lucide-react';

export const StandingsPage: React.FC = () => {
  const { currentTournament } = useSelector((state: RootState) => state.tournaments);
  const [standings, setStandings] = useState<Standing[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchStandings = async () => {
    if (!currentTournament) return;
    try {
      setIsLoading(true);
      const res = await api.get(`/standings/tournament/${currentTournament.id}`);
      setStandings(res.data.data || []);
    } catch (err) {
      setStandings([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchStandings();
  }, [currentTournament]);

  const renderFormBadge = (char: string, idx: number) => {
    let color = 'bg-slate-700 text-slate-300';
    if (char === 'W') color = 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40';
    if (char === 'D') color = 'bg-amber-500/20 text-amber-400 border border-amber-500/40';
    if (char === 'L') color = 'bg-red-500/20 text-red-400 border border-red-500/40';

    return (
      <span key={idx} className={`w-5 h-5 rounded flex items-center justify-center text-[10px] font-mono font-bold ${color}`}>
        {char}
      </span>
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Trophy className="w-6 h-6 text-amber-400" />
            Tournament Standings &amp; Points Table
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            {currentTournament ? `${currentTournament.name} • ${currentTournament.sportName}` : 'Standings overview'}
          </p>
        </div>

        <button
          onClick={fetchStandings}
          disabled={isLoading}
          className="flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition-colors w-fit"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} />
          Refresh Standings
        </button>
      </div>

      {/* Standings Table Card */}
      <div className="rounded-2xl glass-panel border border-slate-800 overflow-hidden shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-900/90 text-slate-400 font-mono text-[11px] uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="py-4 px-4 text-center w-12">#</th>
                <th className="py-4 px-4">Club / College</th>
                <th className="py-4 px-3 text-center">PL</th>
                <th className="py-4 px-3 text-center">W</th>
                <th className="py-4 px-3 text-center">D</th>
                <th className="py-4 px-3 text-center">L</th>
                <th className="py-4 px-3 text-center">GF</th>
                <th className="py-4 px-3 text-center">GA</th>
                <th className="py-4 px-3 text-center font-bold">GD</th>
                <th className="py-4 px-4 text-center font-bold text-brand-400">PTS</th>
                <th className="py-4 px-4 text-center">Recent Form</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 font-medium">
              {standings.length > 0 ? (
                standings.map((s, idx) => {
                  const isTop4 = idx < 4;
                  return (
                    <tr
                      key={s.id}
                      className={`hover:bg-slate-800/40 transition-colors ${
                        idx === 0 ? 'bg-brand-500/5' : ''
                      }`}
                    >
                      <td className="py-4 px-4 text-center font-mono font-bold">
                        <span
                          className={`inline-flex items-center justify-center w-6 h-6 rounded-full text-xs ${
                            idx === 0
                              ? 'bg-amber-500/20 text-amber-300 border border-amber-500/40'
                              : isTop4
                              ? 'bg-sky-500/10 text-sky-400'
                              : 'text-slate-400'
                          }`}
                        >
                          {s.rank}
                        </span>
                      </td>

                      <td className="py-4 px-4">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-lg bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-brand-400 text-xs">
                            {s.shortName || s.teamName.substring(0, 3)}
                          </div>
                          <div>
                            <div className="text-sm font-bold text-white">{s.teamName}</div>
                            <div className="text-[11px] text-slate-400">{s.collegeName}</div>
                          </div>
                        </div>
                      </td>

                      <td className="py-4 px-3 text-center font-mono text-slate-300">{s.played}</td>
                      <td className="py-4 px-3 text-center font-mono text-emerald-400">{s.won}</td>
                      <td className="py-4 px-3 text-center font-mono text-slate-400">{s.drawn}</td>
                      <td className="py-4 px-3 text-center font-mono text-red-400">{s.lost}</td>
                      <td className="py-4 px-3 text-center font-mono text-slate-400">{s.scoreFor}</td>
                      <td className="py-4 px-3 text-center font-mono text-slate-400">{s.scoreAgainst}</td>
                      <td className="py-4 px-3 text-center font-mono font-bold text-white">
                        {s.goalDifference > 0 ? `+${s.goalDifference}` : s.goalDifference}
                      </td>
                      <td className="py-4 px-4 text-center font-mono text-base font-extrabold text-brand-400 bg-slate-900/40">
                        {s.points}
                      </td>
                      <td className="py-4 px-4 text-center">
                        <div className="flex items-center justify-center gap-1">
                          {s.formGuide
                            ? s.formGuide.split('').map((char, i) => renderFormBadge(char, i))
                            : <span className="text-slate-500 font-mono text-xs">-</span>}
                        </div>
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan={11} className="py-12 text-center text-slate-500">
                    No standings calculated yet. Finalize matches to update the points table automatically.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Legend */}
        <div className="p-4 bg-slate-950/60 border-t border-slate-800 text-xs text-slate-400 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-400"></span>
              <span>Tournament Leader</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-sky-400"></span>
              <span>Knockout Playoff Qualification (Top 4)</span>
            </div>
          </div>
          <div className="font-mono text-[11px] text-slate-500">
            Rules: Win = 3 Pts, Draw = 1 Pt, Loss = 0 Pts • Tie-breaker: GD &gt; GF
          </div>
        </div>
      </div>
    </div>
  );
};
