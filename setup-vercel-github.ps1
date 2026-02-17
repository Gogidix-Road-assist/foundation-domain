# Vercel GitHub Integration Setup Script
$env:VERCEL_TOKEN = $env:VERCEL_TOKEN # Set via environment variable
$orgId = "team_ezvVYBQFig1JfaEwJcKfcmc9"
$projectId = "gGUL2QspOcOpjkmY5N2iIHTp"
$repo = "ggx-insurance-saas/Insurance-company-Saas"

Write-Host "Configuring Vercel GitHub integration..."
Write-Host "Org ID: $orgId"
Write-Host "Project ID: $projectId"
Write-Host "Repository: $repo"

# Use Vercel CLI to link the project
$env:VERCEL_ORG_ID = $orgId

Write-Host ""
Write-Host "Linking Vercel project to GitHub repository..."
vercel link --yes --token=$env:VERCEL_TOKEN --scope=$orgId
