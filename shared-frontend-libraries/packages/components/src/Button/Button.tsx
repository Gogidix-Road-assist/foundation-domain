import React from 'react';
import { motion } from 'framer-motion';
import {
  Button as MuiButton,
  ButtonProps as MuiButtonProps,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'destructive' | 'link';
export type ButtonSize = 'small' | 'medium' | 'large';

export interface ButtonProps extends Omit<MuiButtonProps, 'variant' | 'color' | 'size'> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  href?: string;
}

const variantColors: Record<ButtonVariant, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  ghost: 'transparent',
  destructive: MANAGEMENT_COLORS.error,
  link: 'transparent',
};

const sizeStyles: Record<ButtonSize, { padding: string; fontSize: string }> = {
  small: { padding: '6px 12px', fontSize: '0.75rem' },
  medium: { padding: '8px 16px', fontSize: '0.875rem' },
  large: { padding: '12px 24px', fontSize: '1rem' },
};

/**
 * Button Component
 * Enterprise-grade button with variants, sizes, and 60fps animations
 */
export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'medium',
      children,
      href,
      disabled = false,
      onClick,
      className,
      ...props
    },
    ref
  ) => {
    const buttonContent = (
      <MuiButton
        ref={ref}
        disabled={disabled}
        onClick={onClick}
        href={href}
        className={className}
        sx={{
          backgroundColor: variantColors[variant],
          color: variant === 'primary' || variant === 'destructive' ? '#FFFFFF' : 'inherit',
          border: variant === 'ghost' ? '1px solid #E5E7EB' : 'none',
          borderRadius: '8px',
          fontWeight: 500,
          textTransform: 'none',
          ...sizeStyles[size],
          '&:hover': {
            backgroundColor:
              variant === 'primary' ? MANAGEMENT_COLORS.primaryHover :
              variant === 'ghost' ? '#F3F4F6' :
              variant === 'destructive' ? '#B91C1C' :
              variantColors[variant],
          },
          '&:disabled': {
            opacity: 0.5,
            cursor: 'not-allowed',
          },
          '&:focus-visible': {
            outline: '2px solid #0066CC',
            outlineOffset: '2px',
          },
        }}
        {...props}
      >
        {children}
      </MuiButton>
    );

    if (href && !disabled) {
      return <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>{buttonContent}</motion.div>;
    }

    return (
      <motion.button
        ref={ref as any}
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        transition={{ duration: 0.1 }}
      >
        {buttonContent}
      </motion.button>
    );
  }
);

Button.displayName = 'Button';
