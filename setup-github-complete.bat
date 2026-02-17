@echo off
echo ================================================
echo Rapid Assist - GitHub Repository Setup
echo ================================================
echo.

REM Set GitHub configuration with new token
set GITHUB_TOKEN=%GITHUB_TOKEN% REM Set via environment variable
set GITHUB_USER=ggx-insurance-saas
set REPO_NAME=rapid-assist
set GITHUB_REPO=https://%GITHUB_USER%@github.com/%GITHUB_USER%/%REPO_NAME%.git

echo Step 1: Cleaning up any existing git locks...
if exist .git\index.lock (
    del /F /Q .git\index.lock 2>nul
    echo Git lock removed.
)

echo.
echo Step 2: Initializing git repository...
if exist .git (
    echo Git repository already exists.
) else (
    git init
    echo Git repository initialized.
)

echo.
echo Step 3: Staging files (this may take 10-15 minutes due to project size)...
echo Please be patient...
git add .

echo.
echo Step 4: Creating initial commit...
git commit -m "Initial commit: Rapid Assist - GGX Insurance SaaS Platform

This commit establishes the foundation for the Rapid Assist insurance SaaS platform,
a comprehensive monorepo containing multiple domains for roadside assistance,
insurance claims processing, and digital insurance company management.

Key components:
- Foundation Domain: Shared infrastructure and utilities
- Business Domain: Customer-facing applications and services
- Management Domain: Administrative and operational tools
- Digital Insurance Platform: Core insurance domain services

Technology stack includes Spring Boot microservices, React.js/React Native frontends,
MongoDB databases, and Kafka for event-driven architecture."

echo.
echo Step 5: Adding GitHub remote...
git remote remove origin 2>nul
git remote add origin https://%GITHUB_USER%:%GITHUB_TOKEN%@github.com/%GITHUB_USER%/%REPO_NAME%.git

echo.
echo Step 6: Pushing to GitHub...
echo This will upload your entire project to GitHub.
git branch -M main
git push -u origin main

echo.
echo ================================================
echo GitHub Repository Setup Complete!
echo ================================================
echo.
echo Repository URL: https://github.com/%GITHUB_USER%/%REPO_NAME%
echo.
echo You can now access your Rapid Assist project on GitHub!
echo.

pause
