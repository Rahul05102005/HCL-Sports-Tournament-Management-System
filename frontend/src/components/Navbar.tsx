import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Trophy, Activity, Calendar, Shield, Users, LogIn, LogOut, PlusCircle } from 'lucide-react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { logout } from '../store/authSlice';
import { setCurrentTournament } from '../store/tournamentSlice';

interface NavbarProps {
  onOpenCreateTournament?: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenCreateTournament }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch<AppDispatch>();
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);
  const { tournaments, currentTournament } = useSelector((state: RootState) => state.tournaments);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const navLinks = [
    { name: 'Dashboard', path: '/', icon: Activity },
    { name: 'Matches', path: '/matches', icon: Calendar },
    { name: 'Standings', path: '/standings', icon: Trophy },
    { name: 'Teams & Squads', path: '/teams', icon: Users },
  ];

  return (
    <header className="sticky top-0 z-50 glass-panel border-b border-slate-800 bg-[#0a0f1d]/90">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div className="flex items-center gap-3">
            <Link to="/" className="flex items-center gap-2 group">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-brand-600 to-emerald-400 flex items-center justify-center shadow-lg shadow-brand-500/20 group-hover:scale-105 transition-transform">
                <Trophy className="w-5 h-5 text-white" />
              </div>
              <div>
                <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">
                  Sport<span className="text-brand-400">X</span>
                </span>
                <span className="block text-[10px] uppercase tracking-wider text-slate-400 font-mono -mt-1">
                  Tournament OS
                </span>
              </div>
            </Link>

            {/* Tournament Selector Dropdown */}
            {tournaments.length > 0 && (
              <div className="hidden md:flex items-center ml-6 pl-6 border-l border-slate-800">
                <select
                  value={currentTournament?.id || ''}
                  onChange={(e) => {
                    const selected = tournaments.find((t) => t.id === Number(e.target.value));
                    if (selected) dispatch(setCurrentTournament(selected));
                  }}
                  className="bg-slate-900/90 text-sm text-slate-200 border border-slate-700 rounded-lg px-3 py-1.5 focus:outline-none focus:border-brand-500 transition-colors"
                >
                  {tournaments.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.name} ({t.sportName})
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          {/* Navigation Links */}
          <nav className="hidden md:flex items-center gap-1">
            {navLinks.map((link) => {
              const Icon = link.icon;
              const isActive = location.pathname === link.path;
              return (
                <Link
                  key={link.path}
                  to={link.path}
                  className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-brand-500/10 text-brand-400 border border-brand-500/20 shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {link.name}
                </Link>
              );
            })}
          </nav>

          {/* User Auth & Actions */}
          <div className="flex items-center gap-3">
            {isAuthenticated && (user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_ORGANIZER') && (
              <button
                onClick={onOpenCreateTournament}
                className="hidden sm:flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-brand-500 hover:bg-brand-600 text-white shadow-md shadow-brand-500/20 transition-all active:scale-95"
              >
                <PlusCircle className="w-3.5 h-3.5" />
                New Tournament
              </button>
            )}

            {isAuthenticated ? (
              <div className="flex items-center gap-3">
                <div className="hidden sm:flex flex-col text-right">
                  <span className="text-xs font-semibold text-slate-200">{user?.fullName}</span>
                  <span className="text-[10px] font-mono text-emerald-400 uppercase tracking-wider">
                    {user?.role.replace('ROLE_', '')}
                  </span>
                </div>
                <button
                  onClick={handleLogout}
                  title="Logout"
                  className="p-2 rounded-lg text-slate-400 hover:text-red-400 hover:bg-slate-800/50 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                className="flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition-all"
              >
                <LogIn className="w-3.5 h-3.5" />
                Sign In
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
