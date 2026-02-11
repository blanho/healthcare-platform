import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Avatar,
  Button,
  Chip,
  Stack,
  Rating,
  useTheme,
  alpha,
} from '@mui/material';
import {
  Videocam as VideoIcon,
  Person as PersonIcon,
  AttachMoney as MoneyIcon,
  East as ArrowRightIcon,
} from '@mui/icons-material';

interface Doctor {
  name: string;
  title: string;
  specialty: string;
  price: number;
  rating: number;
  consultations: number;
  avatar: string;
  available: boolean;
}

export function DoctorConsultationSection() {
  const doctors: Doctor[] = [
    {
      name: 'Dr. Sarah Johnson',
      title: 'MD, FACC',
      specialty: 'Cardiology',
      price: 150,
      rating: 4.9,
      consultations: 1240,
      avatar: 'SJ',
      available: true,
    },
    {
      name: 'Dr. Michael Chen',
      title: 'MD, PhD',
      specialty: 'Neurology',
      price: 180,
      rating: 4.8,
      consultations: 890,
      avatar: 'MC',
      available: true,
    },
    {
      name: 'Dr. Emily Rodriguez',
      title: 'MD, FAAD',
      specialty: 'Dermatology',
      price: 120,
      rating: 4.7,
      consultations: 2150,
      avatar: 'ER',
      available: false,
    },
    {
      name: 'Dr. James Wilson',
      title: 'MD, FAAP',
      specialty: 'Pediatrics',
      price: 100,
      rating: 4.9,
      consultations: 3200,
      avatar: 'JW',
      available: true,
    },
  ];

  return (
    <Box component="section" py={10} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Stack
          direction={{ xs: 'column', md: 'row' }}
          justifyContent="space-between"
          alignItems={{ xs: 'flex-start', md: 'center' }}
          mb={5}
          spacing={2}
        >
          <Box>
            <Stack direction="row" alignItems="center" spacing={1} mb={1}>
              <VideoIcon sx={{ color: 'primary.main' }} />
              <Typography
                variant="overline"
                sx={{ color: 'primary.main', fontWeight: 600, letterSpacing: 1.5 }}
              >
                Video Consultation
              </Typography>
            </Stack>
            <Typography variant="h4" fontWeight={700}>
              Consult Top Doctors Online
            </Typography>
            <Typography variant="body1" color="text.secondary" mt={1}>
              Connect with specialists from the comfort of your home
            </Typography>
          </Box>
          <Button
            variant="outlined"
            endIcon={<ArrowRightIcon />}
            sx={{ fontWeight: 600, borderRadius: 2 }}
          >
            View All Doctors
          </Button>
        </Stack>

        <Grid container spacing={3}>
          {doctors.map((doctor, index) => (
            <Grid key={index} size={{ xs: 12, sm: 6, lg: 3 }}>
              <DoctorCard doctor={doctor} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface DoctorCardProps {
  doctor: Doctor;
}

function DoctorCard({ doctor }: DoctorCardProps) {
  const theme = useTheme();

  return (
    <Card
      elevation={0}
      sx={{
        height: '100%',
        borderRadius: 3,
        overflow: 'hidden',
        border: `1px solid ${theme.palette.divider}`,
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        position: 'relative',
        '&:hover': {
          borderColor: theme.palette.primary.main,
          boxShadow: `0 12px 40px ${alpha(theme.palette.primary.main, 0.15)}`,
          transform: 'translateY(-4px)',
        },
      }}
    >
      {}
      {doctor.available && (
        <Chip
          label="Available Now"
          size="small"
          color="success"
          sx={{
            position: 'absolute',
            top: 12,
            right: 12,
            fontSize: '0.7rem',
            height: 24,
          }}
        />
      )}

      <CardContent sx={{ p: 3, textAlign: 'center' }}>
        <Stack spacing={2} alignItems="center">
          {}
          <Box sx={{ position: 'relative' }}>
            <Avatar
              sx={{
                width: 80,
                height: 80,
                bgcolor: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
                background: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
                fontSize: '1.5rem',
                fontWeight: 700,
              }}
            >
              {doctor.avatar}
            </Avatar>
            {doctor.available && (
              <Box
                sx={{
                  position: 'absolute',
                  bottom: 4,
                  right: 4,
                  width: 16,
                  height: 16,
                  borderRadius: '50%',
                  bgcolor: 'success.main',
                  border: '2px solid white',
                }}
              />
            )}
          </Box>

          {}
          <Box>
            <Typography variant="subtitle1" fontWeight={600}>
              {doctor.name}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {doctor.title}
            </Typography>
          </Box>

          {}
          <Stack direction="row" alignItems="center" spacing={0.5}>
            <Rating value={doctor.rating} precision={0.1} size="small" readOnly />
            <Typography variant="body2" fontWeight={600}>
              {doctor.rating}
            </Typography>
          </Stack>

          {}
          <Stack spacing={1} width="100%">
            <Stack direction="row" alignItems="center" justifyContent="center" spacing={1}>
              <Chip
                size="small"
                label={doctor.specialty}
                sx={{
                  bgcolor: alpha(theme.palette.primary.main, 0.1),
                  color: 'primary.main',
                }}
              />
            </Stack>

            <Stack direction="row" justifyContent="space-between" sx={{ px: 2 }}>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <MoneyIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                <Typography variant="body2" fontWeight={600}>
                  ${doctor.price}
                </Typography>
              </Stack>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <PersonIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary">
                  {doctor.consultations.toLocaleString()}
                </Typography>
              </Stack>
            </Stack>
          </Stack>

          {}
          <Button
            fullWidth
            variant={doctor.available ? 'contained' : 'outlined'}
            disabled={!doctor.available}
            startIcon={<VideoIcon />}
            sx={{ borderRadius: 2 }}
          >
            {doctor.available ? 'Consult Now' : 'Not Available'}
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
