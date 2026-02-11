import {
  Box,
  Card,
  CardContent,
  Typography,
  List,
  ListItem,
  ListItemAvatar,
  Avatar,
  ListItemText,
  Chip,
  Skeleton,
  Button,
} from '@mui/material';
import { Event as EventIcon, ArrowForward as ArrowIcon } from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { useNavigate } from 'react-router-dom';
import type { UpcomingAppointment } from '../types/dashboard.types';

interface UpcomingAppointmentsProps {
  appointments: UpcomingAppointment[];
  isLoading?: boolean;
}

const statusColors: Record<
  string,
  'default' | 'primary' | 'success' | 'warning' | 'error' | 'info'
> = {
  SCHEDULED: 'primary',
  CONFIRMED: 'success',
  CHECKED_IN: 'info',
  IN_PROGRESS: 'warning',
};

export function UpcomingAppointments({
  appointments,
  isLoading = false,
}: UpcomingAppointmentsProps) {
  const navigate = useNavigate();

  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="h3" sx={{ fontSize: '1.1rem', fontWeight: 600 }}>
            Upcoming Appointments
          </Typography>
          <EventIcon color="primary" sx={{ fontSize: 20 }} />
        </Box>

        {isLoading ? (
          <List disablePadding>
            {[...Array(4)].map((_, i) => (
              <ListItem key={i} disablePadding sx={{ py: 1.5 }}>
                <ListItemAvatar>
                  <Skeleton variant="circular" width={40} height={40} />
                </ListItemAvatar>
                <ListItemText
                  primary={<Skeleton width="60%" />}
                  secondary={<Skeleton width="40%" />}
                />
              </ListItem>
            ))}
          </List>
        ) : appointments.length === 0 ? (
          <Box sx={{ py: 4, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No upcoming appointments today
            </Typography>
          </Box>
        ) : (
          <>
            <List disablePadding>
              {appointments.map((apt) => (
                <ListItem
                  key={apt.id}
                  disablePadding
                  sx={{
                    py: 1.5,
                    px: 1,
                    borderRadius: 1,
                    cursor: 'pointer',
                    transition: 'background-color 150ms',
                    '&:hover': { backgroundColor: 'action.hover' },
                  }}
                  onClick={() => navigate(`/app/appointments/${apt.id}`)}
                >
                  <ListItemAvatar>
                    <Avatar
                      sx={{ bgcolor: 'primary.light', color: 'primary.main', fontSize: '0.875rem' }}
                    >
                      {apt.patientName
                        .split(' ')
                        .map((n) => n[0])
                        .join('')}
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography variant="body2" sx={{ fontWeight: 500 }}>
                          {apt.patientName}
                        </Typography>
                        <Chip
                          label={apt.status.replace('_', ' ')}
                          size="small"
                          color={statusColors[apt.status] || 'default'}
                          sx={{ height: 20, fontSize: '0.65rem' }}
                        />
                      </Box>
                    }
                    secondary={
                      <Typography variant="caption" color="text.secondary">
                        {format(parseISO(apt.scheduledTime), 'h:mm a')} •{' '}
                        {apt.appointmentType.replace('_', ' ')}
                      </Typography>
                    }
                  />
                </ListItem>
              ))}
            </List>
            <Button
              endIcon={<ArrowIcon />}
              size="small"
              sx={{ mt: 2 }}
              onClick={() => navigate('/app/appointments')}
            >
              View All Appointments
            </Button>
          </>
        )}
      </CardContent>
    </Card>
  );
}
