import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import api from '../api/client';
import { Tournament, TournamentPulse, Team } from '../types';

interface TournamentState {
  tournaments: Tournament[];
  currentTournament: Tournament | null;
  pulse: TournamentPulse | null;
  teams: Team[];
  isLoading: boolean;
  error: string | null;
}

const initialState: TournamentState = {
  tournaments: [],
  currentTournament: null,
  pulse: null,
  teams: [],
  isLoading: false,
  error: null,
};

export const fetchTournaments = createAsyncThunk(
  'tournaments/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      const response = await api.get('/tournaments');
      return response.data.data as Tournament[];
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch tournaments');
    }
  }
);

export const fetchTournamentById = createAsyncThunk(
  'tournaments/fetchById',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await api.get(`/tournaments/${id}`);
      return response.data.data as Tournament;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch tournament');
    }
  }
);

export const fetchTournamentPulse = createAsyncThunk(
  'tournaments/fetchPulse',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await api.get(`/tournaments/${id}/pulse`);
      return response.data.data as TournamentPulse;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch tournament pulse');
    }
  }
);

export const fetchTournamentTeams = createAsyncThunk(
  'tournaments/fetchTeams',
  async (tournamentId: number, { rejectWithValue }) => {
    try {
      const response = await api.get(`/tournaments/${tournamentId}/teams`);
      return response.data.data as Team[];
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch teams');
    }
  }
);

export const createTournament = createAsyncThunk(
  'tournaments/create',
  async (data: any, { rejectWithValue }) => {
    try {
      const response = await api.post('/tournaments', data);
      return response.data.data as Tournament;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to create tournament');
    }
  }
);

const tournamentSlice = createSlice({
  name: 'tournaments',
  initialState,
  reducers: {
    setCurrentTournament: (state, action: PayloadAction<Tournament | null>) => {
      state.currentTournament = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchTournaments.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchTournaments.fulfilled, (state, action: PayloadAction<Tournament[]>) => {
        state.isLoading = false;
        state.tournaments = action.payload;
        if (!state.currentTournament && action.payload.length > 0) {
          state.currentTournament = action.payload[0];
        }
      })
      .addCase(fetchTournaments.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchTournamentPulse.fulfilled, (state, action: PayloadAction<TournamentPulse>) => {
        state.pulse = action.payload;
      })
      .addCase(fetchTournamentTeams.fulfilled, (state, action: PayloadAction<Team[]>) => {
        state.teams = action.payload;
      })
      .addCase(createTournament.fulfilled, (state, action: PayloadAction<Tournament>) => {
        state.tournaments.unshift(action.payload);
        state.currentTournament = action.payload;
      });
  }
});

export const { setCurrentTournament } = tournamentSlice.actions;
export default tournamentSlice.reducer;
