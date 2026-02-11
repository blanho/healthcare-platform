# CI/CD Documentation

## Overview

This project uses GitHub Actions for continuous integration and deployment.

## Workflows

### 1. CI (ci.yml)

**Triggers:** Push/PR to `main` or `develop`

**Jobs:**
- **Backend**: Build and test Java/Spring Boot application with PostgreSQL and Redis services
- **Frontend**: Build and lint React/TypeScript application
- **Docker**: Build Docker images (only on main branch push)

### 2. CD (cd.yml)

**Triggers:** Push to `main` or manual dispatch

**Jobs:**
- **Build & Push**: Builds Docker images and pushes to GitHub Container Registry (GHCR)
- **Deploy**: Deploys to DigitalOcean App Platform
- **Notify**: Reports deployment status

### 3. Code Quality (quality.yml)

**Triggers:** Push/PR to `main` or `develop`

**Jobs:**
- **Backend Quality**: Checkstyle, SpotBugs, PMD
- **Frontend Quality**: ESLint, TypeScript check
- **Security**: OWASP Dependency Check

### 4. Pull Request (pr.yml)

**Triggers:** PR to `main` or `develop`

**Jobs:**
- **PR Validation**: Conventional commit title format
- **Size Check**: Warns on large PRs (>1000 lines)
- **Auto Label**: Labels PR based on changed files

## Required Secrets

Configure these secrets in GitHub repository settings:

### For Deployment

| Secret | Description |
|--------|-------------|
| `DIGITALOCEAN_ACCESS_TOKEN` | DigitalOcean API token |
| `DIGITALOCEAN_APP_ID` | DigitalOcean App Platform app ID |

### For Droplet Deployment (Alternative)

| Secret | Description |
|--------|-------------|
| `DROPLET_HOST` | Droplet IP address |
| `DROPLET_USERNAME` | SSH username (usually `root`) |
| `DROPLET_SSH_KEY` | Private SSH key |

## Setup Instructions

### 1. Enable GitHub Container Registry

1. Go to repository Settings → Actions → General
2. Under "Workflow permissions", select "Read and write permissions"
3. Check "Allow GitHub Actions to create and approve pull requests"

### 2. Create DigitalOcean Access Token

1. Log in to DigitalOcean
2. Go to API → Tokens/Keys
3. Generate new token with read/write access
4. Add as `DIGITALOCEAN_ACCESS_TOKEN` secret

### 3. Set Up DigitalOcean App Platform

1. Create new App in DigitalOcean App Platform
2. Note the App ID from the URL or `doctl apps list`
3. Add as `DIGITALOCEAN_APP_ID` secret

### 4. Configure Environment Variables

Set these environment variables in DigitalOcean App Platform:

```
DATABASE_URL=your-managed-db-url
DATABASE_USERNAME=your-db-user
DATABASE_PASSWORD=your-db-password
REDIS_HOST=your-managed-redis-host
REDIS_PORT=25061
REDIS_PASSWORD=your-redis-password
JWT_SECRET=your-production-jwt-secret
```

## Branch Protection

Recommended branch protection rules for `main`:

1. Go to Settings → Branches → Add rule
2. Branch name pattern: `main`
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require approvals (1 or more)
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
4. Status checks to require:
   - `Backend (Java)`
   - `Frontend (React)`

## Dependabot

Automated dependency updates are configured:

- **Maven**: Weekly on Mondays
- **npm**: Weekly on Mondays
- **GitHub Actions**: Weekly on Mondays
- **Docker**: Monthly

## Workflow Badges

Add these to your README:

```markdown
![CI](https://github.com/YOUR_USERNAME/healthcare-platform/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/YOUR_USERNAME/healthcare-platform/actions/workflows/cd.yml/badge.svg)
![Code Quality](https://github.com/YOUR_USERNAME/healthcare-platform/actions/workflows/quality.yml/badge.svg)
```

## Local Testing

Test workflows locally using [act](https://github.com/nektos/act):

```bash
# Install act
brew install act

# Run CI workflow
act push -W .github/workflows/ci.yml

# Run specific job
act push -W .github/workflows/ci.yml -j backend
```

## Troubleshooting

### Build Failures

1. Check Java version matches (21)
2. Verify Node version matches (20)
3. Ensure all tests pass locally

### Docker Push Failures

1. Verify GITHUB_TOKEN has write access
2. Check package visibility settings

### Deployment Failures

1. Verify DigitalOcean tokens are valid
2. Check App Platform logs in DigitalOcean console
3. Verify environment variables are set correctly
