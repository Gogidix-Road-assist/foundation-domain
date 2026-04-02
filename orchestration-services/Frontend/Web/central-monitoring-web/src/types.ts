export interface GPSCoordinates {
  lat: number;
  lng: number;
}

export interface PartnerLocation {
  id: string;
  name: string;
  type: 'TOWING' | 'MECHANIC' | 'INDEPENDENT';
  status: 'AVAILABLE' | 'BUSY' | 'OFFLINE';
  location: GPSCoordinates;
  currentJobId?: string;
  rating: number;
  completedJobs: number;
}

export interface ServiceRequest {
  id: string;
  customerName: string;
  status: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  location: GPSCoordinates;
  vehicle: string;
  issue: string;
  createdAt: string;
}

export interface Alert {
  id: string;
  type: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  title: string;
  message: string;
  createdAt: string;
  acknowledged: boolean;
}

export interface DashboardStats {
  activeRequests: number;
  availablePartners: number;
  criticalAlerts: number;
  avgResponseTime: number;
}
