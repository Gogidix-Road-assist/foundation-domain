#!/bin/bash

# AI Services Monitoring Dashboard Startup Script

echo "=================================="
echo "AI Services Monitoring Dashboard"
echo "=================================="
echo ""

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "Error: Node.js is not installed"
    echo "Please install Node.js from https://nodejs.org/"
    exit 1
fi

echo "Node version: $(node --version)"
echo "NPM version: $(npm --version)"
echo ""

# Navigate to dashboard directory
DASHBOARD_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DASHBOARD_DIR"

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
    echo ""
fi

# Check if .env file exists, if not create a default one
if [ ! -f ".env" ]; then
    echo "Creating .env file..."
    cat > .env << EOF
# AI Services Dashboard Configuration

API_BASE_URL=http://localhost:8080
WS_URL=ws://localhost:8080/ws
REFRESH_INTERVAL=30000
EOF
    echo ".env file created"
    echo ""
fi

# Start the development server
echo "Starting AI Services Monitoring Dashboard..."
echo "Dashboard will be available at: http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Run the dev server
npm run dev