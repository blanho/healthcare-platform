import {
  Box,
  Card,
  CardContent,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Skeleton,
  Chip,
} from '@mui/material';
import {
  PersonAdd as PersonAddIcon,
  Event as EventIcon,
  CheckCircle as CheckCircleIcon,
  Description as DescriptionIcon,
  Payment as PaymentIcon,
  LocalHospital as ProviderIcon,
  Schedule as ScheduleIcon,
} from '@mui/icons-material';
import { formatDistanceToNow } from 'date-fns';
import type { RecentActivityItem } from '../types/dashboard.types';

interface ActivityFeedProps {
  items: RecentActivityItem[];
  isLoading?: boolean;
  maxItems?: number;
}

const activityIcons: Record<RecentActivityItem['type'], React.ReactNode> = {
  patient_registered: <PersonAddIcon fontSize="small" color="primary" />,
  appointment_scheduled: <EventIcon fontSize="small" sx={{ color: '#059669' }} />,
  appointment_completed: <CheckCircleIcon fontSize="small" color="success" />,
  record_created: <DescriptionIcon fontSize="small" sx={{ color: '#D97706' }} />,
  invoice_paid: <PaymentIcon fontSize="small" sx={{ color: '#0891B2' }} />,
  provider_added: <ProviderIcon fontSize="small" sx={{ color: '#7C3AED' }} />,
};

const activityColors: Record<RecentActivityItem['type'], string> = {
  patient_registered: 'primary',
  appointment_scheduled: 'success',
  appointment_completed: 'success',
  record_created: 'warning',
  invoice_paid: 'info',
  provider_added: 'secondary',
};

export function ActivityFeed({ items, isLoading = false, maxItems = 8 }: ActivityFeedProps) {
  const displayItems = items.slice(0, maxItems);

  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="h3" sx={{ fontSize: '1.1rem', fontWeight: 600 }}>
            Recent Activity
          </Typography>
          <ScheduleIcon color="action" sx={{ fontSize: 20 }} />
        </Box>

        {isLoading ? (
          <List disablePadding>
            {[...Array(5)].map((_, i) => (
              <ListItem key={i} disablePadding sx={{ py: 1 }}>
                <ListItemIcon sx={{ minWidth: 36 }}>
                  <Skeleton variant="circular" width={24} height={24} />
                </ListItemIcon>
                <ListItemText
                  primary={<Skeleton width="70%" />}
                  secondary={<Skeleton width="40%" />}
                />
              </ListItem>
            ))}
          </List>
        ) : displayItems.length === 0 ? (
          <Box sx={{ py: 4, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No recent activity
            </Typography>
          </Box>
        ) : (
          <List disablePadding>
            {displayItems.map((item) => (
              <ListItem
                key={item.id}
                disablePadding
                sx={{
                  py: 1,
                  borderBottom: '1px solid',
                  borderColor: 'divider',
                  '&:last-child': { borderBottom: 'none' },
                }}
              >
                <ListItemIcon sx={{ minWidth: 36 }}>{activityIcons[item.type]}</ListItemIcon>
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Typography variant="body2" sx={{ fontWeight: 500 }}>
                        {item.title}
                      </Typography>
                      <Chip
                        label={item.entityType}
                        size="small"
                        color={activityColors[item.type] as 'primary'}
                        sx={{ height: 18, fontSize: '0.65rem' }}
                      />
                    </Box>
                  }
                  secondary={
                    <Typography variant="caption" color="text.secondary">
                      {item.actorName} •{' '}
                      {formatDistanceToNow(new Date(item.timestamp), { addSuffix: true })}
                    </Typography>
                  }
                />
              </ListItem>
            ))}
          </List>
        )}
      </CardContent>
    </Card>
  );
}
