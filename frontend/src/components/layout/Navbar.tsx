import { useState, useEffect, useRef } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { Button } from '../ui/Button';
import { 
  LayoutDashboard, LogOut, ShieldCheck, PlusCircle, Menu, X 
} from 'lucide-react'; 
import { toast } from 'react-hot-toast';
import { cn } from '../../lib/utils';

export const Navbar = () => {
  const { isAuthenticated, logout, user } = useAuthStore();
  const location = useLocation();
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const mobileMenuRef = useRef<HTMLDivElement>(null);

  const handleLogout = () => {
    logout();
    toast.success('Logged out successfully', {
      icon: '👋',
      duration: 3000, // ✅ Ensure toast stays visible
    });
    setIsMobileMenuOpen(false);
    // Small delay to let the state clear cleanly before navigation
    setTimeout(() => navigate('/login'), 100);
  };

  const isActive = (path: string) => location.pathname === path;

  const getInitials = () => {
    if (user?.firstName && user?.lastName) {
        return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
    }
    const fallback = user?.email || user?.sub || 'User';
    return fallback[0].toUpperCase();
  };

  const closeMenu = () => setIsMobileMenuOpen(false);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (mobileMenuRef.current && !mobileMenuRef.current.contains(event.target as Node)) {
        setIsMobileMenuOpen(false);
      }
    };

    if (isMobileMenuOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isMobileMenuOpen]);

  return (
    <nav className="fixed top-0 w-full z-50 bg-white/80 backdrop-blur-md border-b border-slate-200/60 transition-all duration-300 supports-[backdrop-filter]:bg-white/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          
          <Link to="/" className="flex items-center gap-2.5 group" onClick={closeMenu}>
            <div className="w-9 h-9 bg-gradient-to-br from-brand-500 to-brand-700 rounded-xl flex items-center justify-center text-white shadow-lg shadow-brand-500/20 group-hover:scale-105 transition-transform duration-200">
              <ShieldCheck size={20} strokeWidth={2.5} />
            </div>
            <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-slate-800 to-slate-600 tracking-tight">
              UCRMP
            </span>
          </Link>

          {/* Desktop Menu */}
          <div className="hidden md:flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <Link 
                  to="/dashboard" 
                  className={cn(
                    "flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200",
                    isActive('/dashboard') ? "bg-brand-50 text-brand-700 ring-1 ring-brand-200" : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                  )}
                >
                  <LayoutDashboard size={18} />
                  <span className="hidden sm:inline">Dashboard</span>
                </Link>

                 <Link 
                  to="/create-claim" 
                  className={cn(
                    "flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200",
                    isActive('/create-claim') ? "bg-brand-50 text-brand-700 ring-1 ring-brand-200" : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                  )}
                >
                  <PlusCircle size={18} />
                  <span className="hidden sm:inline">New Claim</span>
                </Link>
                
                <div className="h-6 w-px bg-slate-200 mx-1" /> 

                <div 
                    className="hidden md:flex items-center gap-2 text-sm text-slate-500 mr-2 cursor-help" 
                    title={user?.email || user?.sub}
                >
                  <div className="w-8 h-8 bg-brand-50 rounded-full flex items-center justify-center text-brand-700 font-bold border border-brand-100 shadow-sm">
                    {getInitials()}
                  </div>
                </div>

                <Button 
                  variant="outline" 
                  onClick={handleLogout} 
                  className="gap-2 h-9 px-3 border-slate-200 text-slate-600 hover:bg-red-50 hover:text-red-600 hover:border-red-200"
                >
                  <LogOut size={16} />
                  <span className="hidden sm:inline">Sign Out</span>
                </Button>
              </>
            ) : (
              <div className="flex gap-3">
                <Link to="/login">
                  <Button variant="ghost" className="text-slate-600 hover:text-slate-900 hover:bg-slate-50">
                    Sign In
                  </Button>
                </Link>
                <Link to="/register">
                  <Button className="shadow-lg shadow-brand-500/25">
                    Get Started
                  </Button>
                </Link>
              </div>
            )}
          </div>

          {/* Mobile Toggle */}
          <div className="md:hidden">
            <button 
                onClick={(e) => {
                    e.stopPropagation();
                    setIsMobileMenuOpen(!isMobileMenuOpen);
                }} 
                className="p-2 text-slate-600 hover:bg-slate-100 rounded-lg"
            >
                {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMobileMenuOpen && (
        <div 
            ref={mobileMenuRef}
            className="md:hidden bg-white border-t border-slate-100 p-4 shadow-xl absolute w-full left-0 top-16 flex flex-col gap-3 animate-in slide-in-from-top-5"
        >
            {isAuthenticated ? (
                <>
                    <div className="flex items-center gap-3 px-3 py-2 bg-slate-50 rounded-lg border border-slate-100 mb-2">
                        <div className="w-8 h-8 bg-brand-100 text-brand-700 rounded-full flex items-center justify-center font-bold">
                            {getInitials()}
                        </div>
                        <div className="flex flex-col">
                            <span className="text-sm font-bold text-slate-900">{user?.firstName} {user?.lastName}</span>
                            <span className="text-xs text-slate-500">{user?.email || user?.sub}</span>
                        </div>
                    </div>
                    <Link to="/dashboard" onClick={closeMenu} className="flex items-center gap-3 px-3 py-3 rounded-lg hover:bg-slate-50 text-slate-700 font-medium">
                        <LayoutDashboard size={20} /> Dashboard
                    </Link>
                    <Link to="/create-claim" onClick={closeMenu} className="flex items-center gap-3 px-3 py-3 rounded-lg hover:bg-slate-50 text-slate-700 font-medium">
                        <PlusCircle size={20} /> New Claim
                    </Link>
                    <div className="h-px bg-slate-100 my-1" />
                    <button onClick={handleLogout} className="flex items-center gap-3 px-3 py-3 rounded-lg hover:bg-red-50 text-red-600 font-medium w-full text-left">
                        <LogOut size={20} /> Sign Out
                    </button>
                </>
            ) : (
                <>
                    <Link to="/login" onClick={closeMenu} className="w-full"><Button variant="outline" className="w-full justify-center">Sign In</Button></Link>
                    <Link to="/register" onClick={closeMenu} className="w-full"><Button className="w-full justify-center">Get Started</Button></Link>
                </>
            )}
        </div>
      )}
    </nav>
  );
};