import axios from 'axios';
import { toast } from 'react-hot-toast'; // ✅ Import Toast
import { API_BASE_URL, TOKEN_KEY, USER_KEY } from '../utils/constants';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // 1. Handle 401 (Force Logout)
    if (error.response && error.response.status === 401) {
      console.error('[API] Unauthorized! Force logging out...');
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    
    // 2. ✅ Handle 500 (Server Crash) - Global Notification
    if (error.response && error.response.status >= 500) {
        toast.error("Server error. Please try again later.", {
            id: 'server-error', // Prevents duplicate toasts
        });
    }

    // 3. Handle Network Timeout
    if (error.code === 'ECONNABORTED') {
      console.error('[API] Request timed out');
      toast.error("Connection timed out. Check your internet.");
    }

    return Promise.reject(error);
  }
);

export default apiClient;