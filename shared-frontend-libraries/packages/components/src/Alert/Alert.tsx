import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Alert as MuiAlert,
  AlertTitle as MuiAlertTitle,
  AlertProps as MuiAlertProps,
  IconButton,
  Collapse,
} from '@mui/material';
import { Close } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type AlertSeverity = 'success' | 'info' | 'warning' | 'error';
export type AlertVariant = 'filled' | 'outlined' | 'standard';

export interface AlertProps extends Omit<MuiAlertProps, 'severity' | 'variant'> {
  severity?: AlertSeverity;
  variant?: AlertVariant;
  title?: string;
  onClose?: () => void;
  closable?: boolean;
}

const severityConfig: Record<AlertSeverity, { color: string; iconBg: string; icon: string }> = {
  success: { color: MANAGEMENT_COLORS.success, iconBg: '#D1FAE5', icon: '✓' },
  info: { color: MANAGEMENT_COLORS.info, iconBg: '#DBEAFE', icon: 'ℹ' },
  warning: { color: MANAGEMENT_COLORS.warning, iconBg: '#FEF3C7', icon: '⚠' },
  error: { color: MANAGEMENT_COLORS.error, iconBg: '#FEE2E2', icon: '✕' },
};

/**
 * Alert Component
 * Enterprise-grade alert with severity levels and dismiss functionality
 */
export const Alert = React.forwardRef<HTMLDivElement, AlertProps>(
  (
    {
      severity = 'info',
      variant = 'standard',
      title,
      onClose,
      closable = false,
      children,
      className,
      ...props
    },
    ref
  ) => {
    const [open, setOpen] = React.useState(true);

    const handleClose = () => {
      setOpen(false);
      onClose?.();
    };

    const config = severityConfig[severity];

    return (
      <AnimatePresence mode="wait">
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.2 }}
          >
            <MuiAlert
              ref={ref}
              severity={severity}
              variant={variant}
              onClose={closable ? handleClose : undefined}
              action={
                closable ? (
                  <IconButton
                    aria-label="close"
                    onClick={handleClose}
                    sx={{ color: config.color }}
                  >
                    <Close />
                  </IconButton>
                ) : undefined
              }
              className={className}
              sx={{
                backgroundColor:
                  variant === 'filled'
                    ? config.color
                    : variant === 'outlined'
                    ? 'transparent'
                    : variant === 'standard'
                    ? `${config.color}10`
                    : config.color,
                color: variant === 'filled' ? '#FFFFFF' : config.color,
                border: variant === 'outlined' ? `1px solid ${config.color}` : 'none',
                borderRadius: '8px',
                '& .MuiAlert-icon': {
                  color: variant === 'filled' ? '#FFFFFF' : config.color,
                },
                '& .MuiAlert-message': {
                  color: variant === 'filled' ? '#FFFFFF' : 'inherit',
                },
              }}
              {...props}
            >
              {title && <MuiAlertTitle>{title}</MuiAlertTitle>}
              {children}
            </MuiAlert>
          </motion.div>
        )}
      </AnimatePresence>
    );
  }
);

Alert.displayName = 'Alert';
