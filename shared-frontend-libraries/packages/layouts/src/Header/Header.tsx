import React from 'react';
import { AppBar, AppBarProps, Toolbar, Typography, IconButton, Box } from '@mui/material';
import { Menu } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface HeaderProps extends Omit<AppBarProps, 'children'> {
  title?: string;
  onMenuClick?: () => void;
  logo?: React.ReactNode;
  actions?: React.ReactNode;
  elevation?: number;
}

/**
 * Header Component
 * Enterprise-grade application header
 */
export const Header: React.FC<HeaderProps> = ({
  title,
  onMenuClick,
  logo,
  actions,
  elevation = 1,
  ...props
}) => {
  return (
    <AppBar
      position="sticky"
      elevation={elevation}
      sx={{
        backgroundColor: '#FFFFFF',
        color: '#111827',
        borderBottom: '1px solid #E5E7EB',
      }}
      {...props}
    >
      <Toolbar sx={{ minHeight: 64 }}>
        {onMenuClick && (
          <IconButton
            edge="start"
            onClick={onMenuClick}
            sx={{ mr: 2, display: { sm: 'none', xs: 'flex' } }}
          >
            <Menu />
          </IconButton>
        )}
        {logo && <Box sx={{ mr: 2 }}>{logo}</Box>}
        {title && (
          <Typography
            variant="h6"
            component="div"
            sx={{
              flexGrow: 1,
              fontWeight: 600,
              color: MANAGEMENT_COLORS.primary,
            }}
          >
            {title}
          </Typography>
        )}
        {actions && <Box sx={{ ml: 'auto' }}>{actions}</Box>}
      </Toolbar>
    </AppBar>
  );
};
