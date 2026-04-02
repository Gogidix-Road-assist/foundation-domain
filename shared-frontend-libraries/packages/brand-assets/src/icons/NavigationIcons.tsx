import React from 'react';
import HomeIcon from '@mui/icons-material/Home';
import DashboardIcon from '@mui/icons-material/Dashboard';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import SettingsIcon from '@mui/icons-material/Settings';
import LogoutIcon from '@mui/icons-material/Logout';
import AppsIcon from '@mui/icons-material/Apps';
import FolderIcon from '@mui/icons-material/Folder';
import DescriptionIcon from '@mui/icons-material/Description';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';

export const NavigationIcons = {
  Home: HomeIcon,
  Dashboard: DashboardIcon,
  Menu: MenuIcon,
  ChevronLeft: ChevronLeftIcon,
  ChevronRight: ChevronRightIcon,
  ExpandMore: ExpandMoreIcon,
  ExpandLess: ExpandLessIcon,
  Settings: SettingsIcon,
  Logout: LogoutIcon,
  Apps: AppsIcon,
  Folder: FolderIcon,
  Description: DescriptionIcon,
  InsertDriveFile: InsertDriveFileIcon,
} as const;

export type NavigationIconKey = keyof typeof NavigationIcons;

export function getNavigationIcon(key: NavigationIconKey) {
  return NavigationIcons[key];
}
