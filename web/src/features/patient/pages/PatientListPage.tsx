import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, TextField, Box, InputAdornment, MenuItem } from '@mui/material';
import { Add as AddIcon, Search as SearchIcon } from '@mui/icons-material';
import type { GridColDef } from '@mui/x-data-grid';
import { PageHeader, DataTable, StatusChip, EmptyState } from '@/components/shared';
import { RbacGuard } from '@/components/auth';
import { usePatients } from '../hooks/usePatient';
import type { PatientSummaryResponse } from '../types/patient.types';
import type { PatientStatus } from '@/types';

const statusOptions: PatientStatus[] = [
  'ACTIVE',
  'INACTIVE',
  'DECEASED',
  'TRANSFERRED',
  'DISCHARGED',
];

const columns: GridColDef[] = [
  { field: 'medicalRecordNumber', headerName: 'MRN', width: 140 },
  { field: 'fullName', headerName: 'Patient Name', flex: 1, minWidth: 180 },
  { field: 'email', headerName: 'Email', flex: 1, minWidth: 200 },
  { field: 'phoneNumber', headerName: 'Phone', width: 150 },
  { field: 'age', headerName: 'Age', width: 80, type: 'number' },
  {
    field: 'status',
    headerName: 'Status',
    width: 130,
    renderCell: (params) => <StatusChip status={params.value} />,
  },
];

export function PatientListPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  const { data, isLoading } = usePatients({
    page,
    size: pageSize,
    sort: 'fullName,asc',
  });

  const handleRowClick = useCallback(
    (row: PatientSummaryResponse) => {
      navigate(`/patients/${row.id}`);
    },
    [navigate],
  );

  const handlePageChange = useCallback((newPage: number, newSize: number) => {
    setPage(newPage);
    setPageSize(newSize);
  }, []);

  return (
    <>
      <PageHeader
        title="Patients"
        subtitle={`${data?.totalElements ?? 0} patients total`}
        breadcrumbs={[{ label: 'Dashboard', href: '/' }, { label: 'Patients' }]}
        action={
          <RbacGuard permission="patient:write">
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => navigate('/patients/new')}
              sx={{ cursor: 'pointer' }}
            >
              Add Patient
            </Button>
          </RbacGuard>
        }
      />

      {}
      <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
        <TextField
          placeholder="Search patients…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          sx={{ width: 320 }}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            },
          }}
        />
        <TextField
          select
          label="Status"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          sx={{ width: 160 }}
        >
          <MenuItem value="">All</MenuItem>
          {statusOptions.map((s) => (
            <MenuItem key={s} value={s}>
              {s.replace(/_/g, ' ')}
            </MenuItem>
          ))}
        </TextField>
      </Box>

      {data?.content.length === 0 && !isLoading ? (
        <EmptyState
          title="No patients found"
          message="Get started by adding your first patient."
          actionLabel="Add Patient"
          onAction={() => navigate('/patients/new')}
        />
      ) : (
        <DataTable<PatientSummaryResponse>
          rows={data?.content ?? []}
          columns={columns}
          totalElements={data?.totalElements}
          page={page}
          pageSize={pageSize}
          onPageChange={handlePageChange}
          onRowClick={handleRowClick}
          isLoading={isLoading}
        />
      )}
    </>
  );
}
