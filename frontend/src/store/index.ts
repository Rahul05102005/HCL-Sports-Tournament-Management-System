import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';
import tournamentReducer from './tournamentSlice';
import matchReducer from './matchSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    tournaments: tournamentReducer,
    matches: matchReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
