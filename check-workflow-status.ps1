# Check GitHub Actions Workflow Status
$token = $env:GITHUB_TOKEN # Set via environment variable
$org = "ggx-insurance-saas"
$repo = "Insurance-company-Saas"

$headers = @{
    "Authorization" = "token $token"
    "Accept" = "application/vnd.github.v3+json"
}

Write-Host "Checking GitHub Actions workflow runs..." -ForegroundColor Yellow
Write-Host ""

try {
    # Get recent workflow runs
    $response = Invoke-RestMethod -Uri "https://api.github.com/repos/$org/$repo/actions/runs" -Headers $headers

    Write-Host "Recent Workflow Runs:" -ForegroundColor Green
    Write-Host "========================" -ForegroundColor Green

    foreach ($run in $response.workflow_runs | Select-Object -First 5) {
        Write-Host ""
        Write-Host "Workflow: $($run.name)" -ForegroundColor Cyan
        Write-Host "Status: $($run.status)" -ForegroundColor White
        Write-Host "Conclusion: $($run.conclusion)" -ForegroundColor White
        Write-Host "Created: $($run.created_at)" -ForegroundColor White
        Write-Host "Triggered by: $($run.event)" -ForegroundColor White
        Write-Host "URL: $($run.html_url)" -ForegroundColor Yellow

        # Get job details for this run
        $jobsResponse = Invoke-RestMethod -Uri $run.jobs_url -Headers $headers
        Write-Host ""
        Write-Host "Jobs in this workflow:" -ForegroundColor Magenta

        foreach ($job in $jobsResponse.jobs) {
            $statusColor = if ($job.conclusion -eq "success") { "Green" } elseif ($job.conclusion -eq "failure") { "Red" } else { "Yellow" }
            Write-Host "  - $($job.name): $($job.conclusion.ToUpper())" -ForegroundColor $statusColor
        }
    }

} catch {
    Write-Host "Error checking workflow status: $_" -ForegroundColor Red
    Write-Host "The workflow might still be initializing..." -ForegroundColor Yellow
}
