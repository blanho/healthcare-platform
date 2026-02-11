

export const ACTIVITY_TYPE_LABELS: Record<string, string> = {
  patient_registered: 'Patient Registered',
  appointment_scheduled: 'Appointment Scheduled',
  appointment_completed: 'Appointment Completed',
  record_created: 'Record Created',
  invoice_paid: 'Invoice Paid',
  provider_added: 'Provider Added',
};

export const ACTIVITY_TYPE_ICONS: Record<string, string> = {
  patient_registered: 'PersonAdd',
  appointment_scheduled: 'CalendarToday',
  appointment_completed: 'CheckCircle',
  record_created: 'Description',
  invoice_paid: 'AttachMoney',
  provider_added: 'LocalHospital',
};

export const ACTIVITY_TYPE_COLORS: Record<
  string,
  'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info'
> = {
  patient_registered: 'primary',
  appointment_scheduled: 'info',
  appointment_completed: 'success',
  record_created: 'secondary',
  invoice_paid: 'success',
  provider_added: 'primary',
};

export const CHART_COLORS = {
  primary: '#0891B2',
  secondary: '#22C55E',
  accent: '#F97316',
  warning: '#EAB308',
  error: '#EF4444',
  info: '#3B82F6',
  gray: '#6B7280',
} as const;

export const CHART_GRADIENTS = {
  primary: ['rgba(8, 145, 178, 0.8)', 'rgba(8, 145, 178, 0.1)'],
  secondary: ['rgba(34, 197, 94, 0.8)', 'rgba(34, 197, 94, 0.1)'],
  accent: ['rgba(249, 115, 22, 0.8)', 'rgba(249, 115, 22, 0.1)'],
} as const;

export const PERIOD_OPTIONS = [
  { value: '7d', label: 'Last 7 days' },
  { value: '14d', label: 'Last 14 days' },
  { value: '30d', label: 'Last 30 days' },
  { value: '90d', label: 'Last 90 days' },
  { value: '1y', label: 'Last year' },
] as const;

export const DEFAULT_PERIOD = '30d';

export const QUICK_ACTIONS = [
  {
    id: 'new-patient',
    label: 'Register Patient',
    icon: 'PersonAdd',
    route: '/patients/new',
    permission: 'patient:write',
    color: 'primary',
  },
  {
    id: 'schedule-appointment',
    label: 'Schedule Appointment',
    icon: 'CalendarToday',
    route: '/appointments/new',
    permission: 'appointment:write',
    color: 'secondary',
  },
  {
    id: 'create-record',
    label: 'Create Record',
    icon: 'Description',
    route: '/medical-records/new',
    permission: 'medical-record:write',
    color: 'info',
  },
  {
    id: 'create-invoice',
    label: 'Create Invoice',
    icon: 'Receipt',
    route: '/billing/invoices/new',
    permission: 'billing:write',
    color: 'success',
  },
] as const;

export const STAT_CARD_CONFIG = {
  patients: {
    title: 'Total Patients',
    icon: 'People',
    color: 'primary' as const,
    route: '/patients',
  },
  appointments: {
    title: "Today's Appointments",
    icon: 'CalendarToday',
    color: 'secondary' as const,
    route: '/appointments',
  },
  records: {
    title: 'Pending Reviews',
    icon: 'Description',
    color: 'warning' as const,
    route: '/medical-records?status=pending',
  },
  billing: {
    title: 'Outstanding Balance',
    icon: 'AttachMoney',
    color: 'error' as const,
    route: '/billing/invoices?status=unpaid',
  },
} as const;

export const REFRESH_INTERVALS = {
  STATS: 60 * 1000,
  ACTIVITY: 30 * 1000,
  APPOINTMENTS: 60 * 1000,
  CHARTS: 5 * 60 * 1000,
} as const;

export const WIDGET_GRID_CONFIG = {
  STATS_ROW: { xs: 12, sm: 6, md: 3 },
  CHART_LARGE: { xs: 12, md: 8 },
  CHART_SMALL: { xs: 12, md: 4 },
  LIST_WIDGET: { xs: 12, md: 6 },
} as const;
