import { Card, CardContent, Typography, Grid, Divider, Box, Chip } from '@mui/material';
import {
  Favorite as HeartIcon,
  Thermostat as TempIcon,
  Air as BreathIcon,
  Opacity as O2Icon,
} from '@mui/icons-material';
import { VitalValue, PainLevel, BMIDisplay } from './RecordChips';
import type { VitalSignsResponse } from '../types/medical-record.types';

interface VitalsCardProps {
  vitals: VitalSignsResponse | null;
  compact?: boolean;
}

export function VitalsCard({ vitals, compact = false }: VitalsCardProps) {
  if (!vitals) {
    return (
      <Card>
        <CardContent>
          <Typography variant="h4" sx={{ mb: 2 }}>
            Vital Signs
          </Typography>
          <Typography variant="body2" color="text.secondary">
            No vital signs recorded
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4">Vital Signs</Typography>
          {vitals.hasCriticalValue && <Chip label="Critical Values" color="error" size="small" />}
        </Box>

        <Grid container spacing={compact ? 2 : 3}>
          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
              <HeartIcon fontSize="small" sx={{ color: 'error.main' }} />
              <Typography variant="caption" color="text.secondary">
                Blood Pressure
              </Typography>
            </Box>
            <Typography variant="h4" fontWeight={600}>
              {vitals.bloodPressure || '—'}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              mmHg
            </Typography>
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <VitalValue
              label="Heart Rate"
              value={vitals.heartRate}
              unit="bpm"
              normalRange="60-100"
            />
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
              <TempIcon fontSize="small" sx={{ color: 'warning.main' }} />
              <Typography variant="caption" color="text.secondary">
                Temperature
              </Typography>
            </Box>
            <Typography variant="h4" fontWeight={600}>
              {vitals.temperature ?? '—'}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              °F
            </Typography>
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
              <BreathIcon fontSize="small" sx={{ color: 'info.main' }} />
              <Typography variant="caption" color="text.secondary">
                Resp. Rate
              </Typography>
            </Box>
            <Typography variant="h4" fontWeight={600}>
              {vitals.respiratoryRate ?? '—'}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              /min
            </Typography>
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
              <O2Icon fontSize="small" sx={{ color: 'primary.main' }} />
              <Typography variant="caption" color="text.secondary">
                SpO2
              </Typography>
            </Box>
            <Typography variant="h4" fontWeight={600}>
              {vitals.oxygenSaturation ?? '—'}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              %
            </Typography>
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <VitalValue label="Weight" value={vitals.weightKg} unit="kg" />
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <VitalValue label="Height" value={vitals.heightCm} unit="cm" />
          </Grid>

          {}
          <Grid size={{ xs: 6, sm: 4, md: 3 }}>
            <BMIDisplay bmi={vitals.bmi} />
          </Grid>

          {}
          <Grid size={{ xs: 12, sm: 6 }}>
            <PainLevel level={vitals.painLevel} />
          </Grid>
        </Grid>

        {vitals.recordedAt && (
          <>
            <Divider sx={{ my: 2 }} />
            <Typography variant="caption" color="text.secondary">
              Recorded at: {new Date(vitals.recordedAt).toLocaleString()}
            </Typography>
          </>
        )}
      </CardContent>
    </Card>
  );
}
