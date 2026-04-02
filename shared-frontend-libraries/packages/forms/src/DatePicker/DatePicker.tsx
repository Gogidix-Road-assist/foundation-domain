import React, { useState } from 'react';
import {
  TextField as MuiTextField,
  Box,
  IconButton,
  Popper,
  Paper,
  ClickAwayListener,
} from '@mui/material';
import { CalendarToday } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface DatePickerProps {
  label?: string;
  value?: Date | null;
  onChange?: (date: Date | null) => void;
  error?: string;
  helperText?: string;
  minDate?: Date;
  maxDate?: Date;
}

/**
 * DatePicker Component
 * Enterprise-grade date picker for forms
 */
export const DatePicker: React.FC<DatePickerProps> = ({
  label,
  value,
  onChange,
  error,
  helperText,
  minDate,
  maxDate,
}) => {
  const [open, setOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleToggle = () => setOpen((prev) => !prev);

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = e.target.value ? new Date(e.target.value) : null;
    onChange?.(newDate);
    setOpen(false);
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
  };

  return (
    <Box sx={{ width: '100%' }}>
      <MuiTextField
        label={label}
        value={value ? formatDate(value) : ''}
        onChange={handleDateChange}
        error={!!error}
        helperText={error}
        onFocus={(e) => setAnchorEl(e.currentTarget)}
        inputProps={{
          readOnly: true,
          min: minDate?.toISOString().split('T')[0],
          max: maxDate?.toISOString().split('T')[0],
        }}
        sx={{
          '& .MuiOutlinedInput-root': {
            paddingRight: '40px',
          },
        }}
      />
      <IconButton
        size="small"
        sx={{ position: 'absolute', right: 8, top: 18, p: 0.5 }}
        onClick={handleToggle}
      >
        <CalendarToday />
      </IconButton>
      {open && anchorEl && (
        <ClickAwayListener onClickAway={() => setOpen(false)}>
          <Popper
            open={open}
            anchorEl={anchorEl}
            placement="bottom-start"
          >
            <Paper
              elevation={3}
              sx={{
                p: 2,
                maxHeight: 300,
                overflow: 'auto',
                zIndex: 9999,
              }}
            >
              <input
                type="date"
                value={value ? value.toISOString().split('T')[0] : ''}
                onChange={handleDateChange}
                min={minDate?.toISOString().split('T')[0]}
                max={maxDate?.toISOString().split('T')[0]}
                style={{
                  width: '100%',
                  padding: '8px',
                  border: `1px solid ${error ? MANAGEMENT_COLORS.error : '#E5E7EB'}`,
                  borderRadius: '6px',
                }}
              />
            </Paper>
          </Popper>
        </ClickAwayListener>
      )}
      {helperText && !error && (
        <Box component="span" sx={{ ml: '14px', mt: 0.5, color: '#6B7280', fontSize: '0.75rem' }}>
          {helperText}
        </Box>
      )}
    </Box>
  );
};
