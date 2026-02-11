import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  Avatar,
  Chip,
  Stack,
  useTheme,
  alpha,
} from '@mui/material';
import {
  People as UsersIcon,
  Security as ShieldIcon,
  CalendarMonth as CalendarIcon,
  Description as FileTextIcon,
  CreditCard as CreditCardIcon,
  NotificationsActive as BellIcon,
  BarChart as BarChartIcon,
  ChevronRight as ChevronRightIcon,
} from '@mui/icons-material';

interface Feature {
  icon: typeof CalendarIcon;
  title: string;
  description: string;
  gridSize: { xs: number; sm?: number; md: number };
  gradient: string;
}

export function BentoFeaturesSection() {
  const theme = useTheme();

  const features: Feature[] = [
    {
      icon: CalendarIcon,
      title: 'Smart Scheduling',
      description:
        'AI-powered appointment scheduling that adapts to provider availability and patient preferences.',
      gridSize: { xs: 12, md: 6 },
      gradient: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.primary.dark})`,
    },
    {
      icon: FileTextIcon,
      title: 'Electronic Health Records',
      description: 'Unified patient records accessible anywhere.',
      gridSize: { xs: 12, sm: 6, md: 3 },
      gradient: `linear-gradient(135deg, ${theme.palette.secondary.main}, ${theme.palette.secondary.dark})`,
    },
    {
      icon: ShieldIcon,
      title: 'HIPAA Compliant',
      description: 'Enterprise-grade security with full compliance.',
      gridSize: { xs: 12, sm: 6, md: 3 },
      gradient: 'linear-gradient(135deg, #10B981, #059669)',
    },
    {
      icon: CreditCardIcon,
      title: 'Integrated Billing',
      description:
        'Streamlined insurance claims and payment processing with real-time verification.',
      gridSize: { xs: 12, md: 6 },
      gradient: 'linear-gradient(135deg, #F59E0B, #D97706)',
    },
    {
      icon: BellIcon,
      title: 'Smart Notifications',
      description: 'Multi-channel reminders via SMS, email, and push.',
      gridSize: { xs: 12, sm: 6, md: 3 },
      gradient: 'linear-gradient(135deg, #EC4899, #DB2777)',
    },
    {
      icon: BarChartIcon,
      title: 'Analytics Dashboard',
      description: 'Real-time insights into practice performance.',
      gridSize: { xs: 12, sm: 6, md: 3 },
      gradient: 'linear-gradient(135deg, #8B5CF6, #7C3AED)',
    },
    {
      icon: UsersIcon,
      title: 'Multi-Provider Support',
      description:
        'Manage multiple locations and providers from a single dashboard with role-based access.',
      gridSize: { xs: 12, md: 6 },
      gradient: `linear-gradient(135deg, ${theme.palette.info.main}, ${theme.palette.primary.main})`,
    },
  ];

  return (
    <Box component="section" py={12} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Stack spacing={2} alignItems="center" textAlign="center" mb={8}>
          <Chip
            label="Platform Features"
            color="primary"
            variant="outlined"
            sx={{ fontWeight: 600 }}
          />
          <Typography
            variant="h2"
            sx={{ fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700 }}
          >
            Everything You Need
          </Typography>
          <Typography variant="body1" color="text.secondary" maxWidth={600}>
            A complete healthcare management ecosystem designed for modern practices.
          </Typography>
        </Stack>

        <Grid container spacing={3}>
          {features.map((feature, index) => (
            <Grid key={index} size={feature.gridSize}>
              <FeatureCard feature={feature} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface FeatureCardProps {
  feature: Feature;
}

function FeatureCard({ feature }: FeatureCardProps) {
  const theme = useTheme();
  const Icon = feature.icon;

  return (
    <Card
      elevation={0}
      sx={{
        height: '100%',
        p: 3,
        borderRadius: 4,
        border: `1px solid ${theme.palette.divider}`,
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        '&:hover': {
          borderColor: theme.palette.primary.main,
          boxShadow: `0 12px 40px ${alpha(theme.palette.primary.main, 0.15)}`,
          transform: 'translateY(-4px)',
          '& .feature-icon': {
            transform: 'scale(1.1)',
          },
          '& .learn-more': {
            opacity: 1,
          },
        },
      }}
    >
      <Avatar
        className="feature-icon"
        sx={{
          width: 56,
          height: 56,
          background: feature.gradient,
          mb: 3,
          transition: 'transform 0.3s ease',
        }}
      >
        <Icon sx={{ fontSize: 28 }} />
      </Avatar>
      <Typography variant="h6" fontWeight={600} mb={1}>
        {feature.title}
      </Typography>
      <Typography variant="body2" color="text.secondary" lineHeight={1.7}>
        {feature.description}
      </Typography>
      <Stack
        className="learn-more"
        direction="row"
        alignItems="center"
        spacing={0.5}
        mt={2}
        sx={{ opacity: 0, transition: 'opacity 0.3s ease', color: 'primary.main' }}
      >
        <Typography variant="button">Learn more</Typography>
        <ChevronRightIcon fontSize="small" />
      </Stack>
    </Card>
  );
}
