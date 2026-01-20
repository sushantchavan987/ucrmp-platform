import { useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'react-hot-toast';
import { AxiosError } from 'axios';
import { loginSchema, type LoginFormData } from '../lib/schemas';
import { useAuthStore } from '../store/authStore';
import { authService } from '../services/authService';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useTitle } from '../hooks/useTitle';
import { logger } from '../lib/utils';

const LoginPage = () => {
  useTitle('Sign In');
  const navigate = useNavigate();
  const location = useLocation();
  const login = useAuthStore((state) => state.login);

  // Determine where to redirect after login (default to dashboard)
  const from = location.state?.from?.pathname || "/dashboard";

  const { 
    register, 
    handleSubmit, 
    formState: { errors, isSubmitting } 
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    mode: "onBlur"
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      logger.info("🚀 [Login] Attempting login for:", data.email);
      
      const response = await authService.login(data);

      // 🔍 DEBUGGING: See exactly what the backend sent
      console.log("🔥 [DEBUG] Raw Backend Response:", response);

      // ✅ ROBUST TOKEN EXTRACTION
      // Checks for 'token', 'accessToken', or if the response itself is the string
      // Also handles cases where Axios might return the full object (response.data)
      // @ts-ignore - responding to dynamic backend data
      const token = response?.token || 
                    // @ts-ignore
                    response?.accessToken || 
                    // @ts-ignore
                    response?.data?.token ||
                    (typeof response === 'string' ? response : null);

      console.log("🔥 [DEBUG] Extracted Token:", token);

      if (!token) {
        console.error("❌ [Login] Token is undefined. Backend response structure mismatch.");
        toast.error("Login successful, but token missing. Check console.");
        return;
      }

      // 1. Save to Store (LocalStorage + State)
      login(token);
      
      // 2. Success Feedback
      toast.success("Welcome back!", { duration: 3000 });
      
      // 3. Redirect
      console.log("➡️ [Login] Redirecting to:", from);
      navigate(from, { replace: true });
      
    } catch (err) {
      const error = err as AxiosError;
      logger.error("❌ [Login] Failed:", error);
      
      // Friendly error message based on status code
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Invalid email or password");
      } else if (error.code === "ERR_NETWORK") {
        toast.error("Unable to connect to server. Is the backend running?");
      } else {
        toast.error("Something went wrong. Please try again.");
      }
    }
  };

  useEffect(() => { 
    logger.info("📱 [UI] Login Page Mounted"); 
    // Clear any existing stale sessions when landing on login page
    // useAuthStore.getState().logout(); 
  }, []);

  return (
    <div className="w-full max-w-[450px] bg-white/80 backdrop-blur-xl rounded-2xl shadow-2xl shadow-slate-200/50 p-8 border border-white/20 ring-1 ring-slate-900/5">
      
      <div className="text-center mb-8">
        <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-brand-50 text-brand-600 mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" x2="3" y1="12" y2="12"/></svg>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Welcome Back</h1>
        <p className="text-slate-500 mt-2 text-sm">Sign in to access your workspace</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        
        <Input
          label="Email Address"
          autoFocus
          type="email"
          disabled={isSubmitting}
          placeholder="admin@ucrmp.com"
          {...register('email')}
          error={errors.email?.message}
        />

        <Input
          label="Password"
          type="password"
          disabled={isSubmitting}
          placeholder="••••••••"
          {...register('password')}
          error={errors.password?.message}
        />

        <div className="flex items-center justify-between text-sm">
          <label className="flex items-center gap-2 text-slate-600 cursor-pointer select-none">
            <input type="checkbox" className="rounded border-slate-300 text-brand-600 focus:ring-brand-500" />
            Remember me
          </label>
          <a href="#" className="text-brand-600 hover:underline font-medium">Forgot password?</a>
        </div>

        <Button type="submit" className="w-full h-11 text-base shadow-brand-500/20 shadow-lg" isLoading={isSubmitting}>
          Sign In
        </Button>
      </form>

      <div className="mt-8 pt-6 border-t border-slate-100 text-center text-sm text-slate-500">
        Don't have an account?{' '}
        <Link to="/register" className="text-brand-600 hover:underline font-semibold">
          Create an account
        </Link>
      </div>

    </div>
  );
};

export default LoginPage;