import React, { useState } from 'react';
import { Routes, Route } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from './store';
import { Navbar } from './components/Navbar';
import { LiveTicker } from './components/LiveTicker';
import { CreateTournamentModal } from './components/CreateTournamentModal';
import { DashboardPage } from './pages/DashboardPage';
import { MatchesPage } from './pages/MatchesPage';
import { StandingsPage } from './pages/StandingsPage';
import { TeamsPage } from './pages/TeamsPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { fetchTournaments } from './store/tournamentSlice';
import { Trophy, Shield, Heart } from 'lucide-react';

export const App: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { liveMatches } = useSelector((state: RootState) => state.matches);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  return (
    <div className="min-h-screen bg-[#0a0f1d] flex flex-col text-slate-100 selection:bg-brand-500 selection:text-white">
      {/* Global Live Ticker */}
      <LiveTicker liveMatches={liveMatches} />

      {/* Main Navbar */}
      <Navbar onOpenCreateTournament={() => setIsCreateModalOpen(true)} />

      {/* Content Body */}
      <main className="flex-1 pb-16">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/matches" element={<MatchesPage />} />
          <Route path="/standings" element={<StandingsPage />} />
          <Route path="/teams" element={<TeamsPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </main>

      {/* Global Create Tournament Modal */}
      <CreateTournamentModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSuccess={() => {
          dispatch(fetchTournaments());
        }}
      />

      {/* Footer */}
      <footer className="border-t border-slate-800/80 bg-slate-950 py-8 text-xs text-slate-400">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <Trophy className="w-4 h-4 text-brand-400" />
            <span className="font-bold text-slate-200">SportX Platform</span>
            <span>— Smart Sports Tournament Management Platform</span>
          </div>
          <div className="flex items-center gap-4 text-slate-500 font-mono text-[11px]">
            <span>Spring Boot 3.3 • Java 21 • React 18 • Redux Toolkit</span>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default App;
