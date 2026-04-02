import React from 'react';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import ErrorIcon from '@mui/icons-material/Error';
import WarningIcon from '@mui/icons-material/Warning';
import InfoIcon from '@mui/icons-material/Info';
import HelpIcon from '@mui/icons-material/Help';
import CircleIcon from '@mui/icons-material/Circle';
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked';
import ScheduleIcon from '@mui/icons-material/Schedule';
import PendingIcon from '@mui/icons-material/Pending';
import BlockIcon from '@mui/icons-material/Block';
import PanoramaFishEyeIcon from '@mui/icons-material/PanoramaFishEye';
import ToggleOnIcon from '@mui/icons-material/ToggleOn';
import ToggleOffIcon from '@mui/icons-material/ToggleOff';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import CloudDoneIcon from '@mui/icons-material/CloudDone';
import CloudOffIcon from '@mui/icons-material/CloudOff';
import SyncIcon from '@mui/icons-material/Sync';
import SyncDisabledIcon from '@mui/icons-material/SyncDisabled';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import TimelapseIcon from '@mui/icons-material/Timelapse';
import UpdateIcon from '@mui/icons-material/Update';
import NewReleasesIcon from '@mui/icons-material/NewReleases';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import NotificationsOffIcon from '@mui/icons-material/NotificationsOff';
import VerifiedIcon from '@mui/icons-material/Verified';
import GppGoodIcon from '@mui/icons-material/GppGood';
import GppMaybeIcon from '@mui/icons-material/GppMaybe';
import GppBadIcon from '@mui/icons-material/GppBad';

export const StatusIcons = {
  CheckCircle: CheckCircleIcon,
  Cancel: CancelIcon,
  Error: ErrorIcon,
  Warning: WarningIcon,
  Info: InfoIcon,
  Help: HelpIcon,
  Circle: CircleIcon,
  RadioButtonChecked: RadioButtonCheckedIcon,
  RadioButtonUnchecked: RadioButtonUncheckedIcon,
  Schedule: ScheduleIcon,
  Pending: PendingIcon,
  Block: BlockIcon,
  PanoramaFishEye: PanoramaFishEyeIcon,
  ToggleOn: ToggleOnIcon,
  ToggleOff: ToggleOffIcon,
  ThumbUp: ThumbUpIcon,
  ThumbDown: ThumbDownIcon,
  CloudDone: CloudDoneIcon,
  CloudOff: CloudOffIcon,
  Sync: SyncIcon,
  SyncDisabled: SyncDisabledIcon,
  HourglassEmpty: HourglassEmptyIcon,
  Timelapse: TimelapseIcon,
  Update: UpdateIcon,
  NewReleases: NewReleasesIcon,
  NotificationsActive: NotificationsActiveIcon,
  NotificationsNone: NotificationsNoneIcon,
  NotificationsOff: NotificationsOffIcon,
  Verified: VerifiedIcon,
  GppGood: GppGoodIcon,
  GppMaybe: GppMaybeIcon,
  GppBad: GppBadIcon,
} as const;

export type StatusIconKey = keyof typeof StatusIcons;

export function getStatusIcon(key: StatusIconKey) {
  return StatusIcons[key];
}

// Status colors mapping
export const StatusColors = {
  success: '#10B981',
  error: '#EF4444',
  warning: '#F59E0B',
  info: '#3B82F6',
  neutral: '#6B7280',
} as const;

export type StatusColorKey = keyof typeof StatusColors;

export function getStatusColor(key: StatusColorKey) {
  return StatusColors[key];
}
