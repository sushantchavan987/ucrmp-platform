import React, { useState, useId } from 'react'; // ✅ Import useId
import { Eye, EyeOff } from 'lucide-react';
import { cn } from '../../lib/utils';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  prefixIcon?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, className, type, prefixIcon, onKeyDown, ...props }, ref) => {
    
    const [showPassword, setShowPassword] = useState(false);
    const inputType = type === 'password' && showPassword ? 'text' : type;
    
    // ✅ A11Y FIX: Generate unique ID for error message association
    const uniqueId = useId();
    const errorId = `${uniqueId}-error`;

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (type === 'number') {
        if (['e', 'E', '+', '-'].includes(e.key)) {
          e.preventDefault();
        }
      }
      if (onKeyDown) onKeyDown(e);
    };

    return (
      <div className="space-y-1.5">
        {label && (
          <label htmlFor={uniqueId} className="block text-sm font-medium text-slate-700">
            {label}
          </label>
        )}
        
        <div className="relative">
          {prefixIcon && (
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none flex items-center">
              {prefixIcon}
            </div>
          )}

          <input
            id={uniqueId}
            ref={ref}
            type={inputType}
            onWheel={(e) => e.currentTarget.blur()} 
            onKeyDown={handleKeyDown}
            // ✅ A11Y: Link input to error message
            aria-invalid={!!error}
            aria-describedby={error ? errorId : undefined}
            className={cn(
              "flex h-10 w-full rounded-lg border border-slate-300 bg-white py-2 text-sm text-slate-900 placeholder:text-slate-400",
              "focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500",
              "disabled:cursor-not-allowed disabled:opacity-50 transition-all duration-200",
              prefixIcon ? "pl-9 pr-3" : "px-3", 
              error && "border-red-500 focus:ring-red-500/20 focus:border-red-500",
              className
            )}
            {...props}
          />

          {type === 'password' && (
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 focus:outline-none transition-colors p-1"
              tabIndex={-1}
              aria-label={showPassword ? "Hide password" : "Show password"}
              aria-pressed={showPassword}
            >
              {showPassword ? (
                <EyeOff size={18} strokeWidth={2} />
              ) : (
                <Eye size={18} strokeWidth={2} />
              )}
            </button>
          )}
        </div>

        {error && (
          <p id={errorId} className="text-xs font-medium text-red-600 animate-in slide-in-from-top-1">
            {error}
          </p>
        )}
      </div>
    );
  }
);

Input.displayName = "Input";