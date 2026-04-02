#!/usr/bin/env python3
"""
Simple AI Services Dashboard Launcher
Serves the dashboard on localhost:8000
"""

import http.server
import socketserver
import os
import sys
import webbrowser
from pathlib import Path

# Configuration
PORT = 8000
DIRECTORY = Path(__file__).parent

class RequestHandler(http.server.SimpleHTTPRequestHandler):
    """Custom HTTP request handler"""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        # Add CORS headers
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        super().end_headers()

    def do_GET(self):
        # Serve index.html for root
        if self.path == '/' or self.path == '':
            self.path = '/index.html'
        return super().do_GET()

def main():
    os.chdir(DIRECTORY)

    print("\n" + "=" * 55)
    print("  AI Services Monitoring Dashboard")
    print("=" * 55)
    print(f"\n🚀 Starting server on http://localhost:{PORT}")
    print(f"📁 Serving from: {DIRECTORY}")
    print("\nPress Ctrl+C to stop\n")

    try:
        # Create server
        with socketserver.TCPServer(("", PORT), RequestHandler) as httpd:
            # Open browser
            webbrowser.open(f'http://localhost:{PORT}')

            # Serve
            print(f"✓ Dashboard is running at: http://localhost:{PORT}")
            print()
            httpd.serve_forever()

    except KeyboardInterrupt:
        print("\n\n✓ Dashboard stopped")
    except OSError as e:
        if e.errno == 10048:  # Windows port in use
            print(f"\n✗ Port {PORT} already in use!")
        else:
            print(f"\n✗ Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
