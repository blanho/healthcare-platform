import { FormControlLabel, Checkbox, Link, Typography, Stack } from '@mui/material';
import { Controller, type Control, type FieldValues, type Path } from 'react-hook-form';
import { Link as RouterLink } from 'react-router-dom';

interface TermsCheckboxProps<T extends FieldValues> {
  name: Path<T>;
  control: Control<T>;
  disabled?: boolean;
}

export function TermsCheckbox<T extends FieldValues>({
  name,
  control,
  disabled,
}: TermsCheckboxProps<T>) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field, fieldState: { error } }) => (
        <Stack>
          <FormControlLabel
            control={
              <Checkbox
                {...field}
                checked={field.value || false}
                disabled={disabled}
                size="small"
                sx={{ cursor: 'pointer' }}
              />
            }
            label={
              <Typography variant="body2" color="text.secondary">
                I agree to the{' '}
                <Link
                  component={RouterLink}
                  to="/terms"
                  sx={{ cursor: 'pointer' }}
                  onClick={(e) => e.stopPropagation()}
                >
                  Terms of Service
                </Link>{' '}
                and{' '}
                <Link
                  component={RouterLink}
                  to="/privacy"
                  sx={{ cursor: 'pointer' }}
                  onClick={(e) => e.stopPropagation()}
                >
                  Privacy Policy
                </Link>
              </Typography>
            }
            sx={{ alignItems: 'flex-start', mx: 0 }}
          />
          {error && (
            <Typography variant="caption" color="error" sx={{ ml: 4 }}>
              {error.message}
            </Typography>
          )}
        </Stack>
      )}
    />
  );
}
