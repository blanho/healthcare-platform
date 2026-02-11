import { Card, CardContent, Typography, Box, Chip, Stack } from '@mui/material';
import {
  Person as SubjectiveIcon,
  Visibility as ObjectiveIcon,
  Psychology as AssessmentIcon,
  Assignment as PlanIcon,
  CheckCircle as CompleteIcon,
} from '@mui/icons-material';
import type { SoapNoteResponse } from '../types/medical-record.types';

interface SoapNoteCardProps {
  soapNote: SoapNoteResponse | null;
}

interface SectionProps {
  title: string;
  icon: React.ReactNode;
  content: string | null;
  color: string;
}

function Section({ title, icon, content, color }: SectionProps) {
  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
        <Box
          sx={{
            p: 0.5,
            borderRadius: 1,
            bgcolor: `${color}14`,
            color,
            display: 'flex',
          }}
        >
          {icon}
        </Box>
        <Typography variant="subtitle2" fontWeight={600}>
          {title}
        </Typography>
      </Box>
      <Box
        sx={{
          pl: 4,
          borderLeft: '2px solid',
          borderColor: color,
          py: 1,
        }}
      >
        <Typography
          variant="body2"
          sx={{
            whiteSpace: 'pre-wrap',
            color: content ? 'text.primary' : 'text.disabled',
          }}
        >
          {content || 'Not documented'}
        </Typography>
      </Box>
    </Box>
  );
}

export function SoapNoteCard({ soapNote }: SoapNoteCardProps) {
  if (!soapNote) {
    return (
      <Card>
        <CardContent>
          <Typography variant="h4" sx={{ mb: 2 }}>
            SOAP Note
          </Typography>
          <Typography variant="body2" color="text.secondary">
            No SOAP note recorded
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4">SOAP Note</Typography>
          {soapNote.isComplete && (
            <Chip
              icon={<CompleteIcon fontSize="small" />}
              label="Complete"
              color="success"
              size="small"
            />
          )}
        </Box>

        <Stack spacing={3}>
          <Section
            title="Subjective"
            icon={<SubjectiveIcon fontSize="small" />}
            content={soapNote.subjective}
            color="#0891B2"
          />

          <Section
            title="Objective"
            icon={<ObjectiveIcon fontSize="small" />}
            content={soapNote.objective}
            color="#059669"
          />

          <Section
            title="Assessment"
            icon={<AssessmentIcon fontSize="small" />}
            content={soapNote.assessment}
            color="#D97706"
          />

          <Section
            title="Plan"
            icon={<PlanIcon fontSize="small" />}
            content={soapNote.plan}
            color="#7C3AED"
          />
        </Stack>
      </CardContent>
    </Card>
  );
}
