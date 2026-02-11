import {
  Card,
  CardContent,
  CardActionArea,
  Box,
  Typography,
  Avatar,
  Stack,
  Chip,
} from '@mui/material';
import { Email as EmailIcon } from '@mui/icons-material';
import {
  ProviderStatusChip,
  ProviderTypeChip,
  AcceptingPatientsChip,
} from './ProviderChips';
import type { ProviderSummaryResponse } from '../types/provider.types';

interface ProviderCardProps {
  provider: ProviderSummaryResponse;
  onClick?: () => void;
  onEdit?: () => void;
  compact?: boolean;
}

export function ProviderCard({ provider, onClick, compact = false }: ProviderCardProps) {
  const nameParts = provider.displayName.split(' ');
  const initials = nameParts.length >= 2
    ? `${nameParts[0][0]}${nameParts[nameParts.length - 1][0]}`
    : provider.displayName.substring(0, 2).toUpperCase();

  return (
    <Card
      sx={{
        height: '100%',
        transition: 'box-shadow 200ms, transform 200ms',
        '&:hover': onClick ? { boxShadow: 3, transform: 'translateY(-2px)' } : undefined,
      }}
    >
      <CardActionArea
        onClick={onClick}
        disabled={!onClick}
        sx={{ height: '100%', cursor: onClick ? 'pointer' : 'default' }}
      >
        <CardContent>
          {}
          <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 2, mb: 2 }}>
            <Avatar
              sx={{
                width: compact ? 48 : 64,
                height: compact ? 48 : 64,
                bgcolor: 'primary.light',
                color: 'primary.main',
                fontSize: compact ? '1rem' : '1.25rem',
                fontWeight: 600,
              }}
            >
              {initials}
            </Avatar>
            <Box sx={{ flex: 1, minWidth: 0 }}>
              <Typography variant={compact ? 'body1' : 'h4'} sx={{ fontWeight: 600 }} noWrap>
                {provider.displayName}
              </Typography>
              <Typography variant="caption" color="text.secondary" display="block">
                {provider.providerNumber}
              </Typography>
              <Stack direction="row" spacing={0.5} sx={{ mt: 0.5, flexWrap: 'wrap' }}>
                <ProviderStatusChip status={provider.status} />
                <AcceptingPatientsChip accepting={provider.acceptingPatients} />
              </Stack>
            </Box>
          </Box>

          {}
          <Stack direction="row" spacing={1} sx={{ mb: 2 }} flexWrap="wrap" useFlexGap>
            <ProviderTypeChip type={provider.providerType} />
            {provider.specialization && (
              <Chip label={provider.specialization} size="small" variant="outlined" />
            )}
          </Stack>

          {!compact && (
            <>
              {}
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
                <EmailIcon fontSize="small" color="action" />
                <Typography variant="body2" noWrap>
                  {provider.email}
                </Typography>
              </Box>
            </>
          )}
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
