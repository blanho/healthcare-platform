import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Stack,
  useTheme,
  alpha,
} from '@mui/material';
import {
  Favorite as HeartIcon,
  Psychology as BrainIcon,
  Visibility as EyeIcon,
  LocalHospital as HospitalIcon,
  ChildCare as ChildIcon,
  Face as FaceIcon,
  Healing as HealingIcon,
  Accessibility as OrthopedicIcon,
  MonitorHeart as MonitorIcon,
  Science as ScienceIcon,
  Vaccines as VaccineIcon,
  Elderly as ElderlyIcon,
} from '@mui/icons-material';
import type { SvgIconComponent } from '@mui/icons-material';

interface Specialty {
  name: string;
  icon: SvgIconComponent;
  doctors: number;
  color: string;
}

export function SpecialtiesGridSection() {
  const specialties: Specialty[] = [
    { name: 'Cardiology', icon: HeartIcon, doctors: 120, color: '#EF4444' },
    { name: 'Neurology', icon: BrainIcon, doctors: 85, color: '#8B5CF6' },
    { name: 'Ophthalmology', icon: EyeIcon, doctors: 95, color: '#06B6D4' },
    { name: 'General Medicine', icon: HospitalIcon, doctors: 250, color: '#10B981' },
    { name: 'Pediatrics', icon: ChildIcon, doctors: 110, color: '#F59E0B' },
    { name: 'Dermatology', icon: FaceIcon, doctors: 75, color: '#EC4899' },
    { name: 'Orthopedics', icon: OrthopedicIcon, doctors: 90, color: '#6366F1' },
    { name: 'Gastroenterology', icon: HealingIcon, doctors: 65, color: '#14B8A6' },
    { name: 'Pulmonology', icon: MonitorIcon, doctors: 55, color: '#3B82F6' },
    { name: 'Pathology', icon: ScienceIcon, doctors: 45, color: '#A855F7' },
    { name: 'Immunology', icon: VaccineIcon, doctors: 40, color: '#22C55E' },
    { name: 'Geriatrics', icon: ElderlyIcon, doctors: 60, color: '#F97316' },
  ];

  return (
    <Box component="section" py={10} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Stack alignItems="center" textAlign="center" mb={6} spacing={2}>
          <Typography variant="h4" fontWeight={700}>
            Browse by Specialty
          </Typography>
          <Typography variant="body1" color="text.secondary" maxWidth={500}>
            Find the right specialist for your healthcare needs from our comprehensive network of
            medical experts
          </Typography>
        </Stack>

        <Grid container spacing={2}>
          {specialties.map((specialty, index) => (
            <Grid key={index} size={{ xs: 6, sm: 4, md: 3, lg: 2 }}>
              <SpecialtyCard specialty={specialty} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface SpecialtyCardProps {
  specialty: Specialty;
}

function SpecialtyCard({ specialty }: SpecialtyCardProps) {
  const theme = useTheme();
  const Icon = specialty.icon;

  return (
    <Card
      elevation={0}
      sx={{
        borderRadius: 3,
        border: `1px solid ${theme.palette.divider}`,
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        '&:hover': {
          borderColor: specialty.color,
          boxShadow: `0 8px 24px ${alpha(specialty.color, 0.2)}`,
          transform: 'translateY(-4px)',
          '& .specialty-icon': {
            bgcolor: specialty.color,
            color: 'white',
          },
        },
      }}
    >
      <CardContent sx={{ p: 2.5, textAlign: 'center' }}>
        <Stack spacing={1.5} alignItems="center">
          <Box
            className="specialty-icon"
            sx={{
              width: 56,
              height: 56,
              borderRadius: 2.5,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              bgcolor: alpha(specialty.color, 0.1),
              color: specialty.color,
              transition: 'all 0.3s ease',
            }}
          >
            <Icon sx={{ fontSize: 28 }} />
          </Box>
          <Box>
            <Typography
              variant="subtitle2"
              fontWeight={600}
              sx={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {specialty.name}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {specialty.doctors}+ Doctors
            </Typography>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}
