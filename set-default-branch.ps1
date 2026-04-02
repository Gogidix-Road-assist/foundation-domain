# Set dev as default branch
$token = $env:GITHUB_TOKEN # Set via environment variable
$org = "ggx-insurance-saas"
$repo = "Insurance-company-Saas"

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
