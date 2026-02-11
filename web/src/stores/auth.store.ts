import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { Role, Permission } from '@/types';

export interface AuthUser {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  roles: Role[];
  permissions: Permission[];
  patientId: string | null;
  providerId: string | null;
}

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;

  setTokens: (accessToken: string, refreshToken: string) => void;
  setUser: (user: AuthUser) => void;
  login: (accessToken: string, refreshToken: string, user: AuthUser) => void;
  logout: () => void;

  hasRole: (role: Role) => boolean;
  hasPermission: (permission: Permission) => boolean;
  hasAnyPermission: (...permissions: Permission[]) => boolean;
  isAdmin: () => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      isAuthenticated: false,

      setTokens: (accessToken, refreshToken) => set({ accessToken, refreshToken }),

      setUser: (user) => set({ user, isAuthenticated: true }),

      login: (accessToken, refreshToken, user) =>
        set({
          accessToken,
          refreshToken,
          user,
          isAuthenticated: true,
        }),

      logout: () =>
        set({
          accessToken: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
        }),

      hasRole: (role) => get().user?.roles.includes(role) ?? false,

      hasPermission: (permission) => get().user?.permissions.includes(permission) ?? false,

      hasAnyPermission: (...permissions) =>
        permissions.some((p) => get().user?.permissions.includes(p)),

      isAdmin: () => get().user?.roles.includes('ROLE_ADMIN') ?? false,
    }),
    {
      name: 'healthcare-auth',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
