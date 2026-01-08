import apiClient from './apiClient';
import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';
import { logger } from '../lib/utils'; // ✅ Import Logger

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    logger.info("📡 [API] Sending Login Request:", data.email); // ✅ Smart Log
    try {
      const response = await apiClient.post<AuthResponse>('/auth/login', data);
      return response.data;
    } catch (error) {
      // We keep console.error for critical failures
      console.error("❌ [API] Login Request Failed:", error);
      throw error;
    }
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    logger.info("📡 [API] Sending Register Request for:", data.email); // ✅ Smart Log
    const response = await apiClient.post<AuthResponse>('/auth/register', data);
    return response.data;
  }
};