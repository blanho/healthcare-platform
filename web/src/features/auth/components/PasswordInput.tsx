import { useState } from 'react';
import { TextField, InputAdornment, IconButton, type TextFieldProps } from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import { Controller, type Control, type FieldValues, type Path } from 'react-hook-form';

interface PasswordInputProps<T extends FieldValues> {
  name: Path<T>;
  control: Control<T>;
  label?: string;
  placeholder?: string;
  autoComplete?: string;
  disabled?: boolean;
  size?: TextFieldProps['size'];
  autoFocus?: boolean;
}

export function PasswordInput<T extends FieldValues>({
  name,
  control,
  label = 'Password',
  placeholder,
  autoComplete = 'current-password',
  disabled,
  size = 'medium',
  autoFocus = false,
}: PasswordInputProps<T>) {
  const [showPassword, setShowPassword] = useState(false);

  const handleToggle = () => setShowPassword(!showPassword);

  return (
    <Controller
      name={name}
      control={control}
      render={({ field, fieldState: { error } }) => (
        <TextField
          {...field}
          fullWidth
          label={label}
          placeholder={placeholder}
          type={showPassword ? 'text' : 'password'}
          autoComplete={autoComplete}
          disabled={disabled}
          size={size}
          autoFocus={autoFocus}
          error={!!error}
          helperText={error?.message}
          slotProps={{
            input: {
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    onClick={handleToggle}
                    edge="end"
                    size="small"
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                    tabIndex={-1}
                    sx={{ cursor: 'pointer' }}
                  >
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            },
          }}
          sx={{
            '& .MuiOutlinedInput-root': {
              borderRadius: 2,
            },
          }}
        />
      )}
    />
  );
}
