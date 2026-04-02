import React, { useState } from 'react';
import {
  TextField as MuiTextField,
  Box,
  IconButton,
  Popper,
  Paper,
  ClickAwayListener,
} from '@mui/material';
import { AccessTime } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface TimePickerProps {
  label?: string;
  value?: string | null;
  onChange?: (time: string | null) => void;
  error?: string;
  helperText?: string;
}

/**
 * TimePicker Component
 * Enterprise-grade time picker for forms
 */
export const TimePicker: React.FC<TimePickerProps> = ({
  label,
  value,
  onChange,
  error,
  helperText,
}) => {
  const [open, setOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const timeOptions: string[] = [];
  for (let hour = 0; hour < 24; hour++) {
    for (let minute = 0; minute < 60; minute += 15) {
      timeOptions.push(`${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`);
    }
  }

  const handleToggle = () => setOpen((prev) => !prev);

  const handleTimeChange = (time: string) => {
    onChange?.(time);
    setOpen(false);
  };

  return (
    <Box sx={{ width: '100%' }}>
      <MuiTextField
        label={label}
        value={value || ''}
        error={!!error}
        helperText={error}
        onFocus={(e) => setAnchorEl(e.currentTarget)}
        inputProps={{
          readOnly: true,
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
        <AccessTime />
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
                p: 1,
                maxHeight: 250,
                overflow: 'auto',
                zIndex: 9999,
              }}
            >
              {timeOptions.map((time) => (
                <Box
                  key={time}
                  onClick={() => handleTimeChange(time)}
                  sx={{
                    px: 2,
                    py: 1,
                    cursor: 'pointer',
                    '&:hover': {
                      backgroundColor: '#F3F4F6',
                    },
                  }}
                >
                  {time}
                </Box>
              ))}
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
