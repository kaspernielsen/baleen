# Release Procedures

This document describes the release process for Baleen using GitHub Releases and automated Docker image publishing.

## Overview

Baleen uses semantic versioning (MAJOR.MINOR.PATCH) and GitHub Actions to automatically build and publish Docker images to GitHub Container Registry (ghcr.io) when releases are created.

## Creating a Release

### Prerequisites

- Ensure all changes for the release are merged to the main branch
- Verify that the build is passing on the main branch
- Review and update the changelog if necessary

### Release Steps

1. **Navigate to the Releases Page**
   - Go to the [Baleen repository](https://github.com/dma-dk/baleen) on GitHub
   - Click on "Releases" in the right sidebar (or find it under the "Code" tab)

2. **Create a New Release**
   - Click the "Draft a new release" button

3. **Configure the Release**
   
   **Choose a tag:**
   - Click on "Choose a tag"
   - Type a new version tag following semantic versioning: `v1.0.0`
   - Format: `vMAJOR.MINOR.PATCH` (always prefix with 'v')
   - Click "Create new tag: vX.X.X on publish"

   **Target branch:**
   - Ensure "Target: main" is selected (or your default branch)

   **Release title:**
   - Use format: `v1.0.0 - Brief Description`
   - Example: `v1.0.0 - Initial Release`
   - Example: `v1.2.0 - SECOM Connector Improvements`

   **Release notes:**
   - Click "Generate release notes" for an automatic summary
   - Add a summary section at the top describing the main changes
   - Structure your notes with sections like:
     - ✨ New Features
     - 🐛 Bug Fixes
     - 🔧 Improvements
     - 💥 Breaking Changes (if any)
     - 📚 Documentation Updates

4. **Pre-release Option**
   - Check "Set as a pre-release" for beta/RC versions
   - Pre-releases will not update the `stable` or `latest` Docker tags

5. **Publish the Release**
   - Review all information
   - Click "Publish release" (green button)

### What Happens After Publishing

Once you publish the release, GitHub Actions will automatically:

1. Build the application with Maven
2. Run all tests
3. Build multi-architecture Docker images (amd64 and arm64)
4. Push images to GitHub Container Registry with these tags:
   - `ghcr.io/your-username/baleen:v1.0.0` (exact version)
   - `ghcr.io/your-username/baleen:1.0` (major.minor)
   - `ghcr.io/your-username/baleen:1` (major only)
   - `ghcr.io/your-username/baleen:stable` (if not pre-release)
   - `ghcr.io/your-username/baleen:latest` (if not pre-release)

## Version Numbering Guidelines

Follow semantic versioning:

- **MAJOR**: Breaking changes (increment when you make incompatible API changes)
- **MINOR**: New features (increment when you add functionality in a backwards compatible manner)
- **PATCH**: Bug fixes (increment when you make backwards compatible bug fixes)

Examples:
- `v1.0.0` → `v1.0.1`: Bug fix
- `v1.0.1` → `v1.1.0`: New feature added
- `v1.1.0` → `v2.0.0`: Breaking change

## Pre-releases

For testing releases before making them generally available:

1. Use pre-release tags: `v1.0.0-beta.1`, `v1.0.0-rc.1`
2. Check "Set as a pre-release" when creating the release
3. Pre-releases will not update `stable` or `latest` tags

## Docker Image Usage

After a release, users can pull the Docker image:

```bash
# Latest stable version
docker pull ghcr.io/your-username/baleen:latest

# Specific version
docker pull ghcr.io/your-username/baleen:v1.0.0

# Major version (gets latest 1.x.x)
docker pull ghcr.io/your-username/baleen:1
```

## Monitoring the Release

1. Check the Actions tab to monitor the build progress
2. Once complete, verify the Docker image is available in the Packages section
3. The release page will show download statistics and adoption

## Rollback Procedure

If issues are discovered after release:

1. Do not delete the release or tags
2. Create a new patch release with the fix
3. If critical, update the release notes warning users
4. Consider yanking the Docker image if security-related

## Best Practices

- Always test on the main branch before creating a release
- Keep release notes clear and user-focused
- Include migration guides for breaking changes
- Link to relevant issues and pull requests
- Credit contributors in the release notes
- Announce major releases in appropriate channels