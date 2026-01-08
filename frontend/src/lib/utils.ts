import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

const isDev = import.meta.env.DEV; 

export const logger = {
  info: (...args: any[]) => { if (isDev) console.log(...args); },
  error: (...args: any[]) => { console.error(...args); },
  warn: (...args: any[]) => { if (isDev) console.warn(...args); }
};

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

const getLocale = () => {
  try {
    return navigator.language || 'en-US';
  } catch {
    return 'en-US';
  }
};

// ✅ FIX: Defensive Check for NaN/Null
export const formatCurrency = (amount: number | undefined | null) => {
  const value = amount || 0; // Fallback to 0
  
  return new Intl.NumberFormat(getLocale(), {
    style: 'currency',
    currency: 'USD',
  }).format(value);
};

export const formatDate = (dateString: string | undefined | null) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleDateString(getLocale(), {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  } catch {
    return 'Invalid Date';
  }
};