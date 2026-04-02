import React, { useState } from 'react';
import { Box, Drawer, List, ListItem, ListItemButton, ListItemText, ListItemIcon, Collapse, IconButton } from '@mui/material';
import {
  ExpandLess,
  ExpandMore,
  Dashboard,
  Settings,
  AccountTree,
  Business,
  Assessment,
  Description,
} from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type SidebarItem = {
  id: string;
  label: string;
  icon: React.ReactNode;
  path?: string;
  children?: SidebarItem[];
};

export interface SidebarProps {
  items: SidebarItem[];
  collapsed?: boolean;
  onToggle?: () => void;
  onItemClick?: (item: SidebarItem) => void;
  width?: number;
}

/**
 * Sidebar Component
 * Enterprise-grade navigation sidebar
 */
export const Sidebar: React.FC<SidebarProps> = ({
  items,
  collapsed = false,
  onToggle,
  onItemClick,
  width = 250,
}) => {
  const [expandedItems, setExpandedItems] = useState<Set<string>>(new Set());

  const handleToggle = (itemId: string) => {
    setExpandedItems((prev) => {
      const next = new Set(prev);
      if (next.has(itemId)) {
        next.delete(itemId);
      } else {
        next.add(itemId);
      }
      return next;
    });
  };

  const handleItemClick = (item: SidebarItem) => {
    onItemClick?.(item);
  };

  return (
    <Drawer
      variant="permanent"
      open={!collapsed}
      sx={{
        width: collapsed ? 64 : width,
        '& .MuiDrawer-paper': {
          backgroundColor: '#FFFFFF',
          borderRight: '1px solid #E5E7EB',
          borderLeft: 'none',
          borderLeft: collapsed ? 'none' : '1px solid #E5E7EB',
        },
      }}
    >
      <Box sx={{ overflow: 'auto', py: 2 }}>
        <List component="nav" disablePadding>
          {items.map((item) => (
            <React.Fragment key={item.id}>
              <ListItem
                disablePadding
                sx={{ display: 'block' }}
                onClick={() => {
                  if (item.children) {
                    handleToggle(item.id);
                  } else {
                    handleItemClick(item);
                  }
                }}
              >
                <ListItemButton
                  sx={{
                    minHeight: 48,
                    justifyContent: collapsed ? 'center' : 'flex-start',
                    px: collapsed ? 0 : 2,
                    borderRadius: collapsed ? 0 : 1,
                    mx: collapsed ? 0 : 1,
                    '&.Mui-selected': {
                      backgroundColor: `${MANAGEMENT_COLORS.primary}15`,
                      '&:hover': {
                        backgroundColor: `${MANAGEMENT_COLORS.primary}25`,
                      },
                    },
                    '&:hover': {
                      backgroundColor: '#F3F4F6',
                    },
                  }}
                >
                  <ListItemIcon
                    sx={{
                      minWidth: collapsed ? undefined : 48,
                      color: '#6B7280',
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  {!collapsed && (
                    <ListItemText
                      primary={item.label}
                      sx={{
                        color: '#374151',
                        fontWeight: 500,
                      }}
                    />
                  )}
                  {item.children && !collapsed && (
                    <IconButton
                      edge="end"
                      sx={{
                        transform: expandedItems.has(item.id) ? 'rotate(180deg)' : 'rotate(0deg)',
                        transition: 'transform 0.2s',
                      }}
                    >
                      {expandedItems.has(item.id) ? <ExpandLess /> : <ExpandMore />}
                    </IconButton>
                  )}
                </ListItemButton>
              </ListItem>
              {item.children && (collapsed ? null : (
                <Collapse in={expandedItems.has(item.id)} timeout="auto" unmountOnExit>
                  <Box sx={{ pl: 4 }}>
                    {item.children.map((child) => (
                      <ListItem key={child.id} disablePadding sx={{ display: 'block' }}>
                        <ListItemButton
                          onClick={() => handleItemClick(child)}
                          sx={{
                            minHeight: 40,
                            px: 2,
                            borderRadius: 1,
                            '&.Mui-selected': {
                              backgroundColor: `${MANAGEMENT_COLORS.primary}15`,
                            },
                            '&:hover': {
                              backgroundColor: '#F3F4F6',
                            },
                          }}
                        >
                          <ListItemIcon sx={{ minWidth: 36 }}>{child.icon}</ListItemIcon>
                          <ListItemText
                            primary={child.label}
                            sx={{ color: '#374151', fontSize: '0.875rem' }}
                          />
                        </ListItemButton>
                      </ListItem>
                    ))}
                  </Box>
                </Collapse>
              ))}
            </React.Fragment>
          ))}
        </List>
      </Box>
    </Drawer>
  );
};
