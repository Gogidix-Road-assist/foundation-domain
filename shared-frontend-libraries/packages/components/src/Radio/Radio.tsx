import React from 'react';
import { motion } from 'framer-motion';
import {
  Radio as MuiRadio,
  RadioProps as MuiRadioProps,
  FormControlLabel,
  RadioGroup as MuiRadioGroup,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type RadioSize = 'small' | 'medium';
export type RadioColor = 'primary' | 'secondary' | 'error';

export interface RadioOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface RadioProps extends Omit<MuiRadioProps, 'size' | 'color'> {
  size?: RadioSize;
  color?: RadioColor;
  label?: string;
  value?: string;
}

export interface RadioGroupProps {
  name: string;
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>, value: string) => void;
  options: RadioOption[];
  size?: RadioSize;
  color?: RadioColor;
  row?: boolean;
}

const colorMap: Record<RadioColor, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  error: MANAGEMENT_COLORS.error,
};

/**
 * Radio Component
 * Enterprise-grade radio button with variants and animations
 */
export const Radio = React.forwardRef<HTMLInputElement, RadioProps>(
  (
    {
      size = 'medium',
      color = 'primary',
      label,
      checked,
      onChange,
      disabled = false,
      className,
      ...props
    },
    ref
  ) => {
    const radioElement = (
      <MuiRadio
        ref={ref}
        size={size}
        checked={checked}
        onChange={onChange}
        disabled={disabled}
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
        control={radioElement}
        label={label}
        sx={{
          margin: 0,
          '& .MuiFormControlLabel-label': {
            fontSize: size === 'small' ? '0.875rem' : '1rem',
            color: '#374151',
          },
        }}
      />
    ) : radioElement;

    return <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>{content}</motion.div>;
  }
);

Radio.displayName = 'Radio';

/**
 * RadioGroup Component
 * Group of radio buttons with shared state
 */
export const RadioGroup: React.FC<RadioGroupProps> = ({
  name,
  value,
  onChange,
  options,
  size = 'medium',
  color = 'primary',
  row = false,
}) => {
  return (
    <MuiRadioGroup name={name} value={value} onChange={onChange} row={row}>
      {options.map((option) => (
        <Radio
          key={option.value}
          value={option.value}
          label={option.label}
          size={size}
          color={color}
          disabled={option.disabled}
        />
      ))}
    </MuiRadioGroup>
  );
};
