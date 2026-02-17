# GitHub Repository Creation Script with updated name
$token = $env:GITHUB_TOKEN # Set via environment variable
$headers = @{
    "Authorization" = "token $token"
    "Accept" = "application/vnd.github.v3+json"
}

$body = @{
    "name" = "Insurance-company-Saas"
    "description" = "Insurance Company SaaS Platform - Comprehensive insurance management platform with roadside assistance, claims processing, and digital insurance services"
    "private" = $false
    "has_issues" = $true
    "has_projects" = $true
    "has_wiki" = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "https://api.github.com/orgs/ggx-insurance-saas/repos" -Method Post -Headers $headers -Body $body -ContentType "application/json"
    Write-Host "Repository created successfully!" -ForegroundColor Green
    Write-Host "Repository URL: $($response.html_url)" -ForegroundColor Cyan
    Write-Host "Clone URL: $($response.clone_url)" -ForegroundColor Cyan
} catch {
    Write-Host "Error creating repository: $_" -ForegroundColor Red
    Write-Host "The repository might already exist. You can proceed with the setup." -ForegroundColor Yellow
}
