import React from 'react';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import LocationSearchingIcon from '@mui/icons-material/LocationSearching';
import LocationOffIcon from '@mui/icons-material/LocationOff';
import NavigationIcon from '@mui/icons-material/Navigation';
import MyLocationIcon from '@mui/icons-material/MyLocation';
import DirectionsIcon from '@mui/icons-material/Directions';
import DirectionsWalkIcon from '@mui/icons-material/DirectionsWalk';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import DirectionsBikeIcon from '@mui/icons-material/DirectionsBike';
import LocalTaxiIcon from '@mui/icons-material/LocalTaxi';
import AirportShuttleIcon from '@mui/icons-material/AirportShuttle';
import CommuteIcon from '@mui/icons-material/Commute';
import MapIcon from '@mui/icons-material/Map';
import SatelliteIcon from '@mui/icons-material/Satellite';
import TerrainIcon from '@mui/icons-material/Terrain';
import LayersIcon from '@mui/icons-material/Layers';
import ZoomInIcon from '@mui/icons-material/ZoomIn';
import ZoomOutIcon from '@mui/icons-material/ZoomOut';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import ExitFullscreenIcon from '@mui/icons-material/ExitFullscreen';
import CenterFocusStrongIcon from '@mui/icons-material/CenterFocusStrong';
import PlaceIcon from '@mui/icons-material/Place';
import PinDropIcon from '@mui/icons-material/PinDrop';
import FlagIcon from '@mui/icons-material/Flag';
import NearMeIcon from '@mui/icons-material/NearMe';
import ExploreIcon from '@mui/icons-material/Explore';
import RouteIcon from '@mui/icons-material/Route';
import TrafficIcon from '@mui/icons-material/Traffic';
import TollIcon from '@mui/icons-material/Toll';
import LocalParkingIcon from '@mui/icons-material/LocalParking';
import GasStationIcon from '@mui/icons-material/LocalGasStation';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import HotelIcon from '@mui/icons-material/Hotel';
import AtmIcon from '@mui/icons-material/Atm';
import BankIcon from '@mui/icons-material/Bank';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import LocalTaxiOutlinedIcon from '@mui/icons-material/LocalTaxiOutlined';
import TwoWheelerIcon from '@mui/icons-material/TwoWheeler';

export const MapIcons = {
  LocationOn: LocationOnIcon,
  LocationSearching: LocationSearchingIcon,
  LocationOff: LocationOffIcon,
  Navigation: NavigationIcon,
  MyLocation: MyLocationIcon,
  Directions: DirectionsIcon,
  DirectionsWalk: DirectionsWalkIcon,
  DirectionsCar: DirectionsCarIcon,
  DirectionsBike: DirectionsBikeIcon,
  LocalTaxi: LocalTaxiIcon,
  AirportShuttle: AirportShuttleIcon,
  Commute: CommuteIcon,
  Map: MapIcon,
  Satellite: SatelliteIcon,
  Terrain: TerrainIcon,
  Layers: LayersIcon,
  ZoomIn: ZoomInIcon,
  ZoomOut: ZoomOutIcon,
  Fullscreen: FullscreenIcon,
  ExitFullscreen: ExitFullscreenIcon,
  CenterFocusStrong: CenterFocusStrongIcon,
  Place: PlaceIcon,
  PinDrop: PinDropIcon,
  Flag: FlagIcon,
  NearMe: NearMeIcon,
  Explore: ExploreIcon,
  Route: RouteIcon,
  Traffic: TrafficIcon,
  Toll: TollIcon,
  LocalParking: LocalParkingIcon,
  GasStation: GasStationIcon,
  Restaurant: RestaurantIcon,
  Hotel: HotelIcon,
  Atm: AtmIcon,
  Bank: BankIcon,
  LocalShipping: LocalShippingIcon,
  LocalTaxiOutlined: LocalTaxiOutlinedIcon,
  TwoWheeler: TwoWheelerIcon,
} as const;

export type MapIconKey = keyof typeof MapIcons;

export function getMapIcon(key: MapIconKey) {
  return MapIcons[key];
}
