import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { loginUser, loginWithGoogle, clearAuthError } from '../store/authSlice';
import { Trophy, Key, User, AlertCircle, Loader2 } from 'lucide-react';

declare global {
  interface Window {
    google?: any;
  }
}

const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

type LoginFormValues = z.infer<typeof loginSchema>;

// Official Google Multicolor G Logo
const GoogleIcon = ({ className = "w-5 h-5" }: { className?: string }) => (
  <svg className={className} viewBox="0 0 24 24">
    <path
      fill="#4285F4"
      d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
    />
    <path
      fill="#34A853"
      d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
    />
    <path
      fill="#FBBC05"
      d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
    />
    <path
      fill="#EA4335"
      d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
    />
  </svg>
);

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const { isAuthenticated, isLoading, error } = useSelector((state: RootState) => state.auth);

  const [googleStatusError, setGoogleStatusError] = useState<string | null>(null);
  const [isGoogleSigningIn, setIsGoogleSigningIn] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
    return () => {
      dispatch(clearAuthError());
    };
  }, [isAuthenticated, navigate, dispatch]);

  // Initialize Google Identity Services if client ID is configured
  useEffect(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    if (!clientId) return;

    const setupGsi = () => {
      if (window.google?.accounts?.id) {
        try {
          window.google.accounts.id.initialize({
            client_id: clientId,
            callback: (response: any) => {
              if (response?.credential) {
                try {
                  const base64Url = response.credential.split('.')[1];
                  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                  const jsonPayload = decodeURIComponent(
                    atob(base64)
                      .split('')
                      .map((c: string) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                      .join('')
                  );
                  const payload = JSON.parse(jsonPayload);
                  dispatch(
                    loginWithGoogle({
                      email: payload.email,
                      name: payload.name || payload.email.split('@')[0],
                      googleId: payload.sub,
                      avatarUrl: payload.picture,
                    })
                  );
                } catch (e) {
                  setGoogleStatusError('Failed to parse Google account token');
                }
              }
            },
            auto_select: false,
          });
        } catch (e) {
          console.error('Google Identity init error:', e);
        }
      }
    };

    if (window.google?.accounts?.id) {
      setupGsi();
    } else {
      const interval = setInterval(() => {
        if (window.google?.accounts?.id) {
          clearInterval(interval);
          setupGsi();
        }
      }, 400);
      return () => clearInterval(interval);
    }
  }, [dispatch]);

  const onSubmit = (data: LoginFormValues) => {
    dispatch(loginUser(data));
  };

  const handleQuickDemo = (user: string) => {
    setValue('username', user);
    setValue('password', 'password123');
    dispatch(loginUser({ username: user, password: 'password123' }));
  };

  const handleGoogleSignIn = () => {
    setGoogleStatusError(null);
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

    if (!clientId || clientId.trim() === '') {
      setGoogleStatusError(
        'Google Client ID is not configured. Please set VITE_GOOGLE_CLIENT_ID in frontend/.env to enable Google Sign-In.'
      );
      return;
    }

    if (!window.google?.accounts?.oauth2) {
      setGoogleStatusError('Google Sign-In library is loading. Please check your network or refresh.');
      return;
    }

    try {
      setIsGoogleSigningIn(true);
      const tokenClient = window.google.accounts.oauth2.initTokenClient({
        client_id: clientId,
        scope: 'openid email profile',
        prompt: 'select_account',
        callback: async (tokenResponse: any) => {
          if (tokenResponse.error) {
            setIsGoogleSigningIn(false);
            if (tokenResponse.error !== 'popup_closed_by_user') {
              setGoogleStatusError(`Google Sign-In canceled or failed: ${tokenResponse.error}`);
            }
            return;
          }

          try {
            const userinfoRes = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
              headers: { Authorization: `Bearer ${tokenResponse.access_token}` },
            });
            if (!userinfoRes.ok) {
              throw new Error('Failed to fetch account info from Google');
            }
            const profile = await userinfoRes.json();

            await dispatch(
              loginWithGoogle({
                email: profile.email,
                name: profile.name || profile.email.split('@')[0],
                googleId: profile.sub,
                avatarUrl: profile.picture,
              })
            ).unwrap();
          } catch (err: any) {
            setGoogleStatusError(err.message || 'Google authentication failed');
          } finally {
            setIsGoogleSigningIn(false);
          }
        },
      });

      tokenClient.requestAccessToken({ prompt: 'select_account' });
    } catch (err: any) {
      setIsGoogleSigningIn(false);
      setGoogleStatusError(err.message || 'Could not launch Google Sign-In');
    }
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center p-4">
      <div className="w-full max-w-md space-y-6">
        {/* Brand header */}
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-brand-600 to-emerald-400 flex items-center justify-center shadow-xl shadow-brand-500/25 mx-auto">
            <Trophy className="w-6 h-6 text-white" />
          </div>
          <h1 className="text-2xl font-black tracking-tight text-white">Sign In to SportX</h1>
          <p className="text-xs text-slate-400">Smart Sports Tournament Management Platform</p>
        </div>

        {/* Login Card */}
        <div className="rounded-3xl glass-panel-glow border border-slate-700/80 p-8 shadow-2xl space-y-6">
          {(error || googleStatusError) && (
            <div className="p-3.5 rounded-xl bg-red-900/40 border border-red-500/50 text-xs text-red-300 flex items-start gap-2.5">
              <AlertCircle className="w-4 h-4 text-red-400 shrink-0 mt-0.5" />
              <span>{error || googleStatusError}</span>
            </div>
          )}

          {/* GOOGLE SIGN IN BUTTON */}
          <button
            type="button"
            id="google-signin-btn"
            onClick={handleGoogleSignIn}
            disabled={isLoading || isGoogleSigningIn}
            className="w-full flex items-center justify-center gap-3 py-3 px-4 rounded-xl font-semibold text-sm bg-white hover:bg-slate-100 text-slate-800 shadow-lg shadow-white/5 border border-slate-200 transition-all transform active:scale-98 disabled:opacity-50 group"
          >
            {isGoogleSigningIn ? (
              <Loader2 className="w-5 h-5 animate-spin text-slate-600" />
            ) : (
              <GoogleIcon />
            )}
            <span className="group-hover:text-black font-medium">
              {isGoogleSigningIn ? 'Connecting to Google...' : 'Continue with Google'}
            </span>
          </button>

          {/* Divider */}
          <div className="relative flex items-center justify-center">
            <div className="border-t border-slate-700 w-full" />
            <span className="bg-slate-900 px-3 text-[11px] font-medium tracking-wider text-slate-400 uppercase absolute">
              or continue with username
            </span>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Username or Email</label>
              <div className="relative">
                <input
                  type="text"
                  placeholder="admin or organizer1"
                  {...register('username')}
                  className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2.5 text-sm text-white focus:outline-none focus:border-brand-500 pl-10"
                />
                <User className="w-4 h-4 text-slate-500 absolute left-3.5 top-3" />
              </div>
              {errors.username && <p className="text-red-400 text-[11px] mt-1">{errors.username.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Password</label>
              <div className="relative">
                <input
                  type="password"
                  placeholder="••••••••"
                  {...register('password')}
                  className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2.5 text-sm text-white focus:outline-none focus:border-brand-500 pl-10"
                />
                <Key className="w-4 h-4 text-slate-500 absolute left-3.5 top-3" />
              </div>
              {errors.password && <p className="text-red-400 text-[11px] mt-1">{errors.password.message}</p>}
            </div>

            <button
              type="submit"
              disabled={isLoading || isGoogleSigningIn}
              className="w-full py-2.5 px-4 rounded-xl font-bold text-sm bg-brand-500 hover:bg-brand-600 text-white shadow-lg shadow-brand-500/20 transition-all transform active:scale-95 disabled:opacity-50"
            >
              {isLoading ? 'Signing In...' : 'Sign In'}
            </button>
          </form>

          {/* Quick Demo Switcher */}
          <div className="pt-4 border-t border-slate-800 space-y-3">
            <span className="text-[10px] font-mono uppercase tracking-wider text-slate-400 block text-center">
              One-Click Role Demo Sign-in
            </span>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <button
                type="button"
                onClick={() => handleQuickDemo('admin')}
                className="p-2 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-slate-200 text-center transition-colors"
              >
                <span className="font-bold text-brand-400 block">Administrator</span>
                <span className="text-[10px] text-slate-500">admin</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickDemo('organizer1')}
                className="p-2 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-slate-200 text-center transition-colors"
              >
                <span className="font-bold text-sky-400 block">Organizer</span>
                <span className="text-[10px] text-slate-500">organizer1</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickDemo('referee1')}
                className="p-2 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-slate-200 text-center transition-colors"
              >
                <span className="font-bold text-amber-400 block">Referee</span>
                <span className="text-[10px] text-slate-500">referee1</span>
              </button>
              <button
                type="button"
                onClick={() => handleQuickDemo('player1')}
                className="p-2 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-slate-200 text-center transition-colors"
              >
                <span className="font-bold text-emerald-400 block">Player</span>
                <span className="text-[10px] text-slate-500">player1</span>
              </button>
            </div>
          </div>

          <div className="text-center text-xs text-slate-400">
            Need an account?{' '}
            <Link to="/register" className="text-brand-400 hover:underline font-semibold">
              Register here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
