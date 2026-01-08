import { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { claimService } from '../services/claimService';
import { type ClaimResponse } from '../types/claim';
import { Button } from '../components/ui/Button';
import { Skeleton } from '../components/ui/Skeleton'; 
import { FileText, Clock, Plus, TrendingUp, RefreshCw } from 'lucide-react'; 
import { formatCurrency, logger, cn } from '../lib/utils';
import { useTitle } from '../hooks/useTitle';
import { StatCard } from '../components/dashboard/StatCard';
import { ClaimsTable } from '../components/dashboard/ClaimsTable'; // ✅ Import Table

const DashboardPage = () => {
  useTitle('Dashboard');
  const user = useAuthStore((state) => state.user);
  const displayName = user?.firstName 
    ? `${user.firstName} ${user.lastName}` 
    : (user?.sub || user?.email || 'User');

  const [claims, setClaims] = useState<ClaimResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // ✅ LOGIC: Optimized Data Fetching
  const loadData = useCallback(async () => {
    setIsLoading(true);
    setError('');
    
    const controller = new AbortController();

    try {
      // 🚀 PERFORMANCE FIX: Removed artificial setTimeout
      // The app will now feel instant on fast networks.
      
      const data = await claimService.getMyClaims();
      
      if (!controller.signal.aborted) {
          // Sort Newest First
          const sortedData = data.sort((a, b) => {
              const dateA = new Date(a.createdDate || a.createdAt || 0).getTime();
              const dateB = new Date(b.createdDate || b.createdAt || 0).getTime();
              return dateB - dateA;
          });
          
          setClaims(sortedData);
          logger.info(`📱 [UI] Loaded ${data.length} claims`);
      }
    } catch (err) {
      if (!controller.signal.aborted) {
          logger.error("Failed to fetch claims", err);
          setError("Failed to load your claims. Please check your connection.");
      }
    } finally {
      if (!controller.signal.aborted) {
          setIsLoading(false);
      }
    }

    return () => controller.abort();
  }, []);

  // Initial Load
  useEffect(() => {
    loadData();
  }, [loadData]);

  const totalAmount = claims.reduce((sum, claim) => sum + claim.amount, 0);
  
  return (
    <div className="w-full max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-8">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white/60 backdrop-blur-md p-6 rounded-2xl border border-slate-200/60 shadow-sm">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Overview</h1>
          <p className="text-slate-500 text-sm mt-1">
            Welcome back, <span className="font-semibold text-brand-600">{displayName}</span>
          </p>
        </div>
        
        <div className="flex items-center gap-3">
            <Button 
                variant="outline" 
                onClick={loadData} 
                disabled={isLoading}
                className="bg-white/50 border-slate-200 text-slate-600 hover:bg-white"
                title="Refresh Data"
            >
                <RefreshCw size={18} className={cn(isLoading && "animate-spin text-brand-600")} />
            </Button>
            
            <Link to="/create-claim">
                <Button className="shadow-lg shadow-brand-500/20 gap-2 w-full sm:w-auto px-6">
                    <Plus size={18} strokeWidth={2.5} />
                    New Claim
                </Button>
            </Link>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {isLoading ? (
           <>
             <Skeleton className="h-32 w-full" />
             <Skeleton className="h-32 w-full" />
             <Skeleton className="h-32 w-full" />
           </>
        ) : (
           <>
            <StatCard 
                title="Total Claims" 
                value={claims.length.toString()} 
                icon={<FileText size={24} />} 
                variant="brand" 
            />
            <StatCard 
                title="Pending Amount" 
                value={formatCurrency(totalAmount)} 
                icon={<Clock size={24} />} 
                variant="amber" 
            />
            <StatCard 
                title="Avg. Value" 
                value={claims.length > 0 ? formatCurrency(totalAmount / claims.length) : '$0.00'} 
                icon={<TrendingUp size={24} />} 
                variant="emerald" 
            />
           </>
        )}
      </div>

      {/* Recent Activity Table */}
      <div className="space-y-4">
        <h2 className="text-lg font-bold text-slate-900 px-1">Recent Activity</h2>
        
        {isLoading ? (
            <div className="space-y-3">
                <Skeleton className="h-16 w-full" />
                <Skeleton className="h-16 w-full" />
            </div>
        ) : error ? (
            <div className="bg-red-50 p-8 rounded-2xl border border-red-100 text-center animate-in fade-in">
                <div className="text-red-500 font-medium mb-4">{error}</div>
                <Button variant="outline" onClick={loadData} className="border-red-200 text-red-600 hover:bg-red-100 bg-white">
                    <RefreshCw size={16} className="mr-2" />
                    Try Again
                </Button>
            </div>
        ) : (
            // ✅ CLEAN: The Dashboard just passes data. The Table handles rendering.
            <ClaimsTable claims={claims} />
        )}
      </div>
    </div>
  );
};

export default DashboardPage;