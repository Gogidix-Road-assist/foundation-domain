import React from 'react';
import { motion } from 'framer-motion';
import {
  Checkbox as MuiCheckbox,
  CheckboxProps as MuiCheckboxProps,
  FormControlLabel,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type CheckboxSize = 'small' | 'medium';
export type CheckboxColor = 'primary' | 'secondary' | 'error';

export interface CheckboxProps extends Omit<MuiCheckboxProps, 'size' | 'color'> {
  size?: CheckboxSize;
  color?: CheckboxColor;
  label?: string;
  labelPlacement?: 'start' | 'end' | 'top' | 'bottom';
}

const colorMap: Record<CheckboxColor, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  error: MANAGEMENT_COLORS.error,
};

/**
 * Checkbox Component
 * Enterprise-grade checkbox with variants and animations
 */
export const Checkbox = React.forwardRef<HTMLInputElement, CheckboxProps>(
  (
    {
      size = 'medium',
      color = 'primary',
      label,
      labelPlacement = 'end',
      checked,
      onChange,
      disabled = false,
      indeterminate = false,
      className,
      ...props
    },
    ref
  ) => {
    const checkboxElement = (
      <MuiCheckbox
        ref={ref}
        size={size}
        checked={checked}
        onChange={onChange}
        disabled={disabled}
        indeterminate={indeterminate}
        className={className}
        sx={{
          color: '#9CA3AF',
          '&.Mui-checked': {
            color: colorMap[color],
          },
          '&.Mui-disabled': {
            opacity: 0.5,
          },
        }}
        {...props}
      />
    );

    const content = label ? (
      <FormControlLabel
        control={checkboxElement}
        label={label}
        labelPlacement={labelPlacement}
        sx={{
          margin: 0,
          '& .MuiFormControlLabel-label': {
            fontSize: size === 'small' ? '0.875rem' : '1rem',
            color: '#374151',
          },
        }}
      />
    ) : checkboxElement;

    return <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>{content}</motion.div>;
  }
);

Checkbox.displayName = 'Checkbox';
