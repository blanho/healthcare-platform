import { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  TextField,
  InputAdornment,
  Avatar,
  Chip,
  Stack,
  useTheme,
  alpha,
  Rating,
} from '@mui/material';
import {
  Search as SearchIcon,
  CalendarMonth as CalendarIcon,
  Videocam as VideoIcon,
  Science as LabIcon,
  LocalHospital as HospitalIcon,
  East as ArrowRightIcon,
  Verified as VerifiedIcon,
  PlayArrow as PlayIcon,
  Star as StarIcon,
} from '@mui/icons-material';

interface QuickService {
  icon: typeof HospitalIcon;
  title: string;
  description: string;
  color: string;
  gradient: string;
}

export function HeroSection() {
  const theme = useTheme();
  const [searchQuery, setSearchQuery] = useState('');

  const quickServices: QuickService[] = [
    {
      icon: HospitalIcon,
      title: 'Book at Facility',
      description: 'Schedule at hospitals & clinics',
      color: '#0891B2',
      gradient: 'linear-gradient(135deg, #0891B2 0%, #06B6D4 100%)',
    },
    {
      icon: CalendarIcon,
      title: 'Book by Specialty',
      description: 'Find the right specialist',
      color: '#059669',
      gradient: 'linear-gradient(135deg, #059669 0%, #10B981 100%)',
    },
    {
      icon: VideoIcon,
      title: 'Video Consultation',
      description: 'Talk to doctors online',
      color: '#7C3AED',
      gradient: 'linear-gradient(135deg, #7C3AED 0%, #8B5CF6 100%)',
    },
    {
      icon: LabIcon,
      title: 'Book Lab Tests',
      description: 'Home collection available',
      color: '#EA580C',
      gradient: 'linear-gradient(135deg, #EA580C 0%, #F97316 100%)',
    },
  ];

  const stats = [
    { value: '4M+', label: 'Monthly Visits' },
    { value: '2,500+', label: 'Verified Doctors' },
    { value: '100+', label: 'Partner Hospitals' },
    { value: '4.9', label: 'App Rating', isRating: true },
  ];

  return (
    <Box
      component="section"
      sx={{
        position: 'relative',
        overflow: 'hidden',
        bgcolor: '#F0FDFA',
        pt: { xs: 14, md: 18 },
        pb: { xs: 6, md: 10 },
      }}
    >
      {}
      <Box
        sx={{
          position: 'absolute',
          top: -200,
          right: -200,
          width: 600,
          height: 600,
          borderRadius: '50%',
          background: `radial-gradient(circle, ${alpha(theme.palette.primary.main, 0.08)} 0%, transparent 70%)`,
        }}
      />
      <Box
        sx={{
          position: 'absolute',
          bottom: -100,
          left: -100,
          width: 400,
          height: 400,
          borderRadius: '50%',
          background: `radial-gradient(circle, ${alpha(theme.palette.secondary.main, 0.06)} 0%, transparent 70%)`,
        }}
      />

      <Container maxWidth="lg" sx={{ position: 'relative' }}>
        <Grid container spacing={6} alignItems="center">
          {}
          <Grid size={{ xs: 12, lg: 6 }}>
            <Stack spacing={4}>
              {}
              <Chip
                icon={<VerifiedIcon sx={{ fontSize: 18, color: 'primary.main' }} />}
                label="Trusted by 4M+ patients monthly"
                sx={{
                  alignSelf: 'flex-start',
                  bgcolor: alpha(theme.palette.primary.main, 0.1),
                  color: 'primary.main',
                  fontWeight: 600,
                  px: 1,
                  py: 2.5,
                  borderRadius: 3,
                  '& .MuiChip-icon': { ml: 1 },
                }}
              />

              {}
              <Typography
                variant="h1"
                sx={{
                  fontSize: { xs: '2.5rem', md: '3.25rem', lg: '3.75rem' },
                  fontWeight: 800,
                  lineHeight: 1.15,
                  color: '#134E4A',
                }}
              >
                Your Health,{' '}
                <Box
                  component="span"
                  sx={{
                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                  }}
                >
                  Our Priority
                </Box>
              </Typography>

              {}
              <Typography
                variant="h6"
                sx={{
                  color: '#64748B',
                  lineHeight: 1.7,
                  fontWeight: 400,
                  maxWidth: 480,
                }}
              >
                Book appointments with top doctors, get video consultations, and manage your health
                records — all in one platform.
              </Typography>

              {}
              <Card
                elevation={0}
                sx={{
                  borderRadius: 4,
                  boxShadow: `0 8px 32px ${alpha(theme.palette.primary.main, 0.12)}`,
                  border: `1px solid ${alpha(theme.palette.primary.main, 0.1)}`,
                }}
              >
                <CardContent sx={{ p: 2.5, '&:last-child': { pb: 2.5 } }}>
                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <TextField
                      fullWidth
                      placeholder="Search doctors, hospitals, specialties..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <SearchIcon sx={{ color: 'text.secondary' }} />
                          </InputAdornment>
                        ),
                        sx: {
                          borderRadius: 2.5,
                          bgcolor: '#F8FAFC',
                          '& .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'transparent',
                          },
                          '&:hover .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'primary.main',
                          },
                          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                            borderColor: 'primary.main',
                          },
                        },
                      }}
                    />
                    <Button
                      variant="contained"
                      size="large"
                      endIcon={<ArrowRightIcon />}
                      sx={{
                        borderRadius: 2.5,
                        px: 4,
                        py: 1.75,
                        fontWeight: 600,
                        whiteSpace: 'nowrap',
                        boxShadow: `0 4px 14px ${alpha(theme.palette.primary.main, 0.4)}`,
                      }}
                    >
                      Find Now
                    </Button>
                  </Stack>
                </CardContent>
              </Card>

              {}
              <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                <Typography variant="body2" color="text.secondary" sx={{ mr: 1 }}>
                  Popular:
                </Typography>
                {['Cardiology', 'Dermatology', 'Pediatrics', 'Neurology'].map((tag) => (
                  <Chip
                    key={tag}
                    label={tag}
                    size="small"
                    variant="outlined"
                    sx={{
                      cursor: 'pointer',
                      borderColor: alpha(theme.palette.primary.main, 0.3),
                      '&:hover': {
                        bgcolor: alpha(theme.palette.primary.main, 0.08),
                        borderColor: 'primary.main',
                      },
                    }}
                  />
                ))}
              </Stack>

              {}
              <Stack direction="row" spacing={{ xs: 2, md: 4 }} sx={{ pt: 2 }}>
                {stats.map((stat, index) => (
                  <Box key={index}>
                    <Stack direction="row" alignItems="center" spacing={0.5}>
                      <Typography variant="h5" fontWeight={800} sx={{ color: '#134E4A' }}>
                        {stat.value}
                      </Typography>
                      {stat.isRating && <StarIcon sx={{ fontSize: 20, color: '#FBBF24' }} />}
                    </Stack>
                    <Typography variant="caption" color="text.secondary">
                      {stat.label}
                    </Typography>
                  </Box>
                ))}
              </Stack>
            </Stack>
          </Grid>

          {}
          <Grid size={{ xs: 12, lg: 6 }}>
            <Box
              sx={{
                position: 'relative',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
              }}
            >
              {}
              <Box
                sx={{
                  position: 'relative',
                  width: { xs: 320, md: 420 },
                  height: { xs: 380, md: 500 },
                  borderRadius: 6,
                  overflow: 'hidden',
                  boxShadow: `0 32px 64px ${alpha('#0891B2', 0.2)}`,
                }}
              >
                <Box
                  component="img"
                  src="https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=800&q=80"
                  alt="Professional doctor with stethoscope"
                  sx={{
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                    objectPosition: 'top',
                  }}
                />
                {}
                <Box
                  sx={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0,
                    right: 0,
                    height: '40%',
                    background: 'linear-gradient(to top, rgba(0,0,0,0.5) 0%, transparent 100%)',
                  }}
                />
              </Box>

              {}
              <Card
                elevation={0}
                sx={{
                  position: 'absolute',
                  bottom: { xs: -20, md: 40 },
                  left: { xs: 20, md: -40 },
                  borderRadius: 3,
                  boxShadow: `0 16px 48px ${alpha('#000', 0.12)}`,
                  bgcolor: 'white',
                  minWidth: 220,
                }}
              >
                <CardContent sx={{ p: 2.5, '&:last-child': { pb: 2.5 } }}>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <Avatar
                      src="https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&q=80"
                      sx={{ width: 48, height: 48 }}
                    />
                    <Box>
                      <Typography variant="subtitle2" fontWeight={600}>
                        Dr. Sarah Johnson
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Cardiologist • 15 yrs exp
                      </Typography>
                    </Box>
                  </Stack>
                  <Stack direction="row" alignItems="center" spacing={0.5} sx={{ mt: 1.5 }}>
                    <Rating value={4.9} precision={0.1} size="small" readOnly />
                    <Typography variant="caption" fontWeight={600}>
                      4.9
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      (2,341 reviews)
                    </Typography>
                  </Stack>
                </CardContent>
              </Card>

              {}
              <Card
                elevation={0}
                sx={{
                  position: 'absolute',
                  top: { xs: 20, md: 60 },
                  right: { xs: 20, md: -20 },
                  borderRadius: 3,
                  boxShadow: `0 12px 32px ${alpha('#000', 0.1)}`,
                  bgcolor: 'white',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: `0 16px 48px ${alpha('#000', 0.15)}`,
                  },
                }}
              >
                <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
                  <Stack direction="row" spacing={1.5} alignItems="center">
                    <Box
                      sx={{
                        width: 44,
                        height: 44,
                        borderRadius: 2,
                        background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <PlayIcon sx={{ color: 'white' }} />
                    </Box>
                    <Box>
                      <Typography variant="subtitle2" fontWeight={600}>
                        Watch Demo
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        See how it works
                      </Typography>
                    </Box>
                  </Stack>
                </CardContent>
              </Card>

              {}
              <Box
                sx={{
                  position: 'absolute',
                  top: -20,
                  left: 40,
                  width: 80,
                  height: 80,
                  borderRadius: 4,
                  bgcolor: alpha(theme.palette.secondary.main, 0.15),
                  zIndex: -1,
                }}
              />
              <Box
                sx={{
                  position: 'absolute',
                  bottom: 60,
                  right: -20,
                  width: 120,
                  height: 120,
                  borderRadius: '50%',
                  bgcolor: alpha(theme.palette.primary.main, 0.1),
                  zIndex: -1,
                }}
              />
            </Box>
          </Grid>
        </Grid>

        {}
        <Grid container spacing={3} sx={{ mt: { xs: 6, md: 8 } }}>
          {quickServices.map((service, index) => (
            <Grid key={index} size={{ xs: 6, md: 3 }}>
              <ServiceCard service={service} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface ServiceCardProps {
  service: QuickService;
}

function ServiceCard({ service }: ServiceCardProps) {
  const Icon = service.icon;

  return (
    <Card
      elevation={0}
      sx={{
        height: '100%',
        borderRadius: 4,
        bgcolor: 'white',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        border: `1px solid ${alpha(service.color, 0.15)}`,
        boxShadow: `0 4px 20px ${alpha(service.color, 0.08)}`,
        '&:hover': {
          transform: 'translateY(-6px)',
          boxShadow: `0 16px 48px ${alpha(service.color, 0.2)}`,
          borderColor: service.color,
          '& .service-icon': {
            background: service.gradient,
            '& svg': { color: 'white' },
          },
          '& .service-arrow': {
            opacity: 1,
            transform: 'translateX(0)',
          },
        },
      }}
    >
      <CardContent sx={{ p: 3, textAlign: 'center' }}>
        <Stack spacing={2} alignItems="center">
          <Avatar
            className="service-icon"
            sx={{
              width: 64,
              height: 64,
              bgcolor: alpha(service.color, 0.1),
              transition: 'all 0.3s ease',
            }}
          >
            <Icon sx={{ fontSize: 32, color: service.color, transition: 'color 0.3s' }} />
          </Avatar>
          <Box>
            <Stack direction="row" alignItems="center" justifyContent="center" spacing={0.5}>
              <Typography variant="subtitle1" fontWeight={600}>
                {service.title}
              </Typography>
              <ArrowRightIcon
                className="service-arrow"
                sx={{
                  fontSize: 18,
                  opacity: 0,
                  transform: 'translateX(-8px)',
                  transition: 'all 0.3s ease',
                  color: service.color,
                }}
              />
            </Stack>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              {service.description}
            </Typography>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}
