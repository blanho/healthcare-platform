import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Chip,
  IconButton,
  Stack,
  Switch,
  FormControlLabel,
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import type { ScheduleResponse } from '../types/provider.types';
import { DAY_ORDER, DAY_OF_WEEK_SHORT_LABELS } from '../constants';
import { formatTime24to12 } from '../utils';

interface ScheduleGridProps {
  schedules: ScheduleResponse[];
  onEdit?: (schedule: ScheduleResponse) => void;
  onDelete?: (schedule: ScheduleResponse) => void;
  onToggle?: (schedule: ScheduleResponse, active: boolean) => void;
  editable?: boolean;
}

export function ScheduleGrid({
  schedules,
  onEdit,
  onDelete,
  onToggle,
  editable = false,
}: ScheduleGridProps) {
  const schedulesByDay = schedules.reduce(
    (acc, schedule) => {
      const day = schedule.dayOfWeek;
      if (!acc[day]) acc[day] = [];
      acc[day].push(schedule);
      return acc;
    },
    {} as Record<string, ScheduleResponse[]>,
  );

  return (
    <Card>
      <CardContent>
        <Typography variant="h4" sx={{ mb: 2 }}>
          Weekly Schedule
        </Typography>

        <Grid container spacing={1}>
          {DAY_ORDER.map((day) => {
            const daySchedules = schedulesByDay[day] || [];
            const hasSchedule = daySchedules.length > 0;

            return (
              <Grid key={day} size={{ xs: 12, sm: 6, md: 'auto' }} sx={{ flex: 1 }}>
                <Box
                  sx={{
                    p: 1.5,
                    borderRadius: 1,
                    bgcolor: hasSchedule ? 'primary.lighter' : 'grey.100',
                    border: '1px solid',
                    borderColor: hasSchedule ? 'primary.light' : 'grey.200',
                    minHeight: 100,
                  }}
                >
                  <Typography
                    variant="subtitle2"
                    sx={{
                      fontWeight: 600,
                      color: hasSchedule ? 'primary.main' : 'text.secondary',
                      mb: 1,
                    }}
                  >
                    {DAY_OF_WEEK_SHORT_LABELS[day]}
                  </Typography>

                  {hasSchedule ? (
                    <Stack spacing={1}>
                      {daySchedules.map((schedule) => (
                        <Box
                          key={schedule.id}
                          sx={{
                            p: 1,
                            bgcolor: schedule.active ? 'white' : 'grey.100',
                            borderRadius: 0.5,
                            border: '1px solid',
                            borderColor: schedule.active ? 'primary.light' : 'grey.300',
                          }}
                        >
                          <Box
                            sx={{
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center',
                            }}
                          >
                            <Typography variant="caption" fontWeight={500}>
                              {formatTime24to12(schedule.startTime)} - {formatTime24to12(schedule.endTime)}
                            </Typography>
                            {!schedule.active && (
                              <Chip
                                label="Inactive"
                                size="small"
                                sx={{ height: 16, fontSize: '0.6rem' }}
                              />
                            )}
                          </Box>
                          <Typography variant="caption" color="text.secondary" display="block">
                            {schedule.slotDurationMinutes}min slots • {schedule.availableSlotCount}{' '}
                            available
                          </Typography>

                          {editable && (
                            <Box
                              sx={{
                                display: 'flex',
                                justifyContent: 'flex-end',
                                mt: 0.5,
                                gap: 0.5,
                              }}
                            >
                              {onToggle && (
                                <FormControlLabel
                                  control={
                                    <Switch
                                      size="small"
                                      checked={schedule.active}
                                      onChange={(e) => onToggle(schedule, e.target.checked)}
                                    />
                                  }
                                  label=""
                                  sx={{ m: 0 }}
                                />
                              )}
                              {onEdit && (
                                <IconButton size="small" onClick={() => onEdit(schedule)}>
                                  <EditIcon fontSize="small" />
                                </IconButton>
                              )}
                              {onDelete && (
                                <IconButton
                                  size="small"
                                  color="error"
                                  onClick={() => onDelete(schedule)}
                                >
                                  <DeleteIcon fontSize="small" />
                                </IconButton>
                              )}
                            </Box>
                          )}
                        </Box>
                      ))}
                    </Stack>
                  ) : (
                    <Typography variant="caption" color="text.disabled">
                      No schedule
                    </Typography>
                  )}
                </Box>
              </Grid>
            );
          })}
        </Grid>
      </CardContent>
    </Card>
  );
}
