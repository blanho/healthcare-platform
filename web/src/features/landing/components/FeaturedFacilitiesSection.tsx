import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Avatar,
  Button,
  Chip,
  Stack,
  Rating,
  useTheme,
  alpha,
} from '@mui/material';
import {
  LocationOn as LocationIcon,
  Verified as VerifiedIcon,
  East as ArrowRightIcon,
} from '@mui/icons-material';

interface Facility {
  name: string;
  location: string;
  rating: number;
  reviewCount: number;
  specialties: string[];
  image: string;
  verified: boolean;
}

export function FeaturedFacilitiesSection() {
  const facilities: Facility[] = [
    {
      name: 'University Medical Center',
      location: 'District 5, Ho Chi Minh City',
      rating: 4.7,
      reviewCount: 2580,
      specialties: ['Cardiology', 'Oncology', 'Neurology'],
      image: '/api/placeholder/400/200',
      verified: true,
    },
    {
      name: 'Dermatology Hospital',
      location: 'District 3, Ho Chi Minh City',
      rating: 4.5,
      reviewCount: 1890,
      specialties: ['Dermatology', 'Cosmetic', 'Allergy'],
      image: '/api/placeholder/400/200',
      verified: true,
    },
    {
      name: "Children's Hospital 1",
      location: 'District 10, Ho Chi Minh City',
      rating: 4.6,
      reviewCount: 3210,
      specialties: ['Pediatrics', 'Neonatology', 'Surgery'],
      image: '/api/placeholder/400/200',
      verified: true,
    },
    {
      name: 'Eye Hospital',
      location: 'District 3, Ho Chi Minh City',
      rating: 4.8,
      reviewCount: 1540,
      specialties: ['Ophthalmology', 'Laser Surgery', 'Glaucoma'],
      image: '/api/placeholder/400/200',
      verified: true,
    },
  ];

  return (
    <Box component="section" py={10} bgcolor="background.default">
      <Container maxWidth="lg">
        <Stack
          direction={{ xs: 'column', md: 'row' }}
          justifyContent="space-between"
          alignItems={{ xs: 'flex-start', md: 'center' }}
          mb={5}
          spacing={2}
        >
          <Box>
            <Typography
              variant="overline"
              sx={{ color: 'primary.main', fontWeight: 600, letterSpacing: 1.5 }}
            >
              Featured This Month
            </Typography>
            <Typography variant="h4" fontWeight={700}>
              Top Healthcare Facilities
            </Typography>
          </Box>
          <Button variant="text" endIcon={<ArrowRightIcon />} sx={{ fontWeight: 600 }}>
            View All Facilities
          </Button>
        </Stack>

        <Grid container spacing={3}>
          {facilities.map((facility, index) => (
            <Grid key={index} size={{ xs: 12, sm: 6, lg: 3 }}>
              <FacilityCard facility={facility} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface FacilityCardProps {
  facility: Facility;
}

function FacilityCard({ facility }: FacilityCardProps) {
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
        '&:hover': {
          borderColor: theme.palette.primary.main,
          boxShadow: `0 12px 40px ${alpha(theme.palette.primary.main, 0.15)}`,
          transform: 'translateY(-4px)',
        },
      }}
    >
      {}
      <CardMedia
        sx={{
          height: 140,
          bgcolor: alpha(theme.palette.primary.main, 0.1),
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Avatar
          sx={{
            width: 80,
            height: 80,
            bgcolor: 'white',
            color: 'primary.main',
            fontSize: '1.5rem',
            fontWeight: 700,
            boxShadow: 1,
          }}
        >
          {facility.name
            .split(' ')
            .map((w) => w[0])
            .slice(0, 2)
            .join('')}
        </Avatar>
      </CardMedia>

      <CardContent sx={{ p: 2.5 }}>
        <Stack spacing={1.5}>
          <Stack direction="row" alignItems="center" spacing={1}>
            <Typography variant="subtitle1" fontWeight={600} noWrap flex={1}>
              {facility.name}
            </Typography>
            {facility.verified && <VerifiedIcon sx={{ color: 'primary.main', fontSize: 18 }} />}
          </Stack>

          <Stack direction="row" alignItems="center" spacing={0.5}>
            <LocationIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
            <Typography variant="body2" color="text.secondary" noWrap>
              {facility.location}
            </Typography>
          </Stack>

          <Stack direction="row" alignItems="center" spacing={1}>
            <Rating value={facility.rating} precision={0.1} size="small" readOnly />
            <Typography variant="body2" color="text.secondary">
              ({facility.rating})
            </Typography>
          </Stack>

          <Stack direction="row" spacing={0.5} flexWrap="wrap" gap={0.5}>
            {facility.specialties.slice(0, 2).map((specialty, i) => (
              <Chip
                key={i}
                label={specialty}
                size="small"
                sx={{
                  fontSize: '0.7rem',
                  height: 22,
                  bgcolor: alpha(theme.palette.primary.main, 0.08),
                  color: 'primary.main',
                }}
              />
            ))}
            {facility.specialties.length > 2 && (
              <Chip
                label={`+${facility.specialties.length - 2}`}
                size="small"
                sx={{ fontSize: '0.7rem', height: 22 }}
              />
            )}
          </Stack>

          <Button fullWidth variant="contained" size="small" sx={{ mt: 1, borderRadius: 2 }}>
            Book Now
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
