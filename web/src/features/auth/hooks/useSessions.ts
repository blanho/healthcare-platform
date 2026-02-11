

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores';
import { authApi } from '../api/auth.api';
import { SESSION_CONFIG } from '../constants';

const SESSION_QUERY_KEYS = {
  SESSIONS: ['auth', 'sessions'] as const,
  SESSION_COUNT: ['auth', 'sessions', 'count'] as const,
  LOGIN_HISTORY: ['auth', 'sessions', 'history'] as const,
};

export function useSessions() {
  return useQuery({
    queryKey: SESSION_QUERY_KEYS.SESSIONS,
    queryFn: () => authApi.sessions.getAll(),
    staleTime: 30_000,
  });
}

export function useSessionCount() {
  return useQuery({
    queryKey: SESSION_QUERY_KEYS.SESSION_COUNT,
    queryFn: () => authApi.sessions.getCount(),
    staleTime: 60_000,
  });
}

export function useLoginHistory(limit: number = SESSION_CONFIG.DEFAULT_HISTORY_LIMIT) {
  return useQuery({
    queryKey: [...SESSION_QUERY_KEYS.LOGIN_HISTORY, limit],
    queryFn: () => authApi.sessions.getHistory(limit),
    staleTime: 60_000,
  });
}

export function useRevokeSession() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (sessionId: string) => authApi.sessions.revoke(sessionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: SESSION_QUERY_KEYS.SESSIONS });
      queryClient.invalidateQueries({ queryKey: SESSION_QUERY_KEYS.SESSION_COUNT });
    },
  });
}

export function useRevokeOtherSessions() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => authApi.sessions.revokeOthers(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: SESSION_QUERY_KEYS.SESSIONS });
      queryClient.invalidateQueries({ queryKey: SESSION_QUERY_KEYS.SESSION_COUNT });
    },
  });
}

export function useRevokeAllSessions() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { logout } = useAuthStore();

  return useMutation({
    mutationFn: () => authApi.sessions.revokeAll(),
    onSuccess: () => {
      queryClient.clear();
      logout();
      navigate('/login');
    },
  });
}
