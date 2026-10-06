import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import api from '../api/client';
import { User, AuthResponse } from '../types';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

const savedUser = localStorage.getItem('sportx_user');
const savedToken = localStorage.getItem('sportx_access_token');

const initialState: AuthState = {
  user: savedUser ? JSON.parse(savedUser) : null,
  token: savedToken || null,
  isAuthenticated: !!savedToken,
  isLoading: false,
  error: null,
};

export const loginUser = createAsyncThunk(
  'auth/login',
  async (credentials: { username: string; password: string }, { rejectWithValue }) => {
    try {
      const response = await api.post('/auth/login', credentials);
      const data: AuthResponse = response.data.data;
      localStorage.setItem('sportx_access_token', data.accessToken);
      localStorage.setItem('sportx_refresh_token', data.refreshToken);
      localStorage.setItem('sportx_user', JSON.stringify(data.user));
      return data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Login failed');
    }
  }
);

export const loginWithGoogle = createAsyncThunk(
  'auth/googleLogin',
  async (googleData: { email: string; name?: string; googleId?: string; avatarUrl?: string }, { rejectWithValue }) => {
    try {
      const response = await api.post('/auth/google', googleData);
      const data: AuthResponse = response.data.data;
      localStorage.setItem('sportx_access_token', data.accessToken);
      localStorage.setItem('sportx_refresh_token', data.refreshToken);
      localStorage.setItem('sportx_user', JSON.stringify(data.user));
      return data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Google sign-in failed');
    }
  }
);

export const registerUser = createAsyncThunk(
  'auth/register',
  async (formData: any, { rejectWithValue }) => {
    try {
      const response = await api.post('/auth/register', formData);
      const data: AuthResponse = response.data.data;
      localStorage.setItem('sportx_access_token', data.accessToken);
      localStorage.setItem('sportx_refresh_token', data.refreshToken);
      localStorage.setItem('sportx_user', JSON.stringify(data.user));
      return data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Registration failed');
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      state.error = null;
      localStorage.removeItem('sportx_access_token');
      localStorage.removeItem('sportx_refresh_token');
      localStorage.removeItem('sportx_user');
    },
    clearAuthError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action: PayloadAction<AuthResponse>) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.token = action.payload.accessToken;
        state.isAuthenticated = true;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(loginWithGoogle.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginWithGoogle.fulfilled, (state, action: PayloadAction<AuthResponse>) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.token = action.payload.accessToken;
        state.isAuthenticated = true;
      })
      .addCase(loginWithGoogle.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state, action: PayloadAction<AuthResponse>) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.token = action.payload.accessToken;
        state.isAuthenticated = true;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export const { logout, clearAuthError } = authSlice.actions;
export default authSlice.reducer;
