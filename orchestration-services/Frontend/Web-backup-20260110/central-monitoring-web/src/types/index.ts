/**
 * Type definitions for Central Monitoring Dashboard
 */

// Entity Types
export enum EntityType {
  USER = 'USER',
  PARTNER_DRIVER = 'PARTNER_DRIVER',
  PARTNER_VEHICLE = 'PARTNER_VEHICLE',
  MECHANIC_DRIVER = 'MECHANIC_DRIVER',
  MECHANIC_VEHICLE = 'MECHANIC_VEHICLE',
  CORPORATE_VEHICLE = 'CORPORATE_VEHICLE'
}

// Entity Status
export enum EntityStatus {
  AVAILABLE = 'AVAILABLE',
  BUSY = 'BUSY',
  OFFLINE = 'OFFLINE',
  ON_BREAK = 'ON_BREAK',
  EN_ROUTE = 'EN_ROUTE',
  ON_SITE = 'ON_SITE',
  EMERGENCY = 'EMERGENCY'
}

// Service Types
export enum ServiceType {
  TOWING = 'TOWING',
  TIRE_CHANGE = 'TIRE_CHANGE',
  FUEL_DELIVERY = 'FUEL_DELIVERY',
  LOCKOUT = 'LOCKOUT',
  JUMP_START = 'JUMP_START',
  WINDSHIELD = 'WINDSHIELD',
  MECHANIC = 'MECHANIC'
}

// Request Status
export enum RequestStatus {
  PENDING = 'PENDING',
  SEARCHING = 'SEARCHING',
  ASSIGNED = 'ASSIGNED',
  EN_ROUTE = 'EN_ROUTE',
  ON_SITE = 'ON_SITE',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

// Request Priority
export enum RequestPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  EMERGENCY = 'EMERGENCY'
}

// Alert Types
export enum AlertType {
  SLA_BREACH = 'SLA_BREACH',
  LONG_WAIT_TIME = 'LONG_WAIT_TIME',
  PARTNER_UNAVAILABLE = 'PARTNER_UNAVAILABLE',
  EMERGENCY_REQUEST = 'EMERGENCY_REQUEST',
  SYSTEM_ISSUE = 'SYSTEM_ISSUE',
  HIGH_VOLUME = 'HIGH_VOLUME'
}

// Location Update
export interface LocationUpdate {
  updateId: string;
  entityType: EntityType;
  entityId: string;
  latitude: number;
  longitude: number;
  address?: string;
  status: EntityStatus;
  currentJobId?: string;
  speed?: number;
  heading?: number;
  batteryLevel?: number;
  timestamp: string;
  deviceInfo?: string;
}

// Partner Location
export interface PartnerLocation {
  partnerId: string;
  partnerName?: string;
  partnerType: EntityType;
  driverId: string;
  driverName?: string;
  vehicleId?: string;
  vehicleRegistration?: string;
  latitude: number;
  longitude: number;
  address?: string;
  status: EntityStatus;
  currentJobId?: string;
  speed?: number;
  heading?: number;
  distanceKm?: number;
  etaMinutes?: number;
  lastUpdate: string;
  isLocationFresh: boolean;
  rating?: number;
}

// Service Request
export interface ServiceRequest {
  requestId: string;
  userId: string;
  userType: 'individual' | 'corporate';
  serviceType: ServiceType;
  location: {
    latitude: number;
    longitude: number;
    address: string;
  };
  status: RequestStatus;
  assignedPartner?: {
    id: string;
    name: string;
    driverName: string;
    estimatedArrival: string;
  };
  createdAt: string;
  priority: RequestPriority;
  vehicleInfo?: {
    make: string;
    model: string;
    year: number;
    registration: string;
  };
}

// Dashboard Stats
export interface DashboardStats {
  totalTrackedEntities: number;
  totalPartnersAvailable: number;
  totalPartnersOnJob: number;
  totalUsersWaiting: number;
  partnerTypeStats: {
    [key: string]: {
      total: number;
      available: number;
      onJob: number;
      offline: number;
    };
  };
  regionalStats: {
    [key: string]: {
      region: string;
      availablePartners: number;
      onJobPartners: number;
      waitingUsers: number;
      averageResponseTime: number;
    };
  };
}

// Alert
export interface Alert {
  alertId: string;
  type: AlertType;
  severity: 'info' | 'warning' | 'critical';
  title: string;
  message: string;
  requestId?: string;
  partnerId?: string;
  region?: string;
  createdAt: string;
  acknowledged: boolean;
  acknowledgedBy?: string;
  acknowledgedAt?: string;
}

// Broadcast Message
export interface BroadcastMessage {
  messageId: string;
  sender: string;
  recipientType: 'all' | 'towing' | 'mechanics' | 'specific-partner';
  recipientId?: string;
  subject: string;
  message: string;
  priority: 'information' | 'advisory' | 'urgent' | 'emergency';
  sentAt: string;
  acknowledgements: string[];
}

// Dispatch Assignment
export interface DispatchAssignment {
  assignmentId: string;
  requestId: string;
  assignedPartnerId: string;
  assignedDriverId: string;
  assignmentMethod: 'auto' | 'manual';
  estimatedArrival: string;
  distance: number;
  assignedAt: string;
  assignedBy: string;
}

// API Response wrapper
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
  timestamp: string;
}
