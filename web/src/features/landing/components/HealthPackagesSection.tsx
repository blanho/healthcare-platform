import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  Tabs,
  Tab,
  Chip,
  Stack,
  useTheme,
  alpha,
} from '@mui/material';
import { useState } from 'react';
import {
  LocalHospital as HospitalIcon,
  Science as ScienceIcon,
  Vaccines as VaccineIcon,
  Schedule as ScheduleIcon,
  CheckCircle as CheckIcon,
  East as ArrowRightIcon,
} from '@mui/icons-material';

interface HealthPackage {
  id: string;
  name: string;
  provider: string;
  price: number;
  originalPrice?: number;
  tests: number;
  duration: string;
  features: string[];
  popular: boolean;
  image: string;
}

type TabType = 'health' | 'testing' | 'vaccination';

const tabConfig = {
  health: {
    icon: <HospitalIcon />,
    label: 'Health Checkup',
  },
  testing: {
    icon: <ScienceIcon />,
    label: 'Lab Testing',
  },
  vaccination: {
    icon: <VaccineIcon />,
    label: 'Vaccination',
  },
};

export function HealthPackagesSection() {
  const theme = useTheme();
  const [activeTab, setActiveTab] = useState<TabType>('health');

  const packages: Record<TabType, HealthPackage[]> = {
    health: [
      {
        id: '1',
        name: 'Basic Health Screening',
        provider: 'Metro General Hospital',
        price: 99,
        originalPrice: 149,
        tests: 15,
        duration: '2 hours',
        features: ['Blood Test', 'Urine Analysis', 'ECG', 'BMI Assessment'],
        popular: false,
        image: 'health',
      },
      {
        id: '2',
        name: 'Comprehensive Wellness',
        provider: 'City Medical Center',
        price: 249,
        originalPrice: 349,
        tests: 35,
        duration: '4 hours',
        features: ['Full Blood Panel', 'Cardiac Screening', 'Liver Function', 'Kidney Profile'],
        popular: true,
        image: 'wellness',
      },
      {
        id: '3',
        name: 'Executive Health Check',
        provider: 'Premium Care Institute',
        price: 499,
        originalPrice: 699,
        tests: 60,
        duration: '6 hours',
        features: ['MRI Scan', 'Full Body Check', 'Cancer Markers', 'Stress Test'],
        popular: false,
        image: 'executive',
      },
    ],
    testing: [
      {
        id: '4',
        name: 'COVID-19 RT-PCR',
        provider: 'Quick Labs',
        price: 35,
        tests: 1,
        duration: '24 hours',
        features: ['ICMR Approved', 'Home Collection', 'Digital Report'],
        popular: true,
        image: 'covid',
      },
      {
        id: '5',
        name: 'Complete Blood Count',
        provider: 'HealthFirst Labs',
        price: 25,
        tests: 1,
        duration: '6 hours',
        features: ['RBC Count', 'WBC Count', 'Platelet Count', 'Hemoglobin'],
        popular: false,
        image: 'blood',
      },
      {
        id: '6',
        name: 'Thyroid Panel',
        provider: 'Diagnostic Plus',
        price: 45,
        tests: 3,
        duration: '12 hours',
        features: ['T3', 'T4', 'TSH', 'Free T4'],
        popular: false,
        image: 'thyroid',
      },
    ],
    vaccination: [
      {
        id: '7',
        name: 'Flu Vaccine',
        provider: 'Community Health Center',
        price: 35,
        tests: 1,
        duration: '15 mins',
        features: ['Seasonal Protection', 'FDA Approved', 'All Ages'],
        popular: true,
        image: 'flu',
      },
      {
        id: '8',
        name: 'Hepatitis B',
        provider: 'City Vaccination Center',
        price: 75,
        tests: 3,
        duration: '15 mins',
        features: ['3-Dose Course', 'Long-term Protection', 'WHO Recommended'],
        popular: false,
        image: 'hepatitis',
      },
      {
        id: '9',
        name: 'HPV Vaccine',
        provider: "Women's Health Clinic",
        price: 150,
        tests: 2,
        duration: '15 mins',
        features: ['Cancer Prevention', '2-Dose Course', 'Age 9-45'],
        popular: false,
        image: 'hpv',
      },
    ],
  };

  const handleTabChange = (_: React.SyntheticEvent, newValue: TabType) => {
    setActiveTab(newValue);
  };

  return (
    <Box
      component="section"
      py={10}
      sx={{
        background: `linear-gradient(180deg, ${alpha(theme.palette.primary.main, 0.03)} 0%, ${theme.palette.background.default} 100%)`,
      }}
    >
      <Container maxWidth="lg">
        <Stack alignItems="center" textAlign="center" mb={5} spacing={2}>
          <Typography variant="h4" fontWeight={700}>
            Health Packages & Services
          </Typography>
          <Typography variant="body1" color="text.secondary" maxWidth={600}>
            Choose from our curated health packages designed for comprehensive care at competitive
            prices
          </Typography>
        </Stack>

        {}
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'center',
            mb: 5,
          }}
        >
          <Tabs
            value={activeTab}
            onChange={handleTabChange}
            sx={{
              bgcolor: alpha(theme.palette.primary.main, 0.08),
              borderRadius: 3,
              p: 0.5,
              '& .MuiTabs-indicator': {
                display: 'none',
              },
              '& .MuiTab-root': {
                minHeight: 48,
                borderRadius: 2.5,
                textTransform: 'none',
                fontWeight: 600,
                px: 3,
                transition: 'all 0.2s ease',
                '&.Mui-selected': {
                  bgcolor: 'background.paper',
                  boxShadow: theme.shadows[2],
                  color: 'primary.main',
                },
              },
            }}
          >
            {(Object.keys(tabConfig) as TabType[]).map((key) => (
              <Tab
                key={key}
                value={key}
                icon={tabConfig[key].icon}
                iconPosition="start"
                label={tabConfig[key].label}
              />
            ))}
          </Tabs>
        </Box>

        {}
        <Grid container spacing={3}>
          {packages[activeTab].map((pkg) => (
            <Grid key={pkg.id} size={{ xs: 12, md: 4 }}>
              <PackageCard package={pkg} />
            </Grid>
          ))}
        </Grid>

        {}
        <Box textAlign="center" mt={5}>
          <Button
            variant="outlined"
            size="large"
            endIcon={<ArrowRightIcon />}
            sx={{ fontWeight: 600, borderRadius: 2, px: 4 }}
          >
            View All {tabConfig[activeTab].label} Packages
          </Button>
        </Box>
      </Container>
    </Box>
  );
}

interface PackageCardProps {
  package: HealthPackage;
}

function PackageCard({ package: pkg }: PackageCardProps) {
  const theme = useTheme();

  const colorMap: Record<string, string> = {
    health: theme.palette.primary.main,
    wellness: theme.palette.secondary.main,
    executive: '#6366F1',
    covid: '#EF4444',
    blood: '#EC4899',
    thyroid: '#8B5CF6',
    flu: '#10B981',
    hepatitis: '#F59E0B',
    hpv: '#06B6D4',
  };

  const bgColor = colorMap[pkg.image] || theme.palette.primary.main;

  return (
    <Card
      elevation={0}
      sx={{
        height: '100%',
        borderRadius: 3,
        overflow: 'hidden',
        border: `1px solid ${theme.palette.divider}`,
        cursor: 'pointer',
        position: 'relative',
        transition: 'all 0.3s ease',
        '&:hover': {
          borderColor: theme.palette.primary.main,
          boxShadow: `0 16px 48px ${alpha(theme.palette.primary.main, 0.15)}`,
          transform: 'translateY(-4px)',
        },
      }}
    >
      {}
      {pkg.popular && (
        <Chip
          label="Popular"
          size="small"
          color="primary"
          sx={{
            position: 'absolute',
            top: 16,
            right: 16,
            zIndex: 1,
            fontWeight: 600,
          }}
        />
      )}

      {}
      <CardMedia
        sx={{
          height: 8,
          bgcolor: bgColor,
        }}
      />

      <CardContent sx={{ p: 3 }}>
        <Stack spacing={2.5}>
          {}
          <Box>
            <Typography variant="caption" color="text.secondary" fontWeight={500}>
              {pkg.provider}
            </Typography>
            <Typography variant="h6" fontWeight={700} sx={{ mt: 0.5 }}>
              {pkg.name}
            </Typography>
          </Box>

          {}
          <Stack direction="row" alignItems="baseline" spacing={1}>
            <Typography variant="h4" fontWeight={700} color="primary.main">
              ${pkg.price}
            </Typography>
            {pkg.originalPrice && (
              <Typography
                variant="body1"
                color="text.disabled"
                sx={{ textDecoration: 'line-through' }}
              >
                ${pkg.originalPrice}
              </Typography>
            )}
            {pkg.originalPrice && (
              <Chip
                label={`${Math.round(((pkg.originalPrice - pkg.price) / pkg.originalPrice) * 100)}% OFF`}
                size="small"
                color="success"
                sx={{ fontWeight: 600, height: 22 }}
              />
            )}
          </Stack>

          {}
          <Stack direction="row" spacing={2}>
            <Stack direction="row" alignItems="center" spacing={0.5}>
              <ScienceIcon sx={{ fontSize: 18, color: 'text.secondary' }} />
              <Typography variant="body2" color="text.secondary">
                {pkg.tests} {pkg.tests === 1 ? 'Test' : 'Tests'}
              </Typography>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={0.5}>
              <ScheduleIcon sx={{ fontSize: 18, color: 'text.secondary' }} />
              <Typography variant="body2" color="text.secondary">
                {pkg.duration}
              </Typography>
            </Stack>
          </Stack>

          {}
          <Box>
            {pkg.features.slice(0, 4).map((feature, idx) => (
              <Stack key={idx} direction="row" alignItems="center" spacing={1} sx={{ mb: 0.75 }}>
                <CheckIcon
                  sx={{
                    fontSize: 18,
                    color: 'success.main',
                  }}
                />
                <Typography variant="body2" color="text.secondary">
                  {feature}
                </Typography>
              </Stack>
            ))}
          </Box>

          {}
          <Button
            fullWidth
            variant="contained"
            size="large"
            sx={{
              borderRadius: 2,
              fontWeight: 600,
              mt: 1,
            }}
          >
            Book Now
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
