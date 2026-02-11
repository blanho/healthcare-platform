import { apiClient } from '@/lib';
import type {
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  TokenResponse,
  UserResponse,
  ChangePasswordRequest,

  MfaSetupResponse,
  MfaSetupRequest,
  MfaVerifyRequest,
  MfaVerifyResponse,
  MfaDisableRequest,
  BackupCodesResponse,

  ForgotPasswordRequest,
  ResetPasswordRequest,
  PasswordStrengthResponse,
  MessageResponse,

  SessionResponse,
  LoginAttemptResponse,

  VerifyEmailRequest,
  EmailVerificationStatusResponse,
} from '../types/auth.types';

const AUTH_BASE = '/api/v1/auth';
const USERS_BASE = '/api/v1/users';

export const authApi = {

  login: (data: LoginRequest) =>
    apiClient.post<TokenResponse>(`${AUTH_BASE}/login`, data).then((r) => r.data),

  register: (data: RegisterRequest) =>
    apiClient.post<TokenResponse>(`${AUTH_BASE}/register`, data).then((r) => r.data),

  refresh: (data: RefreshTokenRequest) =>
    apiClient.post<TokenResponse>(`${AUTH_BASE}/refresh`, data).then((r) => r.data),

  logout: (data: RefreshTokenRequest) => apiClient.post(`${AUTH_BASE}/logout`, data),

  getMe: () => apiClient.get<UserResponse>(`${USERS_BASE}/me`).then((r) => r.data),

  changePassword: (userId: string, data: ChangePasswordRequest) =>
    apiClient.post(`${USERS_BASE}/${userId}/change-password`, data),

  mfa: {
    getSetup: () => apiClient.get<MfaSetupResponse>(`${AUTH_BASE}/mfa/setup`).then((r) => r.data),

    enable: (data: MfaSetupRequest) =>
      apiClient.post<BackupCodesResponse>(`${AUTH_BASE}/mfa/enable`, data).then((r) => r.data),

    disable: (data: MfaDisableRequest) => apiClient.delete(`${AUTH_BASE}/mfa/disable`, { data }),

    verify: (data: MfaVerifyRequest) =>
      apiClient.post<MfaVerifyResponse>(`${AUTH_BASE}/mfa/verify`, data).then((r) => r.data),

    regenerateBackupCodes: (data: MfaDisableRequest) =>
      apiClient
        .post<BackupCodesResponse>(`${AUTH_BASE}/mfa/backup-codes/regenerate`, data)
        .then((r) => r.data),
  },

  password: {
    forgot: (data: ForgotPasswordRequest) =>
      apiClient.post<MessageResponse>(`${AUTH_BASE}/password/forgot`, data).then((r) => r.data),

    reset: (data: ResetPasswordRequest) =>
      apiClient.post<MessageResponse>(`${AUTH_BASE}/password/reset`, data).then((r) => r.data),

    change: (data: ChangePasswordRequest) =>
      apiClient.post<MessageResponse>(`${AUTH_BASE}/password/change`, data).then((r) => r.data),

    validateStrength: (password: string) =>
      apiClient
        .post<PasswordStrengthResponse>(`${AUTH_BASE}/password/validate`, { password })
        .then((r) => r.data),
  },

  sessions: {
    getAll: () => apiClient.get<SessionResponse[]>(`${AUTH_BASE}/sessions`).then((r) => r.data),

    revoke: (sessionId: string) => apiClient.delete(`${AUTH_BASE}/sessions/${sessionId}`),

    revokeOthers: () =>
      apiClient.delete<MessageResponse>(`${AUTH_BASE}/sessions/others`).then((r) => r.data),

    revokeAll: () =>
      apiClient.delete<MessageResponse>(`${AUTH_BASE}/sessions/all`).then((r) => r.data),

    getCount: () =>
      apiClient.get<{ count: number }>(`${AUTH_BASE}/sessions/count`).then((r) => r.data),

    getHistory: (limit = 10) =>
      apiClient
        .get<LoginAttemptResponse[]>(`${AUTH_BASE}/sessions/history`, { params: { limit } })
        .then((r) => r.data),
  },

  email: {
    verify: (data: VerifyEmailRequest) =>
      apiClient.post<MessageResponse>(`${AUTH_BASE}/email/verify`, data).then((r) => r.data),

    resend: () => apiClient.post<MessageResponse>(`${AUTH_BASE}/email/resend`).then((r) => r.data),

    getStatus: () =>
      apiClient
        .get<EmailVerificationStatusResponse>(`${AUTH_BASE}/email/status`)
        .then((r) => r.data),
  },
};
