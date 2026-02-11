

import {
  Box,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Stack,
  Button,
  Collapse,
  Paper,
  IconButton,
  OutlinedInput,
  InputAdornment,
  type SelectChangeEvent,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { Search, FilterList, Clear, ExpandMore, ExpandLess } from '@mui/icons-material';
import { useState } from 'react';
import type {
  AuditAction,
  AuditOutcome,
  AuditSeverity,
  ResourceCategory,
  AuditSearchParams,
} from '../types/audit.types';

const AUDIT_ACTIONS: AuditAction[] = [
  'CREATE',
  'READ',
  'UPDATE',
  'DELETE',
  'LOGIN',
  'LOGOUT',
  'LOGIN_FAILED',
  'PASSWORD_CHANGE',
  'PASSWORD_RESET',
  'MFA_ENABLE',
  'MFA_DISABLE',
  'MFA_VERIFY',
  'SESSION_CREATE',
  'SESSION_REVOKE',
  'EXPORT',
  'PRINT',
  'SEARCH',
  'VIEW',
];

const AUDIT_OUTCOMES: AuditOutcome[] = ['SUCCESS', 'FAILURE', 'DENIED'];

const AUDIT_SEVERITIES: AuditSeverity[] = ['INFO', 'WARNING', 'ERROR', 'CRITICAL'];

const RESOURCE_CATEGORIES: ResourceCategory[] = [
  'PATIENT',
  'MEDICAL_RECORD',
  'APPOINTMENT',
  'PROVIDER',
  'INVOICE',
  'CLAIM',
  'PAYMENT',
  'USER',
  'NOTIFICATION',
  'SYSTEM',
];

interface AuditFiltersProps {
  filters: AuditSearchParams;
  onFiltersChange: (filters: AuditSearchParams) => void;
  onSearch: () => void;
  onClear: () => void;
}

export function AuditFilters({ filters, onFiltersChange, onSearch, onClear }: AuditFiltersProps) {
  const [expanded, setExpanded] = useState(false);

  const handleTextChange =
    (field: keyof AuditSearchParams) => (event: React.ChangeEvent<HTMLInputElement>) => {
      onFiltersChange({ ...filters, [field]: event.target.value || undefined });
    };

  const handleSelectChange =
    (field: keyof AuditSearchParams) => (event: SelectChangeEvent<string | string[]>) => {
      const value = event.target.value;
      onFiltersChange({
        ...filters,
        [field]: Array.isArray(value) ? (value.length > 0 ? value : undefined) : value || undefined,
      });
    };

  const handleDateChange = (field: 'startDate' | 'endDate') => (date: Date | null) => {
    onFiltersChange({
      ...filters,
      [field]: date ? date.toISOString().split('T')[0] : undefined,
    });
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      onSearch();
    }
  };

  const activeFiltersCount = Object.values(filters).filter(
    (v) => v !== undefined && v !== '' && (!Array.isArray(v) || v.length > 0),
  ).length;

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        {}
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          {}
          <TextField
            label="Search"
            placeholder="Search events..."
            value={filters.searchTerm ?? ''}
            onChange={handleTextChange('searchTerm')}
            onKeyPress={handleKeyPress}
            size="small"
            sx={{ minWidth: 200, flex: 1 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search fontSize="small" />
                </InputAdornment>
              ),
            }}
          />

          {}
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Outcome</InputLabel>
            <Select
              value={filters.outcome ?? ''}
              onChange={handleSelectChange('outcome')}
              label="Outcome"
            >
              <MenuItem value="">All</MenuItem>
              {AUDIT_OUTCOMES.map((outcome) => (
                <MenuItem key={outcome} value={outcome}>
                  {outcome}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {}
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Severity</InputLabel>
            <Select
              multiple
              value={filters.severities ?? []}
              onChange={handleSelectChange('severities')}
              input={<OutlinedInput label="Severity" />}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {(selected as string[]).map((value) => (
                    <Chip key={value} label={value} size="small" />
                  ))}
                </Box>
              )}
            >
              {AUDIT_SEVERITIES.map((severity) => (
                <MenuItem key={severity} value={severity}>
                  {severity}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {}
          <FormControl size="small" sx={{ minWidth: 150 }}>
            <InputLabel>Resource</InputLabel>
            <Select
              multiple
              value={filters.resourceCategories ?? []}
              onChange={handleSelectChange('resourceCategories')}
              input={<OutlinedInput label="Resource" />}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {(selected as string[]).slice(0, 2).map((value) => (
                    <Chip key={value} label={value} size="small" />
                  ))}
                  {(selected as string[]).length > 2 && (
                    <Chip label={`+${(selected as string[]).length - 2}`} size="small" />
                  )}
                </Box>
              )}
            >
              {RESOURCE_CATEGORIES.map((category) => (
                <MenuItem key={category} value={category}>
                  {category.replace('_', ' ')}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {}
          <IconButton onClick={() => setExpanded(!expanded)} size="small">
            {expanded ? <ExpandLess /> : <ExpandMore />}
          </IconButton>
        </Box>

        {}
        <Collapse in={expanded}>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', mt: 2 }}>
            {}
            <FormControl size="small" sx={{ minWidth: 200 }}>
              <InputLabel>Actions</InputLabel>
              <Select
                multiple
                value={filters.actions ?? []}
                onChange={handleSelectChange('actions')}
                input={<OutlinedInput label="Actions" />}
                renderValue={(selected) => (
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                    {(selected as string[]).slice(0, 3).map((value) => (
                      <Chip key={value} label={value} size="small" />
                    ))}
                    {(selected as string[]).length > 3 && (
                      <Chip label={`+${(selected as string[]).length - 3}`} size="small" />
                    )}
                  </Box>
                )}
              >
                {AUDIT_ACTIONS.map((action) => (
                  <MenuItem key={action} value={action}>
                    {action.replace('_', ' ')}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {}
            <TextField
              label="User ID"
              placeholder="UUID"
              value={filters.userId ?? ''}
              onChange={handleTextChange('userId')}
              size="small"
              sx={{ minWidth: 200 }}
            />

            {}
            <TextField
              label="Username"
              placeholder="Username"
              value={filters.username ?? ''}
              onChange={handleTextChange('username')}
              size="small"
              sx={{ minWidth: 150 }}
            />

            {}
            <TextField
              label="Patient ID"
              placeholder="UUID"
              value={filters.patientId ?? ''}
              onChange={handleTextChange('patientId')}
              size="small"
              sx={{ minWidth: 200 }}
            />

            {}
            <TextField
              label="Resource ID"
              placeholder="UUID"
              value={filters.resourceId ?? ''}
              onChange={handleTextChange('resourceId')}
              size="small"
              sx={{ minWidth: 200 }}
            />

            {}
            <TextField
              label="Correlation ID"
              placeholder="UUID"
              value={filters.correlationId ?? ''}
              onChange={handleTextChange('correlationId')}
              size="small"
              sx={{ minWidth: 200 }}
            />

            {}
            <DatePicker
              label="Start Date"
              value={filters.startDate ? new Date(filters.startDate) : null}
              onChange={handleDateChange('startDate')}
              slotProps={{ textField: { size: 'small', sx: { minWidth: 150 } } }}
            />

            <DatePicker
              label="End Date"
              value={filters.endDate ? new Date(filters.endDate) : null}
              onChange={handleDateChange('endDate')}
              slotProps={{ textField: { size: 'small', sx: { minWidth: 150 } } }}
            />
          </Box>
        </Collapse>

        {}
        <Stack direction="row" spacing={1} sx={{ mt: 2 }}>
          <Button variant="contained" startIcon={<FilterList />} onClick={onSearch}>
            Search
            {activeFiltersCount > 0 && (
              <Chip
                label={activeFiltersCount}
                size="small"
                color="secondary"
                sx={{ ml: 1, height: 20, minWidth: 20 }}
              />
            )}
          </Button>
          <Button
            variant="outlined"
            startIcon={<Clear />}
            onClick={onClear}
            disabled={activeFiltersCount === 0}
          >
            Clear
          </Button>
        </Stack>
      </Paper>
    </LocalizationProvider>
  );
}

export default AuditFilters;
