// ... existing API constants ...

export const API_BASE_URL = '/api/v1'; // (Or your window logic)
export const TOKEN_KEY = 'ucrmp_token';
export const USER_KEY = 'ucrmp_user';

// ✅ NEW: Centralized App Name
export const APP_NAME = "UCRMP";

export const CLAIM_TYPES = {
  TRAVEL: 'TRAVEL',
  MEDICAL: 'MEDICAL',
  ENTERTAINMENT: 'ENTERTAINMENT',
} as const;

export const CLAIM_TYPE_OPTIONS = [
  { value: CLAIM_TYPES.TRAVEL, label: 'Travel Expense' },
  { value: CLAIM_TYPES.MEDICAL, label: 'Medical / Health' },
  { value: CLAIM_TYPES.ENTERTAINMENT, label: 'Entertainment' },
];