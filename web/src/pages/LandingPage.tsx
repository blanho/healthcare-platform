import { Box } from '@mui/material';
import {
  Navbar,
  HeroSection,
  PartnerHospitalsSection,
  FeaturedFacilitiesSection,
  DoctorConsultationSection,
  HealthPackagesSection,
  SpecialtiesGridSection,
  StatsSection,
  TestimonialsSection,
  AppDownloadSection,
  MediaCoverageSection,
  CTASection,
  Footer,
} from '@/features/landing';

export function LandingPage() {
  return (
    <Box className="min-h-screen" sx={{ bgcolor: 'background.default' }}>
      <Navbar />
      <Box component="main">
        <HeroSection />
        <PartnerHospitalsSection />
        <FeaturedFacilitiesSection />
        <DoctorConsultationSection />
        <HealthPackagesSection />
        <SpecialtiesGridSection />
        <StatsSection />
        <TestimonialsSection />
        <AppDownloadSection />
        <MediaCoverageSection />
        <CTASection />
      </Box>
      <Footer />
    </Box>
  );
}

export default LandingPage;
