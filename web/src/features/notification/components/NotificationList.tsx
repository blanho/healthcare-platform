import {
  Box,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemButton,
  Typography,
  IconButton,
  Divider,
  Skeleton,
  Badge,
} from '@mui/material';
import { Check as MarkReadIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { CategoryIcon, NotificationCategoryChip } from './NotificationChips';
import type { NotificationSummaryResponse } from '../types/notification.types';

interface NotificationListProps {
  notifications: NotificationSummaryResponse[];
  isLoading?: boolean;
  onSelect?: (notification: NotificationSummaryResponse) => void;
  onMarkRead?: (id: string) => void;
  onDelete?: (id: string) => void;
  emptyMessage?: string;
  compact?: boolean;
}

export function NotificationList({
  notifications,
  isLoading,
  onSelect,
  onMarkRead,
  onDelete,
  emptyMessage = 'No notifications',
  compact = false,
}: NotificationListProps) {
  if (isLoading) {
    return (
      <List disablePadding>
        {[1, 2, 3, 4, 5].map((i) => (
          <ListItem key={i} sx={{ px: 2, py: 1.5 }}>
            <ListItemAvatar>
              <Skeleton variant="circular" width={40} height={40} />
            </ListItemAvatar>
            <ListItemText primary={<Skeleton width="60%" />} secondary={<Skeleton width="80%" />} />
          </ListItem>
        ))}
      </List>
    );
  }

  if (notifications.length === 0) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <Typography color="text.secondary">{emptyMessage}</Typography>
      </Box>
    );
  }

  return (
    <List disablePadding>
      {notifications.map((notification, index) => (
        <Box key={notification.id}>
          <ListItemButton
            onClick={() => onSelect?.(notification)}
            sx={{
              px: 2,
              py: compact ? 1 : 1.5,
              bgcolor: notification.isRead ? 'transparent' : 'action.hover',
              '&:hover': {
                bgcolor: notification.isRead ? 'action.hover' : 'action.selected',
              },
            }}
          >
            <ListItemAvatar>
              <Badge
                color="primary"
                variant="dot"
                invisible={notification.isRead}
                anchorOrigin={{ vertical: 'top', horizontal: 'left' }}
              >
                <CategoryIcon
                  category={notification.category}
                  size={compact ? 'small' : 'medium'}
                />
              </Badge>
            </ListItemAvatar>
            <ListItemText
              primary={
                <Typography
                  variant="body2"
                  fontWeight={notification.isRead ? 400 : 600}
                  sx={{
                    display: '-webkit-box',
                    WebkitLineClamp: 1,
                    WebkitBoxOrient: 'vertical',
                    overflow: 'hidden',
                  }}
                >
                  {notification.title}
                </Typography>
              }
              secondary={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
                  <NotificationCategoryChip category={notification.category} size="small" />
                  <Typography variant="caption" color="text.secondary">
                    {formatDistanceToNow(parseISO(notification.createdAt), { addSuffix: true })}
                  </Typography>
                </Box>
              }
            />
            {(onMarkRead || onDelete) && (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                {onMarkRead && !notification.isRead && (
                  <IconButton
                    size="small"
                    onClick={(e) => {
                      e.stopPropagation();
                      onMarkRead(notification.id);
                    }}
                    aria-label="Mark as read"
                    sx={{ opacity: 0.6, '&:hover': { opacity: 1 } }}
                  >
                    <MarkReadIcon fontSize="small" />
                  </IconButton>
                )}
                {onDelete && (
                  <IconButton
                    size="small"
                    onClick={(e) => {
                      e.stopPropagation();
                      onDelete(notification.id);
                    }}
                    aria-label="Delete notification"
                    color="error"
                    sx={{ opacity: 0.6, '&:hover': { opacity: 1 } }}
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                )}
              </Box>
            )}
          </ListItemButton>
          {index < notifications.length - 1 && <Divider component="li" />}
        </Box>
      ))}
    </List>
  );
}
