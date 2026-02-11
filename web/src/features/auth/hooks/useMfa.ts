

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { authApi } from '../api/auth.api';
import { AUTH_QUERY_KEYS } from '../constants';
import type { MfaSetupRequest, MfaVerifyRequest, MfaDisableRequest } from '../types/auth.types';

export function useMfaSetup() {
  return useMutation({
    mutationFn: () => authApi.mfa.getSetup(),
  });
}

export function useMfaEnable() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: MfaSetupRequest) => authApi.mfa.enable(data),
    onSuccess: () => {

      queryClient.invalidateQueries({ queryKey: AUTH_QUERY_KEYS.ME });
    },
  });
}

export function useMfaDisable() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: MfaDisableRequest) => authApi.mfa.disable(data),
    onSuccess: () => {

      queryClient.invalidateQueries({ queryKey: AUTH_QUERY_KEYS.ME });
    },
  });
}

export function useMfaVerify() {
  return useMutation({
    mutationFn: (data: MfaVerifyRequest) => authApi.mfa.verify(data),
  });
}

export function useRegenerateBackupCodes() {
  return useMutation({
    mutationFn: (data: MfaDisableRequest) => authApi.mfa.regenerateBackupCodes(data),
  });
}
