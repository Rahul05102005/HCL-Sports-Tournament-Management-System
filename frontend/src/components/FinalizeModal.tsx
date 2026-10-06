import React, { useState } from 'react';
import { X, CheckCircle2 } from 'lucide-react';
import { Match } from '../types';

interface FinalizeModalProps {
  match: Match;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: { homeScore: number; awayScore: number; notes?: string }) => void;
}

export const FinalizeModal: React.FC<FinalizeModalProps> = ({ match, isOpen, onClose, onSubmit }) => {
  const [homeScore, setHomeScore] = useState<number>(match.homeScore);
  const [awayScore, setAwayScore] = useState<number>(match.awayScore);
  const [notes, setNotes] = useState<string>(match.notes || '');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      homeScore: Number(homeScore),
      awayScore: Number(awayScore),
      notes,
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
          <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          Finalize Match Result
        </h3>
        <p className="text-xs text-slate-400 mb-5">
          This operation will lock scores and update tournament standings atomically.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4 bg-slate-800/60 p-4 rounded-xl border border-slate-700">
            <div className="text-center">
              <label className="block text-xs font-semibold text-slate-300 mb-1">{match.homeTeamName}</label>
              <input
                type="number"
                min="0"
                max="99"
                value={homeScore}
                onChange={(e) => setHomeScore(Number(e.target.value))}
                className="w-20 text-center text-2xl font-bold bg-slate-900 border border-slate-700 rounded-lg py-2 text-brand-400 focus:outline-none focus:border-brand-500 font-mono mx-auto"
              />
            </div>
            <div className="text-center">
              <label className="block text-xs font-semibold text-slate-300 mb-1">{match.awayTeamName}</label>
              <input
                type="number"
                min="0"
                max="99"
                value={awayScore}
                onChange={(e) => setAwayScore(Number(e.target.value))}
                className="w-20 text-center text-2xl font-bold bg-slate-900 border border-slate-700 rounded-lg py-2 text-brand-400 focus:outline-none focus:border-brand-500 font-mono mx-auto"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Official Notes / Remarks</label>
            <textarea
              rows={3}
              placeholder="e.g. Excellent defense by home squad. No disciplinary issues reported."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
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
              className="px-4 py-2 text-xs font-semibold rounded-lg bg-emerald-500 text-white hover:bg-emerald-600 shadow-md shadow-emerald-500/20"
            >
              Confirm &amp; Finalize
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
