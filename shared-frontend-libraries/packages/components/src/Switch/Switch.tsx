import React from 'react';
import { motion } from 'framer-motion';
import {
  Switch as MuiSwitch,
  SwitchProps as MuiSwitchProps,
  FormControlLabel,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type SwitchSize = 'small' | 'medium';
export type SwitchColor = 'primary' | 'secondary' | 'error';

export interface SwitchProps extends Omit<MuiSwitchProps, 'size' | 'color'> {
  size?: SwitchSize;
  color?: SwitchColor;
  label?: string;
  labelPlacement?: 'start' | 'end' | 'top' | 'bottom';
}

const colorMap: Record<SwitchColor, { checked: string; track: string }> = {
  primary: { checked: MANAGEMENT_COLORS.primary, track: '#93C5FD' },
  secondary: { checked: MANAGEMENT_COLORS.secondary, track: '#6B7280' },
  error: { checked: MANAGEMENT_COLORS.error, track: '#FCA5A5' },
};

/**
 * Switch Component
 * Enterprise-grade toggle switch with variants and animations
 */
export const Switch = React.forwardRef<HTMLInputElement, SwitchProps>(
  (
    {
      size = 'medium',
      color = 'primary',
      label,
      labelPlacement = 'end',
      checked,
      onChange,
      disabled = false,
      className,
      ...props
    },
    ref
  ) => {
    const switchElement = (
      <MuiSwitch
        ref={ref}
        size={size}
        checked={checked}
        onChange={onChange}
        disabled={disabled}
        className={className}
        sx={{
          '& .MuiSwitch-switchBase.Mui-checked': {
            color: colorMap[color].checked,
          },
          '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
            backgroundColor: colorMap[color].track,
          },
          '& .MuiSwitch-disabled': {
            opacity: 0.5,
          },
        }}
        {...props}
      />
    );

    const content = label ? (
      <FormControlLabel
        control={switchElement}
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
    ) : switchElement;

    return <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>{content}</motion.div>;
  }
);

Switch.displayName = 'Switch';
