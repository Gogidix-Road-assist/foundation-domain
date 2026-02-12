import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react'

interface WebSocketContextType {
  connected: boolean
  messages: any[]
  lastMessage: any
  sendMessage: (message: any) => void
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined)

export const useWebSocket = () => {
  const context = useContext(WebSocketContext)
  if (!context) {
    throw new Error('useWebSocket must be used within a WebSocketProvider')
  }
  return context
}

interface WebSocketProviderProps {
  children: ReactNode
}

export const WebSocketProvider: React.FC<WebSocketProviderProps> = ({ children }) => {
  const [connected, setConnected] = useState(false)
  const [messages, setMessages] = useState<any[]>([])
  const [lastMessage, setLastMessage] = useState<any>(null)
  const [ws, setWs] = useState<WebSocket | null>(null)

  useEffect(() => {
    const wsUrl = 'ws://localhost:8080/ws'
    const websocket = new WebSocket(wsUrl)

    websocket.onopen = () => {
      console.log('WebSocket connected')
      setConnected(true)
    }

    websocket.onmessage = (event) => {
      const message = JSON.parse(event.data)
      console.log('WebSocket message:', message)

      setLastMessage(message)
      setMessages(prev => [...prev.slice(-99), message])
    }

    websocket.onclose = () => {
      console.log('WebSocket disconnected')
      setConnected(false)

      // Attempt to reconnect after 5 seconds
      setTimeout(() => {
        console.log('Attempting to reconnect WebSocket...')
        setWs(new WebSocket(wsUrl))
      }, 5000)
    }

    websocket.onerror = (error) => {
      console.error('WebSocket error:', error)
    }

    setWs(websocket)

    return () => {
      websocket.close()
    }
  }, [])

  const sendMessage = (message: any) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(message))
    }
  }

  const value: WebSocketContextType = {
    connected,
    messages,
    lastMessage,
    sendMessage,
  }

  return <WebSocketContext.Provider value={value}>{children}</WebSocketContext.Provider>
}
