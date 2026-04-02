import React from 'react';
import { motion } from 'framer-motion';
import {
  Chip as MuiChip,
  ChipProps as MuiChipProps,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ChipColor = 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info' | 'default';
export type ChipSize = 'small' | 'medium';
export type ChipVariant = 'filled' | 'outlined';

export interface ChipProps extends Omit<MuiChipProps, 'color' | 'size' | 'variant'> {
  color?: ChipColor;
  size?: ChipSize;
  variant?: ChipVariant;
  onClick?: () => void;
  onDelete?: () => void;
}

const colorMap: Record<ChipColor, { filled: string; outlined: string; text: string }> = {
  primary: { filled: MANAGEMENT_COLORS.primary, outlined: MANAGEMENT_COLORS.primary, text: '#FFFFFF' },
  secondary: { filled: MANAGEMENT_COLORS.secondary, outlined: MANAGEMENT_COLORS.secondary, text: '#FFFFFF' },
  success: { filled: MANAGEMENT_COLORS.success, outlined: MANAGEMENT_COLORS.success, text: '#FFFFFF' },
  warning: { filled: MANAGEMENT_COLORS.warning, outlined: MANAGEMENT_COLORS.warning, text: '#000000' },
  error: { filled: MANAGEMENT_COLORS.error, outlined: MANAGEMENT_COLORS.error, text: '#FFFFFF' },
  info: { filled: MANAGEMENT_COLORS.info, outlined: MANAGEMENT_COLORS.info, text: '#FFFFFF' },
  default: { filled: '#F3F4F6', outlined: '#9CA3AF', text: '#374151' },
};

/**
 * Chip Component
 * Enterprise-grade chip for tags, filters, and status indicators
 */
export const Chip = React.forwardRef<HTMLDivElement, ChipProps>(
  (
    {
      color = 'default',
      size = 'medium',
      variant = 'filled',
      onClick,
      onDelete,
      className,
      children,
      icon,
      avatar,
      ...props
    },
    ref
  ) => {
    const colorConfig = colorMap[color];
    const isClickable = !!onClick;
    const isDeletable = !!onDelete;

    const chipElement = (
      <MuiChip
        ref={ref}
        label={children}
        icon={icon}
        avatar={avatar}
        onClick={onClick}
        onDelete={onDelete}
        size={size}
        className={className}
        sx={{
          backgroundColor: variant === 'filled' ? colorConfig.filled : 'transparent',
          color: variant === 'filled' ? colorConfig.text : colorConfig.outlined,
          border: variant === 'outlined' ? `1px solid ${colorConfig.outlined}` : 'none',
          fontWeight: 500,
          '&:hover': isClickable
            ? {
                backgroundColor:
                  variant === 'filled'
                    ? color === 'warning'
                      ? '#D97706'
                      : `${colorConfig.filled}CC`
                    : `${colorConfig.outlined}1A`,
              }
            : undefined,
          '& .MuiChip-deleteIcon': {
            color: variant === 'filled' ? colorConfig.text : colorConfig.outlined,
            '&:hover': {
              color: MANAGEMENT_COLORS.error,
            },
          },
        }}
        {...props}
      />
    );

    return isClickable ? (
      <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} style={{ display: 'inline-flex' }}>
        {chipElement}
      </motion.div>
    ) : (
      chipElement
    );
  }
);

Chip.displayName = 'Chip';
