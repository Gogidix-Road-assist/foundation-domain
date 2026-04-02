import React from 'react';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import TwoWheelerIcon from '@mui/icons-material/TwoWheeler';
import LocalTaxiIcon from '@mui/icons-material/LocalTaxi';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import AirportShuttleIcon from '@mui/icons-material/AirportShuttle';
import DirectionsBusIcon from '@mui/icons-material/DirectionsBus';
import TrainIcon from '@mui/icons-material/Train';
import TramIcon from '@mui/icons-material/Tram';
import SubwayIcon from '@mui/icons-material/Subway';
import AnchorIcon from '@mui/icons-material/Anchor';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import FlightIcon from '@mui/icons-material/Flight';
import AirlineSeatReclineNormalIcon from '@mui/icons-material/AirlineSeatReclineNormal';
import BuildIcon from '@mui/icons-material/Build';
import WrenchIcon from '@mui/icons-material/Wrench';
import ConstructionIcon from '@mui/icons-material/Construction';
import CarRepairIcon from '@mui/icons-material/CarRepair';
import LocalGasStationIcon from '@mui/icons-material/LocalGasStation';
import EvStationIcon from '@mui/icons-material/EvStation';
import BatteryChargingFullIcon from '@mui/icons-material/BatteryChargingFull';
import OilBarrelIcon from '@mui/icons-material/OilBarrel';
import NoCrashIcon from '@mui/icons-material/NoCrash';
import CarCrashIcon from '@mui/icons-material/CarCrash';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import AmbulanceIcon from '@mui/icons-material/Ambulance';
import PoliceCarIcon from '@mui/icons-material/PoliceCar';
import FireTruckIcon from '@mui/icons-material/LocalFireDepartment';
import LocalShippingOutlinedIcon from '@mui/icons-material/LocalShippingOutlined';
import PickupTruckIcon from '@mui/icons-material/PickupTruck';
import TruckIcon from '@mui/icons-material/Truck';
import AirportShuttleOutlinedIcon from '@mui/icons-material/AirportShuttleOutlined';
import AirportShuttleOutlined from AirportShuttleOutlinedIcon;
import ElectricCarIcon from '@mui/icons-material/ElectricCar';
import ElectricScooterIcon from '@mui/icons-material/ElectricScooter';
import PedalBikeIcon from '@mui/icons-material/PedalBike';
import SnowmobileIcon from '@mui/icons-material/AcUnit';
import TwoWheelerOutlinedIcon from '@mui/icons-material/TwoWheelerOutlined';
import TwoWheelerOutlined from TwoWheelerOutlinedIcon;

export const VehicleIcons = {
  Car: DirectionsCarIcon,
  TwoWheeler: TwoWheelerIcon,
  Taxi: LocalTaxiIcon,
  Truck: LocalShippingIcon,
  Van: AirportShuttleIcon,
  Bus: DirectionsBusIcon,
  Train: TrainIcon,
  Tram: TramIcon,
  Subway: SubwayIcon,
  Ship: AnchorIcon,
  Boat: DirectionsBoatIcon,
  Plane: FlightIcon,
  Seat: AirlineSeatReclineNormalIcon,
  Service: BuildIcon,
  Wrench: WrenchIcon,
  Construction: ConstructionIcon,
  Repair: CarRepairIcon,
  GasStation: LocalGasStationIcon,
  EvStation: EvStationIcon,
  Charging: BatteryChargingFullIcon,
  Fuel: OilBarrelIcon,
  Safe: NoCrashIcon,
  Accident: CarCrashIcon,
  Ambulance: AmbulanceIcon,
  Medical: MedicalServicesIcon,
  Police: PoliceCarIcon,
  FireTruck: FireTruckIcon,
  Tow: LocalShippingOutlinedIcon,
  Pickup: PickupTruckIcon,
  HeavyTruck: TruckIcon,
  Shuttle: AirportShuttleOutlined,
  ElectricCar: ElectricCarIcon,
  ElectricScooter: ElectricScooterIcon,
  Bike: PedalBikeIcon,
  Snowmobile: SnowmobileIcon,
  TwoWheelerOutlined: TwoWheelerOutlined,
} as const;

export type VehicleIconKey = keyof typeof VehicleIcons;

export function getVehicleIcon(key: VehicleIconKey) {
  return VehicleIcons[key];
}

// Vehicle status colors
export const VehicleStatusColors = {
  active: '#10B981',
  idle: '#F59E0B',
  offline: '#6B7280',
  error: '#EF4444',
  maintenance: '#8B5CF6',
} as const;

export type VehicleStatusColorKey = keyof typeof VehicleStatusColors;

export function getVehicleStatusColor(key: VehicleStatusColorKey) {
  return VehicleStatusColors[key];
}
