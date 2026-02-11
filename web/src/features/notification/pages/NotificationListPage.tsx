import { useState, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Tabs,
  Tab,
  Stack,
  Pagination,
  Grid,
} from '@mui/material';
import {
  Search as SearchIcon,
  DoneAll as MarkAllReadIcon,
  Settings as SettingsIcon,
} from '@mui/icons-material';
import { PageHeader } from '@/components/shared';
import {
  useNotifications,
  useUnreadCount,
  useMarkAllRead,
  useMarkRead,
  useDeleteNotification,
} from '../hooks/useNotification';
import {
  NotificationList,
  NotificationCategoryChip,
  CategoryIcon,
  NotificationStatusChip,
} from '../components';
import { notificationCategoryConfigData as notificationCategoryConfig } from '../components/notification-chip-config';
import { format, parseISO } from 'date-fns';
import type { NotificationCategory } from '@/types';
import type { NotificationSummaryResponse } from '../types/notification.types';

type TabValue = 'all' | 'unread';

export function NotificationListPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [activeTab, setActiveTab] = useState<TabValue>(
    (searchParams.get('tab') as TabValue) || 'all',
  );
  const [categoryFilter, setCategoryFilter] = useState<NotificationCategory | ''>('');
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(1);
  const [selectedNotification, setSelectedNotification] =
    useState<NotificationSummaryResponse | null>(null);

  const { data: notificationsData, isLoading } = useNotifications({
    page: page - 1,
    size: 20,
    category: categoryFilter || undefined,
  });
  const { data: unreadData } = useUnreadCount();
  const { mutate: markAllRead, isPending: isMarkingAllRead } = useMarkAllRead();
  const { mutate: markRead } = useMarkRead();
  const { mutate: deleteNotification, isPending: isDeleting } = useDeleteNotification();

  const notifications = notificationsData?.content || [];
  const totalPages = notificationsData?.totalPages || 1;
  const unreadCount = unreadData?.unreadCount || 0;

  const filteredNotifications = useMemo(() => {
    let filtered = notifications;

    if (activeTab === 'unread') {
      filtered = filtered.filter((n) => !n.isRead);
    }

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      filtered = filtered.filter((n) => n.title.toLowerCase().includes(query));
    }

    return filtered;
  }, [notifications, activeTab, searchQuery]);

  const handleTabChange = (_: React.SyntheticEvent, value: TabValue) => {
    setActiveTab(value);
    setSearchParams({ tab: value });
  };

  const handleSelectNotification = (notification: NotificationSummaryResponse) => {
    setSelectedNotification(notification);
    if (!notification.isRead) {
      markRead(notification.id);
    }
  };

  const handleDeleteNotification = (id: string) => {
    deleteNotification(id);
    if (selectedNotification?.id === id) {
      setSelectedNotification(null);
    }
  };

  return (
    <Box>
      <PageHeader
        title="Notifications"
        subtitle="View and manage your notifications"
        action={
          <Stack direction="row" spacing={2}>
            {unreadCount > 0 && (
              <Button
                variant="outlined"
                startIcon={<MarkAllReadIcon />}
                onClick={() => markAllRead()}
                disabled={isMarkingAllRead}
                sx={{ minHeight: 44 }}
              >
                Mark All Read
              </Button>
            )}
            <Button
              variant="contained"
              startIcon={<SettingsIcon />}
              onClick={() => navigate('/app/notifications/settings')}
              sx={{ minHeight: 44 }}
            >
              Settings
            </Button>
          </Stack>
        }
      />

      {}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid size={{ xs: 6, sm: 3 }}>
          <Card>
            <CardContent sx={{ textAlign: 'center', py: 2 }}>
              <Typography variant="h3" color="primary.main" fontWeight={700}>
                {unreadCount}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Unread
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 6, sm: 3 }}>
          <Card>
            <CardContent sx={{ textAlign: 'center', py: 2 }}>
              <Typography variant="h3" fontWeight={700}>
                {notifications.length}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Total
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Tabs value={activeTab} onChange={handleTabChange} aria-label="Notification filters">
          <Tab label="All" value="all" sx={{ minHeight: 44 }} />
          <Tab label={`Unread (${unreadCount})`} value="unread" sx={{ minHeight: 44 }} />
        </Tabs>

        <Stack direction="row" spacing={2}>
          <TextField
            placeholder="Search notifications..."
            size="small"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" />
                  </InputAdornment>
                ),
              },
            }}
            sx={{ width: 250 }}
          />

          <FormControl size="small" sx={{ minWidth: 150 }}>
            <InputLabel>Category</InputLabel>
            <Select
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value as NotificationCategory | '')}
              label="Category"
            >
              <MenuItem value="">All Categories</MenuItem>
              {Object.entries(notificationCategoryConfig).map(([key, { label }]) => (
                <MenuItem key={key} value={key}>
                  {label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Stack>
      </Box>

      {}
      <Grid container spacing={3}>
        {}
        <Grid size={{ xs: 12, md: selectedNotification ? 5 : 12 }}>
          <Card>
            <NotificationList
              notifications={filteredNotifications}
              isLoading={isLoading}
              onSelect={handleSelectNotification}
              onMarkRead={(id) => markRead(id)}
              onDelete={handleDeleteNotification}
              emptyMessage={
                activeTab === 'unread'
                  ? "You're all caught up! No unread notifications."
                  : 'No notifications yet.'
              }
            />
          </Card>

          {totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={(_, p) => setPage(p)}
                color="primary"
              />
            </Box>
          )}
        </Grid>

        {}
        {selectedNotification && (
          <Grid size={{ xs: 12, md: 7 }}>
            <Card sx={{ position: 'sticky', top: 24 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 2, mb: 3 }}>
                  <CategoryIcon category={selectedNotification.category} size="large" />
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="h4" sx={{ mb: 1 }}>
                      {selectedNotification.title}
                    </Typography>
                    <Stack direction="row" spacing={1}>
                      <NotificationCategoryChip category={selectedNotification.category} />
                      <NotificationStatusChip status={selectedNotification.status} />
                    </Stack>
                  </Box>
                  <Button
                    variant="outlined"
                    color="error"
                    size="small"
                    onClick={() => handleDeleteNotification(selectedNotification.id)}
                    disabled={isDeleting}
                    sx={{ minHeight: 36 }}
                  >
                    Delete
                  </Button>
                </Box>

                <Typography variant="body2" color="text.secondary">
                  Received {format(parseISO(selectedNotification.createdAt), 'MMMM d, yyyy h:mm a')}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
