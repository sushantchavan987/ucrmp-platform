// 1. The Types of Claims we support
export type ClaimType = 'TRAVEL' | 'MEDICAL' | 'ENTERTAINMENT';

// 2. The Contracts for the Dynamic Data
export interface TravelMetadata {
  hotelName: string;
  flightNumber: string;
}

export interface MedicalMetadata {
  hospitalName: string;
  prescriptionNumber: string;
}

export interface EntertainmentMetadata {
  notes?: string; 
}

// 3. The "Master" Request Object
// Changed 'any' to 'unknown' to satisfy linter
export interface CreateClaimRequest<T = unknown> {
  claimType: ClaimType;
  amount: number;
  description: string;
  metadata: T; 
}

// 4. The Response Object
// ... (Imports and other types remain same)

export interface ClaimResponse {
  id: string;
  claimType: ClaimType;
  amount: number;
  description: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  // ✅ FIX: Support both naming conventions just in case
  createdDate?: string;
  createdAt?: string; 
  metadata?: TravelMetadata | MedicalMetadata | EntertainmentMetadata; 
}