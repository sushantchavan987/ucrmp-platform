import React, { useId } from 'react';
import { cn } from '../../lib/utils';
import { ChevronDown } from 'lucide-react';

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options: { value: string; label: string }[];
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, className, options, ...props }, ref) => {
    
    // ✅ A11Y FIX: Unique IDs
    const uniqueId = useId();
    const errorId = `${uniqueId}-error`;

    return (
      <div className="space-y-1.5">
        {label && (
          <label htmlFor={uniqueId} className="block text-sm font-medium text-slate-700">
            {label}
          </label>
        )}
        
        <div className="relative">
          <select
            id={uniqueId}
            ref={ref}
            aria-invalid={!!error}
            aria-describedby={error ? errorId : undefined}
            className={cn(
              "flex h-10 w-full appearance-none rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 disabled:cursor-not-allowed disabled:opacity-50 disabled:bg-slate-50 transition-all duration-200",
              error && "border-red-500 focus:ring-red-500/20 focus:border-red-500",
              className
            )}
            {...props}
          >
            {options.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
          
          <div className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-slate-500">
            <ChevronDown size={16} strokeWidth={2} />
          </div>
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

Select.displayName = "Select";