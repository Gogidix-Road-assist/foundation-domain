// Main icon library exports
export * from './NavigationIcons';
export * from './ActionIcons';
export * from './StatusIcons';
export * from './BusinessIcons';
export * from './MapIcons';
export * from './VehicleIcons';
export * from './InsuranceIcons';

// Convenience re-exports
export {
  NavigationIcons,
  getNavigationIcon,
  type NavigationIconKey,
} from './NavigationIcons';

export {
  ActionIcons,
  getActionIcon,
  type ActionIconKey,
} from './ActionIcons';

export {
  StatusIcons,
  getStatusIcon,
  StatusColors,
  getStatusColor,
  type StatusIconKey,
  type StatusColorKey,
} from './StatusIcons';

export {
  BusinessIcons,
  getBusinessIcon,
  type BusinessIconKey,
} from './BusinessIcons';

export {
  MapIcons,
  getMapIcon,
  type MapIconKey,
} from './MapIcons';

export {
  VehicleIcons,
  getVehicleIcon,
  VehicleStatusColors,
  getVehicleStatusColor,
  type VehicleIconKey,
  type VehicleStatusColorKey,
} from './VehicleIcons';

export {
  InsuranceIcons,
  getInsuranceIcon,
  ClaimStatusColors,
  getClaimStatusColor,
  type InsuranceIconKey,
  type ClaimStatusColorKey,
} from './InsuranceIcons';
