import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { X, Trophy } from 'lucide-react';
import api from '../api/client';
import { Sport } from '../types';

const tournamentSchema = z.object({
  name: z.string().min(4, 'Name must be at least 4 characters'),
  sportId: z.coerce.number().min(1, 'Please select a sport'),
  format: z.string().min(1, 'Please select tournament format'),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
  minTeams: z.coerce.number().min(2, 'Minimum 2 teams'),
  maxTeams: z.coerce.number().min(2, 'Maximum at least 2 teams'),
  description: z.string().optional(),
});

type TournamentFormValues = z.infer<typeof tournamentSchema>;

interface CreateTournamentModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (data: any) => void;
}

export const CreateTournamentModal: React.FC<CreateTournamentModalProps> = ({ isOpen, onClose, onSuccess }) => {
  const [sports, setSports] = useState<Sport[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TournamentFormValues>({
    resolver: zodResolver(tournamentSchema),
    defaultValues: {
      format: 'ROUND_ROBIN',
      minTeams: 4,
      maxTeams: 8,
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0],
    },
  });

  useEffect(() => {
    if (isOpen) {
      api.get('/sports').then((res) => {
        if (res.data?.data) setSports(res.data.data);
      });
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const onSubmit = async (values: TournamentFormValues) => {
    try {
      setIsSubmitting(true);
      setErrorMsg(null);
      const res = await api.post('/tournaments', values);
      onSuccess(res.data.data);
      reset();
      onClose();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to create tournament');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
      <div className="bg-slate-900 border border-slate-700 w-full max-w-lg rounded-2xl p-6 shadow-2xl relative max-h-[90vh] overflow-y-auto">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800"
        >
          <X className="w-5 h-5" />
        </button>

        <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
          <Trophy className="w-5 h-5 text-brand-400" />
          Create New Tournament
        </h3>
        <p className="text-xs text-slate-400 mb-6">
          Set up a new inter-collegiate competition with smart scheduling rules.
        </p>

        {errorMsg && (
          <div className="mb-4 p-3 bg-red-900/40 border border-red-500/50 rounded-lg text-xs text-red-300">
            {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Tournament Title</label>
            <input
              type="text"
              placeholder="e.g. Tamil Nadu Inter-Engineering Super Cup 2026"
              {...register('name')}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
            />
            {errors.name && <p className="text-red-400 text-[11px] mt-1">{errors.name.message}</p>}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Sport</label>
              <select
                {...register('sportId')}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              >
                <option value="">Select sport</option>
                {sports.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name} ({s.category})
                  </option>
                ))}
              </select>
              {errors.sportId && <p className="text-red-400 text-[11px] mt-1">{errors.sportId.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Format</label>
              <select
                {...register('format')}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              >
                <option value="ROUND_ROBIN">Round Robin</option>
                <option value="KNOCKOUT">Single Elimination Knockout</option>
                <option value="HYBRID">Hybrid (Group + Knockout)</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Start Date</label>
              <input
                type="date"
                {...register('startDate')}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
              {errors.startDate && <p className="text-red-400 text-[11px] mt-1">{errors.startDate.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">End Date</label>
              <input
                type="date"
                {...register('endDate')}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
              {errors.endDate && <p className="text-red-400 text-[11px] mt-1">{errors.endDate.message}</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Min Teams</label>
              <input
                type="number"
                {...register('minTeams')}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Max Teams</label>
              <input
                type="number"
                {...register('maxTeams')}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Description</label>
            <textarea
              rows={2}
              placeholder="Tournament rules, eligibility criteria, venue policies..."
              {...register('description')}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
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
              disabled={isSubmitting}
              className="px-4 py-2 text-xs font-semibold rounded-lg bg-brand-500 hover:bg-brand-600 text-white shadow-md shadow-brand-500/20 disabled:opacity-50"
            >
              {isSubmitting ? 'Creating...' : 'Launch Tournament'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
