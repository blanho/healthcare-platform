import type { NotificationType, NotificationCategory, NotificationStatus } from '@/types';

export interface SendNotificationRequest {
  userId: string;
  patientId?: string;
  type: NotificationType;
  category: NotificationCategory;
  title: string;
  message: string;
  scheduledAt?: string;
  metadata?: Record<string, unknown>;
}

export interface NotificationResponse {
  id: string;
  userId: string;
  patientId: string | null;
  type: NotificationType;
  category: NotificationCategory;
  title: string;
  message: string;
  status: NotificationStatus;
  scheduledAt: string | null;
  sentAt: string | null;
  deliveredAt: string | null;
  readAt: string | null;
  failedAt: string | null;
  failureReason: string | null;
  retryCount: number;
  metadata: Record<string, unknown> | null;
  createdAt: string;
}

export interface NotificationSummaryResponse {
  id: string;
  type: NotificationType;
  category: NotificationCategory;
  title: string;
  status: NotificationStatus;
  isRead: boolean;
  createdAt: string;
}

export interface UnreadCountResponse {
  unreadCount: number;
}

export interface NotificationPreferenceResponse {
  id: string;
  userId: string;
  emailEnabled: boolean;
  smsEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
  mutedCategories: NotificationCategory[];
  quietHoursStart: number | null;
  quietHoursEnd: number | null;
  timezone: string | null;
}

export interface UpdatePreferencesRequest {
  emailEnabled?: boolean;
  smsEnabled?: boolean;
  pushEnabled?: boolean;
  inAppEnabled?: boolean;
  mutedCategories?: NotificationCategory[];
  quietHoursStart?: number;
  quietHoursEnd?: number;
  timezone?: string;
}
