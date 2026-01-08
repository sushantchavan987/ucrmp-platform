import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { Home, SearchX, LayoutDashboard } from 'lucide-react';
import { useAuthStore } from '../store/authStore'; // ✅ Import Store

const NotFoundPage = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  // ✅ LOGIC: Dynamic Destination based on Auth status
  const destination = isAuthenticated ? "/dashboard" : "/";
  const label = isAuthenticated ? "Back to Dashboard" : "Back to Home";
  const Icon = isAuthenticated ? LayoutDashboard : Home;

  return (
    <div className="min-h-[80vh] flex flex-col items-center justify-center text-center px-4">
      <div className="relative mb-8">
        <div className="absolute inset-0 bg-brand-100 rounded-full animate-ping opacity-75"></div>
        <div className="relative bg-white p-6 rounded-full shadow-xl shadow-brand-500/20 border border-slate-100">
          <SearchX size={64} className="text-brand-600" strokeWidth={1.5} />
        </div>
      </div>

      <h1 className="text-4xl md:text-6xl font-bold text-slate-900 tracking-tight mb-4">
        Page not found
      </h1>
      
      <p className="text-lg text-slate-500 max-w-md mb-10 leading-relaxed">
        Sorry, we couldn't find the page you're looking for. It might have been moved or doesn't exist.
      </p>

      <Link to={destination}>
        <Button className="h-12 px-8 text-base shadow-brand-500/25 gap-2">
          <Icon size={20} />
          {label}
        </Button>
      </Link>
    </div>
  );
};

export default NotFoundPage;