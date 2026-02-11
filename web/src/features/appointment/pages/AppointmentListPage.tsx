import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, Card } from '@mui/material';
import {
  Add as AddIcon,
  ViewList as ListIcon,
  CalendarMonth as CalendarIcon,
} from '@mui/icons-material';
import { format } from 'date-fns';
import { PageHeader, DataTable, EmptyState } from '@/components/shared';
import {
  AppointmentStatusChip,
  AppointmentTypeChip,
  TimeSlot,
  AppointmentFilters,
} from '../components';
import { useAppointments } from '../hooks/useAppointment';
import type { AppointmentStatus, AppointmentType } from '@/types';
import type { GridColDef } from '@mui/x-data-grid';

type ViewMode = 'list' | 'calendar';

export function AppointmentListPage() {
  const navigate = useNavigate();
  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  const [status, setStatus] = useState<AppointmentStatus | 'ALL'>('ALL');
  const [type, setType] = useState<AppointmentType | 'ALL'>('ALL');
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  const handleClearFilters = useCallback(() => {
    setStatus('ALL');
    setType('ALL');
    setStartDate(null);
    setEndDate(null);
    setSearchQuery('');
  }, []);

  const criteria = {
    status: status !== 'ALL' ? status : undefined,
    appointmentType: type !== 'ALL' ? type : undefined,
    startDate: startDate ? format(startDate, 'yyyy-MM-dd') : undefined,
    endDate: endDate ? format(endDate, 'yyyy-MM-dd') : undefined,
    page,
    size: pageSize,
  };

  const { data, isLoading } = useAppointments(criteria);

  const columns: GridColDef[] = [
    {
      field: 'appointmentNumber',
      headerName: 'Appointment #',
      width: 140,
      renderCell: ({ value }) => (
        <Box sx={{ fontWeight: 500, fontFamily: 'monospace' }}>{value}</Box>
      ),
    },
    {
      field: 'scheduledDate',
      headerName: 'Date',
      width: 130,
      valueFormatter: (value) => format(new Date(value), 'MMM d, yyyy'),
    },
    {
      field: 'time',
      headerName: 'Time',
      width: 150,
      valueGetter: (_, row) => `${row.startTime} - ${row.endTime}`,
      renderCell: ({ row }) => <TimeSlot startTime={row.startTime} endTime={row.endTime} compact />,
    },
    {
      field: 'appointmentType',
      headerName: 'Type',
      width: 170,
      renderCell: ({ value }) => <AppointmentTypeChip type={value} />,
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 130,
      renderCell: ({ value }) => <AppointmentStatusChip status={value} />,
    },
    {
      field: 'patientId',
      headerName: 'Patient',
      flex: 1,
      minWidth: 150,
      valueFormatter: (value: string) => `Patient #${value.slice(0, 8)}`,
    },
    {
      field: 'providerId',
      headerName: 'Provider',
      flex: 1,
      minWidth: 150,
      valueFormatter: (value: string) => `Provider #${value.slice(0, 8)}`,
    },
  ];

  const handleRowClick = (row: { id: string }) => {
    navigate(`/app/appointments/${row.id}`);
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  return (
    <>
      <PageHeader
        title="Appointments"
        subtitle="Manage patient appointments and scheduling"
        breadcrumbs={[{ label: 'Dashboard', href: '/app' }, { label: 'Appointments' }]}
        action={
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              variant={viewMode === 'list' ? 'contained' : 'outlined'}
              startIcon={<ListIcon />}
              onClick={() => setViewMode('list')}
              size="small"
              sx={{ cursor: 'pointer' }}
            >
              List
            </Button>
            <Button
              variant={viewMode === 'calendar' ? 'contained' : 'outlined'}
              startIcon={<CalendarIcon />}
              onClick={() => setViewMode('calendar')}
              size="small"
              sx={{ cursor: 'pointer' }}
            >
              Calendar
            </Button>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => navigate('/app/appointments/new')}
              sx={{ cursor: 'pointer' }}
            >
              New Appointment
            </Button>
          </Box>
        }
      />

      <AppointmentFilters
        status={status}
        onStatusChange={setStatus}
        type={type}
        onTypeChange={setType}
        startDate={startDate}
        onStartDateChange={setStartDate}
        endDate={endDate}
        onEndDateChange={setEndDate}
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        onClearFilters={handleClearFilters}
      />

      {viewMode === 'list' ? (
        <Card>
          {data?.content && data.content.length > 0 ? (
            <DataTable
              rows={data.content}
              columns={columns}
              isLoading={isLoading}
              totalElements={data.totalElements}
              page={page}
              pageSize={pageSize}
              onPageChange={handlePageChange}
              onRowClick={handleRowClick}
            />
          ) : (
            <EmptyState
              title="No appointments found"
              message="No appointments match your current filters. Try adjusting the filters or create a new appointment."
              actionLabel="New Appointment"
              onAction={() => navigate('/app/appointments/new')}
            />
          )}
        </Card>
      ) : (
        <Card sx={{ p: 4, textAlign: 'center' }}>
          <CalendarIcon sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }} />
          <Box sx={{ color: 'text.secondary' }}>
            Calendar view coming soon. Switch to list view to see appointments.
          </Box>
        </Card>
      )}
    </>
  );
}
