

import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Paper,
  Tabs,
  Tab,
  TablePagination,
  Alert,
  CircularProgress,
  Stack,
  Button,
  IconButton,
  Tooltip,
} from '@mui/material';
import { Refresh, Download, Security } from '@mui/icons-material';
import { AuditFilters } from '../components/AuditFilters';
import { AuditEventCard } from '../components/AuditEventCard';
import { DailyAuditStats, TopActionsSummary } from '../components/AuditStats';
import { useAuditSearch, useRecentAuditEvents, useDailyAuditSummary } from '../hooks/useAudit';
import type { AuditSearchParams } from '../types/audit.types';

type TabValue = 'all' | 'security' | 'phi' | 'failed';

const defaultFilters: AuditSearchParams = {
  page: 0,
  size: 20,
  sort: 'timestamp',
  direction: 'desc',
};

export function AuditLogListPage() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<TabValue>('all');
  const [filters, setFilters] = useState<AuditSearchParams>(defaultFilters);

  const today = new Date().toISOString().split('T')[0];
  const { data: dailySummary, isLoading: summaryLoading } = useDailyAuditSummary(today);

  const getTabFilters = useCallback((): AuditSearchParams => {
    const baseFilters = { ...filters };

    switch (activeTab) {
      case 'security':
        return {
          ...baseFilters,
          actions: [
            'LOGIN',
            'LOGOUT',
            'LOGIN_FAILED',
            'PASSWORD_CHANGE',
            'PASSWORD_RESET',
            'MFA_ENABLE',
            'MFA_DISABLE',
            'SESSION_REVOKE',
          ],
        };
      case 'phi':
        return {
          ...baseFilters,
          resourceCategories: ['PATIENT', 'MEDICAL_RECORD'],
        };
      case 'failed':
        return {
          ...baseFilters,
          outcome: 'FAILURE',
        };
      default:
        return baseFilters;
    }
  }, [activeTab, filters]);

  const searchParams = getTabFilters();
  const { data: searchResults, isLoading, error, refetch } = useAuditSearch(searchParams);

  const { data: recentEvents } = useRecentAuditEvents(10);

  const handleTabChange = (_: React.SyntheticEvent, value: TabValue) => {
    setActiveTab(value);
    setFilters((prev) => ({ ...prev, page: 0 }));
  };

  const handleFiltersChange = (newFilters: AuditSearchParams) => {
    setFilters(newFilters);
  };

  const handleSearch = () => {
    setFilters((prev) => ({ ...prev, page: 0 }));
    refetch();
  };

  const handleClearFilters = () => {
    setFilters(defaultFilters);
  };

  const handlePageChange = (_: unknown, page: number) => {
    setFilters((prev) => ({ ...prev, page }));
  };

  const handleRowsPerPageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFilters((prev) => ({ ...prev, size: Number.parseInt(event.target.value, 10), page: 0 }));
  };

  const handleViewResource = (category: string, resourceId: string) => {

    const routeMap: Record<string, string> = {
      PATIENT: `/app/patients/${resourceId}`,
      MEDICAL_RECORD: `/app/medical-records/${resourceId}`,
      APPOINTMENT: `/app/appointments/${resourceId}`,
      PROVIDER: `/app/providers/${resourceId}`,
      INVOICE: `/app/billing/invoices/${resourceId}`,
    };
    const route = routeMap[category];
    if (route) {
      navigate(route);
    }
  };

  const handleViewUser = (userId: string) => {
    navigate(`/app/audit/users/${userId}`);
  };

  const handleViewPatient = (patientId: string) => {
    navigate(`/app/patients/${patientId}`);
  };

  const handleExport = () => {

    console.warn('Export audit logs functionality coming soon');
  };

  return (
    <Box sx={{ p: 3 }}>
      {}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" component="h1" fontWeight={600}>
            Audit Logs
          </Typography>
          <Typography variant="body2" color="text.secondary">
            HIPAA-compliant audit trail for all system activities
          </Typography>
        </Box>

        <Stack direction="row" spacing={1}>
          <Tooltip title="Refresh">
            <IconButton onClick={() => refetch()}>
              <Refresh />
            </IconButton>
          </Tooltip>
          <Button variant="outlined" startIcon={<Download />} onClick={handleExport}>
            Export
          </Button>
          <Button
            variant="contained"
            startIcon={<Security />}
            onClick={() => navigate('/app/audit/reports')}
          >
            Compliance Reports
          </Button>
        </Stack>
      </Box>

      {}
      <Box sx={{ mb: 3 }}>
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '2fr 1fr' }, gap: 2 }}>
          <DailyAuditStats summary={dailySummary} loading={summaryLoading} />
          <TopActionsSummary actions={dailySummary?.topActions} loading={summaryLoading} />
        </Box>
      </Box>

      {}
      <Paper variant="outlined" sx={{ mb: 2 }}>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          sx={{ borderBottom: 1, borderColor: 'divider' }}
        >
          <Tab label="All Events" value="all" />
          <Tab label="Security Events" value="security" />
          <Tab label="PHI Access" value="phi" />
          <Tab label="Failed Operations" value="failed" />
        </Tabs>
      </Paper>

      {}
      <AuditFilters
        filters={filters}
        onFiltersChange={handleFiltersChange}
        onSearch={handleSearch}
        onClear={handleClearFilters}
      />

      {}
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          Failed to load audit events. Please try again.
        </Alert>
      )}

      {}
      {isLoading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {}
      {!isLoading && searchResults && (
        <>
          {}
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Showing {searchResults.content.length} of{' '}
            {searchResults.page.totalElements.toLocaleString()} events
          </Typography>

          {}
          {searchResults.content.length > 0 ? (
            <Box>
              {searchResults.content.map((event) => (
                <AuditEventCard
                  key={event.id}
                  event={event}
                  onViewResource={handleViewResource}
                  onViewUser={handleViewUser}
                  onViewPatient={handleViewPatient}
                />
              ))}
            </Box>
          ) : (
            <Paper variant="outlined" sx={{ p: 4, textAlign: 'center' }}>
              <Typography color="text.secondary">
                No audit events found matching your criteria.
              </Typography>
            </Paper>
          )}

          {}
          <Paper variant="outlined" sx={{ mt: 2 }}>
            <TablePagination
              component="div"
              count={searchResults.page.totalElements}
              page={filters.page ?? 0}
              onPageChange={handlePageChange}
              rowsPerPage={filters.size ?? 20}
              onRowsPerPageChange={handleRowsPerPageChange}
              rowsPerPageOptions={[10, 20, 50, 100]}
            />
          </Paper>
        </>
      )}

      {}
      {activeTab === 'all' &&
        !filters.searchTerm &&
        recentEvents &&
        recentEvents.length > 0 &&
        !searchResults?.content.length && (
          <Box sx={{ mt: 4 }}>
            <Typography variant="h6" gutterBottom>
              Recent Activity
            </Typography>
            {recentEvents.map((event) => (
              <AuditEventCard
                key={event.id}
                event={event}
                onViewResource={handleViewResource}
                onViewUser={handleViewUser}
                onViewPatient={handleViewPatient}
              />
            ))}
          </Box>
        )}
    </Box>
  );
}

export default AuditLogListPage;
