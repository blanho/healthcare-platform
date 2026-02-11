import { Box, Container, Typography, Grid, Stack, useTheme, alpha } from '@mui/material';
import {
  Visibility as VisitsIcon,
  LocalHospital as HospitalIcon,
  MedicalServices as FacilityIcon,
  Person as DoctorIcon,
} from '@mui/icons-material';
import { useCounter } from '../../../hooks';

interface Stat {
  value: number;
  suffix: string;
  label: string;
  icon: React.ElementType;
  color: string;
}

export function StatsSection() {
  const theme = useTheme();

  const stats: Stat[] = [
    {
      value: 4,
      suffix: 'M+',
      label: 'Monthly Visits',
      icon: VisitsIcon,
      color: '#06B6D4',
    },
    {
      value: 100,
      suffix: '+',
      label: 'Partner Hospitals',
      icon: HospitalIcon,
      color: '#10B981',
    },
    {
      value: 300,
      suffix: '+',
      label: 'Medical Facilities',
      icon: FacilityIcon,
      color: '#8B5CF6',
    },
    {
      value: 2500,
      suffix: '+',
      label: 'Verified Doctors',
      icon: DoctorIcon,
      color: '#F59E0B',
    },
  ];

  return (
    <Box
      component="section"
      py={8}
      sx={{
        bgcolor:
          theme.palette.mode === 'dark'
            ? alpha(theme.palette.primary.main, 0.05)
            : alpha(theme.palette.primary.main, 0.02),
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4}>
          {stats.map((stat, index) => (
            <Grid key={index} size={{ xs: 6, md: 3 }}>
              <StatCard stat={stat} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface StatCardProps {
  stat: Stat;
}

function StatCard({ stat }: StatCardProps) {
  const theme = useTheme();
  const { count, ref } = useCounter(stat.value, { duration: 2000 });
  const Icon = stat.icon;

  return (
    <Stack
      ref={ref}
      alignItems="center"
      textAlign="center"
      spacing={2}
      sx={{
        py: 3,
        px: 2,
        borderRadius: 3,
        transition: 'all 0.3s ease',
        '&:hover': {
          bgcolor: 'background.paper',
          boxShadow: theme.shadows[4],
        },
      }}
    >
      <Box
        sx={{
          width: 64,
          height: 64,
          borderRadius: 3,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          bgcolor: alpha(stat.color, 0.1),
          color: stat.color,
        }}
      >
        <Icon sx={{ fontSize: 32 }} />
      </Box>
      <Box>
        <Typography
          variant="h3"
          fontWeight={800}
          sx={{
            color: stat.color,
            lineHeight: 1,
          }}
        >
          {count}
          {stat.suffix}
        </Typography>
        <Typography variant="body1" color="text.secondary" fontWeight={500} sx={{ mt: 1 }}>
          {stat.label}
        </Typography>
      </Box>
    </Stack>
  );
}
