

import { Chip, type ChipProps } from '@mui/material';
import {
  CheckCircleOutline,
  ErrorOutline,
  WarningAmber,
  Block,
  Info,
  Security,
  PersonOutline,
  Description,
  LocalHospital,
  Event,
  Receipt,
  Notifications,
  Settings,
} from '@mui/icons-material';
import type {
  AuditAction,
  AuditOutcome,
  AuditSeverity,
  ResourceCategory,
} from '../types/audit.types';

const outcomeConfig: Record<
  AuditOutcome,
  { label: string; color: ChipProps['color']; icon: React.ReactNode }
> = {
  SUCCESS: { label: 'Success', color: 'success', icon: <CheckCircleOutline fontSize="small" /> },
  FAILURE: { label: 'Failed', color: 'error', icon: <ErrorOutline fontSize="small" /> },
  DENIED: { label: 'Denied', color: 'warning', icon: <Block fontSize="small" /> },
};

interface OutcomeChipProps {
  outcome: AuditOutcome;
  size?: 'small' | 'medium';
}

export function AuditOutcomeChip({ outcome, size = 'small' }: OutcomeChipProps) {
  const config = outcomeConfig[outcome] ?? { label: outcome, color: 'default', icon: null };
  return (
    <Chip
      label={config.label}
      color={config.color}
      size={size}
      icon={config.icon as React.ReactElement}
      variant="outlined"
    />
  );
}

const severityConfig: Record<
  AuditSeverity,
  { label: string; color: ChipProps['color']; icon: React.ReactNode }
> = {
  INFO: { label: 'Info', color: 'info', icon: <Info fontSize="small" /> },
  WARNING: { label: 'Warning', color: 'warning', icon: <WarningAmber fontSize="small" /> },
  ERROR: { label: 'Error', color: 'error', icon: <ErrorOutline fontSize="small" /> },
  CRITICAL: { label: 'Critical', color: 'error', icon: <Security fontSize="small" /> },
};

interface SeverityChipProps {
  severity: AuditSeverity;
  size?: 'small' | 'medium';
}

export function AuditSeverityChip({ severity, size = 'small' }: SeverityChipProps) {
  const config = severityConfig[severity] ?? { label: severity, color: 'default', icon: null };
  return (
    <Chip
      label={config.label}
      color={config.color}
      size={size}
      icon={config.icon as React.ReactElement}
      variant={severity === 'CRITICAL' ? 'filled' : 'outlined'}
    />
  );
}

const actionLabels: Record<AuditAction, string> = {
  CREATE: 'Create',
  READ: 'Read',
  UPDATE: 'Update',
  DELETE: 'Delete',
  LOGIN: 'Login',
  LOGOUT: 'Logout',
  LOGIN_FAILED: 'Login Failed',
  PASSWORD_CHANGE: 'Password Change',
  PASSWORD_RESET: 'Password Reset',
  MFA_ENABLE: 'MFA Enabled',
  MFA_DISABLE: 'MFA Disabled',
  MFA_VERIFY: 'MFA Verified',
  SESSION_CREATE: 'Session Created',
  SESSION_REVOKE: 'Session Revoked',
  EXPORT: 'Export',
  PRINT: 'Print',
  SEARCH: 'Search',
  VIEW: 'View',
};

const actionColors: Partial<Record<AuditAction, ChipProps['color']>> = {
  CREATE: 'success',
  DELETE: 'error',
  UPDATE: 'warning',
  LOGIN: 'info',
  LOGOUT: 'default',
  LOGIN_FAILED: 'error',
  EXPORT: 'warning',
  PRINT: 'warning',
};

interface ActionChipProps {
  action: AuditAction;
  size?: 'small' | 'medium';
}

export function AuditActionChip({ action, size = 'small' }: ActionChipProps) {
  const label = actionLabels[action] ?? action;
  const color = actionColors[action] ?? 'default';
  return <Chip label={label} color={color} size={size} variant="outlined" />;
}

const resourceConfig: Record<ResourceCategory, { label: string; icon: React.ReactNode }> = {
  PATIENT: { label: 'Patient', icon: <PersonOutline fontSize="small" /> },
  MEDICAL_RECORD: { label: 'Medical Record', icon: <Description fontSize="small" /> },
  APPOINTMENT: { label: 'Appointment', icon: <Event fontSize="small" /> },
  PROVIDER: { label: 'Provider', icon: <LocalHospital fontSize="small" /> },
  INVOICE: { label: 'Invoice', icon: <Receipt fontSize="small" /> },
  CLAIM: { label: 'Claim', icon: <Receipt fontSize="small" /> },
  PAYMENT: { label: 'Payment', icon: <Receipt fontSize="small" /> },
  USER: { label: 'User', icon: <PersonOutline fontSize="small" /> },
  NOTIFICATION: { label: 'Notification', icon: <Notifications fontSize="small" /> },
  SYSTEM: { label: 'System', icon: <Settings fontSize="small" /> },
};

interface ResourceCategoryChipProps {
  category: ResourceCategory;
  size?: 'small' | 'medium';
}

export function ResourceCategoryChip({ category, size = 'small' }: ResourceCategoryChipProps) {
  const config = resourceConfig[category] ?? { label: category, icon: null };
  return (
    <Chip
      label={config.label}
      size={size}
      icon={config.icon as React.ReactElement}
      variant="outlined"
      sx={{ bgcolor: 'background.paper' }}
    />
  );
}

interface TimestampDisplayProps {
  timestamp: string;
  showRelative?: boolean;
}

export function TimestampDisplay({ timestamp, showRelative = true }: TimestampDisplayProps) {
  const date = new Date(timestamp);
  const formatted = date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });

  if (!showRelative) {
    return <span>{formatted}</span>;
  }

  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  let relative = '';
  if (diffMins < 1) {
    relative = 'just now';
  } else if (diffMins < 60) {
    relative = `${diffMins}m ago`;
  } else if (diffHours < 24) {
    relative = `${diffHours}h ago`;
  } else if (diffDays < 7) {
    relative = `${diffDays}d ago`;
  } else {
    relative = formatted;
  }

  return (
    <span title={formatted} style={{ cursor: 'help' }}>
      {relative}
    </span>
  );
}
