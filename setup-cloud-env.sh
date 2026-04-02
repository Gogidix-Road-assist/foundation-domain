#!/bin/bash

# ==============================================================================
# Rapid Assist Foundation Domain - Cloud Setup Script
# ==============================================================================
# This script helps automate the setup and deployment to Railway + Vercel
#
# Usage:
#   chmod +x setup-cloud-env.sh
#   ./setup-cloud-env.sh
# ==============================================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ==============================================================================
# Print Functions
# ==============================================================================

print_header() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# ==============================================================================
# Check Prerequisites
# ==============================================================================

check_prerequisites() {
    print_header "Checking Prerequisites"

    # Check for git
    if command -v git &> /dev/null; then
        print_success "Git is installed"
    else
        print_error "Git is not installed"
        exit 1
    fi

    # Check for node
    if command -v node &> /dev/null; then
        print_success "Node.js is installed (v$(node -v))"
    else
        print_error "Node.js is not installed"
        exit 1
    fi

    # Check for npm
    if command -v npm &> /dev/null; then
        print_success "npm is installed (v$(npm -v))"
    else
        print_error "npm is not installed"
        exit 1
    fi

    # Check for Railway CLI
    if command -v railway &> /dev/null; then
        print_success "Railway CLI is installed"
    else
        print_warning "Railway CLI is not installed"
        print_info "Installing Railway CLI..."
        npm install -g @railway/cli
        print_success "Railway CLI installed"
    fi

    # Check for Vercel CLI
    if command -v vercel &> /dev/null; then
        print_success "Vercel CLI is installed"
    else
        print_warning "Vercel CLI is not installed"
        print_info "Installing Vercel CLI..."
        npm install -g vercel
        print_success "Vercel CLI installed"
    fi

    # Check if we're in the right directory
    if [ -f "INFRASTRUCTURE_SETUP.md" ]; then
        print_success "In the correct directory (Foundation-Domain)"
    else
        print_error "Please run this script from the Foundation-Domain directory"
        exit 1
    fi

    echo ""
}

# ==============================================================================
# Environment Variables Setup
# ==============================================================================

setup_env_vars() {
    print_header "Environment Variables Setup"

    print_info "Creating .env.template file..."

    cat > .env.template << 'EOF'
# ==============================================================================
# Rapid Assist Foundation Domain - Environment Variables
# ==============================================================================
# Copy this file to .env and fill in your actual values
# DO NOT commit .env to GitHub!

# ------------------------------------------------------------------------------
# MongoDB Atlas Configuration
# ------------------------------------------------------------------------------
MONGODB_URI=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority
MONGODB_DATABASE=rapid_assist_foundation

# ------------------------------------------------------------------------------
# Supabase Configuration
# ------------------------------------------------------------------------------
SUPABASE_DB_URL=postgresql://postgres:<password>@db.xxxxx.supabase.co:5432/postgres
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=<your-anon-key>
SUPABASE_SERVICE_ROLE_KEY=<your-service-role-key>

# ------------------------------------------------------------------------------
# JWT Configuration
# ------------------------------------------------------------------------------
JWT_SECRET=<generate-strong-secret-here>
JWT_EXPIRATION=86400000

# ------------------------------------------------------------------------------
# Railway Configuration
# ------------------------------------------------------------------------------
RAILWAY_TOKEN=<your-railway-token>
RAILWAY_PROJECT_ID=<your-project-id>

# ------------------------------------------------------------------------------
# Vercel Configuration
# ------------------------------------------------------------------------------
VERCEL_TOKEN=<your-vercel-token>
VERCEL_ORG_ID=<your-org-id>
VERCEL_PROJECT_ID=<your-project-id>

# ------------------------------------------------------------------------------
# Application Configuration
# ------------------------------------------------------------------------------
SPRING_PROFILES_ACTIVE=production
LOGGING_LEVEL_COM_GOGIDIX=INFO

# ------------------------------------------------------------------------------
# Frontend Configuration
# ------------------------------------------------------------------------------
VITE_API_BASE_URL=https://rapid-assist-foundation.railway.app
VITE_WS_URL=wss://rapid-assist-foundation.railway.app
EOF

    print_success ".env.template created"
    print_warning "Copy this to .env and fill in your actual values"
    print_info "Run: cp .env.template .env"

    echo ""
}

# ==============================================================================
# Railway Setup
# ==============================================================================

setup_railway() {
    print_header "Railway Setup"

    print_info "Please login to Railway..."
    railway login || {
        print_error "Railway login failed"
        return 1
    }

    print_success "Logged in to Railway"

    print_info "Initializing Railway project..."
    railway init || {
        print_warning "Railway project may already exist"
    }

    print_success "Railway project initialized"
    print_info "Visit: https://railway.app/dashboard to configure your project"

    echo ""
}

# ==============================================================================
# Build Docker Images
# ==============================================================================

build_docker_images() {
    print_header "Building Docker Images"

    print_info "Building Docker images for all services..."

    # Find all services with Dockerfile
    find . -name "Dockerfile" -type f | while read dockerfile; do
        service_dir=$(dirname "$dockerfile")
        service_name=$(basename "$service_dir")

        print_info "Building $service_name..."
        cd "$service_dir" || continue

        if docker build -t "rapid-assist-$service_name" . 2>/dev/null; then
            print_success "Built $service_name"
        else
            print_warning "Could not build $service_name (Docker may not be running)"
        fi

        cd - > /dev/null
    done

    echo ""
}

# ==============================================================================
# Create Dev Branch
# ==============================================================================

create_dev_branch() {
    print_header "Create Dev Branch"

    print_info "Creating dev branch if it doesn't exist..."

    if git show-ref --verify --quiet refs/heads/dev; then
        print_warning "Dev branch already exists"
        print_info "Switching to dev branch..."
        git checkout dev
    else
        print_info "Creating new dev branch..."
        git checkout -b dev
        print_success "Dev branch created"
    fi

    echo ""
}

# ==============================================================================
# Git Configuration
# ==============================================================================

configure_git() {
    print_header "Git Configuration"

    print_info "Creating .gitignore entries for sensitive files..."

    cat > .gitignore.cloudflare << 'EOF'
# Environment variables
.env
.env.local
.env.*.local

# Railway
.railway/

# Vercel
.vercel

# IDE
.idea/
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# Logs
*.log
logs/

# Temporary files
tmp/
temp/
EOF

    print_success ".gitignore entries created"
    print_warning "Review and merge with your existing .gitignore"

    echo ""
}

# ==============================================================================
# Summary
# ==============================================================================

print_summary() {
    print_header "Setup Summary"

    cat << 'EOF'
✓ Prerequisites checked and installed
✓ Environment variables template created
✓ Railway project initialized
✓ Dev branch ready
✓ Git configuration updated

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

NEXT STEPS:

1. Configure Environment Variables:
   cp .env.template .env
   nano .env  # Fill in your actual values

2. Set up MongoDB Atlas:
   - Go to: https://www.mongodb.com/cloud/atlas
   - Create a free M0 cluster
   - Add database user
   - Whitelist IP: 0.0.0.0/0
   - Copy connection string to .env

3. Set up Supabase:
   - Go to: https://supabase.com
   - Create a new project
   - Run the SQL script from DEPLOYMENT_GUIDE.md
   - Copy credentials to .env

4. Add GitHub Secrets:
   - Go to: https://github.com/<repo>/settings/secrets/actions
   - Add all values from .env

5. Deploy to Railway:
   - Push to dev branch: git push origin dev
   - Or deploy manually via Railway UI

6. Deploy to Vercel:
   - Connect your repo to Vercel
   - Configure environment variables
   - Deploy!

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📚 Documentation:
   - INFRASTRUCTURE_SETUP.md - Full architecture overview
   - DEPLOYMENT_GUIDE.md - Step-by-step deployment guide

🎯 Need Help?
   - Railway: https://docs.railway.app
   - Vercel: https://vercel.com/docs
   - MongoDB Atlas: https://docs.atlas.mongodb.com
   - Supabase: https://supabase.com/docs

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
EOF

    echo ""
}

# ==============================================================================
# Main Script
# ==============================================================================

main() {
    clear
    cat << 'EOF'
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║     Rapid Assist Foundation Domain - Cloud Setup             ║
║                                                               ║
║     This script will help you set up:                        ║
║     • Railway deployment                                     ║
║     • Vercel deployment                                      ║
║     • GitHub Actions CI/CD                                   ║
║     • Environment variables                                  ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
EOF

    echo ""

    # Run setup steps
    check_prerequisites
    setup_env_vars
    setup_railway
    configure_git
    create_dev_branch
    build_docker_images
    print_summary

    print_success "Setup complete!"
    print_info "Follow the next steps above to complete your deployment"
}

# Run main function
main "$@"
