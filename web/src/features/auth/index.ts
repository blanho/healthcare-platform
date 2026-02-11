

export {

  LoginPage,
  RegisterPage,
  ForgotPasswordPage,

  ResetPasswordPage,
  ChangePasswordPage,

  VerifyEmailPage,

  MfaSetupPage,
  MfaVerifyPage,

  SessionsPage,
} from './pages';

export {

  useLogin,
  useRegister,
  useLogout,
  useCurrentUser,

  useMfaSetup,
  useMfaEnable,
  useMfaDisable,
  useMfaVerify,
  useRegenerateBackupCodes,

  useSessions,
  useSessionCount,
  useLoginHistory,
  useRevokeSession,
  useRevokeOtherSessions,
  useRevokeAllSessions,

  useForgotPassword,
  useResetPassword,
  useChangePassword,
  useValidatePasswordStrength,

  useEmailVerificationStatus,
  useVerifyEmail,
  useResendVerificationEmail,
} from './hooks';

export { authApi } from './api/auth.api';

export {

  AuthLayout,
  AuthBranding,

  PasswordInput,
  PasswordStrengthMeter,

  SocialLoginButtons,

  TermsCheckbox,

  OtpInput,
  MfaQrCode,
  MfaEmptyState,
  BackupCodesList,

  SessionCard,
  SessionsEmptyState,
  LoginHistoryItem,
} from './components';

export {

  loginSchema,
  registerSchema,
  forgotPasswordSchema,
  resetPasswordSchema,
  changePasswordSchema,

  passwordSchema,
  usernameSchema,
  emailSchema,

  mfaCodeSchema,
  backupCodeSchema,
  mfaSetupSchema,
  mfaVerifySchema,
  mfaDisableSchema,

  verifyEmailSchema,
} from './schemas';

export { checkPasswordStrength, decodeToken, isTokenExpired, shouldRefreshToken } from './utils';

export {
  AUTH_ENDPOINTS,
  AUTH_ROUTES,
  AUTH_QUERY_KEYS,
  AUTH_ERROR_MESSAGES,
  AUTH_SUCCESS_MESSAGES,
  TOKEN_KEYS,
  TOKEN_CONFIG,
  PASSWORD_RULES,
  USERNAME_RULES,
  MFA_CONFIG,
  SESSION_CONFIG,
} from './constants';

export type * from './types/auth.types';
export type {
  LoginFormValues,
  RegisterFormValues,
  ForgotPasswordFormValues,
  ResetPasswordFormValues,
  ChangePasswordFormValues,
  MfaSetupFormValues,
  MfaVerifyFormValues,
  MfaDisableFormValues,
  VerifyEmailFormValues,
} from './schemas';
