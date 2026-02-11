import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { PageParams, NotificationCategory } from '@/types';
import { notificationApi } from '../api/notification.api';
import type { UpdatePreferencesRequest } from '../types/notification.types';
import { useAuthStore } from '@/stores/auth.store';

const KEYS = {
  all: ['notifications'] as const,
  lists: () => [...KEYS.all, 'list'] as const,
  list: (params?: unknown) => [...KEYS.lists(), params] as const,
  user: (userId: string, params?: unknown) => [...KEYS.lists(), userId, params] as const,
  unread: (userId: string) => [...KEYS.all, 'unread', userId] as const,
  unreadCount: () => [...KEYS.all, 'unread-count'] as const,
  prefs: () => [...KEYS.all, 'prefs'] as const,
};

interface NotificationParams extends PageParams {
  category?: NotificationCategory;
}

export function useNotifications(params?: NotificationParams) {
  const userId = useAuthStore((s) => s.user?.id);
  return useQuery({
    queryKey: KEYS.list(params),
    queryFn: () => notificationApi.byUser(userId!, params),
    enabled: !!userId,
  });
}

export function useUserNotifications(userId: string, params?: PageParams) {
  return useQuery({
    queryKey: KEYS.user(userId, params),
    queryFn: () => notificationApi.byUser(userId, params),
    enabled: !!userId,
  });
}

export function useUnreadNotifications(userId: string) {
  return useQuery({
    queryKey: KEYS.unread(userId),
    queryFn: () => notificationApi.unread(userId),
    enabled: !!userId,
  });
}

export function useUnreadCount() {
  const userId = useAuthStore((s) => s.user?.id);
  return useQuery({
    queryKey: KEYS.unreadCount(),
    queryFn: () => notificationApi.unreadCount(userId!),
    enabled: !!userId,
    refetchInterval: 30_000,
  });
}

export function useMarkRead() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => notificationApi.markRead(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useMarkNotificationRead() {
  return useMarkRead();
}

export function useMarkAllRead() {
  const userId = useAuthStore((s) => s.user?.id);
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => notificationApi.markAllRead(userId!),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useDeleteNotification() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => notificationApi.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useNotificationPreferences() {
  const userId = useAuthStore((s) => s.user?.id);
  return useQuery({
    queryKey: KEYS.prefs(),
    queryFn: () => notificationApi.getPreferences(userId!),
    enabled: !!userId,
  });
}

export function useUpdatePreferences() {
  const userId = useAuthStore((s) => s.user?.id);
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: UpdatePreferencesRequest) =>
      notificationApi.updatePreferences(userId!, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.prefs() });
    },
  });
}

export function useUpdateNotificationPreferences() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ userId, data }: { userId: string; data: UpdatePreferencesRequest }) =>
      notificationApi.updatePreferences(userId, data),
    onSuccess: (_, { userId: _userId }) => {
      qc.invalidateQueries({ queryKey: KEYS.prefs() });
    },
  });
}

export function useNotificationsByCategory(userId: string, category: string, params?: PageParams) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'category', userId, category, params],
    queryFn: () => notificationApi.byCategory(userId, category, params),
    enabled: !!userId && !!category,
  });
}

export function useSendNotification() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: Parameters<typeof notificationApi.send>[0]) => notificationApi.send(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useSendBulkNotifications() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: Parameters<typeof notificationApi.sendBulk>[0]) =>
      notificationApi.sendBulk(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useSendTemplateNotification() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      templateId,
      data,
    }: {
      templateId: string;
      data: { userId: string; variables?: Record<string, string> };
    }) => notificationApi.sendTemplate(templateId, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useRetryNotification() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => notificationApi.retry(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useArchiveNotification() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => notificationApi.archive(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}

export function useArchiveAllNotifications() {
  const userId = useAuthStore((s) => s.user?.id);
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => notificationApi.archiveAll(userId!),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.all });
    },
  });
}
