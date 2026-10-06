import React from 'react';
import { Radio, ChevronRight } from 'lucide-react';
import { Match } from '../types';
import { Link } from 'react-router-dom';

interface LiveTickerProps {
  liveMatches: Match[];
}

export const LiveTicker: React.FC<LiveTickerProps> = ({ liveMatches }) => {
  if (!liveMatches || liveMatches.length === 0) return null;

  return (
    <div className="bg-emerald-950/40 border-b border-emerald-500/20 backdrop-blur-sm overflow-hidden py-2 px-4">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        <div className="flex items-center gap-2 flex-shrink-0 mr-4">
          <span className="relative flex h-2.5 w-2.5">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-red-500"></span>
          </span>
          <span className="text-xs font-bold tracking-wider uppercase text-emerald-400 font-mono flex items-center gap-1">
            <Radio className="w-3 h-3 text-red-500 animate-pulse" />
            Live Pulse
          </span>
        </div>

        <div className="flex items-center gap-6 overflow-x-auto no-scrollbar scroll-smooth">
          {liveMatches.map((m) => (
            <Link
              key={m.id}
              to="/matches"
              className="flex items-center gap-3 bg-slate-900/80 hover:bg-slate-800 border border-slate-700/60 rounded-full px-3 py-1 text-xs transition-colors flex-shrink-0"
            >
              <span className="font-semibold text-slate-200">{m.homeTeamName}</span>
              <span className="font-mono font-bold text-brand-400 px-1.5 py-0.5 bg-slate-800/80 rounded">
                {m.homeScore} - {m.awayScore}
              </span>
              <span className="font-semibold text-slate-200">{m.awayTeamName}</span>
              <span className="text-[10px] font-mono text-emerald-400">
                {m.currentMinute ? `${m.currentMinute}'` : 'LIVE'}
              </span>
              <ChevronRight className="w-3 h-3 text-slate-500" />
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};
