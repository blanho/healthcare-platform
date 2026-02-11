
export interface DashboardStatsResponse {
  patientCount: number;
  patientGrowth: number;
  appointmentsToday: number;
  appointmentsPending: number;
  recordsPendingReview: number;
  recordsCreatedToday: number;
  outstandingBalance: number;
  overdueInvoices: number;
}

export interface TrendDataPoint {
  date: string;
  value: number;
  label?: string;
}

export interface AppointmentTrendResponse {
  period: string;
  data: TrendDataPoint[];
  totalAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
}

export interface RevenueTrendResponse {
  period: string;
  data: TrendDataPoint[];
  totalRevenue: number;
  totalCollected: number;
  outstanding: number;
}

export interface RecentActivityItem {
  id: string;
  type:
    | 'patient_registered'
    | 'appointment_scheduled'
    | 'appointment_completed'
    | 'record_created'
    | 'invoice_paid'
    | 'provider_added';
  title: string;
  description: string;
  entityId: string;
  entityType: string;
  timestamp: string;
  actorName: string;
}

export interface UpcomingAppointment {
  id: string;
  patientName: string;
  patientId: string;
  scheduledTime: string;
  appointmentType: string;
  status: string;
  providerName?: string;
}

export interface QuickAction {
  id: string;
  label: string;
  icon: string;
  route: string;
  permission: string;
  color: string;
}
