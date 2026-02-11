/* eslint-disable react-refresh/only-export-components */
import { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout';
import { ProtectedRoute } from '@/components/auth';
import { LoadingSkeleton } from '@/components/shared';

const LandingPage = lazy(() =>
  import('@/pages/LandingPage').then((m) => ({ default: m.LandingPage })),
);
const LoginPage = lazy(() =>
  import('@/features/auth/pages/LoginPage').then((m) => ({ default: m.LoginPage })),
);
const RegisterPage = lazy(() =>
  import('@/features/auth/pages/RegisterPage').then((m) => ({ default: m.RegisterPage })),
);
const ForgotPasswordPage = lazy(() =>
  import('@/features/auth/pages/ForgotPasswordPage').then((m) => ({
    default: m.ForgotPasswordPage,
  })),
);
const ResetPasswordPage = lazy(() =>
  import('@/features/auth/pages/ResetPasswordPage').then((m) => ({ default: m.ResetPasswordPage })),
);
const VerifyEmailPage = lazy(() =>
  import('@/features/auth/pages/VerifyEmailPage').then((m) => ({ default: m.VerifyEmailPage })),
);
const MfaVerifyPage = lazy(() =>
  import('@/features/auth/pages/MfaVerifyPage').then((m) => ({ default: m.MfaVerifyPage })),
);

const ChangePasswordPage = lazy(() =>
  import('@/features/auth/pages/ChangePasswordPage').then((m) => ({
    default: m.ChangePasswordPage,
  })),
);
const MfaSetupPage = lazy(() =>
  import('@/features/auth/pages/MfaSetupPage').then((m) => ({ default: m.MfaSetupPage })),
);
const SessionsPage = lazy(() =>
  import('@/features/auth/pages/SessionsPage').then((m) => ({ default: m.SessionsPage })),
);

const DashboardPage = lazy(() =>
  import('@/features/dashboard/DashboardPage').then((m) => ({ default: m.DashboardPage })),
);

const PatientListPage = lazy(() =>
  import('@/features/patient/pages/PatientListPage').then((m) => ({ default: m.PatientListPage })),
);
const PatientDetailPage = lazy(() =>
  import('@/features/patient/pages/PatientDetailPage').then((m) => ({
    default: m.PatientDetailPage,
  })),
);
const PatientFormPage = lazy(() =>
  import('@/features/patient/pages/PatientFormPage').then((m) => ({ default: m.PatientFormPage })),
);

const AppointmentListPage = lazy(() =>
  import('@/features/appointment/pages/AppointmentListPage').then((m) => ({
    default: m.AppointmentListPage,
  })),
);
const AppointmentDetailPage = lazy(() =>
  import('@/features/appointment/pages/AppointmentDetailPage').then((m) => ({
    default: m.AppointmentDetailPage,
  })),
);
const AppointmentFormPage = lazy(() =>
  import('@/features/appointment/pages/AppointmentFormPage').then((m) => ({
    default: m.AppointmentFormPage,
  })),
);

const ProviderListPage = lazy(() =>
  import('@/features/provider/pages/ProviderListPage').then((m) => ({
    default: m.ProviderListPage,
  })),
);
const ProviderDetailPage = lazy(() =>
  import('@/features/provider/pages/ProviderDetailPage').then((m) => ({
    default: m.ProviderDetailPage,
  })),
);

const MedicalRecordListPage = lazy(() =>
  import('@/features/medical-record/pages/MedicalRecordListPage').then((m) => ({
    default: m.MedicalRecordListPage,
  })),
);
const MedicalRecordDetailPage = lazy(() =>
  import('@/features/medical-record/pages/MedicalRecordDetailPage').then((m) => ({
    default: m.MedicalRecordDetailPage,
  })),
);

const InvoiceListPage = lazy(() =>
  import('@/features/billing/pages/InvoiceListPage').then((m) => ({ default: m.InvoiceListPage })),
);
const InvoiceDetailPage = lazy(() =>
  import('@/features/billing/pages/InvoiceDetailPage').then((m) => ({
    default: m.InvoiceDetailPage,
  })),
);
const InvoiceFormPage = lazy(() =>
  import('@/features/billing/pages/InvoiceFormPage').then((m) => ({ default: m.InvoiceFormPage })),
);

const NotificationListPage = lazy(() =>
  import('@/features/notification/pages/NotificationListPage').then((m) => ({
    default: m.NotificationListPage,
  })),
);
const NotificationSettingsPage = lazy(() =>
  import('@/features/notification/pages/NotificationSettingsPage').then((m) => ({
    default: m.NotificationSettingsPage,
  })),
);

const AuditLogListPage = lazy(() =>
  import('@/features/audit/pages/AuditLogListPage').then((m) => ({ default: m.AuditLogListPage })),
);
const AuditReportsPage = lazy(() =>
  import('@/features/audit/pages/AuditReportsPage').then((m) => ({ default: m.AuditReportsPage })),
);
const UserAuditTrailPage = lazy(() =>
  import('@/features/audit/pages/UserAuditTrailPage').then((m) => ({
    default: m.UserAuditTrailPage,
  })),
);

const NotFoundPage = lazy(() =>
  import('@/pages/NotFoundPage').then((m) => ({ default: m.NotFoundPage })),
);
const UnauthorizedPage = lazy(() =>
  import('@/pages/UnauthorizedPage').then((m) => ({ default: m.UnauthorizedPage })),
);

function SuspenseWrap({ children }: { children: React.ReactNode }) {
  return <Suspense fallback={<LoadingSkeleton variant="detail" />}>{children}</Suspense>;
}

export const router = createBrowserRouter([

  {
    path: '/',
    element: (
      <SuspenseWrap>
        <LandingPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/login',
    element: (
      <SuspenseWrap>
        <LoginPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/register',
    element: (
      <SuspenseWrap>
        <RegisterPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/forgot-password',
    element: (
      <SuspenseWrap>
        <ForgotPasswordPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/reset-password',
    element: (
      <SuspenseWrap>
        <ResetPasswordPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/verify-email',
    element: (
      <SuspenseWrap>
        <VerifyEmailPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/mfa-verify',
    element: (
      <SuspenseWrap>
        <MfaVerifyPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '/unauthorized',
    element: (
      <SuspenseWrap>
        <UnauthorizedPage />
      </SuspenseWrap>
    ),
  },

  {
    path: '/app',
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: (
          <SuspenseWrap>
            <DashboardPage />
          </SuspenseWrap>
        ),
      },

      {
        path: 'patients',
        element: (
          <ProtectedRoute requiredPermission="patient:read">
            <SuspenseWrap>
              <PatientListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'patients/new',
        element: (
          <ProtectedRoute requiredPermission="patient:write">
            <SuspenseWrap>
              <PatientFormPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'patients/:id',
        element: (
          <ProtectedRoute requiredPermission="patient:read">
            <SuspenseWrap>
              <PatientDetailPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'patients/:id/edit',
        element: (
          <ProtectedRoute requiredPermission="patient:write">
            <SuspenseWrap>
              <PatientFormPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'providers',
        element: (
          <ProtectedRoute requiredPermission="provider:read">
            <SuspenseWrap>
              <ProviderListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'providers/:id',
        element: (
          <ProtectedRoute requiredPermission="provider:read">
            <SuspenseWrap>
              <ProviderDetailPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'appointments',
        element: (
          <ProtectedRoute requiredPermission="appointment:read">
            <SuspenseWrap>
              <AppointmentListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'appointments/new',
        element: (
          <ProtectedRoute requiredPermission="appointment:write">
            <SuspenseWrap>
              <AppointmentFormPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'appointments/:id',
        element: (
          <ProtectedRoute requiredPermission="appointment:read">
            <SuspenseWrap>
              <AppointmentDetailPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'appointments/:id/edit',
        element: (
          <ProtectedRoute requiredPermission="appointment:write">
            <SuspenseWrap>
              <AppointmentFormPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'medical-records',
        element: (
          <ProtectedRoute requiredPermission="medical_record:read">
            <SuspenseWrap>
              <MedicalRecordListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'medical-records/:id',
        element: (
          <ProtectedRoute requiredPermission="medical_record:read">
            <SuspenseWrap>
              <MedicalRecordDetailPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'billing',
        element: (
          <ProtectedRoute requiredPermission="billing:read">
            <SuspenseWrap>
              <InvoiceListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'billing/new',
        element: (
          <ProtectedRoute requiredPermission="billing:write">
            <SuspenseWrap>
              <InvoiceFormPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'billing/invoices/:id',
        element: (
          <ProtectedRoute requiredPermission="billing:read">
            <SuspenseWrap>
              <InvoiceDetailPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'notifications',
        element: (
          <ProtectedRoute requiredPermission="notification:read">
            <SuspenseWrap>
              <NotificationListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'notifications/settings',
        element: (
          <ProtectedRoute requiredPermission="notification:read">
            <SuspenseWrap>
              <NotificationSettingsPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'audit',
        element: (
          <ProtectedRoute requiredPermission="audit:read">
            <SuspenseWrap>
              <AuditLogListPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'audit/reports',
        element: (
          <ProtectedRoute requiredPermission="audit:read">
            <SuspenseWrap>
              <AuditReportsPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },
      {
        path: 'audit/users/:userId',
        element: (
          <ProtectedRoute requiredPermission="audit:read">
            <SuspenseWrap>
              <UserAuditTrailPage />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'admin',
        element: (
          <ProtectedRoute requiredRole="ROLE_ADMIN">
            <SuspenseWrap>
              <PlaceholderPage title="Administration" />
            </SuspenseWrap>
          </ProtectedRoute>
        ),
      },

      {
        path: 'settings/security',
        element: (
          <SuspenseWrap>
            <SessionsPage />
          </SuspenseWrap>
        ),
      },
      {
        path: 'settings/security/change-password',
        element: (
          <SuspenseWrap>
            <ChangePasswordPage />
          </SuspenseWrap>
        ),
      },
      {
        path: 'settings/security/mfa-setup',
        element: (
          <SuspenseWrap>
            <MfaSetupPage />
          </SuspenseWrap>
        ),
      },
    ],
  },

  {
    path: '/404',
    element: (
      <SuspenseWrap>
        <NotFoundPage />
      </SuspenseWrap>
    ),
  },
  {
    path: '*',
    element: <Navigate to="/404" replace />,
  },
]);

import { Box, Typography } from '@mui/material';
import { PageHeader } from '@/components/shared';

function PlaceholderPage({ title }: { title: string }) {
  return (
    <>
      <PageHeader
        title={title}
        breadcrumbs={[{ label: 'Dashboard', href: '/' }, { label: title }]}
      />
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 12,
        }}
      >
        <Typography variant="body1" color="text.secondary">
          {title} module — pages will be implemented here.
        </Typography>
      </Box>
    </>
  );
}
