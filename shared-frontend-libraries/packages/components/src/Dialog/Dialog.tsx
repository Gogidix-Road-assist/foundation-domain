import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Dialog as MuiDialog,
  DialogActions as MuiDialogActions,
  DialogContent as MuiDialogContent,
  DialogContentText as MuiDialogContentText,
  DialogTitle as MuiDialogTitle,
  DialogProps as MuiDialogProps,
  Button,
  IconButton,
  Box,
  Typography,
} from '@mui/material';
import { Close } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type DialogSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl' | 'full';

export interface DialogProps extends Omit<MuiDialogProps, 'maxWidth' | 'fullScreen'> {
  open: boolean;
  onClose: () => void;
  title?: React.ReactNode;
  size?: DialogSize;
  children?: React.ReactNode;
  actions?: React.ReactNode;
  showCloseButton?: boolean;
}

export interface DialogConfirmProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  severity?: 'info' | 'warning' | 'error' | 'success';
}

/**
 * Dialog Component
 * Enterprise-grade modal dialog with animations
 */
export const Dialog = React.forwardRef<HTMLDivElement, DialogProps>(
  (
    {
      open,
      onClose,
      title,
      size = 'md',
      children,
      actions,
      showCloseButton = true,
      className,
      ...props
    },
    ref
  ) => {
    return (
      <MuiDialog
        ref={ref}
        open={open}
        onClose={onClose}
        maxWidth={size === 'full' ? false : size}
        fullWidth
        PaperProps={{
          component: motion.div,
          initial: { opacity: 0, scale: 0.9, y: 20 },
          animate: { opacity: 1, scale: 1, y: 0 },
          exit: { opacity: 0, scale: 0.9, y: 20 },
          transition: { duration: 0.2 },
          sx: {
            borderRadius: '16px',
            overflow: 'hidden',
          },
        }}
        className={className}
        {...props}
      >
        {title && (
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '24px',
              borderBottom: '1px solid #E5E7EB',
            }}
          >
            <MuiDialogTitle sx={{ padding: 0, fontSize: '1.25rem', fontWeight: 600, color: '#111827' }}>
              {title}
            </MuiDialogTitle>
            {showCloseButton && (
              <IconButton onClick={onClose} size="small">
                <Close />
              </IconButton>
            )}
          </Box>
        )}
        <MuiDialogContent sx={{ padding: '24px' }}>{children}</MuiDialogContent>
        {actions && (
          <MuiDialogActions sx={{ padding: '16px 24px', borderTop: '1px solid #E5E7EB' }}>
            {actions}
          </MuiDialogActions>
        )}
      </MuiDialog>
    );
  }
);

Dialog.displayName = 'Dialog';

/**
 * DialogContent Component
 * Content wrapper for Dialog
 */
export const DialogContent = MuiDialogContent;

/**
 * DialogContentText Component
 * Text content for Dialog
 */
export const DialogContentText = MuiDialogContentText;

/**
 * DialogTitle Component
 * Title component for Dialog
 */
export const DialogTitle = MuiDialogTitle;

/**
 * DialogActions Component
 * Action buttons for Dialog
 */
export const DialogActions = MuiDialogActions;

/**
 * DialogConfirm Component
 * Pre-built confirmation dialog
 */
export const DialogConfirm: React.FC<DialogConfirmProps> = ({
  open,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  severity = 'warning',
}) => {
  const severityColors = {
    info: MANAGEMENT_COLORS.info,
    warning: MANAGEMENT_COLORS.warning,
    error: MANAGEMENT_COLORS.error,
    success: MANAGEMENT_COLORS.success,
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      title={title}
      size="sm"
      actions={
        <>
          <Button variant="ghost" onClick={onClose}>
            {cancelText}
          </Button>
          <Button
            variant="primary"
            onClick={() => {
              onConfirm();
              onClose();
            }}
            style={{ backgroundColor: severityColors[severity] }}
          >
            {confirmText}
          </Button>
        </>
      }
    >
      <DialogContentText>{message}</DialogContentText>
    </Dialog>
  );
};
