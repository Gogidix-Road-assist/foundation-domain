import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Drawer as MuiDrawer,
  DrawerProps as MuiDrawerProps,
  Box,
  Toolbar,
  Divider,
  Typography,
  IconButton,
} from '@mui/material';
import { Close, Menu } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';
import { Button } from '../Button';

export type DrawerAnchor = 'left' | 'right' | 'top' | 'bottom';
export type DrawerVariant = 'temporary' | 'permanent' | 'persistent';

export interface DrawerItem {
  label: string;
  icon?: React.ReactNode;
  onClick?: () => void;
  active?: boolean;
  disabled?: boolean;
}

export interface DrawerProps extends Omit<MuiDrawerProps, 'anchor' | 'variant'> {
  open: boolean;
  onClose: () => void;
  anchor?: DrawerAnchor;
  variant?: DrawerVariant;
  title?: string;
  items?: DrawerItem[];
  width?: number;
}

export interface DrawerHeaderProps {
  title?: string;
  onClose?: () => void;
}

/**
 * DrawerHeader Component
 * Header section of the drawer
 */
export const DrawerHeader: React.FC<DrawerHeaderProps> = ({ title, onClose }) => (
  <Box
    sx={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '16px 24px',
      borderBottom: '1px solid #E5E7EB',
    }}
  >
    {title && (
      <Typography variant="h6" sx={{ fontWeight: 600, color: '#111827' }}>
        {title}
      </Typography>
    )}
    {onClose && (
      <IconButton onClick={onClose} size="small">
        <Close />
      </IconButton>
    )}
  </Box>
);

/**
 * Drawer Component
 * Enterprise-grade side drawer with animations
 */
export const Drawer = React.forwardRef<HTMLDivElement, DrawerProps>(
  (
    {
      open,
      onClose,
      anchor = 'left',
      variant = 'temporary',
      title,
      items,
      width = 280,
      children,
      className,
      ...props
    },
    ref
  ) => {
    const isHorizontal = anchor === 'left' || anchor === 'right';

    const slideVariants = {
      left: { x: -width },
      right: { x: width },
      top: { y: -width },
      bottom: { y: width },
    };

    const PaperComponent = motion.div;

    return (
      <MuiDrawer
        ref={ref}
        open={open}
        onClose={onClose}
        anchor={anchor}
        variant={variant === 'temporary' ? 'temporary' : 'persistent'}
        className={className}
        PaperProps={{
          component: PaperComponent,
          initial: { [anchor === 'left' || anchor === 'right' ? 'x' : 'y']: slideVariants[anchor][anchor === 'left' || anchor === 'right' ? 'x' : 'y'] },
          animate: { [anchor === 'left' || anchor === 'right' ? 'x' : 'y']: 0 },
          exit: { [anchor === 'left' || anchor === 'right' ? 'x' : 'y']: slideVariants[anchor][anchor === 'left' || anchor === 'right' ? 'x' : 'y'] },
          transition: { duration: 0.3, ease: 'easeInOut' },
          sx: {
            width: isHorizontal ? width : 'auto',
            height: !isHorizontal ? width : 'auto',
            borderTopRightRadius: anchor === 'left' ? '0' : '16px',
            borderTopLeftRadius: anchor === 'left' ? '16px' : '0',
            borderBottomRightRadius: anchor === 'left' ? '0' : '16px',
            borderBottomLeftRadius: anchor === 'left' ? '16px' : '0',
          },
        }}
        sx={{
          '& .MuiDrawer-paper': {
            backgroundColor: '#FFFFFF',
          },
        }}
        {...props}
      >
        {title && <DrawerHeader title={title} onClose={variant === 'temporary' ? onClose : undefined} />}
        {items && (
          <Box sx={{ py: 2 }}>
            {items.map((item, index) => (
              <Box key={index}>
                <Button
                  variant={item.active ? 'primary' : 'ghost'}
                  onClick={() => {
                    item.onClick?.();
                    if (variant === 'temporary') onClose();
                  }}
                  disabled={item.disabled}
                  fullWidth
                  style={{
                    justifyContent: 'flex-start',
                    backgroundColor: item.active ? MANAGEMENT_COLORS.primary : 'transparent',
                    borderRadius: 0,
                    border: 'none',
                    padding: '12px 24px',
                  }}
                >
                  {item.icon && <Box sx={{ mr: 2 }}>{item.icon}</Box>}
                  {item.label}
                </Button>
              </Box>
            ))}
          </Box>
        )}
        {children}
      </MuiDrawer>
    );
  }
);

Drawer.displayName = 'Drawer';

/**
 * DrawerTrigger Component
 * Button to open the drawer
 */
export const DrawerTrigger: React.FC<{ onClick: () => void; label?: string; icon?: React.ReactNode }> = ({
  onClick,
  label,
  icon = <Menu />,
}) => (
  <IconButton onClick={onClick} sx={{ color: '#374151' }}>
    {icon}
  </IconButton>
);
