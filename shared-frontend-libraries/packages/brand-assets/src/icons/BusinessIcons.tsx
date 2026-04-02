import React from 'react';
import BusinessIcon from '@mui/icons-material/Business';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import ReceiptIcon from '@mui/icons-material/Receipt';
import PointOfSaleIcon from '@mui/icons-material/PointOfSale';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import InventoryIcon from '@mui/icons-material/Inventory';
import StoreIcon from '@mui/icons-material/Store';
import LocalMallIcon from '@mui/icons-material/LocalMall';
import AssessmentIcon from '@mui/icons-material/Assessment';
import PieChartIcon from '@mui/icons-material/PieChart';
import BarChartIcon from '@mui/icons-material/BarChart';
import TimelineIcon from '@mui/icons-material/Timeline';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import ShowChartIcon from '@mui/icons-material/ShowChart';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import RequestQuoteIcon from '@mui/icons-material/RequestQuote';
import DescriptionIcon from '@mui/icons-material/Description';
import GavelIcon from '@mui/icons-material/Gavel';
import BalanceIcon from '@mui/icons-material/Balance';
import PaidIcon from '@mui/icons-material/Paid';
import PaymentsIcon from '@mui/icons-material/Payments';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import CorporateFareIcon from '@mui/icons-material/CorporateFare';
import DomainIcon from '@mui/icons-material/Domain';
import GroupsIcon from '@mui/icons-material/Groups';
import PeopleIcon from '@mui/icons-material/People';
import PersonIcon from '@mui/icons-material/Person';
import BadgeIcon from '@mui/icons-material/Badge';

export const BusinessIcons = {
  Business: BusinessIcon,
  AccountBalance: AccountBalanceIcon,
  Receipt: ReceiptIcon,
  PointOfSale: PointOfSaleIcon,
  ShoppingBag: ShoppingBagIcon,
  ShoppingCart: ShoppingCartIcon,
  AttachMoney: AttachMoneyIcon,
  CreditCard: CreditCardIcon,
  AccountBalanceWallet: AccountBalanceWalletIcon,
  Inventory: InventoryIcon,
  Store: StoreIcon,
  LocalMall: LocalMallIcon,
  Assessment: AssessmentIcon,
  PieChart: PieChartIcon,
  BarChart: BarChartIcon,
  Timeline: TimelineIcon,
  TrendingUp: TrendingUpIcon,
  TrendingDown: TrendingDownIcon,
  ShowChart: ShowChartIcon,
  ReceiptLong: ReceiptLongIcon,
  RequestQuote: RequestQuoteIcon,
  Description: DescriptionIcon,
  Gavel: GavelIcon,
  Balance: BalanceIcon,
  Paid: PaidIcon,
  Payments: PaymentsIcon,
  AccountTree: AccountTreeIcon,
  CorporateFare: CorporateFareIcon,
  Domain: DomainIcon,
  Groups: GroupsIcon,
  People: PeopleIcon,
  Person: PersonIcon,
  Badge: BadgeIcon,
} as const;

export type BusinessIconKey = keyof typeof BusinessIcons;

export function getBusinessIcon(key: BusinessIconKey) {
  return BusinessIcons[key];
}
