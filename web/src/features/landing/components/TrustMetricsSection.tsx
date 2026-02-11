import {
  Box,
  Container,
  Typography,
  Grid,
  Avatar,
  Paper,
  Stack,
  useTheme,
  alpha,
} from '@mui/material';
import {
  People as UsersIcon,
  Bolt as ZapIcon,
  Public as GlobeIcon,
  MedicalServices as StethoscopeIcon,
} from '@mui/icons-material';
import { useCounter } from '@/hooks';

interface Metric {
  value: number;
  suffix: string;
  label: string;
  icon: typeof StethoscopeIcon;
}

export function TrustMetricsSection() {
  const theme = useTheme();

  const metrics: Metric[] = [
    { value: 10000, suffix: '+', label: 'Healthcare Providers', icon: StethoscopeIcon },
    { value: 2.5, suffix: 'M+', label: 'Patients Served', icon: UsersIcon },
    { value: 99.9, suffix: '%', label: 'Uptime SLA', icon: ZapIcon },
    { value: 50, suffix: '+', label: 'Countries', icon: GlobeIcon },
  ];

  return (
    <Box
      component="section"
      py={12}
      sx={{
        background: `linear-gradient(135deg, ${theme.palette.primary.dark} 0%, ${theme.palette.primary.main} 50%, ${theme.palette.secondary.dark} 100%)`,
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {}
      <Box
        sx={{
          position: 'absolute',
          inset: 0,
          opacity: 0.1,
          backgroundImage: 'radial-gradient(circle at 2px 2px, white 1px, transparent 0)',
          backgroundSize: '40px 40px',
        }}
      />

      <Container maxWidth="lg" sx={{ position: 'relative' }}>
        <Stack spacing={2} alignItems="center" textAlign="center" mb={8}>
          <Typography
            variant="h2"
            sx={{ color: 'white', fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700 }}
          >
            Trusted Worldwide
          </Typography>
          <Typography variant="body1" sx={{ color: alpha('#fff', 0.8), maxWidth: 600 }}>
            Join thousands of healthcare organizations already transforming patient care.
          </Typography>
        </Stack>

        <Grid container spacing={4}>
          {metrics.map((metric, index) => (
            <Grid key={index} size={{ xs: 6, lg: 3 }}>
              <MetricCard metric={metric} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface MetricCardProps {
  metric: Metric;
}

function MetricCard({ metric }: MetricCardProps) {
  const { count, ref } = useCounter(metric.value, { duration: 2500 });
  const Icon = metric.icon;

  return (
    <Paper
      ref={ref}
      elevation={0}
      sx={{
        p: 4,
        borderRadius: 4,
        bgcolor: alpha('#fff', 0.1),
        backdropFilter: 'blur(10px)',
        border: `1px solid ${alpha('#fff', 0.2)}`,
        textAlign: 'center',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        '&:hover': {
          bgcolor: alpha('#fff', 0.2),
          transform: 'translateY(-4px)',
        },
      }}
    >
      <Avatar
        sx={{
          width: 64,
          height: 64,
          bgcolor: alpha('#fff', 0.2),
          mx: 'auto',
          mb: 2,
        }}
      >
        <Icon sx={{ fontSize: 32, color: 'white' }} />
      </Avatar>
      <Typography
        variant="h3"
        sx={{
          color: 'white',
          fontWeight: 700,
          fontSize: { xs: '2rem', md: '2.5rem' },
        }}
      >
        {metric.value < 100 && metric.value % 1 !== 0 ? count.toFixed(1) : count.toLocaleString()}
        {metric.suffix}
      </Typography>
      <Typography sx={{ color: alpha('#fff', 0.8), fontWeight: 500 }}>{metric.label}</Typography>
    </Paper>
  );
}
