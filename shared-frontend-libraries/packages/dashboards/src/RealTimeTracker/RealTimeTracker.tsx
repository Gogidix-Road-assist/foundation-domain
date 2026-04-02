import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Paper,
  Typography,
  Card,
  CardContent,
  Chip,
  IconButton,
  useTheme,
  alpha,
} from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import RefreshIcon from '@mui/icons-material/Refresh';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import PersonIcon from '@mui/icons-material/Person';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ErrorIcon from '@mui/icons-material/Error';

// Types
export interface VehicleStatus {
  id: string;
  type: 'car' | 'truck' | 'ambulance' | 'police' | 'mechanic' | 'tow';
  status: 'active' | 'idle' | 'offline' | 'error';
  position: {
    lat: number;
    lng: number;
  };
  driver?: {
    name: string;
    phone?: string;
    avatar?: string;
  };
  destination?: string;
  eta?: string;
  speed?: number;
  lastUpdate: string;
}

export interface RealTimeTrackerProps {
  vehicles: VehicleStatus[];
  onRefresh?: () => void;
  onSelectVehicle?: (vehicle: VehicleStatus) => void;
  selectedVehicleId?: string;
  autoRefresh?: boolean;
  refreshInterval?: number;
  showMap?: boolean;
  height?: number | string;
  className?: string;
}

const STATUS_COLORS = {
  active: { bg: '#d1fae5', text: '#065f46', icon: 'success' },
  idle: { bg: '#fef3c7', text: '#92400e', icon: 'warning' },
  offline: { bg: '#f3f4f6', text: '#374151', icon: 'default' },
  error: { bg: '#fee2e2', text: '#991b1b', icon: 'error' },
};

const VEHICLE_ICONS: Record<VehicleStatus['type'], React.ReactNode> = {
  car: <DirectionsCarIcon />,
  truck: <LocalShippingIcon />,
  ambulance: <LocalShippingIcon />,
  police: <DirectionsCarIcon />,
  mechanic: <PersonIcon />,
  tow: <LocalShippingIcon />,
};

export function RealTimeTracker({
  vehicles,
  onRefresh,
  onSelectVehicle,
  selectedVehicleId,
  autoRefresh = true,
  refreshInterval = 30000,
  showMap = true,
  height = 600,
  className,
}: RealTimeTrackerProps) {
  const theme = useTheme();
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());

  // Auto refresh
  useEffect(() => {
    if (!autoRefresh || !onRefresh) return;

    const interval = setInterval(() => {
      handleRefresh();
    }, refreshInterval);

    return () => clearInterval(interval);
  }, [autoRefresh, refreshInterval, onRefresh]);

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    try {
      await onRefresh?.();
      setLastRefresh(new Date());
    } finally {
      setIsRefreshing(false);
    }
  }, [onRefresh]);

  const getStatusColor = (status: VehicleStatus['status']) => {
    return STATUS_COLORS[status];
  };

  const sortedVehicles = [...vehicles].sort((a, b) => {
    // Sort by status priority: error > active > idle > offline
    const statusPriority = { error: 0, active: 1, idle: 2, offline: 3 };
    return statusPriority[a.status] - statusPriority[b.status];
  });

  const activeVehicles = sortedVehicles.filter((v) => v.status === 'active');
  const offlineVehicles = sortedVehicles.filter((v) => v.status !== 'active');

  return (
    <Box className={className} sx={{ height }}>
      {/* Header */}
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          mb: 2,
        }}
      >
        <Box>
          <Typography variant="h6" gutterBottom>
            Real-Time Tracker
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Last updated: {lastRefresh.toLocaleTimeString()}
            {` • ${activeVehicles.length} active, ${offlineVehicles.length} inactive`}
          </Typography>
        </Box>
        <IconButton onClick={handleRefresh} disabled={isRefreshing}>
          <motion.div
            animate={isRefreshing ? { rotate: 360 } : { rotate: 0 }}
            transition={{ duration: 1, repeat: isRefreshing ? Infinity : 0, ease: 'linear' }}
          >
            <RefreshIcon />
          </motion.div>
        </IconButton>
      </Box>

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, height: 'calc(100% - 60px)' }}>
        {/* Map placeholder */}
        {showMap && (
          <Paper
            sx={{
              flex: 1,
              minHeight: 300,
              bgcolor: alpha(theme.palette.primary.main, 0.05),
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: 2,
              border: `2px dashed ${theme.palette.divider}`,
            }}
          >
            <Box sx={{ textAlign: 'center', color: 'text.secondary' }}>
              <LocationOnIcon sx={{ fontSize: 48, mb: 1 }} />
              <Typography variant="body2">Interactive Map Component</Typography>
              <Typography variant="caption">
                Requires Map integration (Leaflet/Google Maps)
              </Typography>
            </Box>
          </Paper>
        )}

        {/* Vehicle Cards */}
        <Box
          sx={{
            maxHeight: showMap ? 250 : '100%',
            overflowY: 'auto',
            display: 'flex',
            flexDirection: 'column',
            gap: 1.5,
          }}
        >
          <AnimatePresence>
            {sortedVehicles.map((vehicle, index) => {
              const isSelected = vehicle.id === selectedVehicleId;
              const statusConfig = getStatusColor(vehicle.status);
              const vehicleIcon = VEHICLE_ICONS[vehicle.type];

              return (
                <motion.div
                  key={vehicle.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 20 }}
                  transition={{ delay: index * 0.05 }}
                >
                  <Card
                    onClick={() => onSelectVehicle?.(vehicle)}
                    sx={{
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                      border: isSelected
                        ? `2px solid ${theme.palette.primary.main}`
                        : '1px solid transparent',
                      '&:hover': {
                        bgcolor: alpha(theme.palette.primary.main, 0.05),
                        transform: 'translateX(4px)',
                      },
                    }}
                  >
                    <CardContent sx={{ py: 1.5, '&:last-child': { pb: 1.5 } }}>
                      <Box
                        sx={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'flex-start',
                        }}
                      >
                        <Box sx={{ display: 'flex', gap: 1.5, flex: 1 }}>
                          {/* Vehicle Icon */}
                          <Box
                            sx={{
                              p: 1,
                              borderRadius: 1,
                              bgcolor: alpha(theme.palette.primary.main, 0.1),
                              color: theme.palette.primary.main,
                            }}
                          >
                            {React.cloneElement(vehicleIcon as React.ReactElement, {
                              fontSize: 20,
                            })}
                          </Box>

                          {/* Vehicle Info */}
                          <Box sx={{ flex: 1, minWidth: 0 }}>
                            <Box
                              sx={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: 0.75,
                                mb: 0.5,
                              }}
                            >
                              <Typography
                                variant="subtitle2"
                                sx={{ fontWeight: 600 }}
                              >
                                {vehicle.type.toUpperCase()} #{vehicle.id}
                              </Typography>
                              <Chip
                                label={vehicle.status}
                                size="small"
                                sx={{
                                  bgcolor: statusConfig.bg,
                                  color: statusConfig.text,
                                  fontSize: '11px',
                                  height: 22,
                                  fontWeight: 500,
                                }}
                              />
                            </Box>

                            {/* Driver Info */}
                            {vehicle.driver && (
                              <Typography variant="caption" color="text.secondary">
                                {vehicle.driver.name}
                              </Typography>
                            )}

                            {/* Destination */}
                            {vehicle.destination && (
                              <Typography variant="caption" color="text.secondary">
                                → {vehicle.destination}
                              </Typography>
                            )}

                            {/* ETA */}
                            {vehicle.eta && (
                              <Chip
                                label={`ETA: ${vehicle.eta}`}
                                size="small"
                                variant="outlined"
                                sx={{ fontSize: '11px', height: 22 }}
                              />
                            )}
                          </Box>
                        </Box>

                        {/* Right side info */}
                        <Box sx={{ textAlign: 'right', minWidth: 80 }}>
                          {vehicle.speed && (
                            <Typography variant="caption" color="text.secondary">
                              {vehicle.speed} km/h
                            </Typography>
                          )}
                          <Typography variant="caption" color="text.secondary">
                            {vehicle.lastUpdate}
                          </Typography>
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                </motion.div>
              );
            })}
          </AnimatePresence>
        </Box>
      </Box>
    </Box>
  );
}

RealTimeTracker.displayName = 'RealTimeTracker';

// Compact card variant for lists
export interface VehicleCardProps {
  vehicle: VehicleStatus;
  selected?: boolean;
  onClick?: (vehicle: VehicleStatus) => void;
}

export function VehicleCard({ vehicle, selected, onClick }: VehicleCardProps) {
  const theme = useTheme();
  const statusConfig = getStatusColor(vehicle.status);
  const vehicleIcon = VEHICLE_ICONS[vehicle.type];

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2 }}
    >
      <Card
        onClick={() => onClick?.(vehicle)}
        sx={{
          cursor: 'pointer',
          border: selected
            ? `2px solid ${theme.palette.primary.main}`
            : '1px solid transparent',
          '&:hover': {
            bgcolor: alpha(theme.palette.primary.main, 0.05),
            boxShadow: theme.shadows[2],
          },
        }}
      >
        <CardContent sx={{ py: 1 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
            <Box
              sx={{
                p: 0.75,
                borderRadius: 1,
                bgcolor: alpha(theme.palette.primary.main, 0.1),
                color: theme.palette.primary.main,
              }}
            >
              {React.cloneElement(vehicleIcon as React.ReactElement, {
                fontSize: 18,
              })}
            </Box>
            <Box sx={{ flex: 1 }}>
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 0.75,
                }}
              >
                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                  {vehicle.type.toUpperCase()} #{vehicle.id}
                </Typography>
                <Chip
                  label={vehicle.status}
                  size="small"
                  sx={{
                    bgcolor: statusConfig.bg,
                    color: statusConfig.text,
                    fontSize: '10px',
                    height: 20,
                    fontWeight: 500,
                  }}
                />
              </Box>
            </Box>
          </Box>
        </CardContent>
      </Card>
    </motion.div>
  );
}

VehicleCard.displayName = 'VehicleCard';
