import { z } from 'zod';
import { CLAIM_TYPES } from '../utils/constants'; // ✅ Import Constants

// --- LOGIN SCHEMA ---
export const loginSchema = z.object({
  email: z.string().trim().min(1, "Required").email("Invalid email").max(255),
  password: z.string().min(1, "Required").min(8, "Min 8 chars").max(50)
});

export type LoginFormData = z.infer<typeof loginSchema>;

// --- REGISTRATION SCHEMA ---
export const registerSchema = z.object({
  firstName: z.string().trim().min(2, "First name is required"), // ✅ Added trim()
  lastName: z.string().trim().min(2, "Last name is required"),   // ✅ Added trim()
  email: z.string().trim().email("Invalid email address"),
  password: z.string().min(8, "Password must be at least 8 characters"),
  confirmPassword: z.string()
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords do not match",
  path: ["confirmPassword"],
});
export type RegisterFormData = z.infer<typeof registerSchema>;

// --- CLAIM SCHEMAS ---

const baseClaimSchema = z.object({
  amount: z.coerce.number().min(1, "Amount must be at least $1").max(100000, "Amount cannot exceed $100,000"),
  description: z.string().trim().min(5, "Description is too short"), // ✅ Added trim()
});

// 2. Travel Rules
export const travelClaimSchema = baseClaimSchema.extend({
  claimType: z.literal(CLAIM_TYPES.TRAVEL),
  metadata: z.object({
    hotelName: z.string().trim().min(2, "Hotel name is required"), // ✅ Added trim()
    flightNumber: z.string().trim().min(2, "Flight number is required") // ✅ Added trim()
  })
});

// 3. Medical Rules
export const medicalClaimSchema = baseClaimSchema.extend({
  claimType: z.literal(CLAIM_TYPES.MEDICAL),
  metadata: z.object({
    hospitalName: z.string().trim().min(2, "Hospital name is required"), // ✅ Added trim()
    prescriptionNumber: z.string().trim().min(5, "Rx Number must be 5+ chars") // ✅ Added trim()
  })
});

// 4. Entertainment Rules
export const entertainmentClaimSchema = baseClaimSchema.extend({
  claimType: z.literal(CLAIM_TYPES.ENTERTAINMENT), // ✅ Used Constant
  metadata: z.object({
    notes: z.string().optional()
  })
});

// 5. The Master Union
export const createClaimSchema = z.discriminatedUnion("claimType", [
  travelClaimSchema,
  medicalClaimSchema,
  entertainmentClaimSchema
]);

export type CreateClaimFormData = z.infer<typeof createClaimSchema>;