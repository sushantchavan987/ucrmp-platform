import { useState, useEffect, useCallback } from 'react';
import { claimService } from '../services/claimService';
import { type ClaimResponse } from '../types/claim';
import { logger } from '../lib/utils';

export const useClaims = () => {
  const [claims, setClaims] = useState<ClaimResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const loadClaims = useCallback(async () => {
    setIsLoading(true);
    setError('');
    
    const controller = new AbortController();

    try {
      // Fetch data
      const data = await claimService.getMyClaims();
      
      if (!controller.signal.aborted) {
          // Sort Logic: Newest First (Safe Date Parsing)
          const sortedData = data.sort((a, b) => {
              const dateA = new Date(a.createdDate || a.createdAt || 0).getTime();
              const dateB = new Date(b.createdDate || b.createdAt || 0).getTime();
              return dateB - dateA;
          });
          
          setClaims(sortedData);
          // Only log in dev mode via smart logger
          logger.info(`📱 [Hook] Loaded ${data.length} claims`);
      }
    } catch (err) {
      if (!controller.signal.aborted) {
          logger.error("Failed to fetch claims", err);
          setError("Failed to load claims. Please check your connection.");
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
    loadClaims();
  }, [loadClaims]);

  return { claims, isLoading, error, refresh: loadClaims };
};