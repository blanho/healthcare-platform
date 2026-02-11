

import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth.api';
import type {
  ForgotPasswordRequest,
  ResetPasswordRequest,
  ChangePasswordRequest,
} from '../types/auth.types';

export function useForgotPassword() {
  return useMutation({
    mutationFn: (data: ForgotPasswordRequest) => authApi.password.forgot(data),
  });
}

export function useResetPassword() {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: ResetPasswordRequest) => authApi.password.reset(data),
    onSuccess: () => {

      navigate('/login', {
        state: { message: 'Password reset successful. Please sign in with your new password.' },
      });
    },
  });
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (data: ChangePasswordRequest) => authApi.password.change(data),
  });
}

export function useValidatePasswordStrength() {
  return useMutation({
    mutationFn: (password: string) => authApi.password.validateStrength(password),
  });
}
