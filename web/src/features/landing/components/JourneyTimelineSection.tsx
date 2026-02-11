import { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Avatar,
  Chip,
  Stack,
  useTheme,
  alpha,
} from '@mui/material';
import {
  CalendarMonth as CalendarIcon,
  Description as FileTextIcon,
  CreditCard as CreditCardIcon,
  AccessTime as ClockIcon,
  Check as CheckIcon,
  MedicalServices as StethoscopeIcon,
} from '@mui/icons-material';

interface Step {
  title: string;
  description: string;
  icon: typeof CalendarIcon;
}

export function JourneyTimelineSection() {
  const [activeStep, setActiveStep] = useState(0);

  const steps: Step[] = [
    {
      title: 'Book Appointment',
      description: 'Patients can self-schedule 24/7 with smart availability matching.',
      icon: CalendarIcon,
    },
    {
      title: 'Pre-Visit Forms',
      description: 'Digital intake forms and insurance verification completed before arrival.',
      icon: FileTextIcon,
    },
    {
      title: 'Check-In',
      description: 'Contactless check-in via mobile app with real-time wait time updates.',
      icon: ClockIcon,
    },
    {
      title: 'Consultation',
      description: 'Provider accesses complete patient history with AI-assisted insights.',
      icon: StethoscopeIcon,
    },
    {
      title: 'Billing & Follow-up',
      description: 'Automated billing, prescription management, and care plan reminders.',
      icon: CreditCardIcon,
    },
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setActiveStep((prev) => (prev + 1) % steps.length);
    }, 4000);
    return () => clearInterval(interval);
  }, [steps.length]);

  return (
    <Box component="section" py={12} bgcolor="background.default">
      <Container maxWidth="lg">
        <Stack spacing={2} alignItems="center" textAlign="center" mb={8}>
          <Chip
            label="Patient Experience"
            color="secondary"
            variant="outlined"
            sx={{ fontWeight: 600 }}
          />
          <Typography
            variant="h2"
            sx={{ fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700 }}
          >
            Seamless Care Journey
          </Typography>
          <Typography variant="body1" color="text.secondary" maxWidth={600}>
            From booking to billing, every touchpoint is designed for efficiency.
          </Typography>
        </Stack>

        {}
        <ProgressBar activeStep={activeStep} totalSteps={steps.length} />

        <Grid container spacing={3}>
          {steps.map((step, index) => (
            <Grid key={index} size={{ xs: 12, sm: 6, lg: 2.4 }}>
              <StepCard
                step={step}
                index={index}
                isActive={index === activeStep}
                isCompleted={index < activeStep}
                onClick={() => setActiveStep(index)}
              />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}

interface ProgressBarProps {
  activeStep: number;
  totalSteps: number;
}

function ProgressBar({ activeStep, totalSteps }: ProgressBarProps) {
  const theme = useTheme();

  return (
    <Box sx={{ display: { xs: 'none', lg: 'block' }, position: 'relative', mb: 6 }}>
      <Box
        sx={{
          position: 'absolute',
          top: 28,
          left: '10%',
          right: '10%',
          height: 4,
          bgcolor: 'divider',
          borderRadius: 2,
        }}
      >
        <Box
          sx={{
            height: '100%',
            background: `linear-gradient(90deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
            borderRadius: 2,
            transition: 'width 0.5s ease',
            width: `${((activeStep + 1) / totalSteps) * 100}%`,
          }}
        />
      </Box>
    </Box>
  );
}

interface StepCardProps {
  step: Step;
  index: number;
  isActive: boolean;
  isCompleted: boolean;
  onClick: () => void;
}

function StepCard({ step, index, isActive, isCompleted, onClick }: StepCardProps) {
  const theme = useTheme();
  const Icon = step.icon;

  return (
    <Box
      onClick={onClick}
      sx={{
        textAlign: 'center',
        cursor: 'pointer',
        opacity: isActive ? 1 : 0.6,
        transform: isActive ? 'scale(1.05)' : 'scale(1)',
        transition: 'all 0.3s ease',
        '&:hover': { opacity: 1 },
      }}
    >
      <Avatar
        sx={{
          width: 64,
          height: 64,
          mx: 'auto',
          mb: 2,
          transition: 'all 0.3s ease',
          ...(isActive
            ? {
                background: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
                boxShadow: `0 8px 24px ${alpha(theme.palette.primary.main, 0.35)}`,
              }
            : isCompleted
              ? { bgcolor: 'secondary.light', color: 'secondary.dark' }
              : { bgcolor: 'background.paper', border: `2px solid ${theme.palette.divider}` }),
        }}
      >
        {isCompleted ? <CheckIcon /> : <Icon />}
      </Avatar>
      <Typography variant="caption" color="text.secondary" fontWeight={600}>
        Step {index + 1}
      </Typography>
      <Typography variant="subtitle1" fontWeight={600} mt={0.5}>
        {step.title}
      </Typography>
      <Typography
        variant="body2"
        color="text.secondary"
        sx={{
          opacity: { xs: 1, lg: isActive ? 1 : 0 },
          maxHeight: { xs: 'none', lg: isActive ? 100 : 0 },
          overflow: 'hidden',
          transition: 'all 0.3s ease',
          mt: 1,
        }}
      >
        {step.description}
      </Typography>
    </Box>
  );
}
