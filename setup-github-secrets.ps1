# GitHub Secrets Setup Script
# CTO/DevOps Automation - Development Environment

$ErrorActionPreference = "Stop"

# Configuration
$token = $env:GITHUB_TOKEN # Set via environment variable
$owner = "ggx-insurance-saas"
$repo = "Insurance-company-Saas"
$apiUrl = "https://api.github.com"

$headers = @{
    "Authorization" = "token $token"
    "Accept" = "application/vnd.github+json"
    "X-GitHub-Api-Version" = "2022-11-28"
}

# Function to create or update a secret
function Set-GitHubSecret {
    param(
        [string]$name,
        [string]$value
    )

    # GitHub requires secrets to be encrypted using public key
    # First, get the repository's public key
    $publicKeyUrl = "$apiUrl/repos/$owner/$repo/actions/secrets/public-key"

    try {
        $keyResponse = Invoke-RestMethod -Uri $publicKeyUrl -Method GET -Headers $headers
        $publicKey = $keyResponse.key
        $keyId = $keyResponse.key_id

        # Encrypt the secret value using the public key
        $encryptedValue = Encrypt-Secret -value $value -publicKey $publicKey

        # Create or update the secret
        $secretUrl = "$apiUrl/repos/$owner/$repo/actions/secrets/$name"

        $body = @{
            "encrypted_value" = $encryptedValue
            "key_id" = $keyId
        } | ConvertTo-Json

        $response = Invoke-RestMethod -Uri $secretUrl -Method PUT -Headers $headers -Body $body -ContentType "application/json"

        Write-Host "✅ Created secret: $name" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host "❌ Failed to create secret: $name" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
        return $false
    }
}

# Function to encrypt a value using RSA public key
function Encrypt-Secret {
    param(
        [string]$value,
        [string]$publicKey
    )

    # Convert public key from PEM format
    $pem = $publicKey -replace "-----BEGIN PUBLIC KEY-----" -replace "-----END PUBLIC KEY-----" -replace "`n", ""
    $keyBytes = [Convert]::FromBase64String($pem)

    # Create RSA object and import key
    $rsa = [System.Security.Cryptography.RSA]::Create()
    $rsa.ImportSubjectPublicKeyInfo($keyBytes, [ref]$null)

    # Encrypt the value
    $valueBytes = [System.Text.Encoding]::UTF8.GetBytes($value)
    $encryptedBytes = $rsa.Encrypt($valueBytes, [System.Security.Cryptography.RSAEncryptionPadding]::OaepSHA256)

    return [Convert]::ToBase64String($encryptedBytes)
}

# Main execution
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "    GITHUB SECRETS AUTOMATED SETUP - DEVELOPMENT ENVIRONMENT    " -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Secrets to configure - Set these via environment variables or update with your actual values
$secrets = @{
    # Railway Secrets
    "RAILWAY_API_TOKEN"      = $env:RAILWAY_API_TOKEN ?? "<your-railway-token>"
    "RAILWAY_PROJECT_ID_DEV" = $env:RAILWAY_PROJECT_ID_DEV ?? "<your-railway-project-id>"

    # Vercel Secrets
    "VERCEL_TOKEN"       = $env:VERCEL_TOKEN ?? "<your-vercel-token>"
    "VERCEL_ORG_ID"      = $env:VERCEL_ORG_ID ?? "<your-vercel-org-id>"
    "VERCEL_PROJECT_ID"  = $env:VERCEL_PROJECT_ID ?? "<your-vercel-project-id>"

    # Redis Secrets
    "REDIS_USERNAME" = $env:REDIS_USERNAME ?? "default"
    "REDIS_PASSWORD" = $env:REDIS_PASSWORD ?? "<your-redis-password>"
    "REDIS_PORT"     = "6379"

    # Auth0/JWT Secrets
    "AUTH0_DOMAIN"   = $env:AUTH0_DOMAIN ?? "<your-auth0-domain>"
    "JWT_SECRET_DEV" = $env:JWT_SECRET_DEV ?? "<your-jwt-secret>"

    # Postman Secrets
    "POSTMAN_API_KEY"       = $env:POSTMAN_API_KEY ?? "<your-postman-api-key>"
    "POSTMAN_WORKSPACE_ID"  = $env:POSTMAN_WORKSPACE_ID ?? "<your-postman-workspace-id>"
}

$successCount = 0
$failCount = 0

foreach ($secret in $secrets.GetEnumerator()) {
    $result = Set-GitHubSecret -name $secret.Key -value $secret.Value
    if ($result) {
        $successCount++
    }
    else {
        $failCount++
    }
    Start-Sleep -Milliseconds 500
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "                    SETUP SUMMARY                          " -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "Total secrets: $($secrets.Count)"
Write-Host "Successful: $successCount" -ForegroundColor Green
Write-Host "Failed: $failCount" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Green" })
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

if ($failCount -eq 0) {
    Write-Host "✅ All GitHub secrets configured successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Verify secrets at: https://github.com/$owner/$repo/settings/secrets/actions"
    Write-Host "2. Test the deployment pipeline"
}
else {
    Write-Host "⚠️ Some secrets failed to configure. Please check the errors above." -ForegroundColor Yellow
}

Write-Host ""
