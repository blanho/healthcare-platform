import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Button,
  Card,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  ToggleButton,
  ToggleButtonGroup,
  Stack,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  ViewModule as GridIcon,
  ViewList as ListIcon,
} from '@mui/icons-material';
import { PageHeader, DataTable, EmptyState } from '@/components/shared';
import {
  ProviderCard,
  ProviderStatusChip,
  ProviderTypeChip,
  AcceptingPatientsChip,
} from '../components';
import { useProviders } from '../hooks/useProvider';
import type { ProviderStatus, ProviderType } from '@/types';
import type { GridColDef } from '@mui/x-data-grid';

type ViewMode = 'grid' | 'list';

export function ProviderListPage() {
  const navigate = useNavigate();
  const [viewMode, setViewMode] = useState<ViewMode>('grid');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  const [searchQuery, setSearchQuery] = useState('');
  const [status, setStatus] = useState<ProviderStatus | 'ALL'>('ALL');
  const [type, setType] = useState<ProviderType | 'ALL'>('ALL');
  const [acceptingPatients, setAcceptingPatients] = useState<boolean | 'ALL'>('ALL');

  const criteria = {
    name: searchQuery || undefined,
    status: status !== 'ALL' ? status : undefined,
    providerType: type !== 'ALL' ? type : undefined,
    acceptingPatients: acceptingPatients !== 'ALL' ? acceptingPatients : undefined,
    page,
    size: pageSize,
  };

  const { data, isLoading } = useProviders(criteria);

  const columns: GridColDef[] = [
    {
      field: 'providerNumber',
      headerName: 'Provider #',
      width: 130,
      renderCell: ({ value }) => (
        <Box sx={{ fontWeight: 500, fontFamily: 'monospace' }}>{value}</Box>
      ),
    },
    {
      field: 'displayName',
      headerName: 'Name',
      flex: 1,
      minWidth: 180,
    },
    {
      field: 'providerType',
      headerName: 'Type',
      width: 180,
      renderCell: ({ value }) => <ProviderTypeChip type={value} />,
    },
    {
      field: 'specialization',
      headerName: 'Specialization',
      width: 150,
      valueFormatter: (value: string | null) => value || '—',
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: ({ value }) => <ProviderStatusChip status={value} />,
    },
    {
      field: 'acceptingPatients',
      headerName: 'Accepting',
      width: 150,
      renderCell: ({ value }) => <AcceptingPatientsChip accepting={value} />,
    },
    {
      field: 'email',
      headerName: 'Email',
      flex: 1,
      minWidth: 200,
    },
  ];

  const handleRowClick = (row: { id: string }) => {
    navigate(`/app/providers/${row.id}`);
  };

  return (
    <>
      <PageHeader
        title="Providers"
        subtitle="Manage healthcare providers and their schedules"
        breadcrumbs={[{ label: 'Dashboard', href: '/app' }, { label: 'Providers' }]}
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/app/providers/new')}
            sx={{ cursor: 'pointer' }}
          >
            Add Provider
          </Button>
        }
      />

      {}
      <Card sx={{ mb: 3, p: 2 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center">
          <TextField
            placeholder="Search providers..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            size="small"
            sx={{ minWidth: 250 }}
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" color="action" />
                  </InputAdornment>
                ),
              },
            }}
          />

          <FormControl size="small" sx={{ minWidth: 140 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={status}
              label="Status"
              onChange={(e) => setStatus(e.target.value as ProviderStatus | 'ALL')}
            >
              <MenuItem value="ALL">All Statuses</MenuItem>
              <MenuItem value="ACTIVE">Active</MenuItem>
              <MenuItem value="INACTIVE">Inactive</MenuItem>
              <MenuItem value="PENDING_VERIFICATION">Pending</MenuItem>
              <MenuItem value="SUSPENDED">Suspended</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small" sx={{ minWidth: 180 }}>
            <InputLabel>Type</InputLabel>
            <Select
              value={type}
              label="Type"
              onChange={(e) => setType(e.target.value as ProviderType | 'ALL')}
            >
              <MenuItem value="ALL">All Types</MenuItem>
              <MenuItem value="PHYSICIAN">Physician</MenuItem>
              <MenuItem value="NURSE_PRACTITIONER">Nurse Practitioner</MenuItem>
              <MenuItem value="PHYSICIAN_ASSISTANT">Physician Assistant</MenuItem>
              <MenuItem value="REGISTERED_NURSE">Registered Nurse</MenuItem>
              <MenuItem value="SPECIALIST">Specialist</MenuItem>
              <MenuItem value="THERAPIST">Therapist</MenuItem>
              <MenuItem value="TECHNICIAN">Technician</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small" sx={{ minWidth: 150 }}>
            <InputLabel>Accepting</InputLabel>
            <Select
              value={String(acceptingPatients)}
              label="Accepting"
              onChange={(e) =>
                setAcceptingPatients(e.target.value === 'ALL' ? 'ALL' : e.target.value === 'true')
              }
            >
              <MenuItem value="ALL">All</MenuItem>
              <MenuItem value="true">Accepting</MenuItem>
              <MenuItem value="false">Not Accepting</MenuItem>
            </Select>
          </FormControl>

          <Box sx={{ flex: 1 }} />

          <ToggleButtonGroup
            value={viewMode}
            exclusive
            onChange={(_, val) => val && setViewMode(val)}
            size="small"
          >
            <ToggleButton value="grid" sx={{ cursor: 'pointer' }}>
              <GridIcon fontSize="small" />
            </ToggleButton>
            <ToggleButton value="list" sx={{ cursor: 'pointer' }}>
              <ListIcon fontSize="small" />
            </ToggleButton>
          </ToggleButtonGroup>
        </Stack>
      </Card>

      {}
      {data?.content && data.content.length > 0 ? (
        viewMode === 'grid' ? (
          <Grid container spacing={3}>
            {data.content.map((provider) => (
              <Grid key={provider.id} size={{ xs: 12, sm: 6, lg: 4 }}>
                <ProviderCard
                  provider={provider}
                  onClick={() => navigate(`/app/providers/${provider.id}`)}
                />
              </Grid>
            ))}
          </Grid>
        ) : (
          <Card>
            <DataTable
              rows={data.content}
              columns={columns}
              isLoading={isLoading}
              totalElements={data.totalElements}
              page={page}
              pageSize={pageSize}
              onPageChange={(p, s) => {
                setPage(p);
                setPageSize(s);
              }}
              onRowClick={handleRowClick}
            />
          </Card>
        )
      ) : (
        <Card>
          <EmptyState
            title="No providers found"
            message="No providers match your current filters. Try adjusting the filters or add a new provider."
            actionLabel="Add Provider"
            onAction={() => navigate('/app/providers/new')}
          />
        </Card>
      )}
    </>
  );
}
