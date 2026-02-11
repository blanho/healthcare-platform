import { useState } from 'react';
import {
  IconButton,
  Badge,
  Popover,
  Box,
  Typography,
  Button,
  Divider,
  CircularProgress,
} from '@mui/material';
import { Notifications as NotificationIcon, DoneAll as MarkAllReadIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import {
  useNotifications,
  useUnreadCount,
  useMarkAllRead,
  useMarkRead,
} from '../hooks/useNotification';
import { NotificationList } from './NotificationList';

export function NotificationDropdown() {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const open = Boolean(anchorEl);

  const { data: notificationsData, isLoading: notificationsLoading } = useNotifications({
    page: 0,
    size: 10,
  });
  const { data: unreadData } = useUnreadCount();
  const { mutate: markAllRead, isPending: isMarkingAllRead } = useMarkAllRead();
  const { mutate: markRead } = useMarkRead();

  const notifications = notificationsData?.content || [];
  const unreadCount = unreadData?.unreadCount || 0;

  const handleOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleViewAll = () => {
    handleClose();
    navigate('/app/notifications');
  };

  const handleMarkAllRead = () => {
    markAllRead();
  };

  const handleMarkRead = (id: string) => {
    markRead(id);
  };

  return (
    <>
      <IconButton
        onClick={handleOpen}
        size="large"
        aria-label={`${unreadCount} unread notifications`}
        aria-describedby={open ? 'notification-popover' : undefined}
        sx={{ minWidth: 44, minHeight: 44 }}
      >
        <Badge badgeContent={unreadCount} color="error" max={99}>
          <NotificationIcon />
        </Badge>
      </IconButton>

      <Popover
        id="notification-popover"
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        PaperProps={{
          sx: {
            width: 380,
            maxHeight: 480,
            overflow: 'hidden',
          },
        }}
      >
        {}
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            px: 2,
            py: 1.5,
            borderBottom: 1,
            borderColor: 'divider',
          }}
        >
          <Typography variant="subtitle1" fontWeight={600}>
            Notifications
            {unreadCount > 0 && (
              <Typography component="span" variant="body2" color="text.secondary" sx={{ ml: 1 }}>
                ({unreadCount} unread)
              </Typography>
            )}
          </Typography>
          {unreadCount > 0 && (
            <Button
              size="small"
              startIcon={isMarkingAllRead ? <CircularProgress size={14} /> : <MarkAllReadIcon />}
              onClick={handleMarkAllRead}
              disabled={isMarkingAllRead}
            >
              Mark all read
            </Button>
          )}
        </Box>

        {}
        <Box sx={{ maxHeight: 360, overflow: 'auto' }}>
          <NotificationList
            notifications={notifications}
            isLoading={notificationsLoading}
            onMarkRead={handleMarkRead}
            compact
            emptyMessage="You're all caught up!"
          />
        </Box>

        {}
        <Divider />
        <Box sx={{ p: 1 }}>
          <Button fullWidth onClick={handleViewAll} sx={{ minHeight: 44 }}>
            View All Notifications
          </Button>
        </Box>
      </Popover>
    </>
  );
}
