import React, { useState } from 'react';
import { X, Award, AlertTriangle, ShieldAlert } from 'lucide-react';
import { Match, Player } from '../types';

interface ScoreModalProps {
  match: Match;
  players: Player[];
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: { teamId: number; playerId?: number; eventType: string; minuteOccurred?: number; extraInfo?: string }) => void;
}

export const ScoreModal: React.FC<ScoreModalProps> = ({ match, players, isOpen, onClose, onSubmit }) => {
  const [selectedTeamId, setSelectedTeamId] = useState<number>(match.homeTeamId);
  const [selectedPlayerId, setSelectedPlayerId] = useState<number | undefined>(undefined);
  const [eventType, setEventType] = useState<string>('GOAL');
  const [minuteOccurred, setMinuteOccurred] = useState<number>(match.currentMinute || 45);
  const [extraInfo, setExtraInfo] = useState<string>('');

  if (!isOpen) return null;

  const filteredPlayers = players.filter((p) => p.teamId === selectedTeamId);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      teamId: selectedTeamId,
      playerId: selectedPlayerId,
      eventType,
      minuteOccurred: Number(minuteOccurred),
      extraInfo,
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
      <div className="bg-slate-900 border border-slate-700 w-full max-w-md rounded-2xl p-6 shadow-2xl relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800"
        >
          <X className="w-5 h-5" />
        </button>

        <h3 className="text-lg font-bold text-white mb-1 flex items-center gap-2">
          <Award className="w-5 h-5 text-brand-400" />
          Log Score / Match Event
        </h3>
        <p className="text-xs text-slate-400 mb-5">
          {match.homeTeamName} vs {match.awayTeamName} ({match.roundName})
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Team Select */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Select Team</label>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => {
                  setSelectedTeamId(match.homeTeamId);
                  setSelectedPlayerId(undefined);
                }}
                className={`py-2 px-3 text-xs font-semibold rounded-lg border text-center transition-all ${
                  selectedTeamId === match.homeTeamId
                    ? 'bg-brand-500/20 border-brand-500 text-brand-300'
                    : 'bg-slate-800 border-slate-700 text-slate-400 hover:bg-slate-750'
                }`}
              >
                {match.homeTeamName}
              </button>
              <button
                type="button"
                onClick={() => {
                  setSelectedTeamId(match.awayTeamId);
                  setSelectedPlayerId(undefined);
                }}
                className={`py-2 px-3 text-xs font-semibold rounded-lg border text-center transition-all ${
                  selectedTeamId === match.awayTeamId
                    ? 'bg-brand-500/20 border-brand-500 text-brand-300'
                    : 'bg-slate-800 border-slate-700 text-slate-400 hover:bg-slate-750'
                }`}
              >
                {match.awayTeamName}
              </button>
            </div>
          </div>

          {/* Event Type */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Event Type</label>
            <div className="grid grid-cols-3 gap-2">
              {[
                { type: 'GOAL', label: 'Goal / Point' },
                { type: 'YELLOW_CARD', label: 'Yellow Card' },
                { type: 'RED_CARD', label: 'Red Card' },
              ].map((ev) => (
                <button
                  type="button"
                  key={ev.type}
                  onClick={() => setEventType(ev.type)}
                  className={`py-1.5 px-2 text-xs font-medium rounded-lg border text-center transition-all ${
                    eventType === ev.type
                      ? 'bg-slate-700 border-brand-400 text-white shadow-sm'
                      : 'bg-slate-800 border-slate-700 text-slate-400'
                  }`}
                >
                  {ev.label}
                </button>
              ))}
            </div>
          </div>

          {/* Player Select */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Player (Optional)</label>
            <select
              value={selectedPlayerId || ''}
              onChange={(e) => setSelectedPlayerId(e.target.value ? Number(e.target.value) : undefined)}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
            >
              <option value="">-- Choose player or leave unassigned --</option>
              {filteredPlayers.map((p) => (
                <option key={p.id} value={p.id}>
                  #{p.jerseyNumber || '?'} {p.fullName} ({p.position || 'Player'})
                </option>
              ))}
            </select>
          </div>

          {/* Minute */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Minute Occurred</label>
            <input
              type="number"
              min="1"
              max="150"
              value={minuteOccurred}
              onChange={(e) => setMinuteOccurred(Number(e.target.value))}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500 font-mono"
            />
          </div>

          {/* Description */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Extra Details</label>
            <input
              type="text"
              placeholder="e.g. Header from corner kick, Free-kick 25 yards"
              value={extraInfo}
              onChange={(e) => setExtraInfo(e.target.value)}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
            />
          </div>

          <div className="pt-2 flex justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold rounded-lg bg-slate-800 text-slate-300 hover:bg-slate-700"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold rounded-lg bg-brand-500 text-white hover:bg-brand-600 shadow-md shadow-brand-500/20"
            >
              Save Event
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
