import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { fetchTournamentTeams } from '../store/tournamentSlice';
import { Team, Player } from '../types';
import api from '../api/client';
import { Users, ShieldCheck, AlertCircle, Plus, UserPlus, X, CheckCircle2 } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

const teamSchema = z.object({
  name: z.string().min(3, 'Team name must be at least 3 characters'),
  shortName: z.string().max(5, 'Short code max 5 characters').optional(),
  collegeName: z.string().min(3, 'College name is required'),
});

type TeamFormValues = z.infer<typeof teamSchema>;

const playerSchema = z.object({
  fullName: z.string().min(3, 'Full name is required'),
  jerseyNumber: z.coerce.number().min(1).max(99),
  position: z.string().min(2, 'Position is required'),
  studentRollNo: z.string().min(2, 'Student roll number required'),
  contactEmail: z.string().email('Valid student email required'),
  isCaptain: z.boolean().default(false),
});

type PlayerFormValues = z.infer<typeof playerSchema>;

export const TeamsPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { currentTournament, teams } = useSelector((state: RootState) => state.tournaments);
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);

  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);
  const [squad, setSquad] = useState<Player[]>([]);
  const [isTeamModalOpen, setIsTeamModalOpen] = useState(false);
  const [isPlayerModalOpen, setIsPlayerModalOpen] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const teamForm = useForm<TeamFormValues>({
    resolver: zodResolver(teamSchema),
  });

  const playerForm = useForm<PlayerFormValues>({
    resolver: zodResolver(playerSchema),
    defaultValues: {
      isCaptain: false,
    },
  });

  useEffect(() => {
    if (currentTournament) {
      dispatch(fetchTournamentTeams(currentTournament.id));
    }
  }, [currentTournament, dispatch]);

  useEffect(() => {
    if (teams.length > 0 && !selectedTeam) {
      handleSelectTeam(teams[0]);
    }
  }, [teams]);

  const handleSelectTeam = async (team: Team) => {
    setSelectedTeam(team);
    try {
      const res = await api.get(`/teams/${team.id}/players`);
      setSquad(res.data.data || []);
    } catch (err) {
      setSquad([]);
    }
  };

  const handleRegisterTeam = async (values: TeamFormValues) => {
    if (!currentTournament) return;
    try {
      await api.post('/teams', {
        ...values,
        tournamentId: currentTournament.id,
      });
      dispatch(fetchTournamentTeams(currentTournament.id));
      setIsTeamModalOpen(false);
      teamForm.reset();
      setStatusMessage('Team registered successfully!');
    } catch (err: any) {
      setStatusMessage(err.response?.data?.message || 'Failed to register team');
    }
  };

  const handleAddPlayer = async (values: PlayerFormValues) => {
    if (!selectedTeam) return;
    try {
      await api.post(`/teams/${selectedTeam.id}/players`, values);
      handleSelectTeam(selectedTeam);
      if (currentTournament) dispatch(fetchTournamentTeams(currentTournament.id));
      setIsPlayerModalOpen(false);
      playerForm.reset();
      setStatusMessage('Player added to squad!');
    } catch (err: any) {
      setStatusMessage(err.response?.data?.message || 'Failed to add player');
    }
  };

  const isTeamManagerOrAdmin =
    isAuthenticated && (user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_ORGANIZER' || selectedTeam?.managerId === user?.id);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Users className="w-6 h-6 text-brand-400" />
            Teams &amp; Squad Rosters
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            {currentTournament ? `${currentTournament.name} • Enrolled Teams` : 'Browse teams'}
          </p>
        </div>

        {isAuthenticated && (
          <button
            onClick={() => setIsTeamModalOpen(true)}
            className="flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-semibold bg-brand-500 hover:bg-brand-600 text-white shadow-md shadow-brand-500/20 transition-all w-fit"
          >
            <Plus className="w-4 h-4" />
            Register New Team
          </button>
        )}
      </div>

      {statusMessage && (
        <div className="p-3 bg-brand-500/10 border border-brand-500/30 rounded-xl text-xs text-brand-300 flex justify-between items-center">
          <span>{statusMessage}</span>
          <button onClick={() => setStatusMessage(null)} className="text-brand-400 font-bold ml-4">
            ✕
          </button>
        </div>
      )}

      {/* 2-Column Layout: Team List & Squad Details */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Team List Column */}
        <div className="space-y-3">
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 font-mono">
            Participating Teams ({teams.length})
          </h3>
          <div className="space-y-2">
            {teams.map((t) => {
              const isSelected = selectedTeam?.id === t.id;
              return (
                <div
                  key={t.id}
                  onClick={() => handleSelectTeam(t)}
                  className={`p-4 rounded-xl border cursor-pointer transition-all ${
                    isSelected
                      ? 'bg-slate-900 border-brand-500/50 shadow-lg shadow-brand-500/10'
                      : 'bg-slate-900/60 border-slate-800 hover:border-slate-700'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center font-mono font-bold text-brand-400 text-sm">
                        {t.shortName || t.name.substring(0, 3)}
                      </div>
                      <div>
                        <h4 className="text-sm font-bold text-white">{t.name}</h4>
                        <p className="text-[11px] text-slate-400">{t.collegeName}</p>
                      </div>
                    </div>

                    <div className="text-right">
                      <span
                        className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-mono font-bold ${
                          t.squadValid
                            ? 'bg-emerald-500/20 text-emerald-400'
                            : 'bg-amber-500/20 text-amber-400'
                        }`}
                      >
                        {t.squadValid ? (
                          <>
                            <ShieldCheck className="w-3 h-3" />
                            Squad Valid
                          </>
                        ) : (
                          <>
                            <AlertCircle className="w-3 h-3" />
                            Needs Players
                          </>
                        )}
                      </span>
                      <span className="block text-[10px] text-slate-500 mt-1 font-mono">
                        {t.playerCount} Players
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Squad Details Column (2 spans) */}
        <div className="lg:col-span-2 space-y-4">
          {selectedTeam ? (
            <div className="rounded-2xl glass-panel border border-slate-800 p-6 space-y-6">
              {/* Team Info Header */}
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-800">
                <div>
                  <div className="flex items-center gap-3">
                    <h2 className="text-2xl font-bold text-white">{selectedTeam.name}</h2>
                    <span className="px-2.5 py-0.5 rounded-full text-xs font-mono font-bold bg-slate-800 text-brand-400 border border-slate-700">
                      {selectedTeam.shortName}
                    </span>
                  </div>
                  <p className="text-xs text-slate-400 mt-1">{selectedTeam.collegeName}</p>
                  <p className="text-xs text-slate-500 mt-0.5">Manager: {selectedTeam.managerName || 'Faculty In-Charge'}</p>
                </div>

                {isTeamManagerOrAdmin && (
                  <button
                    onClick={() => setIsPlayerModalOpen(true)}
                    className="flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/40 hover:bg-brand-500/30 transition-colors w-fit"
                  >
                    <UserPlus className="w-4 h-4" />
                    Add Player to Roster
                  </button>
                )}
              </div>

              {/* Player Roster Table */}
              <div>
                <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 font-mono mb-4">
                  Official Squad Roster ({squad.length} Athletes)
                </h3>

                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-slate-900/80 text-slate-400 font-mono text-[11px] uppercase border-b border-slate-800">
                      <tr>
                        <th className="py-3 px-3 w-12 text-center">No.</th>
                        <th className="py-3 px-4">Player Name</th>
                        <th className="py-3 px-3">Position</th>
                        <th className="py-3 px-3">Roll Number</th>
                        <th className="py-3 px-3 text-center">Status</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800/60 font-medium">
                      {squad.length > 0 ? (
                        squad.map((p) => (
                          <tr key={p.id} className="hover:bg-slate-800/30 transition-colors">
                            <td className="py-3 px-3 text-center font-mono font-bold text-brand-400">
                              #{p.jerseyNumber || '-'}
                            </td>
                            <td className="py-3 px-4">
                              <div className="flex items-center gap-2">
                                <span className="font-bold text-white">{p.fullName}</span>
                                {p.isCaptain && (
                                  <span className="px-1.5 py-0.2 rounded text-[9px] font-mono font-extrabold bg-amber-500/20 text-amber-300 border border-amber-500/30">
                                    CAPTAIN
                                  </span>
                                )}
                              </div>
                              <span className="text-[10px] text-slate-400">{p.contactEmail}</span>
                            </td>
                            <td className="py-3 px-3 text-slate-300">{p.position}</td>
                            <td className="py-3 px-3 font-mono text-slate-400">{p.studentRollNo}</td>
                            <td className="py-3 px-3 text-center">
                              <span className="inline-flex items-center gap-1 text-[11px] text-emerald-400">
                                <CheckCircle2 className="w-3.5 h-3.5" />
                                Eligible
                              </span>
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={5} className="py-8 text-center text-slate-500">
                            No players enrolled in this squad yet. Click "Add Player to Roster" to register athletes.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          ) : (
            <div className="p-12 text-center rounded-2xl bg-slate-900/40 border border-slate-800 text-slate-500">
              Select a team to view its squad roster and eligibility status.
            </div>
          )}
        </div>
      </div>

      {/* Modal: Register Team */}
      {isTeamModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-700 w-full max-w-md rounded-2xl p-6 shadow-2xl relative">
            <button
              onClick={() => setIsTeamModalOpen(false)}
              className="absolute top-4 right-4 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800"
            >
              <X className="w-5 h-5" />
            </button>
            <h3 className="text-lg font-bold text-white mb-1">Register College Team</h3>
            <p className="text-xs text-slate-400 mb-5">
              Enrolling team into {currentTournament?.name}
            </p>

            <form onSubmit={teamForm.handleSubmit(handleRegisterTeam)} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Team Name</label>
                <input
                  type="text"
                  placeholder="e.g. GCT Titans"
                  {...teamForm.register('name')}
                  className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                />
                {teamForm.formState.errors.name && (
                  <p className="text-red-400 text-[11px] mt-1">{teamForm.formState.errors.name.message}</p>
                )}
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Short Code (3-5 letters)</label>
                <input
                  type="text"
                  placeholder="e.g. GCT"
                  {...teamForm.register('shortName')}
                  className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500 uppercase"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">College / Institution</label>
                <input
                  type="text"
                  placeholder="e.g. Government College of Technology, Coimbatore"
                  {...teamForm.register('collegeName')}
                  className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                />
                {teamForm.formState.errors.collegeName && (
                  <p className="text-red-400 text-[11px] mt-1">{teamForm.formState.errors.collegeName.message}</p>
                )}
              </div>

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsTeamModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold rounded-lg bg-slate-800 text-slate-300 hover:bg-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-semibold rounded-lg bg-brand-500 text-white hover:bg-brand-600 shadow-md shadow-brand-500/20"
                >
                  Submit Registration
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Add Player */}
      {isPlayerModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-700 w-full max-w-md rounded-2xl p-6 shadow-2xl relative">
            <button
              onClick={() => setIsPlayerModalOpen(false)}
              className="absolute top-4 right-4 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800"
            >
              <X className="w-5 h-5" />
            </button>
            <h3 className="text-lg font-bold text-white mb-1">Add Player to Squad</h3>
            <p className="text-xs text-slate-400 mb-5">
              Adding athlete to {selectedTeam?.name} ({selectedTeam?.collegeName})
            </p>

            <form onSubmit={playerForm.handleSubmit(handleAddPlayer)} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Athlete Full Name</label>
                <input
                  type="text"
                  placeholder="e.g. Ramesh Kannan"
                  {...playerForm.register('fullName')}
                  className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                />
                {playerForm.formState.errors.fullName && (
                  <p className="text-red-400 text-[11px] mt-1">{playerForm.formState.errors.fullName.message}</p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Jersey Number</label>
                  <input
                    type="number"
                    placeholder="10"
                    {...playerForm.register('jerseyNumber')}
                    className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Playing Position</label>
                  <input
                    type="text"
                    placeholder="e.g. Midfielder"
                    {...playerForm.register('position')}
                    className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Student Roll No</label>
                  <input
                    type="text"
                    placeholder="22CS301"
                    {...playerForm.register('studentRollNo')}
                    className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500 uppercase font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Student Email</label>
                  <input
                    type="email"
                    placeholder="student@college.edu"
                    {...playerForm.register('contactEmail')}
                    className="w-full bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                  />
                </div>
              </div>

              <div className="flex items-center gap-2 pt-1">
                <input
                  type="checkbox"
                  id="captainCheck"
                  {...playerForm.register('isCaptain')}
                  className="rounded bg-slate-800 border-slate-700 text-brand-500 focus:ring-brand-500"
                />
                <label htmlFor="captainCheck" className="text-xs text-slate-300 font-medium">
                  Designate as Team Captain
                </label>
              </div>

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsPlayerModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold rounded-lg bg-slate-800 text-slate-300 hover:bg-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-semibold rounded-lg bg-brand-500 text-white hover:bg-brand-600 shadow-md shadow-brand-500/20"
                >
                  Add to Squad
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
