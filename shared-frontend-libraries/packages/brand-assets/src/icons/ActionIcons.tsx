import React from 'react';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import SearchIcon from '@mui/icons-material/Search';
import FilterListIcon from '@mui/icons-material/FilterList';
import SortIcon from '@mui/icons-material/Sort';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import DownloadIcon from '@mui/icons-material/Download';
import UploadIcon from '@mui/icons-material/Upload';
import RefreshIcon from '@mui/icons-material/Refresh';
import PrintIcon from '@mui/icons-material/Print';
import ShareIcon from '@mui/icons-material/Share';
import CopyIcon from '@mui/icons-material/ContentCopy';
import PasteIcon from '@mui/icons-material/ContentPaste';
import UndoIcon from '@mui/icons-material/Undo';
import RedoIcon from '@mui/icons-material/Redo';
import ZoomInIcon from '@mui/icons-material/ZoomIn';
import ZoomOutIcon from '@mui/icons-material/ZoomOut';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import LockIcon from '@mui/icons-material/Lock';
import LockOpenIcon from '@mui/icons-material/LockOpen';
import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import BookmarkIcon from '@mui/icons-material/Bookmark';
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder';
import SendIcon from '@mui/icons-material/Send';
import PhoneIcon from '@mui/icons-material/Phone';
import EmailIcon from '@mui/icons-material/Email';

export const ActionIcons = {
  Add: AddIcon,
  Edit: EditIcon,
  Delete: DeleteIcon,
  Save: SaveIcon,
  Cancel: CancelIcon,
  Search: SearchIcon,
  FilterList: FilterListIcon,
  Sort: SortIcon,
  MoreVert: MoreVertIcon,
  MoreHoriz: MoreHorizIcon,
  Download: DownloadIcon,
  Upload: UploadIcon,
  Refresh: RefreshIcon,
  Print: PrintIcon,
  Share: ShareIcon,
  Copy: CopyIcon,
  Paste: PasteIcon,
  Undo: UndoIcon,
  Redo: RedoIcon,
  ZoomIn: ZoomInIcon,
  ZoomOut: ZoomOutIcon,
  Visibility: VisibilityIcon,
  VisibilityOff: VisibilityOffIcon,
  Lock: LockIcon,
  LockOpen: LockOpenIcon,
  Check: CheckIcon,
  Close: CloseIcon,
  Star: StarIcon,
  StarBorder: StarBorderIcon,
  Bookmark: BookmarkIcon,
  BookmarkBorder: BookmarkBorderIcon,
  Send: SendIcon,
  Phone: PhoneIcon,
  Email: EmailIcon,
} as const;

export type ActionIconKey = keyof typeof ActionIcons;

export function getActionIcon(key: ActionIconKey) {
  return ActionIcons[key];
}
