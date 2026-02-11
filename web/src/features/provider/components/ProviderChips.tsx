import { Chip, Avatar } from '@mui/material';
import {
  CheckCircle as ActiveIcon,
  Cancel as InactiveIcon,
  Pending as PendingIcon,
  Block as SuspendedIcon,
  EventBusy as OnLeaveIcon,
  PersonOff as RetiredIcon,
} from '@mui/icons-material';
import type { ProviderStatus, ProviderType } from '@/types';
import { providerStatusConfig, providerTypeConfig } from './provider-chip-config';

const statusIcons: Record<ProviderStatus, React.ReactNode> = {
  ACTIVE: <ActiveIcon fontSize="small" />,
  INACTIVE: <InactiveIcon fontSize="small" />,
  PENDING_VERIFICATION: <PendingIcon fontSize="small" />,
  SUSPENDED: <SuspendedIcon fontSize="small" />,
  ON_LEAVE: <OnLeaveIcon fontSize="small" />,
  RETIRED: <RetiredIcon fontSize="small" />,
};

interface ProviderStatusChipProps {
  status: ProviderStatus;
  size?: 'small' | 'medium';
  showIcon?: boolean;
}

export function ProviderStatusChip({
  status,
  size = 'small',
  showIcon = false,
}: ProviderStatusChipProps) {
  const config = providerStatusConfig[status] || { label: status, color: 'default' as const };
  const icon = statusIcons[status];
  return (
    <Chip
      label={config.label}
      color={config.color}
      size={size}
      icon={showIcon ? (icon as React.ReactElement) : undefined}
    />
  );
}

interface ProviderTypeChipProps {
  type: ProviderType;
  showAvatar?: boolean;
}

export function ProviderTypeChip({ type, showAvatar = true }: ProviderTypeChipProps) {
  const config = providerTypeConfig[type] || { label: type, color: '#6B7280', abbreviation: '?' };

  return (
    <Chip
      size="small"
      avatar={
        showAvatar ? (
          <Avatar
            sx={{
              bgcolor: config.color,
              color: 'white',
              fontSize: '0.6rem',
              width: 24,
              height: 24,
            }}
          >
            {config.abbreviation}
          </Avatar>
        ) : undefined
      }
      label={config.label}
      sx={{
        borderColor: config.color,
        color: config.color,
      }}
      variant="outlined"
    />
  );
}

interface AcceptingPatientsChipProps {
  accepting: boolean;
}

export function AcceptingPatientsChip({ accepting }: AcceptingPatientsChipProps) {
  return (
    <Chip
      label={accepting ? 'Accepting Patients' : 'Not Accepting'}
      color={accepting ? 'success' : 'default'}
      size="small"
      variant="outlined"
    />
  );
}

interface LicenseBadgeProps {
  valid: boolean;
  daysUntilExpiry: number;
  licenseState: string;
}

export function LicenseBadge({ valid, daysUntilExpiry, licenseState }: LicenseBadgeProps) {
  const getColor = () => {
    if (!valid) return 'error';
    if (daysUntilExpiry <= 30) return 'warning';
    return 'success';
  };

  const getLabel = () => {
    if (!valid) return 'License Expired';
    if (daysUntilExpiry <= 30) return `Expires in ${daysUntilExpiry}d`;
    return `Licensed (${licenseState})`;
  };

  return <Chip label={getLabel()} color={getColor()} size="small" />;
}
