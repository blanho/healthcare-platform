import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  Avatar,
  Chip,
  Stack,
  Rating,
  useTheme,
  alpha,
} from '@mui/material';

interface Testimonial {
  quote: string;
  author: string;
  role: string;
  organization: string;
  avatar: string;
}

export function TestimonialsSection() {
  const testimonials: Testimonial[] = [
    {
      quote:
        'This platform reduced our no-show rate by 40% and increased patient satisfaction scores dramatically.',
      author: 'Dr. Sarah Johnson',
      role: 'Medical Director',
      organization: 'Metro Health Clinic',
      avatar: 'SJ',
    },
    {
      quote:
        'The integrated billing system alone saves us 20 hours per week. Game-changing for our practice.',
      author: 'Michael Chen',
      role: 'Practice Manager',
      organization: 'Family Care Associates',
      avatar: 'MC',
    },
    {
      quote:
        "Finally, a healthcare platform that's as intuitive as the consumer apps we use every day.",
      author: 'Dr. Emily Rodriguez',
      role: 'Chief Medical Officer',
      organization: 'Sunrise Medical Group',
      avatar: 'ER',
    },
  ];

  return (
    <Box component="section" py={12} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Stack spacing={2} alignItems="center" textAlign="center" mb={8}>
          <Chip label="Testimonials" color="primary" variant="outlined" sx={{ fontWeight: 600 }} />
          <Typography
            variant="h2"
            sx={{ fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700 }}
          >
            Loved by Healthcare Teams
          </Typography>
        </Stack>

        <Grid container spacing={4}>
          {testimonials.map((testimonial, index) => (
            <Grid key={index} size={{ xs: 12, md: 4 }}>
              <TestimonialCard testimonial={testimonial} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface TestimonialCardProps {
  testimonial: Testimonial;
}

function TestimonialCard({ testimonial }: TestimonialCardProps) {
  const theme = useTheme();

  return (
    <Card
      elevation={0}
      sx={{
        height: '100%',
        p: 4,
        borderRadius: 4,
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
      <Rating value={5} readOnly sx={{ mb: 2 }} />
      <Typography variant="body1" color="text.primary" lineHeight={1.8} mb={4}>
        "{testimonial.quote}"
      </Typography>
      <Stack direction="row" spacing={2} alignItems="center">
        <Avatar
          sx={{
            background: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
            fontWeight: 700,
          }}
        >
          {testimonial.avatar}
        </Avatar>
        <Box>
          <Typography fontWeight={600}>{testimonial.author}</Typography>
          <Typography variant="caption" color="text.secondary">
            {testimonial.role}, {testimonial.organization}
          </Typography>
        </Box>
      </Stack>
    </Card>
  );
}
