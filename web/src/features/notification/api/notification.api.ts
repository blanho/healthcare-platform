import { apiClient } from '@/lib';
import type { PageResponse, PageParams } from '@/types';
import type {
  SendNotificationRequest,
  NotificationResponse,
  NotificationSummaryResponse,
  UnreadCountResponse,
  NotificationPreferenceResponse,
  UpdatePreferencesRequest,
} from '../types/notification.types';

const BASE = '/api/v1/notifications';

export const notificationApi = {
  send: (data: SendNotificationRequest) =>
    apiClient.post<NotificationResponse>(BASE, data).then((r) => r.data),

  getById: (id: string) => apiClient.get<NotificationResponse>(`${BASE}/${id}`).then((r) => r.data),

  byUser: (userId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<NotificationSummaryResponse>>(`${BASE}/user/${userId}`, { params })
      .then((r) => r.data),

  unread: (userId: string) =>
    apiClient
      .get<NotificationSummaryResponse[]>(`${BASE}/user/${userId}/unread`)
      .then((r) => r.data),

  unreadCount: (userId: string) =>
    apiClient.get<UnreadCountResponse>(`${BASE}/user/${userId}/unread/count`).then((r) => r.data),

  markRead: (id: string) => apiClient.patch(`${BASE}/${id}/read`),

  markAllRead: (userId: string) => apiClient.patch(`${BASE}/user/${userId}/read-all`),

  delete: (id: string) => apiClient.delete(`${BASE}/${id}`),

  getPreferences: (userId: string) =>
    apiClient
      .get<NotificationPreferenceResponse>(`${BASE}/preferences/${userId}`)
      .then((r) => r.data),

  updatePreferences: (userId: string, data: UpdatePreferencesRequest) =>
    apiClient
      .put<NotificationPreferenceResponse>(`${BASE}/preferences/${userId}`, data)
      .then((r) => r.data),

  byCategory: (userId: string, category: string, params?: PageParams) =>
    apiClient
      .get<
        PageResponse<NotificationSummaryResponse>
      >(`${BASE}/user/${userId}/category/${category}`, { params })
      .then((r) => r.data),

  sendBulk: (data: SendNotificationRequest[]) =>
    apiClient.post<NotificationResponse[]>(`${BASE}/bulk`, data).then((r) => r.data),

  sendTemplate: (
    templateId: string,
    data: { userId: string; variables?: Record<string, string> },
  ) =>
    apiClient
      .post<NotificationResponse>(`${BASE}/template/${templateId}`, data)
      .then((r) => r.data),

  retry: (id: string) =>
    apiClient.post<NotificationResponse>(`${BASE}/${id}/retry`).then((r) => r.data),

  archive: (id: string) => apiClient.patch(`${BASE}/${id}/archive`),

  archiveAll: (userId: string) => apiClient.patch(`${BASE}/user/${userId}/archive-all`),
};
