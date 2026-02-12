import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react'
import { AIService, AI_SERVICES } from '../data/services'

interface ServicesContextType {
  services: AIService[]
  updateService: (id: string, updates: Partial<AIService>) => void
  updateServiceMetrics: (id: string, metrics: Partial<AIService['metrics']>) => void
  getServiceById: (id: string) => AIService | undefined
  getServicesByCategory: (category: AIService['category']) => AIService[]
  getHealthyServices: () => AIService[]
  getDegradedServices: () => AIService[]
  getDownServices: () => AIService[]
  refreshServices: () => Promise<void>
}

const ServicesContext = createContext<ServicesContextType | undefined>(undefined)

export const useServices = () => {
  const context = useContext(ServicesContext)
  if (!context) {
    throw new Error('useServices must be used within a ServicesProvider')
  }
  return context
}

interface ServicesProviderProps {
  children: ReactNode
}

export const ServicesProvider: React.FC<ServicesProviderProps> = ({ children }) => {
  const [services, setServices] = useState<AIService[]>(AI_SERVICES)

  const updateService = useCallback((id: string, updates: Partial<AIService>) => {
    setServices(prev =>
      prev.map(service =>
        service.id === id ? { ...service, ...updates } : service
      )
    )
  }, [])

  const updateServiceMetrics = useCallback((id: string, metrics: Partial<AIService['metrics']>) => {
    setServices(prev =>
      prev.map(service =>
        service.id === id
          ? { ...service, metrics: { ...service.metrics, ...metrics } }
          : service
      )
    )
  }, [])

  const getServiceById = useCallback(
    (id: string) => services.find(s => s.id === id),
    [services]
  )

  const getServicesByCategory = useCallback(
    (category: AIService['category']) =>
      services.filter(s => s.category === category),
    [services]
  )

  const getHealthyServices = useCallback(
    () => services.filter(s => s.status === 'healthy'),
    [services]
  )

  const getDegradedServices = useCallback(
    () => services.filter(s => s.status === 'degraded'),
    [services]
  )

  const getDownServices = useCallback(
    () => services.filter(s => s.status === 'down'),
    [services]
  )

  const refreshServices = useCallback(async () => {
    // Simulate API call to refresh service statuses
    await Promise.all(
      services.map(async (service) => {
        try {
          const response = await fetch(`http://localhost:${service.port}${service.healthEndpoint}`)
          const isHealthy = response.ok

          updateService(service.id, {
            status: isHealthy ? 'healthy' : 'down',
            lastHealthCheck: new Date(),
          })
        } catch (error) {
          updateService(service.id, {
            status: 'down',
            lastHealthCheck: new Date(),
          })
        }
      })
    )
  }, [services, updateService])

  const value: ServicesContextType = {
    services,
    updateService,
    updateServiceMetrics,
    getServiceById,
    getServicesByCategory,
    getHealthyServices,
    getDegradedServices,
    getDownServices,
    refreshServices,
  }

  return <ServicesContext.Provider value={value}>{children}</ServicesContext.Provider>
}
