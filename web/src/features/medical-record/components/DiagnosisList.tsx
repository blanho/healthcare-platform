import {
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  Stack,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Avatar,
} from '@mui/material';
import {
  LocalHospital as DiagnosisIcon,
  Star as PrimaryIcon,
  CheckCircle as ResolvedIcon,
  Loop as ChronicIcon,
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import type { DiagnosisResponse } from '../types/medical-record.types';
import type { DiagnosisType } from '@/types';

interface DiagnosisListProps {
  diagnoses: DiagnosisResponse[];
  compact?: boolean;
}

const diagnosisTypeColors: Record<DiagnosisType, string> = {
  PRIMARY: '#0891B2',
  SECONDARY: '#059669',
  DIFFERENTIAL: '#D97706',
  RULE_OUT: '#DC2626',
};

export function DiagnosisList({ diagnoses }: DiagnosisListProps) {
  if (diagnoses.length === 0) {
    return (
      <Card>
        <CardContent>
          <Typography variant="h4" sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
            <DiagnosisIcon fontSize="small" /> Diagnoses
          </Typography>
          <Typography variant="body2" color="text.secondary">
            No diagnoses recorded
          </Typography>
        </CardContent>
      </Card>
    );
  }

  const sortedDiagnoses = [...diagnoses].sort((a, b) => {
    if (a.primary && !b.primary) return -1;
    if (!a.primary && b.primary) return 1;
    if (!a.isResolved && b.isResolved) return -1;
    if (a.isResolved && !b.isResolved) return 1;
    return 0;
  });

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <DiagnosisIcon fontSize="small" /> Diagnoses
          </Typography>
          <Chip label={`${diagnoses.length} total`} size="small" variant="outlined" />
        </Box>

        <List disablePadding>
          {sortedDiagnoses.map((diagnosis, index) => (
            <ListItem
              key={`${diagnosis.code}-${index}`}
              disablePadding
              sx={{
                py: 1.5,
                borderBottom: index < sortedDiagnoses.length - 1 ? '1px solid' : 'none',
                borderColor: 'divider',
                opacity: diagnosis.isResolved ? 0.7 : 1,
              }}
            >
              <ListItemIcon sx={{ minWidth: 48 }}>
                <Avatar
                  sx={{
                    width: 36,
                    height: 36,
                    bgcolor: `${diagnosisTypeColors[diagnosis.type]}14`,
                    color: diagnosisTypeColors[diagnosis.type],
                    fontSize: '0.75rem',
                    fontWeight: 600,
                  }}
                >
                  {diagnosis.code.slice(0, 3)}
                </Avatar>
              </ListItemIcon>
              <ListItemText
                primary={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
                    <Typography
                      variant="body1"
                      fontWeight={500}
                      sx={{ textDecoration: diagnosis.isResolved ? 'line-through' : 'none' }}
                    >
                      {diagnosis.description}
                    </Typography>
                    <Stack direction="row" spacing={0.5}>
                      {diagnosis.primary && (
                        <Chip
                          icon={<PrimaryIcon fontSize="small" />}
                          label="Primary"
                          size="small"
                          color="primary"
                          sx={{ height: 20 }}
                        />
                      )}
                      {diagnosis.isChronic && (
                        <Chip
                          icon={<ChronicIcon fontSize="small" />}
                          label="Chronic"
                          size="small"
                          color="secondary"
                          variant="outlined"
                          sx={{ height: 20 }}
                        />
                      )}
                      {diagnosis.isResolved && (
                        <Chip
                          icon={<ResolvedIcon fontSize="small" />}
                          label="Resolved"
                          size="small"
                          color="success"
                          variant="outlined"
                          sx={{ height: 20 }}
                        />
                      )}
                    </Stack>
                  </Box>
                }
                secondary={
                  <Box sx={{ mt: 0.5 }}>
                    <Typography variant="caption" fontFamily="monospace" sx={{ mr: 2 }}>
                      {diagnosis.code}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {diagnosis.type.replace('_', ' ')}
                    </Typography>
                    {diagnosis.onsetDate && (
                      <Typography variant="caption" color="text.secondary" sx={{ ml: 2 }}>
                        Onset: {format(parseISO(diagnosis.onsetDate), 'MMM d, yyyy')}
                      </Typography>
                    )}
                    {diagnosis.resolvedDate && (
                      <Typography variant="caption" color="text.secondary" sx={{ ml: 2 }}>
                        Resolved: {format(parseISO(diagnosis.resolvedDate), 'MMM d, yyyy')}
                      </Typography>
                    )}
                  </Box>
                }
              />
            </ListItem>
          ))}
        </List>
      </CardContent>
    </Card>
  );
}
