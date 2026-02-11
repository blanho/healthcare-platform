import {
  Box,
  ToggleButton,
  ToggleButtonGroup,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  InputAdornment,
  Stack,
  Button,
} from '@mui/material';
import {
  Search as SearchIcon,
  FilterList as FilterIcon,
  Clear as ClearIcon,
} from '@mui/icons-material';
import { DatePicker } from '@mui/x-date-pickers';
import type { AppointmentStatus, AppointmentType } from '@/types';

interface AppointmentFiltersProps {
  status: AppointmentStatus | 'ALL';
  onStatusChange: (status: AppointmentStatus | 'ALL') => void;
  type: AppointmentType | 'ALL';
  onTypeChange: (type: AppointmentType | 'ALL') => void;
  startDate: Date | null;
  onStartDateChange: (date: Date | null) => void;
  endDate: Date | null;
  onEndDateChange: (date: Date | null) => void;
  searchQuery: string;
  onSearchChange: (query: string) => void;
  onClearFilters: () => void;
}

const statuses: Array<{ value: AppointmentStatus | 'ALL'; label: string }> = [
  { value: 'ALL', label: 'All' },
  { value: 'SCHEDULED', label: 'Scheduled' },
  { value: 'CONFIRMED', label: 'Confirmed' },
  { value: 'CHECKED_IN', label: 'Checked In' },
  { value: 'IN_PROGRESS', label: 'In Progress' },
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'CANCELLED', label: 'Cancelled' },
  { value: 'NO_SHOW', label: 'No Show' },
];

const types: Array<{ value: AppointmentType | 'ALL'; label: string }> = [
  { value: 'ALL', label: 'All Types' },
  { value: 'CONSULTATION', label: 'Consultation' },
  { value: 'FOLLOW_UP', label: 'Follow Up' },
  { value: 'CHECKUP', label: 'Checkup' },
  { value: 'EMERGENCY', label: 'Emergency' },
  { value: 'SURGERY', label: 'Surgery' },
  { value: 'LAB_TEST', label: 'Lab Test' },
  { value: 'IMAGING', label: 'Imaging' },
  { value: 'VACCINATION', label: 'Vaccination' },
  { value: 'PHYSICAL_THERAPY', label: 'Physical Therapy' },
  { value: 'MENTAL_HEALTH', label: 'Mental Health' },
  { value: 'DENTAL', label: 'Dental' },
  { value: 'TELEMEDICINE', label: 'Telemedicine' },
  { value: 'OTHER', label: 'Other' },
];

export function AppointmentFilters({
  status,
  onStatusChange,
  type,
  onTypeChange,
  startDate,
  onStartDateChange,
  endDate,
  onEndDateChange,
  searchQuery,
  onSearchChange,
  onClearFilters,
}: AppointmentFiltersProps) {
  const hasFilters = status !== 'ALL' || type !== 'ALL' || startDate || endDate || searchQuery;

  return (
    <Box sx={{ mb: 3 }}>
      {}
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
        <TextField
          placeholder="Search appointments..."
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          size="small"
          sx={{ minWidth: 280 }}
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

        <ToggleButtonGroup
          value={status}
          exclusive
          onChange={(_, val) => val && onStatusChange(val)}
          size="small"
          sx={{ flexWrap: 'wrap' }}
        >
          {statuses.slice(0, 5).map((s) => (
            <ToggleButton key={s.value} value={s.value} sx={{ textTransform: 'none', px: 2 }}>
              {s.label}
            </ToggleButton>
          ))}
        </ToggleButtonGroup>
      </Stack>

      {}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="center">
        <FilterIcon color="action" sx={{ display: { xs: 'none', sm: 'block' } }} />

        <FormControl size="small" sx={{ minWidth: 160 }}>
          <InputLabel>Type</InputLabel>
          <Select
            value={type}
            label="Type"
            onChange={(e) => onTypeChange(e.target.value as AppointmentType | 'ALL')}
          >
            {types.map((t) => (
              <MenuItem key={t.value} value={t.value}>
                {t.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <DatePicker
          label="From Date"
          value={startDate}
          onChange={onStartDateChange}
          slotProps={{ textField: { size: 'small', sx: { minWidth: 150 } } }}
        />

        <DatePicker
          label="To Date"
          value={endDate}
          onChange={onEndDateChange}
          slotProps={{ textField: { size: 'small', sx: { minWidth: 150 } } }}
        />

        {hasFilters && (
          <Button
            startIcon={<ClearIcon />}
            onClick={onClearFilters}
            size="small"
            color="inherit"
            sx={{ textTransform: 'none' }}
          >
            Clear Filters
          </Button>
        )}
      </Stack>
    </Box>
  );
}
