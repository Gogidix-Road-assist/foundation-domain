import React from 'react'
import { render, screen } from '@testing-library/react-native'
import ErrorBoundary from './ErrorBoundary'

// Component that throws an error
const ThrowError = ({ shouldThrow }) => {
  if (shouldThrow) {
    throw new Error('Test error')
  }
  return <React.Fragment>No error</React.Fragment>
}

describe('ErrorBoundary', () => {
  // Suppress console.error for these tests
  const originalError = console.error
  beforeEach(() => {
    console.error = jest.fn()
  })

  afterEach(() => {
    console.error = originalError
  })

  it('should render children when there is no error', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={false} />
      </ErrorBoundary>
    )

    expect(screen.getByText('No error')).toBeTruthy()
  })

  it('should catch and render error UI when child throws error', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    )

    expect(screen.getByText('Oops!')).toBeTruthy()
    expect(screen.getByText('Something went wrong')).toBeTruthy()
  })

  it('should display error message', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    )

    expect(screen.getByText(/We're sorry for the inconvenience/)).toBeTruthy()
  })

  it('should have try again button', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    )

    expect(screen.getByText('Try Again')).toBeTruthy()
  })

  it('should have restart app button', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    )

    expect(screen.getByText('Restart App')).toBeTruthy()
  })

  it('should display support email', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    )

    expect(screen.getByText('support@gogidix.com')).toBeTruthy()
  })
})
