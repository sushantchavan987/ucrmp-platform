import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { ShieldCheck, Zap, CheckCircle2, ArrowRight } from 'lucide-react'; 
import { useTitle } from '../hooks/useTitle'; // Import Hook

const LandingPage = () => {
  useTitle('Home'); // Set Browser Tab Name

  return (
    <div className="relative">
      
      {/* HERO SECTION */}
      <div className="w-full max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8 pt-20 pb-24 lg:pt-32 lg:pb-40 text-center">
        
        <div className="max-w-4xl mx-auto">
          
          {/* ❌ REMOVED: The "v1.0 is live" banner div was deleted from here. 
             The design is now cleaner and focuses strictly on the Headline.
          */}

          <h1 className="text-5xl sm:text-6xl md:text-7xl font-extrabold text-slate-900 tracking-tight mb-8 leading-[1.1]">
            Claims Management <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-600 to-purple-600">
              Reimagined.
            </span>
          </h1>
          
          <p className="mt-6 max-w-2xl mx-auto text-lg sm:text-xl text-slate-600 mb-10 leading-relaxed">
            The secure, type-safe platform for managing travel, medical, and corporate reimbursements. Built for speed, designed for humans, scaled for the Enterprise.
          </p>

          <div className="flex flex-col sm:flex-row justify-center gap-4 animate-in fade-in slide-in-from-bottom-8 duration-1000 fill-mode-backwards">
            <Link to="/register">
              <Button className="h-14 px-8 text-lg shadow-brand-500/40 shadow-xl w-full sm:w-auto gap-2">
                Start Free Trial <ArrowRight size={18} />
              </Button>
            </Link>
            <Link to="/login">
              <Button variant="outline" className="h-14 px-8 text-lg w-full sm:w-auto bg-white/50 backdrop-blur-sm">
                Live Demo
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* FEATURES GRID */}
      <div className="bg-white/60 backdrop-blur-md border-t border-slate-200/60 py-24">
        <div className="w-full max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-3 gap-8">
            <FeatureCard 
              title="Smart Security" 
              desc="Powered by JWT and Spring Security. Your data is encrypted at rest and in transit using banking-grade protocols."
              icon={<ShieldCheck className="text-brand-600" size={32} />}
            />
            <FeatureCard 
              title="Dynamic Claims" 
              desc="Intelligent forms that adapt to your needs—Travel, Medical, and Entertainment. No more clutter."
              icon={<Zap className="text-amber-500" size={32} />}
            />
            <FeatureCard 
              title="Instant Approval" 
              desc="Automated workflows ensure you get paid faster than ever before. Real-time status tracking included."
              icon={<CheckCircle2 className="text-emerald-500" size={32} />}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

const FeatureCard = ({ title, desc, icon }: { title: string, desc: string, icon: React.ReactNode }) => (
  <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-100 hover:shadow-md hover:-translate-y-1 transition-all duration-300 group">
    <div className="mb-6 p-4 bg-slate-50 rounded-2xl inline-block group-hover:bg-brand-50 transition-colors">{icon}</div>
    <h3 className="text-xl font-bold text-slate-900 mb-3">{title}</h3>
    <p className="text-slate-500 leading-relaxed">{desc}</p>
  </div>
);

export default LandingPage;