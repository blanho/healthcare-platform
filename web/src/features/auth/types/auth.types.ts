

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  roles?: string[];
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: string;
  roles: string[];
  permissions: string[];
  mfaRequired?: boolean;
  mfaToken?: string;
}

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  phoneNumber: string;
  status: UserStatus;
  emailVerified: boolean;
  mfaEnabled: boolean;
  patientId: string | null;
  providerId: string | null;
  roles: string[];
  permissions: string[];
  lastLoginAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'PENDING_VERIFICATION';

export interface MfaSetupResponse {
  secret: string;
  qrCodeUri: string;
  issuer: string;
  accountName: string;
}

export interface MfaSetupRequest {
  secret: string;
  code: string;
}

export interface MfaVerifyRequest {
  code: string;
  useBackupCode?: boolean;
}

export interface MfaDisableRequest {
  password: string;
}

export interface BackupCodesResponse {
  backupCodes: string[];
  message: string;
}

export interface MfaVerifyResponse {
  valid: boolean;
}

export interface MfaStatusResponse {
  enabled: boolean;
  backupCodesRemaining?: number;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface PasswordStrengthResponse {
  score: number;
  strength: 'WEAK' | 'FAIR' | 'GOOD' | 'STRONG';
  suggestions: string[];
  meetsRequirements: boolean;
}

export interface MessageResponse {
  message: string;
}

export interface SessionResponse {
  id: string;
  deviceName: string | null;
  deviceType: string | null;
  browser: string | null;
  operatingSystem: string | null;
  ipAddress: string;
  location: string | null;
  lastActivityAt: string;
  current: boolean;
}

export interface LoginAttemptResponse {
  id: string;
  ipAddress: string;
  userAgent: string;
  status: LoginAttemptStatus;
  attemptedAt: string;
  location: string | null;
  successful: boolean;
}

export type LoginAttemptStatus =
  | 'SUCCESS'
  | 'FAILED_INVALID_CREDENTIALS'
  | 'FAILED_ACCOUNT_LOCKED'
  | 'FAILED_ACCOUNT_DISABLED'
  | 'FAILED_EMAIL_NOT_VERIFIED'
  | 'FAILED_MFA_REQUIRED'
  | 'FAILED_MFA_INVALID';

export interface VerifyEmailRequest {
  token: string;
}

export interface EmailVerificationStatusResponse {
  verified: boolean;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  roles?: string[];
  emailVerified?: boolean;
  mustChangePassword?: boolean;
}

export interface UpdateUserRequest {
  email?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

export interface AuthError {
  code: AuthErrorCode;
  message: string;
  details?: Record<string, unknown>;
}

export type AuthErrorCode =
  | 'INVALID_CREDENTIALS'
  | 'ACCOUNT_LOCKED'
  | 'ACCOUNT_DISABLED'
  | 'EMAIL_NOT_VERIFIED'
  | 'MFA_REQUIRED'
  | 'MFA_INVALID'
  | 'TOKEN_EXPIRED'
  | 'TOKEN_INVALID'
  | 'PASSWORD_WEAK'
  | 'PASSWORD_RECENTLY_USED'
  | 'SESSION_EXPIRED'
  | 'RATE_LIMITED';
