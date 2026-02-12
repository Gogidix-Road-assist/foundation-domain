/**
 * Partner Status Component
 * Displays summary statistics for partner types
 */

import { FC } from 'react';

interface PartnerStats {
  total: number;
  available: number;
  onJob: number;
  offline: number;
}

interface PartnerStatusProps {
  stats?: {
    [key: string]: PartnerStats;
  };
  loading?: boolean;
}

const partnerTypeLabels: Record<string, { label: string; icon: string; color: string }> = {
  towing: { label: 'Towing', icon: '🚗', color: 'bg-blue-500' },
  mechanic: { label: 'Mechanics', icon: '👨‍🔧', color: 'bg-green-500' }
};

const PartnerStatus: FC<PartnerStatusProps> = ({ stats, loading }) => {
  if (loading) {
    return (
      <div className="p-4 space-y-3">
        {[1, 2].map((i) => (
          <div key={i} className="animate-pulse">
            <div className="h-4 bg-gray-200 rounded w-20 mb-2"></div>
            <div className="h-2 bg-gray-200 rounded w-full mb-1"></div>
            <div className="h-2 bg-gray-200 rounded w-3/4"></div>
          </div>
        ))}
      </div>
    );
  }

  if (!stats) {
    return (
      <div className="p-4 text-center text-gray-500 text-sm">
        No partner data available
      </div>
    );
  }

  return (
    <div className="p-4 space-y-4">
      {Object.entries(stats).map(([type, data]) => {
        const config = partnerTypeLabels[type];
        if (!config) return null;

        const utilization = data.total > 0 ? (data.onJob / data.total) * 100 : 0;

        return (
          <div key={type} className="space-y-2">
            {/* Header */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="text-lg">{config.icon}</span>
                <span className="font-medium text-gray-900">{config.label}</span>
              </div>
              <span className="text-sm text-gray-600">
                {data.available} available
              </span>
            </div>

            {/* Progress bar */}
            <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
              <div className="bg-gray-500 h-2 transition-all" style={{ width: `${data.offline / data.total * 100}%` }}></div>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden -mt-2">
              <div className={`${config.color} h-2 transition-all`} style={{ width: `${data.onJob / data.total * 100}%` }}></div>
            </div>

            {/* Stats */}
            <div className="flex items-center justify-between text-xs text-gray-500">
              <span>On Job: {data.onJob}</span>
              <span>Offline: {data.offline}</span>
              <span>Total: {data.total}</span>
            </div>

            {/* Utilization */}
            <div className="flex items-center justify-between text-xs">
              <span className="text-gray-500">Utilization</span>
              <span className={`font-medium ${utilization > 75 ? 'text-green-600' : utilization > 50 ? 'text-amber-600' : 'text-gray-600'}`}>
                {utilization.toFixed(0)}%
              </span>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default PartnerStatus;
