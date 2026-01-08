import React from 'react';
import { cn } from '../../lib/utils';

interface StatCardProps {
  title: string;
  value: string;
  icon: React.ReactNode;
  variant?: 'brand' | 'amber' | 'emerald'; // ✅ Semantic Variants, not CSS classes
}

export const StatCard: React.FC<StatCardProps> = ({ 
  title, 
  value, 
  icon, 
  variant = 'brand' 
}) => {
  
  // Encapsulated Styling Logic
  const variants = {
    brand: "bg-brand-50 border-brand-100 text-brand-600",
    amber: "bg-amber-50 border-amber-100 text-amber-600",
    emerald: "bg-emerald-50 border-emerald-100 text-emerald-600",
  };

  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-all duration-200">
      <div className="flex items-start justify-between mb-4">
          <div>
              <p className="text-sm font-medium text-slate-500">{title}</p>
              <h3 className="text-2xl font-bold text-slate-900 mt-1">{value}</h3>
          </div>
          <div className={cn("p-3 rounded-xl border", variants[variant])}>
              {icon}
          </div>
      </div>
    </div>
  );
};