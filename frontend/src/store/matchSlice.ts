import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import api from '../api/client';
import { Match, ScoreEvent } from '../types';

interface MatchState {
  matches: Match[];
  liveMatches: Match[];
  currentMatch: Match | null;
  scoreEvents: ScoreEvent[];
  isLoading: boolean;
  error: string | null;
}

const initialState: MatchState = {
  matches: [],
  liveMatches: [],
  currentMatch: null,
  scoreEvents: [],
  isLoading: false,
  error: null,
};

export const fetchTournamentMatches = createAsyncThunk(
  'matches/fetchByTournament',
  async (tournamentId: number, { rejectWithValue }) => {
    try {
      const response = await api.get(`/tournaments/${tournamentId}/matches`);
      return response.data.data as Match[];
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch matches');
    }
  }
);

export const fetchLiveMatches = createAsyncThunk(
  'matches/fetchLive',
  async (_, { rejectWithValue }) => {
    try {
      const response = await api.get('/matches/live');
      return response.data.data as Match[];
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch live matches');
    }
  }
);

export const fetchMatchDetails = createAsyncThunk(
  'matches/fetchDetails',
  async (matchId: number, { rejectWithValue }) => {
    try {
      const matchRes = await api.get(`/matches/${matchId}`);
      const eventsRes = await api.get(`/matches/${matchId}/score-events`);
      return {
        match: matchRes.data.data as Match,
        events: eventsRes.data.data as ScoreEvent[],
      };
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch match details');
    }
  }
);

export const addScoreEvent = createAsyncThunk(
  'matches/addScoreEvent',
  async ({ matchId, data }: { matchId: number; data: any }, { rejectWithValue }) => {
    try {
      const response = await api.post(`/matches/${matchId}/score-events`, data);
      return { event: response.data.data as ScoreEvent, matchId };
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to log score event');
    }
  }
);

export const finalizeMatch = createAsyncThunk(
  'matches/finalize',
  async ({ matchId, data }: { matchId: number; data: any }, { rejectWithValue }) => {
    try {
      const response = await api.post(`/matches/${matchId}/finalize`, data);
      return response.data.data as Match;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to finalize match');
    }
  }
);

const matchSlice = createSlice({
  name: 'matches',
  initialState,
  reducers: {
    setCurrentMatch: (state, action: PayloadAction<Match | null>) => {
      state.currentMatch = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchTournamentMatches.fulfilled, (state, action: PayloadAction<Match[]>) => {
        state.matches = action.payload;
      })
      .addCase(fetchLiveMatches.fulfilled, (state, action: PayloadAction<Match[]>) => {
        state.liveMatches = action.payload;
      })
      .addCase(fetchMatchDetails.fulfilled, (state, action) => {
        state.currentMatch = action.payload.match;
        state.scoreEvents = action.payload.events;
      })
      .addCase(addScoreEvent.fulfilled, (state, action) => {
        state.scoreEvents.push(action.payload.event);
        if (state.currentMatch && state.currentMatch.id === action.payload.matchId) {
          state.currentMatch.matchStatus = 'LIVE';
          if (['GOAL', 'POINT'].includes(action.payload.event.eventType)) {
            if (state.currentMatch.homeTeamId === action.payload.event.teamId) {
              state.currentMatch.homeScore += 1;
            } else if (state.currentMatch.awayTeamId === action.payload.event.teamId) {
              state.currentMatch.awayScore += 1;
            }
          }
        }
      })
      .addCase(finalizeMatch.fulfilled, (state, action: PayloadAction<Match>) => {
        state.currentMatch = action.payload;
        const idx = state.matches.findIndex(m => m.id === action.payload.id);
        if (idx !== -1) {
          state.matches[idx] = action.payload;
        }
        state.liveMatches = state.liveMatches.filter(m => m.id !== action.payload.id);
      });
  }
});

export const { setCurrentMatch } = matchSlice.actions;
export default matchSlice.reducer;
