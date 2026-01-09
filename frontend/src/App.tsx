import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { MainLayout } from './components/layout/MainLayout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';
import { PublicRoute } from './components/layout/PublicRoute';
import { ScrollToTop } from './components/layout/ScrollToTop';
import { OfflineBanner } from './components/layout/OfflineBanner'; // ✅ Import
import { Skeleton } from './components/ui/Skeleton';

const LoginPage = lazy(() => import('./pages/LoginPage'));
const LandingPage = lazy(() => import('./pages/LandingPage'));
const DashboardPage = lazy(() => import('./pages/DashboardPage'));
const CreateClaimPage = lazy(() => import('./pages/CreateClaimPage'));
const RegisterPage = lazy(() => import('./pages/RegisterPage'));
const NotFoundPage = lazy(() => import('./pages/NotFoundPage'));

// Trigger the CI
const PageLoader = () => (
  <div className="p-8 space-y-4 max-w-4xl mx-auto">
    <Skeleton className="h-12 w-1/3 mb-8" />
    <Skeleton className="h-64 w-full rounded-2xl" />
  </div>
);

function App() {
  return (
    <BrowserRouter>
      <ScrollToTop />
      {/* ✅ NEW: Global Offline Warning */}
      <OfflineBanner />
      
      <MainLayout>
        <Suspense fallback={<PageLoader />}>
          <Routes>
            {/* Public Routes */}
            <Route element={<PublicRoute />}>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
            </Route>

            {/* Landing */}
            <Route path="/" element={<LandingPage />} />
            
            {/* Protected Routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/create-claim" element={<CreateClaimPage />} />
            </Route>

            {/* Catch-All */}
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </Suspense>
      </MainLayout>
    </BrowserRouter>
  );
}

export default App;