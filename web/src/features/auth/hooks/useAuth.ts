import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores';
import { authApi } from '../api/auth.api';
import type { LoginRequest, RegisterRequest } from '../types/auth.types';
import type { AuthUser } from '@/stores/auth.store';
import type { Role, Permission } from '@/types';

export function useLogin() {
  const { login } = useAuthStore();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: LoginRequest) => authApi.login(data),
    onSuccess: async (tokenRes) => {
      const me = await authApi.getMe();
      const user: AuthUser = {
        id: me.id,
        username: me.username,
        email: me.email,
        firstName: me.firstName,
        lastName: me.lastName,
        fullName: me.fullName,
        roles: me.roles as Role[],
        permissions: me.permissions as Permission[],
        patientId: me.patientId,
        providerId: me.providerId,
      };
      login(tokenRes.accessToken, tokenRes.refreshToken, user);
      navigate('/');
    },
  });
}

export function useRegister() {
  const { login } = useAuthStore();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: async (tokenRes) => {
      const me = await authApi.getMe();
      const user: AuthUser = {
        id: me.id,
        username: me.username,
        email: me.email,
        firstName: me.firstName,
        lastName: me.lastName,
        fullName: me.fullName,
        roles: me.roles as Role[],
        permissions: me.permissions as Permission[],
        patientId: me.patientId,
        providerId: me.providerId,
      };
      login(tokenRes.accessToken, tokenRes.refreshToken, user);
      navigate('/');
    },
  });
}

export function useLogout() {
  const { refreshToken, logout } = useAuthStore();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (refreshToken) {
        await authApi.logout({ refreshToken });
      }
    },
    onSettled: () => {
      logout();
      queryClient.clear();
      navigate('/login');
    },
  });
}

export function useCurrentUser() {
  const { isAuthenticated } = useAuthStore();

  return useQuery({
    queryKey: ['auth', 'me'],
    queryFn: () => authApi.getMe(),
    enabled: isAuthenticated,
    staleTime: 5 * 60 * 1000,
  });
}
