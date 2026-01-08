import { create } from 'zustand';
import { jwtDecode } from 'jwt-decode';
import type { User } from '../types/auth';
import { TOKEN_KEY, USER_KEY } from '../utils/constants';
import { logger } from '../lib/utils';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  
  // Actions
  login: (token: string) => void;
  logout: () => void;
  restoreSession: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,

  login: (token: string) => {
    // 1. Persist raw token
    localStorage.setItem(TOKEN_KEY, token);

    try {
      // 2. Decode payload
      const decodedUser = jwtDecode<User>(token);
      
      // Optional: Persist user data for faster read next time (caching)
      localStorage.setItem(USER_KEY, JSON.stringify(decodedUser));

      // 3. Update State
      set({ 
        user: decodedUser, 
        token: token, 
        isAuthenticated: true 
      });
      
      logger.info("✅ [Auth] Session Established:", decodedUser.sub);

    } catch (error) {
      console.error("❌ [Auth] Failed to decode token:", error);
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      set({ user: null, token: null, isAuthenticated: false });
    }
  },

  logout: () => {
    console.log("👋 [Auth] Logging out...");
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    set({ user: null, token: null, isAuthenticated: false });
  },

  restoreSession: () => {
    // Attempt to read from storage on page load
    const token = localStorage.getItem(TOKEN_KEY);
    
    if (token) {
      try {
        const decodedUser = jwtDecode<User>(token);
        
        // Check if token is expired
        const currentTime = Date.now() / 1000;
        if (decodedUser.exp && decodedUser.exp < currentTime) {
          console.warn("⚠️ [Auth] Token expired during restoration");
          localStorage.removeItem(TOKEN_KEY);
          set({ user: null, token: null, isAuthenticated: false });
          return;
        }

        set({ 
          user: decodedUser, 
          token: token, 
          isAuthenticated: true 
        });
        
      } catch (error) {
        console.error("❌ [Auth] Corrupt token in storage");
        localStorage.removeItem(TOKEN_KEY);
        set({ user: null, token: null, isAuthenticated: false });
      }
    }
  }
}));

// ✅ TOP 1% PROTOCOL: Immediate Hydration
// This runs as soon as the JavaScript file is loaded by the browser.
// It ensures the user state is ready BEFORE React even renders the first component.
try {
  useAuthStore.getState().restoreSession();
} catch (err) {
  console.error("Failed to hydrate auth session", err);
}