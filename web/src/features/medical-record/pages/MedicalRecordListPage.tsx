import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Card,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stack,
} from '@mui/material';
import { Add as AddIcon, Search as SearchIcon } from '@mui/icons-material';
import { DatePicker } from '@mui/x-date-pickers';
import { format } from 'date-fns';
import { PageHeader, DataTable, EmptyState } from '@/components/shared';
import { RecordStatusChip, RecordTypeChip } from '../components';
import { useMedicalRecords } from '../hooks/useMedicalRecord';
import type { RecordStatus, RecordType } from '@/types';
import type { GridColDef } from '@mui/x-data-grid';

export function MedicalRecordListPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  const [searchQuery, setSearchQuery] = useState('');
  const [status, setStatus] = useState<RecordStatus | 'ALL'>('ALL');
  const [type, setType] = useState<RecordType | 'ALL'>('ALL');
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);

  const criteria = {
    status: status !== 'ALL' ? status : undefined,
    recordType: type !== 'ALL' ? type : undefined,
    startDate: startDate ? format(startDate, 'yyyy-MM-dd') : undefined,
    endDate: endDate ? format(endDate, 'yyyy-MM-dd') : undefined,
    page,
    size: pageSize,
  };

  const { data, isLoading } = useMedicalRecords(criteria);

  const columns: GridColDef[] = [
    {
      field: 'recordNumber',
      headerName: 'Record #',
      width: 140,
      renderCell: ({ value }) => (
        <Box sx={{ fontWeight: 500, fontFamily: 'monospace' }}>{value}</Box>
      ),
    },
    {
      field: 'recordDate',
      headerName: 'Date',
      width: 120,
      valueFormatter: (value) => format(new Date(value), 'MMM d, yyyy'),
    },
    {
      field: 'recordType',
      headerName: 'Type',
      width: 150,
      renderCell: ({ value }) => <RecordTypeChip type={value} />,
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: ({ value }) => <RecordStatusChip status={value} />,
    },
    {
      field: 'chiefComplaint',
      headerName: 'Chief Complaint',
      flex: 1,
      minWidth: 200,
      valueFormatter: (value: string | null) => value || '—',
    },
    {
      field: 'primaryDiagnosisDescription',
      headerName: 'Primary Diagnosis',
      flex: 1,
      minWidth: 200,
      valueFormatter: (value: string | null) => value || '—',
    },
    {
      field: 'attachmentsCount',
      headerName: 'Attachments',
      width: 110,
      align: 'center',
      headerAlign: 'center',
    },
  ];

  const handleRowClick = (row: { id: string }) => {
    navigate(`/app/medical-records/${row.id}`);
  };

  return (
    <>
      <PageHeader
        title="Medical Records"
        subtitle="View and manage patient medical records"
        breadcrumbs={[{ label: 'Dashboard', href: '/app' }, { label: 'Medical Records' }]}
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/app/medical-records/new')}
            sx={{ cursor: 'pointer' }}
          >
            New Record
          </Button>
        }
      />

      {}
      <Card sx={{ mb: 3, p: 2 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center">
          <TextField
            placeholder="Search records..."
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

          <FormControl size="small" sx={{ minWidth: 130 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={status}
              label="Status"
              onChange={(e) => setStatus(e.target.value as RecordStatus | 'ALL')}
            >
              <MenuItem value="ALL">All</MenuItem>
              <MenuItem value="DRAFT">Draft</MenuItem>
              <MenuItem value="FINALIZED">Finalized</MenuItem>
              <MenuItem value="AMENDED">Amended</MenuItem>
              <MenuItem value="VOIDED">Voided</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small" sx={{ minWidth: 160 }}>
            <InputLabel>Type</InputLabel>
            <Select
              value={type}
              label="Type"
              onChange={(e) => setType(e.target.value as RecordType | 'ALL')}
            >
              <MenuItem value="ALL">All Types</MenuItem>
              <MenuItem value="CONSULTATION">Consultation</MenuItem>
              <MenuItem value="FOLLOW_UP">Follow Up</MenuItem>
              <MenuItem value="EMERGENCY">Emergency</MenuItem>
              <MenuItem value="ROUTINE_CHECKUP">Routine Checkup</MenuItem>
              <MenuItem value="LAB_RESULT">Lab Result</MenuItem>
              <MenuItem value="IMAGING">Imaging</MenuItem>
              <MenuItem value="PROCEDURE">Procedure</MenuItem>
              <MenuItem value="DISCHARGE_SUMMARY">Discharge Summary</MenuItem>
            </Select>
          </FormControl>

          <DatePicker
            label="From"
            value={startDate}
            onChange={setStartDate}
            slotProps={{ textField: { size: 'small', sx: { minWidth: 140 } } }}
          />

          <DatePicker
            label="To"
            value={endDate}
            onChange={setEndDate}
            slotProps={{ textField: { size: 'small', sx: { minWidth: 140 } } }}
          />
        </Stack>
      </Card>

      {}
      <Card>
        {data?.content && data.content.length > 0 ? (
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
        ) : (
          <EmptyState
            title="No medical records found"
            message="No records match your current filters. Try adjusting the filters or create a new record."
            actionLabel="New Record"
            onAction={() => navigate('/app/medical-records/new')}
          />
        )}
      </Card>
    </>
  );
}
