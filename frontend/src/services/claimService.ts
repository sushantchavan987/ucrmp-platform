import apiClient from './apiClient';
import type { CreateClaimRequest, ClaimResponse } from '../types/claim';
import { logger } from '../lib/utils'; // ✅ Import Logger

export const claimService = {
  
  createClaim: async (data: CreateClaimRequest): Promise<ClaimResponse> => {
    logger.info("📡 [API] Creating Claim:", data); // ✅ Smart Log
    
    try {
      const response = await apiClient.post<ClaimResponse>('/claims', data);
      logger.info("✅ [API] Claim Created! ID:", response.data.id); // ✅ Smart Log
      return response.data;
    } catch (error) {
      console.error("❌ [API] Failed to create claim:", error);
      throw error;
    }
  },

  getMyClaims: async (): Promise<ClaimResponse[]> => {
    const response = await apiClient.get<ClaimResponse[]>('/claims');
    return response.data;
  }
};