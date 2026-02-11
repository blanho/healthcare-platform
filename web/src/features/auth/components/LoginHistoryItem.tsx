

import { Box, Paper, Typography, Chip, useTheme } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import BlockIcon from '@mui/icons-material/Block';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import type { LoginAttemptResponse, LoginAttemptStatus } from '../types/auth.types';
import { formatLoginDateTime } from '../utils';

interface LoginHistoryItemProps {
  attempt: LoginAttemptResponse;
}

export function LoginHistoryItem({ attempt }: LoginHistoryItemProps) {
  const theme = useTheme();

  const getStatusConfig = (status: LoginAttemptStatus) => {
    const configs: Record<
      LoginAttemptStatus,
      { icon: React.ReactNode; label: string; color: 'success' | 'error' | 'warning' }
    > = {
      SUCCESS: {
        icon: <CheckCircleIcon fontSize="small" />,
        label: 'Successful',
        color: 'success',
      },
      FAILED_INVALID_CREDENTIALS: {
        icon: <ErrorIcon fontSize="small" />,
        label: 'Invalid credentials',
        color: 'error',
      },
      FAILED_ACCOUNT_LOCKED: {
        icon: <BlockIcon fontSize="small" />,
        label: 'Account locked',
        color: 'error',
      },
      FAILED_ACCOUNT_DISABLED: {
        icon: <BlockIcon fontSize="small" />,
        label: 'Account disabled',
        color: 'error',
      },
      FAILED_EMAIL_NOT_VERIFIED: {
        icon: <ErrorIcon fontSize="small" />,
        label: 'Email not verified',
        color: 'warning',
      },
      FAILED_MFA_REQUIRED: {
        icon: <ErrorIcon fontSize="small" />,
        label: 'MFA required',
        color: 'warning',
      },
      FAILED_MFA_INVALID: {
        icon: <ErrorIcon fontSize="small" />,
        label: 'Invalid MFA code',
        color: 'error',
      },
    };
    return (
      configs[status] || {
        icon: <ErrorIcon fontSize="small" />,
        label: status,
        color: 'error' as const,
      }
    );
  };

  const statusConfig = getStatusConfig(attempt.status);

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 2,
        borderRadius: 2,
        display: 'flex',
        alignItems: 'center',
        gap: 2,
        borderLeftWidth: 3,
        borderLeftColor: attempt.successful ? theme.palette.success.main : theme.palette.error.main,
      }}
    >
      {}
      <Box
        sx={{
          width: 40,
          height: 40,
          borderRadius: '50%',
          backgroundColor: attempt.successful
            ? theme.palette.success.light + '20'
            : theme.palette.error.light + '20',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: attempt.successful ? theme.palette.success.main : theme.palette.error.main,
        }}
      >
        {statusConfig.icon}
      </Box>

      {}
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
          <Chip
            label={statusConfig.label}
            size="small"
            color={statusConfig.color}
            variant="outlined"
            sx={{ height: 22, fontSize: '0.75rem' }}
          />
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
          {}
          {attempt.location && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <LocationOnIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
              <Typography variant="caption" color="text.secondary">
                {attempt.location}
              </Typography>
            </Box>
          )}

          {}
          <Typography variant="caption" color="text.secondary" sx={{ fontFamily: 'monospace' }}>
            {attempt.ipAddress}
          </Typography>

          {}
          <Typography variant="caption" color="text.secondary">
            {formatLoginDateTime(attempt.attemptedAt)}
          </Typography>
        </Box>
      </Box>
    </Paper>
  );
}
