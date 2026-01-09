// 1. The "User" object
// Represents the data inside the decoded JWT token
export interface User {
  userId: string;
  sub: string;      // The "Subject" (usually email)
  email?: string;   
  roles: string[];
  
  // --- NEW FIELDS ---
  firstName?: string; // Optional because old tokens might not have it
  lastName?: string;
  
  exp?: number;     // Expiration timestamp
}

// ... rest of the file (LoginRequest, RegisterRequest, etc.) stays the same
// (Ensure RegisterRequest still has firstName/lastName as we added earlier)
// trigger
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role?: string;
}

export interface AuthResponse {
  token: string;
}
