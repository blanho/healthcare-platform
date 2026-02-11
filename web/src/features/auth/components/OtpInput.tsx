

import {
  useRef,
  useState,
  useCallback,
  useEffect,
  type KeyboardEvent,
  type ClipboardEvent,
} from 'react';
import { Box, TextField, Typography, useTheme } from '@mui/material';

interface OtpInputProps {
  length?: number;
  value: string;
  onChange: (value: string) => void;
  onComplete?: (value: string) => void;
  error?: boolean;
  helperText?: string;
  disabled?: boolean;
  autoFocus?: boolean;
}

export function OtpInput({
  length = 6,
  value,
  onChange,
  onComplete,
  error = false,
  helperText,
  disabled = false,
  autoFocus = true,
}: OtpInputProps) {
  const theme = useTheme();
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);
  const [focusedIndex, setFocusedIndex] = useState<number | null>(null);

  const valueArray = value.split('').slice(0, length);
  while (valueArray.length < length) {
    valueArray.push('');
  }

  useEffect(() => {
    if (autoFocus && inputRefs.current[0]) {
      inputRefs.current[0].focus();
    }
  }, [autoFocus]);

  useEffect(() => {
    if (value.length === length && onComplete) {
      onComplete(value);
    }
  }, [value, length, onComplete]);

  const focusInput = useCallback(
    (index: number) => {
      const clampedIndex = Math.max(0, Math.min(index, length - 1));
      inputRefs.current[clampedIndex]?.focus();
    },
    [length],
  );

  const handleChange = useCallback(
    (index: number, digit: string) => {
      if (!/^\d?$/.test(digit)) return;

      const newValue = valueArray.slice();
      newValue[index] = digit;
      const newString = newValue.join('');
      onChange(newString);

      if (digit && index < length - 1) {
        focusInput(index + 1);
      }
    },
    [valueArray, onChange, length, focusInput],
  );

  const handleKeyDown = useCallback(
    (index: number, e: KeyboardEvent<HTMLInputElement>) => {
      if (e.key === 'Backspace') {
        if (!valueArray[index] && index > 0) {

          focusInput(index - 1);
          handleChange(index - 1, '');
        } else {
          handleChange(index, '');
        }
        e.preventDefault();
      } else if (e.key === 'ArrowLeft' && index > 0) {
        focusInput(index - 1);
        e.preventDefault();
      } else if (e.key === 'ArrowRight' && index < length - 1) {
        focusInput(index + 1);
        e.preventDefault();
      }
    },
    [valueArray, handleChange, focusInput, length],
  );

  const handlePaste = useCallback(
    (e: ClipboardEvent<HTMLInputElement>) => {
      e.preventDefault();
      const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, length);
      if (pastedData) {
        onChange(pastedData);
        focusInput(Math.min(pastedData.length, length - 1));
      }
    },
    [onChange, focusInput, length],
  );

  return (
    <Box>
      <Box
        sx={{
          display: 'flex',
          gap: 1,
          justifyContent: 'center',
        }}
      >
        {valueArray.map((digit, index) => (
          <TextField
            key={index}
            inputRef={(el) => {
              inputRefs.current[index] = el;
            }}
            value={digit}
            onChange={(e) => handleChange(index, e.target.value.slice(-1))}
            onKeyDown={(e) => handleKeyDown(index, e as KeyboardEvent<HTMLInputElement>)}
            onPaste={handlePaste}
            onFocus={() => setFocusedIndex(index)}
            onBlur={() => setFocusedIndex(null)}
            disabled={disabled}
            error={error}
            inputProps={{
              maxLength: 1,
              inputMode: 'numeric',
              pattern: '[0-9]*',
              autoComplete: 'one-time-code',
              'aria-label': `Digit ${index + 1}`,
              style: {
                textAlign: 'center',
                fontSize: '1.5rem',
                fontWeight: 600,
                fontFamily: 'monospace',
                padding: '12px 8px',
              },
            }}
            sx={{
              width: 48,
              '& .MuiOutlinedInput-root': {
                borderRadius: 2,
                transition: 'all 0.2s ease',
                backgroundColor:
                  focusedIndex === index ? theme.palette.action.hover : 'transparent',
              },
            }}
          />
        ))}
      </Box>
      {helperText && (
        <Typography
          variant="caption"
          color={error ? 'error' : 'text.secondary'}
          sx={{ display: 'block', textAlign: 'center', mt: 1 }}
        >
          {helperText}
        </Typography>
      )}
    </Box>
  );
}
