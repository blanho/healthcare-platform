import {
  Box,
  Container,
  Typography,
  Grid,
  Avatar,
  Chip,
  Stack,
  Paper,
  useTheme,
  alpha,
} from '@mui/material';
import { Security as ShieldIcon, Lock as LockIcon, Check as CheckIcon } from '@mui/icons-material';

interface Certification {
  name: string;
  description: string;
}

export function SecuritySection() {
  const certifications: Certification[] = [
    { name: 'HIPAA', description: 'Full compliance with healthcare data regulations' },
    { name: 'SOC 2', description: 'Type II certified security controls' },
    { name: 'GDPR', description: 'European data protection compliant' },
    { name: 'HL7 FHIR', description: 'Healthcare interoperability standard' },
  ];

  const securityFeatures = [
    'End-to-end encryption in transit and at rest',
    'Multi-factor authentication',
    'Role-based access controls',
    'Audit logging and monitoring',
    'Regular penetration testing',
  ];

  return (
    <Box component="section" py={12} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Grid container spacing={8} alignItems="center">
          <Grid size={{ xs: 12, lg: 6 }}>
            <Stack spacing={3}>
              <Chip
                label="Security & Compliance"
                color="primary"
                variant="outlined"
                sx={{ alignSelf: 'flex-start', fontWeight: 600 }}
              />
              <Typography
                variant="h2"
                sx={{ fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700 }}
              >
                Enterprise-Grade Security
              </Typography>
              <Typography variant="body1" color="text.secondary" lineHeight={1.8}>
                Your patient data is protected by the same security standards used by leading
                financial institutions. We take compliance seriously.
              </Typography>

              <Grid container spacing={2}>
                {certifications.map((cert, index) => (
                  <Grid key={index} size={6}>
                    <CertificationCard certification={cert} />
                  </Grid>
                ))}
              </Grid>
            </Stack>
          </Grid>

          <Grid size={{ xs: 12, lg: 6 }}>
            <SecurityFeatures features={securityFeatures} />
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}

interface CertificationCardProps {
  certification: Certification;
}

function CertificationCard({ certification }: CertificationCardProps) {
  const theme = useTheme();

  return (
    <Paper
      elevation={0}
      sx={{
        p: 2,
        borderRadius: 3,
        bgcolor: 'background.default',
        cursor: 'pointer',
        transition: 'all 0.2s ease',
        '&:hover': { bgcolor: alpha(theme.palette.primary.main, 0.05) },
      }}
    >
      <Stack direction="row" spacing={2} alignItems="flex-start">
        <Avatar sx={{ bgcolor: alpha(theme.palette.primary.main, 0.1), width: 40, height: 40 }}>
          <ShieldIcon color="primary" fontSize="small" />
        </Avatar>
        <Box>
          <Typography fontWeight={600}>{certification.name}</Typography>
          <Typography variant="caption" color="text.secondary">
            {certification.description}
          </Typography>
        </Box>
      </Stack>
    </Paper>
  );
}

interface SecurityFeaturesProps {
  features: string[];
}

function SecurityFeatures({ features }: SecurityFeaturesProps) {
  const theme = useTheme();

  return (
    <Paper
      elevation={0}
      sx={{
        p: 4,
        borderRadius: 4,
        bgcolor: 'background.paper',
        border: `1px solid ${theme.palette.divider}`,
        boxShadow: `0 24px 48px ${alpha(theme.palette.primary.main, 0.1)}`,
      }}
    >
      <Stack direction="row" spacing={2} alignItems="center" mb={4}>
        <Avatar
          sx={{
            width: 64,
            height: 64,
            background: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
          }}
        >
          <LockIcon sx={{ fontSize: 32 }} />
        </Avatar>
        <Box>
          <Typography variant="h5" fontWeight={600}>
            256-bit Encryption
          </Typography>
          <Typography color="text.secondary">Bank-level data protection</Typography>
        </Box>
      </Stack>

      <Stack spacing={2}>
        {features.map((feature, index) => (
          <Stack key={index} direction="row" spacing={2} alignItems="center">
            <CheckIcon color="secondary" />
            <Typography color="text.secondary">{feature}</Typography>
          </Stack>
        ))}
      </Stack>
    </Paper>
  );
}
