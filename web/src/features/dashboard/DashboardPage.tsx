import { useNavigate } from 'react-router-dom';
import { Box, Grid, Alert, AlertTitle } from '@mui/material';
import {
  People as PeopleIcon,
  CalendarMonth as CalendarIcon,
  Description as RecordIcon,
  Receipt as BillingIcon,
} from '@mui/icons-material';
import { PageHeader } from '@/components/shared';
import { RbacGuard } from '@/components/auth';
import { useAuthStore } from '@/stores';
import { StatCard, ActivityFeed, UpcomingAppointments, QuickActions } from './components';
import {
  useDashboardStats,
  useRecentActivity,
  useUpcomingAppointments,
} from './hooks/useDashboard';
import { formatCurrency } from './utils';

export function DashboardPage() {
  const navigate = useNavigate();
  const { user } = useAuthStore();

  const { data: stats, isLoading: statsLoading, error: statsError } = useDashboardStats();
  const { data: activity = [], isLoading: activityLoading } = useRecentActivity(8);
  const { data: appointments = [], isLoading: appointmentsLoading } = useUpcomingAppointments(5);

  const greeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  return (
    <>
      <PageHeader
        title={`${greeting()}, ${user?.firstName ?? 'User'}`}
        subtitle="Here's an overview of your healthcare platform."
      />

      {}
      {statsError && (
        <Alert severity="info" sx={{ mb: 3 }}>
          <AlertTitle>Dashboard data unavailable</AlertTitle>
          Statistics will appear once the backend dashboard endpoint is implemented.
        </Alert>
      )}

      {}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <RbacGuard permission="patient:read">
          <Grid size={{ xs: 12, sm: 6, lg: 3 }}>
            <StatCard
              title="Patients"
              value={stats?.patientCount ?? '—'}
              subtitle="Total registered"
              trend={stats?.patientGrowth}
              icon={<PeopleIcon />}
              color="#0891B2"
              isLoading={statsLoading}
              onClick={() => navigate('/app/patients')}
            />
          </Grid>
        </RbacGuard>
        <RbacGuard permission="appointment:read">
          <Grid size={{ xs: 12, sm: 6, lg: 3 }}>
            <StatCard
              title="Today's Appointments"
              value={stats?.appointmentsToday ?? '—'}
              subtitle={`${stats?.appointmentsPending ?? 0} pending`}
              icon={<CalendarIcon />}
              color="#059669"
              isLoading={statsLoading}
              onClick={() => navigate('/app/appointments')}
            />
          </Grid>
        </RbacGuard>
        <RbacGuard permission="medical_record:read">
          <Grid size={{ xs: 12, sm: 6, lg: 3 }}>
            <StatCard
              title="Pending Review"
              value={stats?.recordsPendingReview ?? '—'}
              subtitle={`${stats?.recordsCreatedToday ?? 0} created today`}
              icon={<RecordIcon />}
              color="#D97706"
              isLoading={statsLoading}
              onClick={() => navigate('/app/medical-records')}
            />
          </Grid>
        </RbacGuard>
        <RbacGuard permission="billing:read">
          <Grid size={{ xs: 12, sm: 6, lg: 3 }}>
            <StatCard
              title="Outstanding Balance"
              value={stats ? formatCurrency(stats.outstandingBalance) : '—'}
              subtitle={`${stats?.overdueInvoices ?? 0} overdue`}
              icon={<BillingIcon />}
              color="#DC2626"
              isLoading={statsLoading}
              onClick={() => navigate('/app/billing')}
            />
          </Grid>
        </RbacGuard>
      </Grid>

      {}
      <Box sx={{ mb: 4 }}>
        <QuickActions />
      </Box>

      {}
      <Grid container spacing={3}>
        {}
        <RbacGuard permission="appointment:read">
          <Grid size={{ xs: 12, lg: 6 }}>
            <UpcomingAppointments appointments={appointments} isLoading={appointmentsLoading} />
          </Grid>
        </RbacGuard>

        {}
        <Grid size={{ xs: 12, lg: 6 }}>
          <ActivityFeed items={activity} isLoading={activityLoading} />
        </Grid>
      </Grid>
    </>
  );
}
