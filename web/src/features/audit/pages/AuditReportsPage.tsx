

import { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Card,
  CardContent,
  CardActions,
  Button,
  Grid,
  Alert,
  CircularProgress,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  Stack,
  LinearProgress,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import {
  Security,
  VerifiedUser,
  Assessment,
  Download,
  Warning,
  CheckCircle,
  Error as ErrorIcon,
  Person,
  Description,
} from '@mui/icons-material';
import { useHipaaReport, useSecurityReport, useAccessReport } from '../hooks/useAudit';
import type {
  DateRangeParams,
  HipaaComplianceReport,
  SecurityReport,
  AccessReport,
} from '../types/audit.types';
import { subDays, format } from 'date-fns';

type ReportType = 'hipaa' | 'security' | 'access';

export function AuditReportsPage() {
  const [activeReport, setActiveReport] = useState<ReportType | null>(null);
  const [dateRange, setDateRange] = useState<DateRangeParams>({
    startDate: format(subDays(new Date(), 30), 'yyyy-MM-dd'),
    endDate: format(new Date(), 'yyyy-MM-dd'),
  });

  const {
    data: hipaaReport,
    isLoading: hipaaLoading,
    error: hipaaError,
  } = useHipaaReport(dateRange, activeReport === 'hipaa');

  const {
    data: securityReport,
    isLoading: securityLoading,
    error: securityError,
  } = useSecurityReport(dateRange, activeReport === 'security');

  const {
    data: accessReport,
    isLoading: accessLoading,
    error: accessError,
  } = useAccessReport(dateRange, activeReport === 'access');

  const handleGenerateReport = (type: ReportType) => {
    setActiveReport(type);
  };

  const handleExportReport = (type: ReportType) => {

    console.warn('Export report not yet implemented:', type, dateRange);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Box sx={{ p: 3 }}>
        {}
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" component="h1" fontWeight={600}>
            Compliance Reports
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Generate HIPAA compliance, security, and access reports
          </Typography>
        </Box>

        {}
        <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
          <Typography variant="subtitle2" gutterBottom>
            Report Period
          </Typography>
          <Stack direction="row" spacing={2} alignItems="center">
            <DatePicker
              label="Start Date"
              value={new Date(dateRange.startDate)}
              onChange={(date) =>
                date && setDateRange((prev) => ({ ...prev, startDate: format(date, 'yyyy-MM-dd') }))
              }
              slotProps={{ textField: { size: 'small' } }}
            />
            <Typography color="text.secondary">to</Typography>
            <DatePicker
              label="End Date"
              value={new Date(dateRange.endDate)}
              onChange={(date) =>
                date && setDateRange((prev) => ({ ...prev, endDate: format(date, 'yyyy-MM-dd') }))
              }
              slotProps={{ textField: { size: 'small' } }}
            />
            <Button
              variant="outlined"
              size="small"
              onClick={() =>
                setDateRange({
                  startDate: format(subDays(new Date(), 7), 'yyyy-MM-dd'),
                  endDate: format(new Date(), 'yyyy-MM-dd'),
                })
              }
            >
              Last 7 Days
            </Button>
            <Button
              variant="outlined"
              size="small"
              onClick={() =>
                setDateRange({
                  startDate: format(subDays(new Date(), 30), 'yyyy-MM-dd'),
                  endDate: format(new Date(), 'yyyy-MM-dd'),
                })
              }
            >
              Last 30 Days
            </Button>
            <Button
              variant="outlined"
              size="small"
              onClick={() =>
                setDateRange({
                  startDate: format(subDays(new Date(), 90), 'yyyy-MM-dd'),
                  endDate: format(new Date(), 'yyyy-MM-dd'),
                })
              }
            >
              Last 90 Days
            </Button>
          </Stack>
        </Paper>

        {}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {}
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined" sx={{ height: '100%' }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                  <VerifiedUser color="primary" />
                  <Typography variant="h6">HIPAA Compliance</Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                  PHI access tracking, user activity breakdown, compliance alerts, and HIPAA audit
                  requirements.
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  variant="contained"
                  onClick={() => handleGenerateReport('hipaa')}
                  disabled={hipaaLoading}
                  startIcon={hipaaLoading ? <CircularProgress size={16} /> : <Assessment />}
                >
                  Generate
                </Button>
                <Button
                  onClick={() => handleExportReport('hipaa')}
                  disabled={!hipaaReport}
                  startIcon={<Download />}
                >
                  Export
                </Button>
              </CardActions>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined" sx={{ height: '100%' }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                  <Security color="error" />
                  <Typography variant="h6">Security Report</Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                  Failed login attempts, account lockouts, MFA usage, session management, and
                  security incidents.
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  variant="contained"
                  color="error"
                  onClick={() => handleGenerateReport('security')}
                  disabled={securityLoading}
                  startIcon={securityLoading ? <CircularProgress size={16} /> : <Assessment />}
                >
                  Generate
                </Button>
                <Button
                  onClick={() => handleExportReport('security')}
                  disabled={!securityReport}
                  startIcon={<Download />}
                >
                  Export
                </Button>
              </CardActions>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined" sx={{ height: '100%' }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                  <Person color="info" />
                  <Typography variant="h6">Access Report</Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                  User access patterns, resource access breakdown, role-based activity, and peak
                  access times.
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  variant="contained"
                  color="info"
                  onClick={() => handleGenerateReport('access')}
                  disabled={accessLoading}
                  startIcon={accessLoading ? <CircularProgress size={16} /> : <Assessment />}
                >
                  Generate
                </Button>
                <Button
                  onClick={() => handleExportReport('access')}
                  disabled={!accessReport}
                  startIcon={<Download />}
                >
                  Export
                </Button>
              </CardActions>
            </Card>
          </Grid>
        </Grid>

        {}
        {activeReport === 'hipaa' && (
          <HipaaReportDisplay report={hipaaReport} loading={hipaaLoading} error={hipaaError} />
        )}
        {activeReport === 'security' && (
          <SecurityReportDisplay
            report={securityReport}
            loading={securityLoading}
            error={securityError}
          />
        )}
        {activeReport === 'access' && (
          <AccessReportDisplay report={accessReport} loading={accessLoading} error={accessError} />
        )}
      </Box>
    </LocalizationProvider>
  );
}

interface HipaaReportDisplayProps {
  report?: HipaaComplianceReport;
  loading: boolean;
  error: unknown;
}

function HipaaReportDisplay({ report, loading, error }: HipaaReportDisplayProps) {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">Failed to generate HIPAA compliance report.</Alert>;
  }

  if (!report) return null;

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        HIPAA Compliance Report
      </Typography>
      <Typography variant="body2" color="text.secondary" gutterBottom>
        Period: {report.periodStart} to {report.periodEnd}
      </Typography>

      <Grid container spacing={3} sx={{ mt: 1 }}>
        {}
        <Grid size={{ xs: 12, md: 6 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Overview
              </Typography>
              <List dense>
                <ListItem>
                  <ListItemText
                    primary="Total Audit Events"
                    secondary={report.totalEvents.toLocaleString()}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="PHI Access Events"
                    secondary={report.phiAccessCount.toLocaleString()}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="Unique Users Accessing PHI"
                    secondary={report.uniqueUsersAccessingPhi}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="Failed Access Attempts"
                    secondary={report.failedAccessAttempts}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="Security Incidents"
                    secondary={
                      <Chip
                        label={report.securityIncidents}
                        size="small"
                        color={report.securityIncidents > 0 ? 'error' : 'success'}
                      />
                    }
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12, md: 6 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                PHI Access Summary
              </Typography>
              <List dense>
                <ListItem>
                  <ListItemIcon>
                    <Person fontSize="small" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Patient Records Accessed"
                    secondary={report.summary.totalPatientRecordsAccessed.toLocaleString()}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Description fontSize="small" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Medical Records Accessed"
                    secondary={report.summary.totalMedicalRecordsAccessed.toLocaleString()}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Download fontSize="small" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Export Operations"
                    secondary={report.summary.exportOperations}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="Print Operations"
                    secondary={report.summary.printOperations}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="After-Hours Access"
                    secondary={
                      <Chip
                        label={report.summary.afterHoursAccess}
                        size="small"
                        color={report.summary.afterHoursAccess > 10 ? 'warning' : 'default'}
                      />
                    }
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {}
        {report.alerts.length > 0 && (
          <Grid size={{ xs: 12 }}>
            <Alert severity="warning" sx={{ mb: 2 }}>
              {report.alerts.length} compliance alert(s) require attention
            </Alert>
            {report.alerts.map((alert, index) => (
              <Alert
                key={index}
                severity={
                  alert.severity === 'CRITICAL'
                    ? 'error'
                    : alert.severity === 'WARNING'
                      ? 'warning'
                      : 'info'
                }
                sx={{ mb: 1 }}
              >
                <Typography variant="subtitle2">{alert.type}</Typography>
                <Typography variant="body2">{alert.message}</Typography>
              </Alert>
            ))}
          </Grid>
        )}

        {}
        <Grid size={{ xs: 12 }}>
          <Typography variant="subtitle2" gutterBottom>
            Top Users by PHI Access
          </Typography>
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
              gap: 2,
            }}
          >
            {report.userBreakdown.slice(0, 6).map((user) => (
              <Card key={user.userId} variant="outlined">
                <CardContent sx={{ py: 1.5 }}>
                  <Typography variant="body2" fontWeight={500}>
                    {user.username}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {user.role}
                  </Typography>
                  <Box sx={{ mt: 1 }}>
                    <Typography variant="body2">
                      PHI Access: {user.phiAccessCount} | Patients: {user.patientsAccessed} |
                      Records: {user.recordsAccessed}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            ))}
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );
}

interface SecurityReportDisplayProps {
  report?: SecurityReport;
  loading: boolean;
  error: unknown;
}

function SecurityReportDisplay({ report, loading, error }: SecurityReportDisplayProps) {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">Failed to generate security report.</Alert>;
  }

  if (!report) return null;

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Security Report
      </Typography>
      <Typography variant="body2" color="text.secondary" gutterBottom>
        Period: {report.periodStart} to {report.periodEnd}
      </Typography>

      <Grid container spacing={3} sx={{ mt: 1 }}>
        {}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Authentication
              </Typography>
              <List dense>
                <ListItem>
                  <ListItemIcon>
                    {report.failedLogins > 10 ? (
                      <Warning color="warning" />
                    ) : (
                      <CheckCircle color="success" />
                    )}
                  </ListItemIcon>
                  <ListItemText primary="Failed Logins" secondary={report.failedLogins} />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    {report.accountLockouts > 0 ? (
                      <ErrorIcon color="error" />
                    ) : (
                      <CheckCircle color="success" />
                    )}
                  </ListItemIcon>
                  <ListItemText primary="Account Lockouts" secondary={report.accountLockouts} />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    {report.suspiciousActivities > 0 ? (
                      <Warning color="warning" />
                    ) : (
                      <CheckCircle color="success" />
                    )}
                  </ListItemIcon>
                  <ListItemText
                    primary="Suspicious Activities"
                    secondary={report.suspiciousActivities}
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Multi-Factor Authentication
              </Typography>
              <List dense>
                <ListItem>
                  <ListItemText primary="MFA Enabled" secondary={report.mfaEvents.enabledCount} />
                </ListItem>
                <ListItem>
                  <ListItemText primary="MFA Disabled" secondary={report.mfaEvents.disabledCount} />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    {report.mfaEvents.failedVerifications > 5 ? (
                      <Warning color="warning" />
                    ) : (
                      <CheckCircle color="success" />
                    )}
                  </ListItemIcon>
                  <ListItemText
                    primary="Failed Verifications"
                    secondary={report.mfaEvents.failedVerifications}
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Sessions
              </Typography>
              <List dense>
                <ListItem>
                  <ListItemText
                    primary="Total Sessions"
                    secondary={report.sessionEvents.totalSessions}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="Revoked Sessions"
                    secondary={report.sessionEvents.revokedSessions}
                  />
                </ListItem>
                <ListItem>
                  <ListItemText
                    primary="Expired Sessions"
                    secondary={report.sessionEvents.expiredSessions}
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {}
        {report.securityIncidents.length > 0 && (
          <Grid size={{ xs: 12 }}>
            <Typography variant="subtitle2" gutterBottom>
              Security Incidents
            </Typography>
            {report.securityIncidents.map((incident) => (
              <Alert
                key={incident.id}
                severity={incident.severity === 'CRITICAL' ? 'error' : 'warning'}
                sx={{ mb: 1 }}
              >
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'flex-start',
                  }}
                >
                  <Box>
                    <Typography variant="subtitle2">{incident.type}</Typography>
                    <Typography variant="body2">{incident.description}</Typography>
                    {incident.username && (
                      <Typography variant="caption">User: {incident.username}</Typography>
                    )}
                  </Box>
                  <Chip
                    label={incident.resolved ? 'Resolved' : 'Open'}
                    size="small"
                    color={incident.resolved ? 'success' : 'warning'}
                  />
                </Box>
              </Alert>
            ))}
          </Grid>
        )}
      </Grid>
    </Paper>
  );
}

interface AccessReportDisplayProps {
  report?: AccessReport;
  loading: boolean;
  error: unknown;
}

function AccessReportDisplay({ report, loading, error }: AccessReportDisplayProps) {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">Failed to generate access report.</Alert>;
  }

  if (!report) return null;

  const maxRoleAccess = Math.max(...report.accessByRole.map((r) => r.accessCount));

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Access Report
      </Typography>
      <Typography variant="body2" color="text.secondary" gutterBottom>
        Period: {report.periodStart} to {report.periodEnd}
      </Typography>

      <Grid container spacing={3} sx={{ mt: 1 }}>
        {}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Overview
              </Typography>
              <Typography variant="h3" fontWeight={600}>
                {report.totalAccesses.toLocaleString()}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Total access events
              </Typography>
              <Typography variant="body2" sx={{ mt: 1 }}>
                {report.uniqueUsers} unique users
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12, md: 8 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Access by Role
              </Typography>
              <Stack spacing={1.5}>
                {report.accessByRole.map((role) => (
                  <Box key={role.role}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                      <Typography variant="body2">{role.role}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        {role.accessCount.toLocaleString()} ({role.uniqueUsers} users)
                      </Typography>
                    </Box>
                    <LinearProgress
                      variant="determinate"
                      value={(role.accessCount / maxRoleAccess) * 100}
                      sx={{ height: 8, borderRadius: 4 }}
                    />
                  </Box>
                ))}
              </Stack>
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12 }}>
          <Typography variant="subtitle2" gutterBottom>
            Access by Resource Type
          </Typography>
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: 2,
            }}
          >
            {report.accessByResource.map((resource) => (
              <Card key={resource.resourceCategory} variant="outlined">
                <CardContent sx={{ py: 1.5 }}>
                  <Typography variant="body2" fontWeight={500}>
                    {resource.resourceCategory.replace('_', ' ')}
                  </Typography>
                  <Typography variant="h5" fontWeight={600}>
                    {resource.accessCount.toLocaleString()}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    C: {resource.createCount} | R: {resource.readCount} | U: {resource.updateCount}{' '}
                    | D: {resource.deleteCount}
                  </Typography>
                </CardContent>
              </Card>
            ))}
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );
}

export default AuditReportsPage;
