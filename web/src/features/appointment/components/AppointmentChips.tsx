import { Chip, Avatar, Box, Typography } from '@mui/material';
import type { AppointmentStatus, AppointmentType } from '@/types';
import { appointmentStatusConfig, appointmentTypeConfig } from './appointment-chip-config';

interface AppointmentStatusChipProps {
  status: AppointmentStatus;
  size?: 'small' | 'medium';
}

export function AppointmentStatusChip({ status, size = 'small' }: AppointmentStatusChipProps) {
  const config = appointmentStatusConfig[status] || { label: status, color: 'default' };
  return <Chip label={config.label} color={config.color} size={size} />;
}

interface AppointmentTypeChipProps {
  type: AppointmentType;
  showIcon?: boolean;
}

export function AppointmentTypeChip({ type, showIcon = true }: AppointmentTypeChipProps) {
  const config = appointmentTypeConfig[type] || { label: type, color: '#6B7280', icon: '?' };

  return (
    <Chip
      size="small"
      avatar={
        showIcon ? (
          <Avatar
            sx={{
              bgcolor: config.color,
              color: 'white',
              fontSize: '0.6rem',
              width: 24,
              height: 24,
            }}
          >
            {config.icon}
          </Avatar>
        ) : undefined
      }
      label={config.label}
      sx={{
        borderColor: config.color,
        color: config.color,
        '& .MuiChip-avatar': { color: 'white' },
      }}
      variant="outlined"
    />
  );
}

interface TimeSlotProps {
  startTime: string;
  endTime: string;
  compact?: boolean;
}

export function TimeSlot({ startTime, endTime, compact = false }: TimeSlotProps) {
  const formatTime = (time: string) => {
    const [hours, minutes] = time.split(':');
    const h = parseInt(hours);
    const ampm = h >= 12 ? 'PM' : 'AM';
    const hour12 = h % 12 || 12;
    return compact ? `${hour12}:${minutes}` : `${hour12}:${minutes} ${ampm}`;
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
      <Typography variant="body2" fontWeight={500}>
        {formatTime(startTime)}
      </Typography>
      <Typography variant="body2" color="text.secondary">
        –
      </Typography>
      <Typography variant="body2" fontWeight={500}>
        {formatTime(endTime)}
      </Typography>
    </Box>
  );
}
