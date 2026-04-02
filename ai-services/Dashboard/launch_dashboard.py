#!/usr/bin/env python3
"""
AI Services Monitoring Dashboard Launcher

This script launches the AI Services Dashboard on localhost.
It serves the frontend using Python's built-in HTTP server.
"""

import http.server
import socketserver
import webbrowser
import os
import sys
import subprocess
import threading
import time
from pathlib import Path

# Configuration
DASHBOARD_DIR = Path(__file__).parent
FRONTEND_DIR = DASHBOARD_DIR
BACKEND_DIR = DASHBOARD_DIR / "backend"
PORT = 3000
BACKEND_PORT = 8080

# Color codes for terminal output
class Colors:
    GREEN = '\033[92m'
    BLUE = '\033[94m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def print_header():
    """Print the dashboard header"""
    print(f"\n{Colors.BOLD}{Colors.BLUE}")
    print("=" * 60)
    print("     AI Services Monitoring Dashboard")
    print("     Aggregation Service Launcher")
    print("=" * 60)
    print(f"{Colors.ENDC}\n")

def print_success(message):
    print(f"{Colors.GREEN}✓ {message}{Colors.ENDC}")

def print_info(message):
    print(f"{Colors.BLUE}ℹ {message}{Colors.ENDC}")

def print_warning(message):
    print(f"{Colors.YELLOW}⚠ {message}{Colors.ENDC}")

def print_error(message):
    print(f"{Colors.RED}✗ {message}{Colors.ENDC}")

def check_frontend():
    """Check if frontend files exist"""
    index_file = FRONTEND_DIR / "index.html"
    if index_file.exists():
        print_success(f"Frontend found: {FRONTEND_DIR}")
        return True
    else:
        print_error(f"Frontend not found: {FRONTEND_DIR}")
        print_info("Please ensure the dashboard files are in place.")
        return False

def check_backend():
    """Check if backend exists"""
    pom_file = BACKEND_DIR / "pom.xml"
    if pom_file.exists():
        print_success(f"Backend found: {BACKEND_DIR}")
        return True
    else:
        print_warning(f"Backend not found: {BACKEND_DIR}")
        return False

def start_backend():
    """Start the Spring Boot backend aggregation service"""
    backend_jar = None

    # Find the backend JAR file
    for jar_file in BACKEND_DIR.rglob("*.jar"):
        backend_jar = jar_file
        break

    if backend_jar and backend_jar.exists():
        print_info(f"Starting backend from JAR: {backend_jar}")
        cmd = ["java", "-jar", str(backend_jar)]
    else:
        # Try to run with Maven
        print_info("Starting backend with Maven...")
        cmd = ["mvn", "spring-boot:run"]
        os.chdir(BACKEND_DIR)

    try:
        # Start backend process
        backend_process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            cwd=BACKEND_DIR
        )

        # Give it time to start
        time.sleep(5)

        if backend_process.poll() is None:
            print_success(f"Backend started on port {BACKEND_PORT}")
            print_info(f"Backend API: http://localhost:{BACKEND_PORT}")
            print_info(f"WebSocket: ws://localhost:{BACKEND_PORT}/ws")
            return backend_process
        else:
            print_error("Backend failed to start")
            return None

    except Exception as e:
        print_error(f"Failed to start backend: {e}")
        return None

def start_backend_mock():
    """Start a mock backend server for development"""
    try:
        from flask import Flask, jsonify, send_from_directory
        from flask_cors import CORS

        app = Flask(__name__)
        CORS(app)

        @app.route('/api/services/health')
        def get_services_health():
            return jsonify([])  # Will be filled by frontend mock data

        @app.route('/api/dashboard/stats')
        def get_dashboard_stats():
            return jsonify({
                'total': 27,
                'healthy': 25,
                'degraded': 1,
                'down': 1,
                'totalRequests': 250000,
                'avgResponseTime': 150
            })

        @app.route('/ws')
        def websocket():
            return "WebSocket endpoint"

        # Serve static files
        @app.route('/')
        @app.route('/<path:path>')
        def serve_static(path=''):
            return send_from_directory(FRONTEND_DIR, 'index.html')

        # Run Flask app in a thread
        def run_flask():
            app.run(port=BACKEND_PORT, debug=False, use_reloader=False)

        flask_thread = threading.Thread(target=run_flask, daemon=True)
        flask_thread.start()

        time.sleep(2)
        print_success(f"Mock backend started on port {BACKEND_PORT}")
        return True

    except ImportError:
        print_warning("Flask not installed, using frontend-only mode")
        return None

def start_frontend_server():
    """Start the frontend HTTP server"""

    class DashboardHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
        """Custom handler for the dashboard"""

        def end_headers(self):
            # Add CORS headers
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
            self.send_header('Access-Control-Allow-Headers', 'Content-Type')
            self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
            super().end_headers()

        def do_GET(self):
            # Serve index.html for root path
            if self.path == '/' or self.path == '':
                self.path = '/index.html'
            return http.server.SimpleHTTPRequestHandler.do_GET(self)

        def do_OPTIONS(self):
            self.send_response(200)
            self.end_headers()

    try:
        # Change to frontend directory
        os.chdir(FRONTEND_DIR)

        # Create server
        handler = DashboardHTTPRequestHandler
        socketserver.TCPServer.allow_reuse_address = True

        with socketserver.TCPServer(("", PORT), handler) as httpd:
            print_success(f"Dashboard server running on port {PORT}")
            print_info(f"Dashboard URL: {Colors.BOLD}http://localhost:{PORT}{Colors.ENDC}")
            print()
            print("=" * 60)
            print(f"  Open your browser and visit: {Colors.BLUE}http://localhost:{PORT}{Colors.ENDC}")
            print("=" * 60)
            print()
            print_info("Press Ctrl+C to stop the server")
            print()

            # Open browser automatically
            print_info("Opening browser...")
            webbrowser.open(f'http://localhost:{PORT}')

            # Keep server running
            httpd.serve_forever()

    except KeyboardInterrupt:
        print()
        print_warning("Dashboard server stopped")
    except OSError as e:
        if e.errno == 10048:  # Windows: Address already in use
            print_error(f"Port {PORT} is already in use!")
            print_info("Another application may be using this port.")
            print_info("Try changing the PORT variable in the script.")
        else:
            print_error(f"Error starting server: {e}")
        sys.exit(1)

def install_frontend_dependencies():
    """Install frontend npm dependencies if needed"""
    node_modules = FRONTEND_DIR / "node_modules"
    package_json = FRONTEND_DIR / "package.json"

    if not node_modules.exists() and package_json.exists():
        print_info("Installing frontend dependencies...")
        print_info("This may take a minute...")

        try:
            subprocess.run(
                ["npm", "install"],
                cwd=FRONTEND_DIR,
                check=True,
                capture_output=True
            )
            print_success("Frontend dependencies installed")
        except subprocess.CalledProcessError:
            print_warning("npm install failed, starting anyway...")
        except FileNotFoundError:
            print_warning("npm not found, starting anyway...")

def main():
    """Main function to launch the dashboard"""
    print_header()

    # Check if we're in the right directory
    if not (FRONTEND_DIR / "index.html").exists():
        print_error("Dashboard files not found!")
        print_info(f"Current directory: {os.getcwd()}")
        print_info(f"Looking for dashboard in: {FRONTEND_DIR}")
        sys.exit(1)

    # Install dependencies if needed
    install_frontend_dependencies()

    # Check backend
    backend_process = None
    backend_available = check_backend()

    if backend_available:
        print()
        print_info("Starting backend aggregation service...")
        print_info("This connects to all 27 AI services on ports 8081-8107")
        print()

        # Start backend (optional - comment out if you want frontend-only)
        # backend_process = start_backend()
        backend_process = start_backend_mock()

    print()
    print_info("Starting frontend dashboard server...")
    print()

    try:
        # Start the frontend server
        start_frontend_server()
    finally:
        # Cleanup backend process if it was started
        if backend_process:
            try:
                backend_process.terminate()
                print_info("Backend stopped")
            except:
                pass

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print()
        print_info("Dashboard launcher stopped")
        sys.exit(0)
