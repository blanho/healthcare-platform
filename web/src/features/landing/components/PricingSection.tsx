import { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  Avatar,
  Button,
  Chip,
  Stack,
  Paper,
  useTheme,
  alpha,
} from '@mui/material';
import { Check as CheckIcon } from '@mui/icons-material';

interface Plan {
  name: string;
  description: string;
  price: number | null;
  features: string[];
  cta: string;
  popular: boolean;
}

export function PricingSection() {
  const [isAnnual, setIsAnnual] = useState(true);

  const plans: Plan[] = [
    {
      name: 'Starter',
      description: 'For small practices getting started',
      price: isAnnual ? 79 : 99,
      features: [
        'Up to 5 providers',
        '1,000 patient records',
        'Basic scheduling',
        'Email support',
        'HIPAA compliant',
      ],
      cta: 'Start Free Trial',
      popular: false,
    },
    {
      name: 'Professional',
      description: 'For growing healthcare organizations',
      price: isAnnual ? 199 : 249,
      features: [
        'Up to 25 providers',
        'Unlimited patients',
        'Advanced scheduling',
        'Integrated billing',
        'Analytics dashboard',
        'Priority support',
        'API access',
      ],
      cta: 'Start Free Trial',
      popular: true,
    },
    {
      name: 'Enterprise',
      description: 'For large health systems',
      price: null,
      features: [
        'Unlimited providers',
        'Unlimited patients',
        'Custom integrations',
        'Dedicated success manager',
        'SLA guarantee',
        'On-premise option',
        'Custom training',
      ],
      cta: 'Contact Sales',
      popular: false,
    },
  ];

  return (
    <Box component="section" py={12} bgcolor="background.default">
      <Container maxWidth="lg">
        <Stack spacing={2} alignItems="center" textAlign="center" mb={6}>
          <Chip label="Pricing" color="secondary" variant="outlined" sx={{ fontWeight: 600 }} />
          <Typography
            variant="h2"
            sx={{ fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700 }}
          >
            Simple, Transparent Pricing
          </Typography>
          <Typography variant="body1" color="text.secondary" maxWidth={600}>
            Choose the plan that fits your practice. All plans include a 14-day free trial.
          </Typography>

          {}
          <BillingToggle isAnnual={isAnnual} onToggle={setIsAnnual} />
        </Stack>

        <Grid container spacing={4} alignItems="stretch">
          {plans.map((plan, index) => (
            <Grid key={index} size={{ xs: 12, md: 4 }}>
              <PricingCard plan={plan} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface BillingToggleProps {
  isAnnual: boolean;
  onToggle: (value: boolean) => void;
}

function BillingToggle({ isAnnual, onToggle }: BillingToggleProps) {
  const theme = useTheme();

  return (
    <Paper
      elevation={0}
      sx={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 1,
        p: 0.75,
        borderRadius: 10,
        bgcolor: 'background.paper',
        border: `1px solid ${theme.palette.divider}`,
      }}
    >
      <Button
        size="small"
        variant={!isAnnual ? 'contained' : 'text'}
        onClick={() => onToggle(false)}
        sx={{ borderRadius: 8, px: 3 }}
      >
        Monthly
      </Button>
      <Button
        size="small"
        variant={isAnnual ? 'contained' : 'text'}
        onClick={() => onToggle(true)}
        sx={{ borderRadius: 8, px: 3 }}
        endIcon={
          <Chip
            label="Save 20%"
            size="small"
            color="success"
            sx={{ height: 20, fontSize: '0.65rem' }}
          />
        }
      >
        Annual
      </Button>
    </Paper>
  );
}

interface PricingCardProps {
  plan: Plan;
}

function PricingCard({ plan }: PricingCardProps) {
  const theme = useTheme();

  return (
    <Card
      elevation={plan.popular ? 12 : 0}
      sx={{
        height: '100%',
        p: 4,
        borderRadius: 4,
        position: 'relative',
        transition: 'all 0.3s ease',
        ...(plan.popular
          ? {
              background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
              color: 'white',
              transform: 'scale(1.05)',
              zIndex: 1,
            }
          : {
              bgcolor: 'background.paper',
              border: `1px solid ${theme.palette.divider}`,
              '&:hover': {
                borderColor: theme.palette.primary.main,
                boxShadow: `0 12px 40px ${alpha(theme.palette.primary.main, 0.15)}`,
              },
            }),
      }}
    >
      {plan.popular && (
        <Chip
          label="Most Popular"
          size="small"
          sx={{
            position: 'absolute',
            top: -12,
            left: '50%',
            transform: 'translateX(-50%)',
            bgcolor: '#FBBF24',
            color: '#78350F',
            fontWeight: 600,
          }}
        />
      )}

      <Typography variant="h5" fontWeight={700}>
        {plan.name}
      </Typography>
      <Typography
        variant="body2"
        sx={{ color: plan.popular ? alpha('#fff', 0.8) : 'text.secondary', mb: 3 }}
      >
        {plan.description}
      </Typography>

      <Box mb={4}>
        {plan.price ? (
          <Stack direction="row" alignItems="baseline" spacing={0.5}>
            <Typography variant="h3" fontWeight={700}>
              ${plan.price}
            </Typography>
            <Typography sx={{ color: plan.popular ? alpha('#fff', 0.7) : 'text.secondary' }}>
              /month
            </Typography>
          </Stack>
        ) : (
          <Typography variant="h3" fontWeight={700}>
            Custom
          </Typography>
        )}
      </Box>

      <Stack spacing={2} mb={4}>
        {plan.features.map((feature, i) => (
          <Stack key={i} direction="row" spacing={1.5} alignItems="center">
            <Avatar
              sx={{
                width: 20,
                height: 20,
                bgcolor: plan.popular ? alpha('#fff', 0.2) : 'secondary.light',
              }}
            >
              <CheckIcon sx={{ fontSize: 14, color: plan.popular ? 'white' : 'secondary.dark' }} />
            </Avatar>
            <Typography
              variant="body2"
              sx={{ color: plan.popular ? alpha('#fff', 0.9) : 'text.secondary' }}
            >
              {feature}
            </Typography>
          </Stack>
        ))}
      </Stack>

      <Button
        fullWidth
        variant={plan.popular ? 'contained' : 'outlined'}
        size="large"
        sx={{
          py: 1.5,
          borderRadius: 3,
          fontWeight: 600,
          ...(plan.popular && {
            bgcolor: 'white',
            color: 'primary.main',
            '&:hover': { bgcolor: alpha('#fff', 0.9) },
          }),
        }}
      >
        {plan.cta}
      </Button>
    </Card>
  );
}
