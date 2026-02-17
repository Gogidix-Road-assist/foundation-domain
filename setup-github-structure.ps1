# GitHub Repository Structure Setup Script
# Creates proper domain structure for Rapid Assist project

$token = $env:GITHUB_TOKEN # Set via environment variable
$org = "ggx-insurance-saas"
$repo = "rapid-assist"

Write-Host "Setting up GitHub repository structure for Rapid Assist..." -ForegroundColor Green

# Kill any running git processes
Write-Host "Cleaning up git processes..." -ForegroundColor Yellow
taskkill /F /IM git.exe /T 2>$null

# Remove existing git directory
Write-Host "Removing existing git configuration..." -ForegroundColor Yellow
Remove-Item ".git" -Recurse -Force -ErrorAction SilentlyContinue

# Initialize fresh git repository
Write-Host "Initializing git repository..." -ForegroundColor Yellow
git init | Out-Null

# Create domain structure
Write-Host "Creating domain structure..." -ForegroundColor Yellow

# Foundation Domain
Write-Host "Creating Foundation Domain..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "Foundation-Domain/shared-infrastructure" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-frontend" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-backend" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-utilities" -Force | Out-Null

# Foundation Domain Subdomains
Write-Host "Creating Foundation Domain subdomains..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "Foundation-Domain/shared-infrastructure/config" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-infrastructure/security" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-infrastructure/monitoring" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-infrastructure/messaging" -Force | Out-Null
New-Item -ItemType Directory -Path "Foundation-Domain/shared-infrastructure/database" -Force | Out-Null

# Management Domain
Write-Host "Creating Management Domain..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "Management-domain/Central-Monitoring" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Infrastructure" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Admin-Tools" -Force | Out-Null

# Management Domain Subdomains
Write-Host "Creating Management Domain subdomains..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "Management-domain/Central-Monitoring/Frontend" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Central-Monitoring/Backend" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Infrastructure/DevOps" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Infrastructure/CI-CD" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Admin-Tools/User-Management" -Force | Out-Null
New-Item -ItemType Directory -Path "Management-domain/Admin-Tools/Analytics" -Force | Out-Null

# Business Domain
Write-Host "Creating Business Domain..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "Business-domain/individual-insurance-customer" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/corporate-insurance-customer" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/Insurance-claim-automation" -Force | Out-Null

# Business Domain Subdomains
Write-Host "Creating Business Domain subdomains..." -ForegroundColor Cyan
# Individual Insurance Customer Subdomains
New-Item -ItemType Directory -Path "Business-domain/individual-insurance-customer/Frontend" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/individual-insurance-customer/Backend" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/individual-insurance-customer/customer-accounts" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/individual-insurance-customer/payment-billing" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/individual-insurance-customer/insurance-verification" -Force | Out-Null

# Corporate Insurance Customer Subdomains
New-Item -ItemType Directory -Path "Business-domain/corporate-insurance-customer/Frontend" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/corporate-insurance-customer/Backend" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/corporate-insurance-customer/corporate-accounts" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/corporate-insurance-customer/partner-management" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/corporate-insurance-customer/service-catalog" -Force | Out-Null

# Insurance Claim Automation Subdomains
New-Item -ItemType Directory -Path "Business-domain/Insurance-claim-automation/Frontend" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/Insurance-claim-automation/Backend" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/Insurance-claim-automation/claims-processing" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/Insurance-claim-automation/fraud-detection" -Force | Out-Null
New-Item -ItemType Directory -Path "Business-domain/Insurance-claim-automation/approval-workflow" -Force | Out-Null

# Digital Insurance Company Platform
Write-Host "Creating Digital Insurance Company Platform..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Insurance-Domain" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Platform-Services" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Integration-Layer" -Force | Out-Null

# Digital Insurance Platform Subdomains
Write-Host "Creating Digital Insurance Platform subdomains..." -ForegroundColor Cyan
# Insurance Domain Subdomains
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Insurance-Domain/Policy-Management" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Insurance-Domain/Risk-Assessment" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Insurance-Domain/Premium-Calculations" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Insurance-Domain/Underwriting" -Force | Out-Null

# Platform Services Subdomains
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Platform-Services/API-Gateway" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Platform-Services/Service-Registry" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Platform-Services/Config-Server" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Platform-Services/Auth-Service" -Force | Out-Null

# Integration Layer Subdomains
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Integration-Layer/External-APIs" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Integration-Layer/Message-Queue" -Force | Out-Null
New-Item -ItemType Directory -Path "Digital-Insurance-Company-Platform/Integration-Layer/Data-Sync" -Force | Out-Null

# Create README files for each domain
Write-Host "Creating README files..." -ForegroundColor Yellow

# Foundation Domain README
@"
# Foundation Domain

Shared infrastructure and utilities for the Rapid Assist platform.

## Subdomains:
- **shared-infrastructure**: Common infrastructure components
  - config: Configuration management
  - security: Security utilities
  - monitoring: Monitoring and logging
  - messaging: Message queue integration
  - database: Database utilities
- **shared-frontend**: Shared UI components
- **shared-backend**: Shared backend services
- **shared-utilities**: Common utilities
"@ | Out-File -FilePath "Foundation-Domain/README.md" -Encoding UTF8

# Management Domain README
@"
# Management Domain

Administrative and operational tools for the Rapid Assist platform.

## Subdomains:
- **Central-Monitoring**: System monitoring and alerting
- **Infrastructure**: DevOps and CI/CD tools
- **Admin-Tools**: Administrative interfaces
"@ | Out-File -FilePath "Management-domain/README.md" -Encoding UTF8

# Business Domain README
@"
# Business Domain

Customer-facing business applications and services.

## Subdomains:
- **individual-insurance-customer**: Individual customer services
  - customer-accounts
  - payment-billing
  - insurance-verification
- **corporate-insurance-customer**: Corporate customer services
  - corporate-accounts
  - partner-management
  - service-catalog
- **Insurance-claim-automation**: Claims processing services
  - claims-processing
  - fraud-detection
  - approval-workflow
"@ | Out-File -FilePath "Business-domain/README.md" -Encoding UTF8

# Digital Insurance Platform README
@"
# Digital Insurance Company Platform

Core insurance domain services and platform infrastructure.

## Subdomains:
- **Insurance-Domain**: Insurance business logic
  - Policy-Management
  - Risk-Assessment
  - Premium-Calculations
  - Underwriting
- **Platform-Services**: Platform infrastructure
  - API-Gateway
  - Service-Registry
  - Config-Server
  - Auth-Service
- **Integration-Layer**: External integrations
  - External-APIs
  - Message-Queue
  - Data-Sync
"@ | Out-File -FilePath "Digital-Insurance-Company-Platform/README.md" -Encoding UTF8

# Create .gitkeep files to preserve empty directories
Write-Host "Creating .gitkeep files..." -ForegroundColor Yellow
Get-ChildItem -Recurse -Directory | ForEach-Object {
    New-Item -ItemType File -Path "$($_.FullName)/.gitkeep" -Force | Out-Null
}

# Create main README
@"
# Rapid Assist - GGX Insurance SaaS Platform

A comprehensive insurance SaaS platform built as a monorepo with multiple domains.

## Domain Structure

### Foundation Domain
Shared infrastructure and utilities for the entire platform.

### Management Domain
Administrative and operational tools.

### Business Domain
Customer-facing applications and services.

### Digital Insurance Company Platform
Core insurance domain services and platform infrastructure.

## Development Branch

This repository uses `dev` as the primary development branch.

## Getting Started

Clone the repository and start developing in your respective domain.

## License

Copyright © 2024 GGX Insurance SaaS. All rights reserved.
"@ | Out-File -FilePath "README.md" -Encoding UTF8

# Create comprehensive .gitignore
@"
# Dependencies
node_modules/
**/node_modules/
package-lock.json

# Build outputs
dist/
build/
target/
*.jar

# Environment files
.env
.env.local
*.env

# IDE
.vscode/
.idea/
*.swp

# Logs
logs/
*.log

# MongoDB
mongodb-data/
data/db/

# OS
.DS_Store
Thumbs.db
"@ | Out-File -FilePath ".gitignore" -Encoding UTF8

# Stage all files
Write-Host "Staging files..." -ForegroundColor Yellow
git add .

# Create initial commit
Write-Host "Creating initial commit..." -ForegroundColor Yellow
git commit -m "Initial commit: Rapid Assist domain structure

- Created Foundation Domain with shared infrastructure
- Created Management Domain with admin tools
- Created Business Domain with customer services
- Created Digital Insurance Company Platform
- All domains include proper subdomain structure
- Setup dev branch as primary development branch"

# Create dev branch
Write-Host "Creating dev branch..." -ForegroundColor Yellow
git checkout -b dev

# Add GitHub remote
Write-Host "Adding GitHub remote..." -ForegroundColor Yellow
git remote add origin "https://$($token)@github.com/$org/$repo.git"

# Push dev branch
Write-Host "Pushing to GitHub (dev branch)..." -ForegroundColor Yellow
git push -u origin dev

# Set dev as default branch
Write-Host "Setting dev as default branch..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "token $token"
    "Accept" = "application/vnd.github.v3+json"
}
$body = @{
    "default_branch" = "dev"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "https://api.github.com/repos/$org/$repo" -Method Patch -Headers $headers -Body $body -ContentType "application/json" | Out-Null
    Write-Host "Dev branch set as default!" -ForegroundColor Green
} catch {
    Write-Host "Note: Dev branch may need to be set as default manually in GitHub settings" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==================================" -ForegroundColor Green
Write-Host "Repository Setup Complete!" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green
Write-Host ""
Write-Host "Repository URL: https://github.com/$org/$repo" -ForegroundColor Cyan
Write-Host "Primary Branch: dev" -ForegroundColor Cyan
Write-Host "Domains created:" -ForegroundColor Cyan
Write-Host "  - Foundation-Domain" -ForegroundColor White
Write-Host "  - Management-domain" -ForegroundColor White
Write-Host "  - Business-domain" -ForegroundColor White
Write-Host "  - Digital-Insurance-Company-Platform" -ForegroundColor White
Write-Host ""
Write-Host "All subdomains have been created with proper structure." -ForegroundColor Green
Write-Host ""
