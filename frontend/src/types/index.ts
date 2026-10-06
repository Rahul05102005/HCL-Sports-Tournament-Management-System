export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Sport {
  id: number;
  name: string;
  category: string;
  minSquadSize: number;
  maxSquadSize: number;
  matchDurationMinutes?: number;
  rulesConfig?: string;
}

export interface Venue {
  id: number;
  name: string;
  location: string;
  capacity?: number;
  status: string;
  facilities?: string;
}

export interface Tournament {
  id: number;
  name: string;
  sportId: number;
  sportName: string;
  organizerId: number;
  organizerName: string;
  format: string;
  startDate: string;
  endDate: string;
  status: 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
  minTeams: number;
  maxTeams: number;
  currentTeamsCount: number;
  description?: string;
  createdAt: string;
}

export interface Team {
  id: number;
  name: string;
  shortName: string;
  collegeName: string;
  tournamentId: number;
  tournamentName: string;
  managerId?: number;
  managerName?: string;
  logoUrl?: string;
  registrationStatus: string;
  playerCount: number;
  squadValid: boolean;
}

export interface Player {
  id: number;
  teamId: number;
  teamName: string;
  fullName: string;
  jerseyNumber?: number;
  position?: string;
  studentRollNo?: string;
  contactEmail?: string;
  isEligible: boolean;
  isCaptain: boolean;
}

export interface Match {
  id: number;
  fixtureId: number;
  tournamentId: number;
  tournamentName: string;
  homeTeamId: number;
  homeTeamName: string;
  homeCollegeName: string;
  awayTeamId: number;
  awayTeamName: string;
  awayCollegeName: string;
  homeScore: number;
  awayScore: number;
  matchStatus: 'SCHEDULED' | 'LIVE' | 'HALF_TIME' | 'COMPLETED' | 'ABANDONED';
  currentMinute?: number;
  scheduledTime: string;
  venueName: string;
  venueLocation: string;
  refereeName: string;
  roundName: string;
  winnerTeamId?: number;
  winnerTeamName?: string;
  isDraw: boolean;
  notes?: string;
}

export interface ScoreEvent {
  id: number;
  matchId: number;
  teamId: number;
  teamName: string;
  playerId?: number;
  playerName?: string;
  jerseyNumber?: number;
  eventType: string; // GOAL, POINT, YELLOW_CARD, RED_CARD
  minuteOccurred?: number;
  extraInfo?: string;
  createdAt: string;
}

export interface Standing {
  id: number;
  tournamentId: number;
  teamId: number;
  teamName: string;
  shortName: string;
  collegeName: string;
  played: number;
  won: number;
  drawn: number;
  lost: number;
  scoreFor: number;
  scoreAgainst: number;
  goalDifference: number;
  points: number;
  formGuide: string;
  rank: number;
}

export interface TournamentPulse {
  tournamentId: number;
  tournamentName: string;
  sportName: string;
  status: string;
  totalTeams: number;
  totalFixtures: number;
  completedMatches: number;
  liveMatchesCount: number;
  completionPercentage: number;
  leaderTeam?: Standing;
  liveMatches: Match[];
  upcomingMatches: Match[];
  recentResults: Match[];
  recentAnnouncements: string[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
