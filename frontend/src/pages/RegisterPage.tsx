import { useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'react-hot-toast';
import { AxiosError } from 'axios';
import { registerSchema, type RegisterFormData } from '../lib/schemas';
import { authService } from '../services/authService';
// REMOVED: import { useAuthStore } from '../store/authStore'; // Not needed anymore
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useTitle } from '../hooks/useTitle';
import { logger } from '../lib/utils';

const RegisterPage = () => {
  useTitle('Create Account');
  const navigate = useNavigate();
  // REMOVED: const login = useAuthStore((state) => state.login);

  const { 
    register, 
    handleSubmit, 
    formState: { errors, isSubmitting }
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    mode: "onBlur"
  });

  const onSubmit = async (data: RegisterFormData) => {
    await toast.promise(
      (async () => {
        logger.info("📝 Starting registration for:", data.email);
        
        // 1. Call Register API
        await authService.register({
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.email,
          password: data.password,
          role: 'ROLE_USER'
        });

        // 2. DO NOT AUTO-LOGIN (This was the cause of the error)
        // login(response.token); <--- DELETED THIS LINE

        // 3. Redirect to Login Page
        setTimeout(() => navigate('/login'), 1500);
        
        return "Success";
      })(),
      {
        loading: 'Creating your secure account...',
        success: 'Account created! Please log in.', // Updated message
        error: (err) => {
            const error = err as AxiosError;
            logger.error("Registration failed", error);
            
            if(error.response?.status === 500 || error.response?.status === 409) {
                return "Email already registered. Try logging in.";
            }
            return "Registration failed. Please try again.";
        }
      }
    );
  };

  useEffect(() => { logger.info("📱 [UI] Register Page Mounted"); }, []);

  return (
    <div className="w-full max-w-[550px] bg-white/80 backdrop-blur-xl rounded-2xl shadow-2xl shadow-slate-200/50 p-8 border border-white/20 ring-1 ring-slate-900/5 my-8">
      
      <div className="text-center mb-8">
        <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-brand-50 text-brand-600 mb-4">
             <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" x2="20" y1="8" y2="14"/><line x1="23" x2="17" y1="11" y2="11"/></svg>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Create Account</h1>
        <p className="text-slate-500 mt-2 text-sm">Join UCRMP to manage your claims</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="First Name"
            autoFocus
            placeholder="John"
            {...register('firstName')}
            error={errors.firstName?.message}
          />
          <Input
            label="Last Name"
            placeholder="Doe"
            {...register('lastName')}
            error={errors.lastName?.message}
          />
        </div>

        <Input
          label="Email Address"
          type="email"
          placeholder="john@example.com"
          {...register('email')}
          error={errors.email?.message}
        />

        <Input
          label="Password"
          type="password"
          placeholder="••••••••"
          {...register('password')}
          error={errors.password?.message}
        />

        <Input
          label="Confirm Password"
          type="password"
          placeholder="••••••••"
          {...register('confirmPassword')}
          error={errors.confirmPassword?.message}
        />

        <Button type="submit" className="w-full h-11 text-base shadow-brand-500/20 shadow-lg" isLoading={isSubmitting}>
          Create Account
        </Button>
      </form>

      <div className="mt-6 text-center text-sm text-slate-500">
        Already have an account?{' '}
        <Link to="/login" className="text-brand-600 font-semibold hover:underline">
          Sign in
        </Link>
      </div>

    </div>
  );
};

export default RegisterPage;