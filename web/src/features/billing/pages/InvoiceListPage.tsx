import { useState, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Box,
  Typography,
  TextField,
  InputAdornment,
  ToggleButtonGroup,
  ToggleButton,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Tabs,
  Tab,
  Stack,
  Grid,
  Skeleton,
  Pagination,
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
  ViewList as ListIcon,
  ViewModule as GridIcon,
  Receipt as InvoiceIcon,
  HealthAndSafety as ClaimIcon,
} from '@mui/icons-material';
import { DataTable, PageHeader } from '@/components/shared';
import { useInvoices, useBillingStats, useClaims } from '../hooks/useBilling';
import {
  InvoiceCard,
  InvoiceStatusChip,
  ClaimStatusChip,
  CurrencyDisplay,
  BillingStatsCard,
} from '../components';
import { invoiceStatusConfig, claimStatusConfig } from '../components/billing-chip-config';
import { format, parseISO } from 'date-fns';
import type { InvoiceStatus, ClaimStatus } from '@/types';
import type { InvoiceSummaryResponse, ClaimResponse } from '../types/billing.types';

type ViewMode = 'grid' | 'list';
type TabValue = 'invoices' | 'claims';

export function InvoiceListPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [activeTab, setActiveTab] = useState<TabValue>(
    (searchParams.get('tab') as TabValue) || 'invoices',
  );
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<InvoiceStatus | ''>('');
  const [claimStatusFilter, setClaimStatusFilter] = useState<ClaimStatus | ''>('');
  const [page, setPage] = useState(1);

  const { data: invoicesData, isLoading: invoicesLoading } = useInvoices({
    page: page - 1,
    size: 12,
    status: statusFilter || undefined,
  });
  const { data: claimsData, isLoading: claimsLoading } = useClaims({
    page: page - 1,
    size: 12,
    status: claimStatusFilter || undefined,
  });
  const { data: stats, isLoading: statsLoading } = useBillingStats();

  const invoices = invoicesData?.content || [];
  const claims = claimsData?.content || [];
  const totalPages =
    activeTab === 'invoices' ? invoicesData?.totalPages || 1 : claimsData?.totalPages || 1;

  const filteredInvoices = useMemo(() => {
    if (!searchQuery) return invoices;
    const query = searchQuery.toLowerCase();
    return invoices.filter((inv) => inv.invoiceNumber.toLowerCase().includes(query));
  }, [invoices, searchQuery]);

  const filteredClaims = useMemo(() => {
    if (!searchQuery) return claims;
    const query = searchQuery.toLowerCase();
    return claims.filter((c) => c.claimNumber.toLowerCase().includes(query));
  }, [claims, searchQuery]);

  const handleTabChange = (_: React.SyntheticEvent, value: TabValue) => {
    setActiveTab(value);
    setSearchParams({ tab: value });
    setPage(1);
  };

  const invoiceColumns = [
    {
      field: 'invoiceNumber',
      headerName: 'Invoice #',
      width: 140,
      renderCell: (params: { row: InvoiceSummaryResponse }) => (
        <Typography variant="body2" fontFamily="monospace" fontWeight={600}>
          {params.row.invoiceNumber}
        </Typography>
      ),
    },
    {
      field: 'invoiceDate',
      headerName: 'Date',
      width: 120,
      renderCell: (params: { row: InvoiceSummaryResponse }) =>
        format(parseISO(params.row.invoiceDate), 'MMM d, yyyy'),
    },
    {
      field: 'dueDate',
      headerName: 'Due Date',
      width: 120,
      renderCell: (params: { row: InvoiceSummaryResponse }) =>
        format(parseISO(params.row.dueDate), 'MMM d, yyyy'),
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: (params: { row: InvoiceSummaryResponse }) => (
        <InvoiceStatusChip status={params.row.status} />
      ),
    },
    {
      field: 'totalAmount',
      headerName: 'Total',
      width: 120,
      renderCell: (params: { row: InvoiceSummaryResponse }) => (
        <CurrencyDisplay amount={params.row.totalAmount} />
      ),
    },
    {
      field: 'balanceDue',
      headerName: 'Balance',
      width: 120,
      renderCell: (params: { row: InvoiceSummaryResponse }) => (
        <CurrencyDisplay
          amount={params.row.balanceDue}
          color={params.row.balanceDue > 0 ? 'error' : 'success'}
        />
      ),
    },
  ];

  const claimColumns = [
    {
      field: 'claimNumber',
      headerName: 'Claim #',
      width: 140,
      renderCell: (params: { row: ClaimResponse }) => (
        <Typography variant="body2" fontFamily="monospace" fontWeight={600}>
          {params.row.claimNumber}
        </Typography>
      ),
    },
    {
      field: 'insuranceProvider',
      headerName: 'Insurance',
      width: 160,
    },
    {
      field: 'serviceDate',
      headerName: 'Service Date',
      width: 130,
      renderCell: (params: { row: ClaimResponse }) =>
        format(parseISO(params.row.serviceDate), 'MMM d, yyyy'),
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 130,
      renderCell: (params: { row: ClaimResponse }) => (
        <ClaimStatusChip status={params.row.status} />
      ),
    },
    {
      field: 'billedAmount',
      headerName: 'Billed',
      width: 120,
      renderCell: (params: { row: ClaimResponse }) => (
        <CurrencyDisplay amount={params.row.billedAmount} />
      ),
    },
    {
      field: 'paidAmount',
      headerName: 'Paid',
      width: 120,
      renderCell: (params: { row: ClaimResponse }) => (
        <CurrencyDisplay
          amount={params.row.paidAmount || 0}
          color={params.row.paidAmount ? 'success' : 'text'}
        />
      ),
    },
  ];

  return (
    <Box>
      <PageHeader
        title="Billing"
        subtitle="Manage invoices, payments, and insurance claims"
        action={
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddIcon />}
            onClick={() => navigate('/app/billing/new')}
            sx={{ minHeight: 44 }}
          >
            New Invoice
          </Button>
        }
      />

      {}
      {statsLoading ? (
        <Skeleton variant="rectangular" height={200} sx={{ mb: 3, borderRadius: 2 }} />
      ) : stats ? (
        <Box sx={{ mb: 3 }}>
          <BillingStatsCard stats={stats} />
        </Box>
      ) : null}

      {}
      <Tabs
        value={activeTab}
        onChange={handleTabChange}
        sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}
        aria-label="Billing sections"
      >
        <Tab
          label="Invoices"
          value="invoices"
          icon={<InvoiceIcon />}
          iconPosition="start"
          sx={{ minHeight: 44 }}
        />
        <Tab
          label="Insurance Claims"
          value="claims"
          icon={<ClaimIcon />}
          iconPosition="start"
          sx={{ minHeight: 44 }}
        />
      </Tabs>

      {}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 3 }} alignItems="center">
        <TextField
          placeholder={`Search ${activeTab}...`}
          size="small"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" />
                </InputAdornment>
              ),
            },
          }}
          sx={{ width: 300 }}
        />

        {activeTab === 'invoices' ? (
          <FormControl size="small" sx={{ minWidth: 160 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as InvoiceStatus | '')}
              label="Status"
            >
              <MenuItem value="">All Status</MenuItem>
              {Object.entries(invoiceStatusConfig).map(([key, { label }]) => (
                <MenuItem key={key} value={key}>
                  {label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        ) : (
          <FormControl size="small" sx={{ minWidth: 160 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={claimStatusFilter}
              onChange={(e) => setClaimStatusFilter(e.target.value as ClaimStatus | '')}
              label="Status"
            >
              <MenuItem value="">All Status</MenuItem>
              {Object.entries(claimStatusConfig).map(([key, { label }]) => (
                <MenuItem key={key} value={key}>
                  {label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}

        <Box sx={{ flex: 1 }} />

        {activeTab === 'invoices' && (
          <ToggleButtonGroup
            value={viewMode}
            exclusive
            onChange={(_, v) => v && setViewMode(v)}
            size="small"
            aria-label="View mode"
          >
            <ToggleButton value="list" aria-label="List view" sx={{ minWidth: 44, minHeight: 44 }}>
              <ListIcon />
            </ToggleButton>
            <ToggleButton value="grid" aria-label="Grid view" sx={{ minWidth: 44, minHeight: 44 }}>
              <GridIcon />
            </ToggleButton>
          </ToggleButtonGroup>
        )}
      </Stack>

      {}
      {activeTab === 'invoices' ? (
        viewMode === 'list' ? (
          <DataTable
            rows={filteredInvoices}
            columns={invoiceColumns}
            loading={invoicesLoading}
            onRowClick={(row) => navigate(`/app/billing/invoices/${row.id}`)}
            getRowId={(row) => row.id}
          />
        ) : (
          <>
            {invoicesLoading ? (
              <Grid container spacing={3}>
                {[1, 2, 3, 4, 5, 6].map((i) => (
                  <Grid size={{ xs: 12, sm: 6, md: 4 }} key={i}>
                    <Skeleton variant="rectangular" height={200} sx={{ borderRadius: 2 }} />
                  </Grid>
                ))}
              </Grid>
            ) : (
              <Grid container spacing={3}>
                {filteredInvoices.map((invoice) => (
                  <Grid size={{ xs: 12, sm: 6, md: 4 }} key={invoice.id}>
                    <InvoiceCard
                      invoice={invoice}
                      onClick={() => navigate(`/app/billing/invoices/${invoice.id}`)}
                    />
                  </Grid>
                ))}
              </Grid>
            )}
          </>
        )
      ) : (
        <DataTable
          rows={filteredClaims}
          columns={claimColumns}
          loading={claimsLoading}
          onRowClick={(row) => navigate(`/app/billing/claims/${row.id}`)}
          getRowId={(row) => row.id}
        />
      )}

      {}
      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
          <Pagination
            count={totalPages}
            page={page}
            onChange={(_, p) => setPage(p)}
            color="primary"
          />
        </Box>
      )}
    </Box>
  );
}
