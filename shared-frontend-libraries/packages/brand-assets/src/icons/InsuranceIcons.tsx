import React from 'react';
import ShieldIcon from '@mui/icons-material/Shield';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import GppGoodIcon from '@mui/icons-material/GppGood';
import GppMaybeIcon from '@mui/icons-material/GppMaybe';
import GppBadIcon from '@mui/icons-material/GppBad';
import SecurityIcon from '@mui/icons-material/Security';
import LockIcon from '@mui/icons-material/Lock';
import LockOpenIcon from '@mui/icons-material/LockOpen';
import KeyIcon from '@mui/icons-material/Key';
import HttpsIcon from '@mui/icons-material/Https';
import HttpIcon from '@mui/icons-material/Http';
import PrivacyTipIcon from '@mui/icons-material/PrivacyTip';
import PolicyIcon from '@mui/icons-material/Policy';
import ArticleIcon from '@mui/icons-material/Article';
import DescriptionIcon from '@mui/icons-material/Description';
import FolderOpenIcon from '@mui/icons-material/FolderOpen';
import FolderIcon from '@mui/icons-material/Folder';
import AssignmentIcon from '@mui/icons-material/Assignment';
import AssignmentTurnedInIcon from '@mui/icons-material/AssignmentTurnedIn';
import AssignmentLateIcon from '@mui/icons-material/AssignmentLate';
import AssignmentReturnedIcon from '@mui/icons-material/AssignmentReturned';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PendingIcon from '@mui/icons-material/Pending';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import ScheduleIcon from '@mui/icons-material/Schedule';
import EventIcon from '@mui/icons-material/Event';
import DateRangeIcon from '@mui/icons-material/DateRange';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import UpdateIcon from '@mui/icons-material/Update';
import AutorenewIcon from '@mui/icons-material/Autorenew';
import WarningIcon from '@mui/icons-material/Warning';
import ErrorIcon from '@mui/icons-material/Error';
import ReportIcon from '@mui/icons-material/Report';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';
import CampaignIcon from '@mui/icons-material/Campaign';
import AnnouncementIcon from '@mui/icons-material/Announcement';
import NotificationsIcon from '@mui/icons-material/Notifications';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import MarkEmailUnreadIcon from '@mui/icons-material/MarkEmailUnread';
import EmailIcon from '@mui/icons-material/Email';
import PhoneIcon from '@mui/icons-material/Phone';
import SupportAgentIcon from '@mui/icons-material/SupportAgent';
import HeadsetMicIcon from '@mui/icons-material/HeadsetMic';
import LiveHelpIcon from '@mui/icons-material/LiveHelp';
import ForumIcon from '@mui/icons-material/Forum';
import QuestionAnswerIcon from '@mui/icons-material/QuestionAnswer';
import FactCheckIcon from '@mui/icons-material/FactCheck';
import DoneAllIcon from '@mui/icons-material/DoneAll';
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import PlaylistAddCheckIcon from '@mui/icons-material/PlaylistAddCheck';

export const InsuranceIcons = {
  Shield: ShieldIcon,
  VerifiedUser: VerifiedUserIcon,
  GppGood: GppGoodIcon,
  GppMaybe: GppMaybeIcon,
  GppBad: GppBadIcon,
  Security: SecurityIcon,
  Lock: LockIcon,
  LockOpen: LockOpenIcon,
  Key: KeyIcon,
  Https: HttpsIcon,
  Http: HttpIcon,
  PrivacyTip: PrivacyTipIcon,
  Policy: PolicyIcon,
  Article: ArticleIcon,
  Description: DescriptionIcon,
  FolderOpen: FolderOpenIcon,
  Folder: FolderIcon,
  Assignment: AssignmentIcon,
  AssignmentTurnedIn: AssignmentTurnedInIcon,
  AssignmentLate: AssignmentLateIcon,
  AssignmentReturned: AssignmentReturnedIcon,
  CheckCircle: CheckCircleIcon,
  Pending: PendingIcon,
  HourglassEmpty: HourglassEmptyIcon,
  Schedule: ScheduleIcon,
  Event: EventIcon,
  DateRange: DateRangeIcon,
  AccessTime: AccessTimeIcon,
  Update: UpdateIcon,
  Autorenew: AutorenewIcon,
  Warning: WarningIcon,
  Error: ErrorIcon,
  Report: ReportIcon,
  ReportProblem: ReportProblemIcon,
  Campaign: CampaignIcon,
  Announcement: AnnouncementIcon,
  Notifications: NotificationsIcon,
  NotificationsActive: NotificationsActiveIcon,
  MarkEmailRead: MarkEmailReadIcon,
  MarkEmailUnread: MarkEmailUnreadIcon,
  Email: EmailIcon,
  Phone: PhoneIcon,
  SupportAgent: SupportAgentIcon,
  HeadsetMic: HeadsetMicIcon,
  LiveHelp: LiveHelpIcon,
  Forum: ForumIcon,
  QuestionAnswer: QuestionAnswerIcon,
  FactCheck: FactCheckIcon,
  DoneAll: DoneAllIcon,
  PendingActions: PendingActionsIcon,
  PlaylistAddCheck: PlaylistAddCheckIcon,
} as const;

export type InsuranceIconKey = keyof typeof InsuranceIcons;

export function getInsuranceIcon(key: InsuranceIconKey) {
  return InsuranceIcons[key];
}

// Claim status colors
export const ClaimStatusColors = {
  submitted: '#3B82F6',
  underReview: '#F59E0B',
  approved: '#10B981',
  rejected: '#EF4444',
  pending: '#8B5CF6',
  paid: '#10B981',
  processing: '#06B6D4',
} as const;

export type ClaimStatusColorKey = keyof typeof ClaimStatusColors;

export function getClaimStatusColor(key: ClaimStatusColorKey) {
  return ClaimStatusColors[key];
}
