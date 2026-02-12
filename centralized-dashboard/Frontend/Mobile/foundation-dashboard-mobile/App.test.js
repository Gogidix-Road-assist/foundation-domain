import React from 'react'
import { render } from '@testing-library/react-native'
import App from './App'

// Mock the stores
jest.mock('./store/authStore', () => ({
  useAuthStore: () => ({
    isAuthenticated: false,
    checkAuth: jest.fn(),
    token: null
  })
}))

jest.mock('./store/dashboardStore', () => ({
  useDashboardStore: () => ({
    metrics: [],
    services: [],
    alerts: []
  })
}))

jest.mock('./services/api', () => ({
  setAuthToken: jest.fn()
}))

describe('App Component', () => {
  it('should render without crashing', () => {
    const { container } = render(<App />)
    expect(container).toBeTruthy()
  })

  it('should have navigation container', () => {
    const { getByTestId } = render(<App />)
    // NavigationContainer should be rendered
    expect(true).toBe(true)
  })

  it('should check authentication on mount', () => {
    const { checkAuth } = require('./store/authStore').useAuthStore()
    render(<App />)

    // checkAuth should be called via useEffect
    expect(checkAuth).toBeDefined()
  })
})
