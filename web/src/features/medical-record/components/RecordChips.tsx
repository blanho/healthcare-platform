import { Chip, Avatar, Box, Typography, LinearProgress } from '@mui/material';
import {
  CheckCircle as FinalizedIcon,
  Edit as DraftIcon,
  History as AmendedIcon,
  Cancel as VoidedIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';
import type { RecordStatus, RecordType } from '@/types';
import { recordStatusConfig, recordTypeConfig } from './record-chip-config';

const statusIcons: Record<RecordStatus, React.ReactNode> = {
  DRAFT: <DraftIcon fontSize="small" />,
  FINALIZED: <FinalizedIcon fontSize="small" />,
  AMENDED: <AmendedIcon fontSize="small" />,
  VOIDED: <VoidedIcon fontSize="small" />,
};

interface RecordStatusChipProps {
  status: RecordStatus;
  size?: 'small' | 'medium';
  showIcon?: boolean;
}

export function RecordStatusChip({
  status,
  size = 'small',
  showIcon = false,
}: RecordStatusChipProps) {
  const config = recordStatusConfig[status] || { label: status, color: 'default' as const };
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

interface RecordTypeChipProps {
  type: RecordType;
  showAvatar?: boolean;
}

export function RecordTypeChip({ type, showAvatar = true }: RecordTypeChipProps) {
  const config = recordTypeConfig[type] || { label: type, color: '#6B7280', icon: '?' };

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
            {config.icon}
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

interface VitalValueProps {
  label: string;
  value: string | number | null;
  unit?: string;
  isCritical?: boolean;
  normalRange?: string;
}

export function VitalValue({
  label,
  value,
  unit,
  isCritical = false,
  normalRange,
}: VitalValueProps) {
  return (
    <Box>
      <Typography variant="caption" color="text.secondary" display="block">
        {label}
      </Typography>
      <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 0.5 }}>
        <Typography
          variant="h4"
          sx={{
            color: isCritical ? 'error.main' : 'text.primary',
            fontWeight: 600,
          }}
        >
          {value ?? '—'}
        </Typography>
        {unit && (
          <Typography variant="caption" color="text.secondary">
            {unit}
          </Typography>
        )}
        {isCritical && <WarningIcon sx={{ fontSize: 16, color: 'error.main', ml: 0.5 }} />}
      </Box>
      {normalRange && (
        <Typography variant="caption" color="text.secondary">
          Normal: {normalRange}
        </Typography>
      )}
    </Box>
  );
}

interface PainLevelProps {
  level: number | null;
}

export function PainLevel({ level }: PainLevelProps) {
  if (level === null)
    return (
      <Typography variant="body2" color="text.secondary">
        —
      </Typography>
    );

  const getColor = () => {
    if (level <= 3) return 'success.main';
    if (level <= 6) return 'warning.main';
    return 'error.main';
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
        <Typography variant="caption" color="text.secondary">
          Pain Level
        </Typography>
        <Typography variant="caption" fontWeight={600} sx={{ color: getColor() }}>
          {level}/10
        </Typography>
      </Box>
      <LinearProgress
        variant="determinate"
        value={level * 10}
        sx={{
          height: 8,
          borderRadius: 4,
          bgcolor: 'grey.200',
          '& .MuiLinearProgress-bar': {
            bgcolor: getColor(),
            borderRadius: 4,
          },
        }}
      />
    </Box>
  );
}

interface BMIDisplayProps {
  bmi: number | null;
}

export function BMIDisplay({ bmi }: BMIDisplayProps) {
  if (bmi === null) return null;

  const getCategory = () => {
    if (bmi < 18.5) return { label: 'Underweight', color: 'info.main' };
    if (bmi < 25) return { label: 'Normal', color: 'success.main' };
    if (bmi < 30) return { label: 'Overweight', color: 'warning.main' };
    return { label: 'Obese', color: 'error.main' };
  };

  const category = getCategory();

  return (
    <Box>
      <Typography variant="caption" color="text.secondary" display="block">
        BMI
      </Typography>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <Typography variant="h4" fontWeight={600}>
          {bmi.toFixed(1)}
        </Typography>
        <Chip
          label={category.label}
          size="small"
          sx={{ bgcolor: category.color, color: 'white' }}
        />
      </Box>
    </Box>
  );
}
