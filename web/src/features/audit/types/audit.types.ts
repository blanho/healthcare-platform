

export type AuditAction =
  | 'CREATE'
  | 'READ'
  | 'UPDATE'
  | 'DELETE'
  | 'LOGIN'
  | 'LOGOUT'
  | 'LOGIN_FAILED'
  | 'PASSWORD_CHANGE'
  | 'PASSWORD_RESET'
  | 'MFA_ENABLE'
  | 'MFA_DISABLE'
  | 'MFA_VERIFY'
  | 'SESSION_CREATE'
  | 'SESSION_REVOKE'
  | 'EXPORT'
  | 'PRINT'
  | 'SEARCH'
  | 'VIEW';

export type AuditOutcome = 'SUCCESS' | 'FAILURE' | 'DENIED';

export type AuditSeverity = 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL';

export type ResourceCategory =
  | 'PATIENT'
  | 'MEDICAL_RECORD'
  | 'APPOINTMENT'
  | 'PROVIDER'
  | 'INVOICE'
  | 'CLAIM'
  | 'PAYMENT'
  | 'USER'
  | 'NOTIFICATION'
  | 'SYSTEM';

export interface AuditEventResponse {
  id: string;
  timestamp: string;
  correlationId?: string;
  sessionId?: string;
  userId?: string;
  username?: string;
  userRole?: string;
  ipAddressHash?: string;
  action: AuditAction;
  outcome: AuditOutcome;
  severity: AuditSeverity;
  resourceCategory: ResourceCategory;
  resourceId?: string;
  patientId?: string;
  description?: string;
  details?: Record<string, unknown>;
  userAgent?: string;
}

export interface AuditEventSummary {
  id: string;
  timestamp: string;
  correlationId?: string;
  sessionId?: string;
  userId?: string;
  username?: string;
  userRole?: string;
  ipAddressHash?: string;
  action: AuditAction;
  outcome: AuditOutcome;
  severity: AuditSeverity;
  resourceCategory: ResourceCategory;
  resourceId?: string;
  patientId?: string;
  description?: string;
  details?: Record<string, unknown>;
  userAgent?: string;
}

export interface UserActivitySummary {
  userId: string;
  username: string;
  totalEvents: number;
  successfulEvents: number;
  failedEvents: number;
  deniedEvents: number;
  lastActivityAt: string;
  topActions: ActionCount[];
  topResources: ResourceCount[];
}

export interface ActionCount {
  action: AuditAction;
  count: number;
}

export interface ResourceCount {
  resourceCategory: ResourceCategory;
  count: number;
}

export interface PatientAccessHistory {
  patientId: string;
  patientName?: string;
  totalAccesses: number;
  uniqueUsers: number;
  accessingUsers: UserAccessSummary[];
  accessTimeline: AccessTimelineEntry[];
}

export interface UserAccessSummary {
  userId: string;
  username: string;
  userRole: string;
  accessCount: number;
  lastAccessAt: string;
  actions: AuditAction[];
}

export interface AccessTimelineEntry {
  timestamp: string;
  userId: string;
  username: string;
  action: AuditAction;
  outcome: AuditOutcome;
}

export interface HipaaComplianceReport {
  reportId: string;
  generatedAt: string;
  periodStart: string;
  periodEnd: string;
  totalEvents: number;
  phiAccessCount: number;
  uniqueUsersAccessingPhi: number;
  failedAccessAttempts: number;
  securityIncidents: number;
  summary: HipaaReportSummary;
  userBreakdown: UserHipaaActivity[];
  alerts: ComplianceAlert[];
}

export interface HipaaReportSummary {
  totalPatientRecordsAccessed: number;
  totalMedicalRecordsAccessed: number;
  exportOperations: number;
  printOperations: number;
  afterHoursAccess: number;
  bulkAccessOperations: number;
}

export interface UserHipaaActivity {
  userId: string;
  username: string;
  role: string;
  phiAccessCount: number;
  patientsAccessed: number;
  recordsAccessed: number;
  lastPhiAccess: string;
}

export interface ComplianceAlert {
  severity: AuditSeverity;
  type: string;
  message: string;
  timestamp: string;
  relatedEventIds: string[];
}

export interface SecurityReport {
  reportId: string;
  generatedAt: string;
  periodStart: string;
  periodEnd: string;
  failedLogins: number;
  suspiciousActivities: number;
  accountLockouts: number;
  mfaEvents: MfaEventSummary;
  sessionEvents: SessionEventSummary;
  securityIncidents: SecurityIncident[];
}

export interface MfaEventSummary {
  enabledCount: number;
  disabledCount: number;
  failedVerifications: number;
}

export interface SessionEventSummary {
  totalSessions: number;
  revokedSessions: number;
  expiredSessions: number;
}

export interface SecurityIncident {
  id: string;
  timestamp: string;
  type: string;
  severity: AuditSeverity;
  description: string;
  userId?: string;
  username?: string;
  resolved: boolean;
}

export interface AccessReport {
  reportId: string;
  generatedAt: string;
  periodStart: string;
  periodEnd: string;
  totalAccesses: number;
  uniqueUsers: number;
  accessByRole: RoleAccessBreakdown[];
  accessByResource: ResourceAccessBreakdown[];
  peakAccessTimes: PeakAccessTime[];
}

export interface RoleAccessBreakdown {
  role: string;
  accessCount: number;
  uniqueUsers: number;
}

export interface ResourceAccessBreakdown {
  resourceCategory: ResourceCategory;
  accessCount: number;
  createCount: number;
  readCount: number;
  updateCount: number;
  deleteCount: number;
}

export interface PeakAccessTime {
  hour: number;
  dayOfWeek: string;
  accessCount: number;
}

export interface DailyAuditSummary {
  date: string;
  totalEvents: number;
  uniqueUsers: number;
  successCount: number;
  failureCount: number;
  deniedCount: number;
  topActions: ActionCount[];
  criticalEvents: number;
}

export interface MonthlyAuditSummary {
  year: number;
  month: number;
  totalEvents: number;
  dailyAverageEvents: number;
  uniqueUsers: number;
  phiAccessCount: number;
  securityIncidents: number;
  complianceScore: number;
}

export interface AuditSearchCriteria {
  userId?: string;
  username?: string;
  action?: AuditAction;
  actions?: AuditAction[];
  outcome?: AuditOutcome;
  severity?: AuditSeverity;
  severities?: AuditSeverity[];
  resourceCategory?: ResourceCategory;
  resourceCategories?: ResourceCategory[];
  resourceId?: string;
  patientId?: string;
  correlationId?: string;
  startDate?: string;
  endDate?: string;
  searchTerm?: string;
}

export interface AuditSearchParams extends AuditSearchCriteria {
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export interface DateRangeParams {
  startDate: string;
  endDate: string;
}

export interface PageResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}
