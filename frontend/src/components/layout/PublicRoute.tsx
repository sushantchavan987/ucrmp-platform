import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';

export const PublicRoute = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  // If authenticated, force redirect to Dashboard.
  // Otherwise, render the child route (Login/Register).
  return isAuthenticated ? <Navigate to="/dashboard" replace /> : <Outlet />;
};