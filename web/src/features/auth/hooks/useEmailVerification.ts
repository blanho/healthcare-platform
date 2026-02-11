

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth.api';
import { AUTH_QUERY_KEYS } from '../constants';
import type { VerifyEmailRequest } from '../types/auth.types';

const EMAIL_QUERY_KEYS = {
  STATUS: ['auth', 'email', 'status'] as const,
};

export function useEmailVerificationStatus() {
  return useQuery({
    queryKey: EMAIL_QUERY_KEYS.STATUS,
    queryFn: () => authApi.email.getStatus(),
    staleTime: 60_000,
  });
}

export function useVerifyEmail() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: VerifyEmailRequest) => authApi.email.verify(data),
    onSuccess: () => {

      queryClient.invalidateQueries({ queryKey: AUTH_QUERY_KEYS.ME });
      queryClient.invalidateQueries({ queryKey: EMAIL_QUERY_KEYS.STATUS });

      navigate('/login', {
        state: { message: 'Email verified successfully! Please sign in.' },
      });
    },
  });
}

export function useResendVerificationEmail() {
  return useMutation({
    mutationFn: () => authApi.email.resend(),
  });
}
