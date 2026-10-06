import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { registerUser, clearAuthError } from '../store/authSlice';
import { Trophy, UserPlus } from 'lucide-react';

const registerSchema = z.object({
  fullName: z.string().min(3, 'Full name is required'),
  username: z.string().min(3, 'Username must be at least 3 characters'),
  email: z.string().email('Valid email is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  role: z.string().default('ROLE_ORGANIZER'),
  phone: z.string().optional(),
});

type RegisterFormValues = z.infer<typeof registerSchema>;

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const { isAuthenticated, isLoading, error } = useSelector((state: RootState) => state.auth);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      role: 'ROLE_ORGANIZER',
    },
  });

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
    return () => {
      dispatch(clearAuthError());
    };
  }, [isAuthenticated, navigate, dispatch]);

  const onSubmit = (data: RegisterFormValues) => {
    dispatch(registerUser(data));
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center p-4">
      <div className="w-full max-w-md space-y-6">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-brand-600 to-emerald-400 flex items-center justify-center shadow-xl shadow-brand-500/25 mx-auto">
            <Trophy className="w-6 h-6 text-white" />
          </div>
          <h1 className="text-2xl font-black tracking-tight text-white">Create SportX Account</h1>
          <p className="text-xs text-slate-400">Join the smart collegiate sports platform</p>
        </div>

        <div className="rounded-3xl glass-panel-glow border border-slate-700/80 p-8 shadow-2xl space-y-5">
          {error && (
            <div className="p-3 rounded-xl bg-red-900/40 border border-red-500/50 text-xs text-red-300">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Full Name</label>
              <input
                type="text"
                placeholder="Dr. K. Annamalai"
                {...register('fullName')}
                className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
              {errors.fullName && <p className="text-red-400 text-[11px] mt-1">{errors.fullName.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Username</label>
                <input
                  type="text"
                  placeholder="annamalai_k"
                  {...register('username')}
                  className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                />
                {errors.username && <p className="text-red-400 text-[11px] mt-1">{errors.username.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Role</label>
                <select
                  {...register('role')}
                  className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
                >
                  <option value="ROLE_ORGANIZER">Organizer</option>
                  <option value="ROLE_REFEREE">Match Referee</option>
                  <option value="ROLE_PLAYER">Student Athlete</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Email Address</label>
              <input
                type="email"
                placeholder="annamalai@college.edu.in"
                {...register('email')}
                className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
              {errors.email && <p className="text-red-400 text-[11px] mt-1">{errors.email.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Password</label>
              <input
                type="password"
                placeholder="••••••••"
                {...register('password')}
                className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
              {errors.password && <p className="text-red-400 text-[11px] mt-1">{errors.password.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Contact Phone</label>
              <input
                type="text"
                placeholder="+91 98765 43210"
                {...register('phone')}
                className="w-full bg-slate-900/90 border border-slate-700 rounded-xl px-3.5 py-2 text-sm text-white focus:outline-none focus:border-brand-500"
              />
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-2.5 px-4 rounded-xl font-bold text-sm bg-brand-500 hover:bg-brand-600 text-white shadow-lg shadow-brand-500/20 transition-all transform active:scale-95 disabled:opacity-50"
            >
              {isLoading ? 'Registering...' : 'Create Account'}
            </button>
          </form>

          <div className="text-center text-xs text-slate-400 pt-2 border-t border-slate-800">
            Already have an account?{' '}
            <Link to="/login" className="text-brand-400 hover:underline font-semibold">
              Sign in
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
