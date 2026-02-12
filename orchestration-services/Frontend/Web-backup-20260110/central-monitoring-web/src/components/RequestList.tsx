/**
 * Request List Component
 * Displays list of active service requests
 */

import { FC } from 'react';
import { formatDistanceToNow } from 'date-fns';
import type { ServiceRequest } from '../types';
import { RequestStatus, ServiceType } from '../types';

interface RequestListProps {
  requests: ServiceRequest[];
  onViewRequest: (requestId: string) => void;
  onAssignPartner?: (requestId: string, partnerId: string) => void;
}

const statusColors: Record<RequestStatus, string> = {
  [RequestStatus.PENDING]: 'bg-amber-100 text-amber-700',
  [RequestStatus.SEARCHING]: 'bg-blue-100 text-blue-700',
  [RequestStatus.ASSIGNED]: 'bg-indigo-100 text-indigo-700',
  [RequestStatus.EN_ROUTE]: 'bg-purple-100 text-purple-700',
  [RequestStatus.ON_SITE]: 'bg-teal-100 text-teal-700',
  [RequestStatus.IN_PROGRESS]: 'bg-orange-100 text-orange-700',
  [RequestStatus.COMPLETED]: 'bg-green-100 text-green-700',
  [RequestStatus.CANCELLED]: 'bg-gray-100 text-gray-700'
};

const serviceIcons: Record<ServiceType, string> = {
  [ServiceType.TOWING]: '🚗',
  [ServiceType.TIRE_CHANGE]: '🔧',
  [ServiceType.FUEL_DELIVERY]: '⛽',
  [ServiceType.LOCKOUT]: '🔑',
  [ServiceType.JUMP_START]: '🔋',
  [ServiceType.WINDSHIELD]: '🪟',
  [ServiceType.MECHANIC]: '👨‍🔧'
};

const priorityColors = {
  LOW: 'text-gray-500',
  MEDIUM: 'text-amber-500',
  HIGH: 'text-orange-500',
  EMERGENCY: 'text-red-500'
};

const RequestList: FC<RequestListProps> = ({ requests, onViewRequest, onAssignPartner }) => {
  if (requests.length === 0) {
    return (
      <div className="p-6 text-center text-gray-500 text-sm">
        No active requests
      </div>
    );
  }

  return (
    <div className="divide-y divide-gray-100">
      {requests.map((request) => (
        <div
          key={request.requestId}
          className="p-4 hover:bg-gray-50 cursor-pointer transition-colors"
          onClick={() => onViewRequest(request.requestId)}
        >
          <div className="flex items-start justify-between">
            <div className="flex items-start gap-3">
              <span className="text-xl">{serviceIcons[request.serviceType]}</span>
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-medium text-gray-900 text-sm">
                    {request.serviceType.replace('_', ' ')}
                  </span>
                  {request.priority !== 'LOW' && (
                    <span className={`text-xs font-medium ${priorityColors[request.priority]}`}>
                      {request.priority}
                    </span>
                  )}
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  {request.location.address}
                </p>
                <div className="flex items-center gap-3 mt-2">
                  <span className={`px-2 py-0.5 text-xs font-medium rounded-full ${statusColors[request.status]}`}>
                    {request.status.replace('_', ' ')}
                  </span>
                  <span className="text-xs text-gray-400">
                    {formatDistanceToNow(new Date(request.createdAt), { addSuffix: true })}
                  </span>
                </div>
                {request.assignedPartner && (
                  <div className="mt-2 flex items-center gap-2">
                    <div className="w-5 h-5 bg-blue-500 rounded-full flex items-center justify-center">
                      <span className="text-white text-xs">P</span>
                    </div>
                    <span className="text-xs text-gray-600">{request.assignedPartner.name}</span>
                  </div>
                )}
              </div>
            </div>
            {onAssignPartner && !request.assignedPartner && request.status === RequestStatus.SEARCHING && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  // Trigger assign flow
                  onAssignPartner(request.requestId, '');
                }}
                className="px-3 py-1 text-xs font-medium bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Assign
              </button>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default RequestList;
