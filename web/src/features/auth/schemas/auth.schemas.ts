

import { z } from 'zod';
import { PASSWORD_RULES, USERNAME_RULES } from '../constants';

export const passwordSchema = z
  .string()
  .min(
    PASSWORD_RULES.MIN_LENGTH,
    `Password must be at least ${PASSWORD_RULES.MIN_LENGTH} characters`,
  )
  .max(
    PASSWORD_RULES.MAX_LENGTH,
    `Password must be less than ${PASSWORD_RULES.MAX_LENGTH} characters`,
  )
  .refine(
    (val) => !PASSWORD_RULES.REQUIRE_UPPERCASE || /[A-Z]/.test(val),
    'Password must contain at least one uppercase letter',
  )
  .refine(
    (val) => !PASSWORD_RULES.REQUIRE_LOWERCASE || /[a-z]/.test(val),
    'Password must contain at least one lowercase letter',
  )
  .refine(
    (val) => !PASSWORD_RULES.REQUIRE_DIGIT || /\d/.test(val),
    'Password must contain at least one digit',
  );

export const usernameSchema = z
  .string()
  .min(
    USERNAME_RULES.MIN_LENGTH,
    `Username must be at least ${USERNAME_RULES.MIN_LENGTH} characters`,
  )
  .max(
    USERNAME_RULES.MAX_LENGTH,
    `Username must be less than ${USERNAME_RULES.MAX_LENGTH} characters`,
  )
  .regex(USERNAME_RULES.PATTERN, `Username can only contain ${USERNAME_RULES.ALLOWED_CHARS}`);

export const emailSchema = z
  .string()
  .min(1, 'Email is required')
  .email('Please enter a valid email address');

export const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Username or email is required'),
  password: z.string().min(1, 'Password is required'),
  rememberMe: z.boolean().optional(),
});

export type LoginFormValues = z.infer<typeof loginSchema>;

export const registerSchema = z
  .object({
    username: usernameSchema,
    email: emailSchema,
    firstName: z.string().max(100).optional(),
    lastName: z.string().max(100).optional(),
    password: passwordSchema,
    confirmPassword: z.string().min(1, 'Please confirm your password'),
    acceptTerms: z.boolean().refine((val) => val === true, {
      message: 'You must accept the terms and conditions',
    }),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

export type RegisterFormValues = z.infer<typeof registerSchema>;

export const forgotPasswordSchema = z.object({
  email: emailSchema,
});

export type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>;

export const resetPasswordSchema = z
  .object({
    token: z.string().optional(),
    password: passwordSchema,
    confirmPassword: z.string().min(1, 'Please confirm your password'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

export type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>;

export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Current password is required'),
    newPassword: passwordSchema,
    confirmPassword: z.string().min(1, 'Please confirm your new password'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  })
  .refine((data) => data.currentPassword !== data.newPassword, {
    message: 'New password must be different from current password',
    path: ['newPassword'],
  });

export type ChangePasswordFormValues = z.infer<typeof changePasswordSchema>;

export const mfaCodeSchema = z
  .string()
  .length(6, 'Code must be 6 digits')
  .regex(/^\d{6}$/, 'Code must contain only numbers');

export const backupCodeSchema = z
  .string()
  .length(8, 'Backup code must be 8 characters')
  .regex(/^[A-Z0-9]{8}$/i, 'Invalid backup code format');

export const mfaSetupSchema = z.object({
  secret: z.string().min(1, 'Secret is required'),
  code: mfaCodeSchema,
});

export type MfaSetupFormValues = z.infer<typeof mfaSetupSchema>;

export const mfaVerifySchema = z.object({
  code: z.string().min(1, 'Code is required'),
  useBackupCode: z.boolean().optional(),
});

export type MfaVerifyFormValues = z.infer<typeof mfaVerifySchema>;

export const mfaDisableSchema = z.object({
  password: z.string().min(1, 'Password is required'),
});

export type MfaDisableFormValues = z.infer<typeof mfaDisableSchema>;

export const verifyEmailSchema = z.object({
  token: z.string().min(1, 'Verification token is required'),
});

export type VerifyEmailFormValues = z.infer<typeof verifyEmailSchema>;
