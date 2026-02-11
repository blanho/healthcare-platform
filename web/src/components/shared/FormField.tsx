import { TextField, FormControl, FormLabel, FormHelperText, type TextFieldProps } from '@mui/material';
import { Controller, type Control, type FieldValues, type Path } from 'react-hook-form';

type FormFieldProps<T extends FieldValues> = Omit<TextFieldProps, 'name'> & {
  name: Path<T>;
  control: Control<T>;
};

export function FormField<T extends FieldValues>({ name, control, ...rest }: FormFieldProps<T>) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field, fieldState: { error } }) => (
        <TextField
          {...field}
          {...rest}
          error={!!error}
          helperText={error?.message ?? rest.helperText}
          fullWidth
          value={field.value ?? ''}
        />
      )}
    />
  );
}

interface FormFieldWrapperProps {
  label?: string;
  required?: boolean;
  error?: string;
  children: React.ReactNode;
}

export function FormFieldWrapper({ label, required, error, children }: FormFieldWrapperProps) {
  return (
    <FormControl fullWidth error={!!error}>
      {label && (
        <FormLabel required={required} sx={{ mb: 0.5, fontSize: '0.875rem', fontWeight: 500 }}>
          {label}
        </FormLabel>
      )}
      {children}
      {error && <FormHelperText error>{error}</FormHelperText>}
    </FormControl>
  );
}
