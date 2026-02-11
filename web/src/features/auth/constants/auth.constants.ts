

export const AUTH_ENDPOINTS = {
  LOGIN: '/api/v1/auth/login',
  REGISTER: '/api/v1/auth/register',
  LOGOUT: '/api/v1/auth/logout',
  REFRESH: '/api/v1/auth/refresh',
  FORGOT_PASSWORD: '/api/v1/auth/forgot-password',
  RESET_PASSWORD: '/api/v1/auth/reset-password',
  VERIFY_EMAIL: '/api/v1/auth/verify-email',
  RESEND_VERIFICATION: '/api/v1/auth/resend-verification',
} as const;

export const USER_ENDPOINTS = {
  ME: '/api/v1/users/me',
  CHANGE_PASSWORD: '/api/v1/users/:id/change-password',
  UPDATE_PROFILE: '/api/v1/users/:id',
} as const;

export const AUTH_ROUTES = {

  LOGIN: '/login',
  REGISTER: '/register',
  FORGOT_PASSWORD: '/forgot-password',
  RESET_PASSWORD: '/reset-password',
  VERIFY_EMAIL: '/verify-email',
  MFA_VERIFY: '/mfa-verify',

  SECURITY_SETTINGS: '/app/settings/security',
  CHANGE_PASSWORD: '/app/settings/security/change-password',
  MFA_SETUP: '/app/settings/security/mfa-setup',
} as const;

export const TOKEN_KEYS = {
  ACCESS_TOKEN: 'accessToken',
  REFRESH_TOKEN: 'refreshToken',
  USER: 'user',
} as const;

export const TOKEN_CONFIG = {
  REFRESH_THRESHOLD_MS: 5 * 60 * 1000,
  ACCESS_TOKEN_EXPIRY: 15 * 60,
  REFRESH_TOKEN_EXPIRY: 7 * 24 * 60 * 60,
} as const;

export const AUTH_QUERY_KEYS = {
  ME: ['auth', 'me'] as const,
  SESSION: ['auth', 'session'] as const,
} as const;

export const PASSWORD_RULES = {
  MIN_LENGTH: 8,
  MAX_LENGTH: 128,
  REQUIRE_UPPERCASE: true,
  REQUIRE_LOWERCASE: true,
  REQUIRE_DIGIT: true,
  REQUIRE_SPECIAL: false,
} as const;

export const USERNAME_RULES = {
  MIN_LENGTH: 3,
  MAX_LENGTH: 50,
  PATTERN: /^[a-zA-Z0-9_]+$/,
  ALLOWED_CHARS: 'letters, numbers, and underscores',
} as const;

export const AUTH_ERROR_MESSAGES = {
  INVALID_CREDENTIALS: 'Invalid username or password. Please try again.',
  ACCOUNT_LOCKED: 'Your account has been locked. Please contact support.',
  EMAIL_NOT_VERIFIED: 'Please verify your email address before signing in.',
  SESSION_EXPIRED: 'Your session has expired. Please sign in again.',
  NETWORK_ERROR: 'Unable to connect. Please check your internet connection.',
  REGISTRATION_FAILED: 'Registration failed. Please try again.',
  PASSWORD_MISMATCH: 'Passwords do not match.',
  WEAK_PASSWORD: 'Password does not meet security requirements.',
  EMAIL_ALREADY_EXISTS: 'An account with this email already exists.',
  USERNAME_ALREADY_EXISTS: 'This username is already taken.',
  TOKEN_EXPIRED: 'This link has expired. Please request a new one.',
  GENERIC_ERROR: 'Something went wrong. Please try again.',
  MFA_REQUIRED: 'Please enter your verification code.',
  MFA_INVALID: 'Invalid verification code. Please try again.',
  MFA_SETUP_FAILED: 'Failed to set up two-factor authentication.',
  SESSION_REVOKED: 'Session has been revoked.',
  PASSWORD_RECENTLY_USED: 'This password was recently used. Please choose a different one.',
  RATE_LIMITED: 'Too many requests. Please try again later.',
} as const;

export const AUTH_SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Welcome back!',
  LOGOUT_SUCCESS: 'You have been signed out.',
  REGISTER_SUCCESS: 'Account created successfully! Please verify your email.',
  PASSWORD_RESET_SENT: 'Password reset instructions sent to your email.',
  PASSWORD_CHANGED: 'Your password has been changed successfully.',
  EMAIL_VERIFIED: 'Your email has been verified.',
  MFA_ENABLED: 'Two-factor authentication has been enabled.',
  MFA_DISABLED: 'Two-factor authentication has been disabled.',
  BACKUP_CODES_REGENERATED: 'Backup codes have been regenerated.',
  SESSION_REVOKED: 'Session has been revoked.',
  ALL_SESSIONS_REVOKED: 'All sessions have been revoked.',
  VERIFICATION_EMAIL_SENT: 'Verification email has been sent.',
} as const;

export const MFA_CONFIG = {
  CODE_LENGTH: 6,
  BACKUP_CODE_LENGTH: 8,
  BACKUP_CODES_COUNT: 10,
} as const;

export const SESSION_CONFIG = {
  DEFAULT_HISTORY_LIMIT: 10,
  MAX_SESSIONS: 10,
} as const;
