import { Chip, Avatar, Box, Typography } from '@mui/material';
import {
  Mail as EmailIcon,
  Sms as SmsIcon,
  NotificationsActive as PushIcon,
  Inbox as InAppIcon,
  DraftsOutlined as ReadIcon,
  MarkEmailUnread as UnreadIcon,
} from '@mui/icons-material';
import type { NotificationType, NotificationCategory, NotificationStatus } from '@/types';
import {
  notificationTypeConfigData,
  notificationStatusConfigData,
  notificationCategoryConfigData,
} from './notification-chip-config';

const typeIcons: Record<NotificationType, React.ReactNode> = {
  EMAIL: <EmailIcon fontSize="small" />,
  SMS: <SmsIcon fontSize="small" />,
  PUSH: <PushIcon fontSize="small" />,
  IN_APP: <InAppIcon fontSize="small" />,
};

interface NotificationTypeChipProps {
  type: NotificationType;
  size?: 'small' | 'medium';
}

export function NotificationTypeChip({ type, size = 'small' }: NotificationTypeChipProps) {
  const config = notificationTypeConfigData[type] || { label: type, color: '#666' };
  const icon = typeIcons[type] || <InAppIcon fontSize="small" />;
  return (
    <Chip
      icon={<Box sx={{ display: 'flex', color: config.color }}>{icon}</Box>}
      label={config.label}
      size={size}
      variant="outlined"
    />
  );
}

interface NotificationStatusChipProps {
  status: NotificationStatus;
  size?: 'small' | 'medium';
}

export function NotificationStatusChip({ status, size = 'small' }: NotificationStatusChipProps) {
  const config = notificationStatusConfigData[status] || { label: status, color: 'default' };
  return <Chip label={config.label} color={config.color} size={size} />;
}

interface NotificationCategoryChipProps {
  category: NotificationCategory;
  size?: 'small' | 'medium';
}

export function NotificationCategoryChip({
  category,
  size = 'small',
}: NotificationCategoryChipProps) {
  const config = notificationCategoryConfigData[category] || { label: category, color: '#666' };
  return (
    <Chip
      label={config.label}
      size={size}
      sx={{
        bgcolor: `${config.color}14`,
        color: config.color,
        border: `1px solid ${config.color}40`,
      }}
    />
  );
}

interface ReadIndicatorProps {
  isRead: boolean;
}

export function ReadIndicator({ isRead }: ReadIndicatorProps) {
  if (isRead) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.secondary' }}>
        <ReadIcon fontSize="small" />
        <Typography variant="caption">Read</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'primary.main' }}>
      <UnreadIcon fontSize="small" />
      <Typography variant="caption" fontWeight={600}>
        Unread
      </Typography>
    </Box>
  );
}

interface CategoryIconProps {
  category: NotificationCategory;
  size?: 'small' | 'medium' | 'large';
}

export function CategoryIcon({ category, size = 'medium' }: CategoryIconProps) {
  const config = notificationCategoryConfigData[category] || { label: category, color: '#666' };
  const sizes = { small: 32, medium: 40, large: 48 };
  const iconSizes = { small: 16, medium: 20, large: 24 };

  return (
    <Avatar
      sx={{
        width: sizes[size],
        height: sizes[size],
        bgcolor: `${config.color}14`,
        color: config.color,
        fontSize: iconSizes[size],
      }}
    >
      {config.label.charAt(0)}
    </Avatar>
  );
}
