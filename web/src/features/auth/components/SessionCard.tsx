

import { Box, Paper, Typography, Chip, IconButton, Tooltip, useTheme } from '@mui/material';
import ComputerIcon from '@mui/icons-material/Computer';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import TabletIcon from '@mui/icons-material/Tablet';
import DevicesOtherIcon from '@mui/icons-material/DevicesOther';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import type { SessionResponse } from '../types/auth.types';
import { formatRelativeDate } from '../utils';

interface SessionCardProps {
  session: SessionResponse;
  onRevoke: (sessionId: string) => void;
  isRevoking?: boolean;
}

export function SessionCard({ session, onRevoke, isRevoking = false }: SessionCardProps) {
  const theme = useTheme();

  const getDeviceIcon = (deviceType: string | null) => {
    switch (deviceType?.toLowerCase()) {
      case 'desktop':
        return <ComputerIcon />;
      case 'mobile':
        return <PhoneIphoneIcon />;
      case 'tablet':
        return <TabletIcon />;
      default:
        return <DevicesOtherIcon />;
    }
  };

  const deviceName =
    session.deviceName ||
    [session.browser, session.operatingSystem].filter(Boolean).join(' on ') ||
    'Unknown device';

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 2,
        borderRadius: 2,
        display: 'flex',
        alignItems: 'center',
        gap: 2,
        borderColor: session.current ? theme.palette.primary.main : undefined,
        backgroundColor: session.current ? theme.palette.action.hover : undefined,
      }}
    >
      {}
      <Box
        sx={{
          width: 48,
          height: 48,
          borderRadius: '50%',
          backgroundColor: theme.palette.action.hover,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: session.current ? theme.palette.primary.main : theme.palette.text.secondary,
        }}
      >
        {getDeviceIcon(session.deviceType)}
      </Box>

      {}
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
          <Typography variant="body1" fontWeight={500} noWrap>
            {deviceName}
          </Typography>
          {session.current && (
            <Chip
              label="Current"
              size="small"
              color="primary"
              variant="outlined"
              sx={{ height: 20, fontSize: '0.7rem' }}
            />
          )}
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
          {}
          {session.location && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <LocationOnIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
              <Typography variant="caption" color="text.secondary">
                {session.location}
              </Typography>
            </Box>
          )}

          {}
          <Typography variant="caption" color="text.secondary" sx={{ fontFamily: 'monospace' }}>
            {session.ipAddress}
          </Typography>

          {}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <AccessTimeIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
            <Typography variant="caption" color="text.secondary">
              {formatRelativeDate(session.lastActivityAt)}
            </Typography>
          </Box>
        </Box>
      </Box>

      {}
      {!session.current && (
        <Tooltip title="Revoke session">
          <IconButton
            onClick={() => onRevoke(session.id)}
            disabled={isRevoking}
            color="error"
            size="small"
            sx={{ cursor: 'pointer' }}
          >
            <DeleteOutlineIcon />
          </IconButton>
        </Tooltip>
      )}
    </Paper>
  );
}

export function SessionsEmptyState() {
  const theme = useTheme();

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 4,
        textAlign: 'center',
        borderRadius: 3,
        borderStyle: 'dashed',
      }}
    >
      <DevicesOtherIcon sx={{ fontSize: 48, color: theme.palette.text.secondary, mb: 2 }} />
      <Typography variant="body1" color="text.secondary">
        No other active sessions
      </Typography>
    </Paper>
  );
}
