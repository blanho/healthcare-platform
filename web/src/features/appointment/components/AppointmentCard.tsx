import {
  Card,
  CardContent,
  CardActionArea,
  Box,
  Typography,
  Avatar,
  Stack,
  Divider,
  IconButton,
  Menu,
  MenuItem,
} from '@mui/material';
import { MoreVert as MoreIcon, AccessTime as TimeIcon } from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { useState } from 'react';
import { AppointmentStatusChip, AppointmentTypeChip, TimeSlot } from './AppointmentChips';
import type { AppointmentResponse } from '../types/appointment.types';

interface AppointmentCardProps {
  appointment: AppointmentResponse;
  onView?: () => void;
  onReschedule?: () => void;
  onCancel?: () => void;
  onCheckIn?: () => void;
  onComplete?: () => void;
  showPatientInfo?: boolean;
  showProviderInfo?: boolean;
}

export function AppointmentCard({
  appointment,
  onView,
  onReschedule,
  onCancel,
  onCheckIn,
  onComplete,
  showPatientInfo = true,
  showProviderInfo = true,
}: AppointmentCardProps) {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const menuOpen = Boolean(anchorEl);

  const handleMenuOpen = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    setAnchorEl(e.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleAction = (action?: () => void) => {
    handleMenuClose();
    action?.();
  };

  const canReschedule = ['SCHEDULED', 'CONFIRMED'].includes(appointment.status);
  const canCancel = ['SCHEDULED', 'CONFIRMED', 'CHECKED_IN'].includes(appointment.status);
  const canCheckIn = appointment.status === 'CONFIRMED';
  const canComplete = appointment.status === 'IN_PROGRESS';

  return (
    <Card
      sx={{
        transition: 'box-shadow 200ms, transform 200ms',
        '&:hover': onView ? { boxShadow: 3, transform: 'translateY(-1px)' } : undefined,
      }}
    >
      <CardActionArea
        onClick={onView}
        disabled={!onView}
        sx={{ cursor: onView ? 'pointer' : 'default' }}
      >
        <CardContent>
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              mb: 2,
            }}
          >
            <Stack direction="row" spacing={1}>
              <AppointmentStatusChip status={appointment.status} />
              <AppointmentTypeChip type={appointment.appointmentType} />
            </Stack>
            <IconButton size="small" onClick={handleMenuOpen}>
              <MoreIcon fontSize="small" />
            </IconButton>
          </Box>

          {}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
            <TimeIcon fontSize="small" color="action" />
            <Typography variant="body2" fontWeight={600}>
              {format(parseISO(appointment.scheduledDate), 'EEEE, MMM d, yyyy')}
            </Typography>
            <Box sx={{ mx: 1 }}>
              <TimeSlot startTime={appointment.startTime} endTime={appointment.endTime} />
            </Box>
          </Box>

          <Divider sx={{ my: 1.5 }} />

          {}
          <Stack spacing={1.5}>
            {showPatientInfo && (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Avatar
                  sx={{
                    width: 32,
                    height: 32,
                    fontSize: '0.75rem',
                    bgcolor: 'primary.light',
                    color: 'primary.main',
                  }}
                >
                  PA
                </Avatar>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Patient
                  </Typography>
                  <Typography variant="body2" fontWeight={500}>
                    Patient #{appointment.patientId.slice(0, 8)}
                  </Typography>
                </Box>
              </Box>
            )}
            {showProviderInfo && (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <Avatar
                  sx={{
                    width: 32,
                    height: 32,
                    fontSize: '0.75rem',
                    bgcolor: 'secondary.light',
                    color: 'secondary.main',
                  }}
                >
                  DR
                </Avatar>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Provider
                  </Typography>
                  <Typography variant="body2" fontWeight={500}>
                    Provider #{appointment.providerId.slice(0, 8)}
                  </Typography>
                </Box>
              </Box>
            )}
          </Stack>

          {}
          {appointment.reasonForVisit && (
            <Box sx={{ mt: 2, p: 1.5, bgcolor: 'grey.50', borderRadius: 1 }}>
              <Typography variant="caption" color="text.secondary" display="block">
                Reason for Visit
              </Typography>
              <Typography variant="body2">{appointment.reasonForVisit}</Typography>
            </Box>
          )}
        </CardContent>
      </CardActionArea>

      {}
      <Menu anchorEl={anchorEl} open={menuOpen} onClose={handleMenuClose}>
        {onView && <MenuItem onClick={() => handleAction(onView)}>View Details</MenuItem>}
        {canReschedule && onReschedule && (
          <MenuItem onClick={() => handleAction(onReschedule)}>Reschedule</MenuItem>
        )}
        {canCheckIn && onCheckIn && (
          <MenuItem onClick={() => handleAction(onCheckIn)}>Check In</MenuItem>
        )}
        {canComplete && onComplete && (
          <MenuItem onClick={() => handleAction(onComplete)}>Complete</MenuItem>
        )}
        {canCancel && onCancel && (
          <MenuItem onClick={() => handleAction(onCancel)} sx={{ color: 'error.main' }}>
            Cancel
          </MenuItem>
        )}
      </Menu>
    </Card>
  );
}
