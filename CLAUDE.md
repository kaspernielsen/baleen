# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Baleen is a Spring Boot-based S-124 navigational warnings management platform implementing IHO S-100 series standards. It provides specialized data exchange services for S-124 Navigational Warnings using SECOM protocol and web connectors, with integration to the Niord system.

## Build Commands

```bash
# Build the application (skip tests for faster build)
mvn -DskipTests clean install

# Build with tests
mvn clean install

# Run a single test
mvn test -Dtest=ClassName#methodName

# Build and push Docker image
./build-and-upload.sh

# Manual Docker build
mvn -DskipTests clean install
docker build --platform linux/amd64 -f Dockerfile -t sfs0cr.azurecr.io/baleen-server .
docker push sfs0cr.azurecr.io/baleen-server
```

## Frontend Development

The project includes an Angular frontend located in `src/main/frontend/`.

```bash
# Navigate to frontend directory
cd src/main/frontend

# Install dependencies (first time only)
npm install

# Run Angular dev server (proxies API calls to :8080)
npm start

# Build for production (outputs to src/main/resources/static)
npm run build

# The Maven build automatically builds the Angular app
mvn clean install
```

### Frontend Structure
- Angular 19 standalone components
- Builds to Spring Boot static resources directory
- API calls proxied to `/api/*` endpoints
- Development server runs on http://localhost:4200
- REST API endpoint moved to `/api/hello` to avoid conflicts with Angular routing

## Architecture

### Project Structure
The project is a single Spring Boot application containing:
- Core entities, SPIs, and shared functionality
- SECOM protocol connector implementation
- REST/Web connector implementation
- S-124 Navigational Warnings implementation
- Niord system integration

### Key Design Patterns
- **Connector/Provider Pattern**: Abstract communication protocols through `BaleenProvider` interface
- **DataBundle**: Core data exchange unit containing dataset ID, bounds, and GML data
- **Inventory/Ledger System**: Track dataset versions and updates
- **GML-based Storage**: Spatial data stored as Geography Markup Language with H2GIS support

### Database
- H2 in-memory database with spatial extensions (H2GIS)
- Hibernate Spatial for ORM
- Test configurations use PostgreSQL mode

### Security
- MCP (Maritime Connectivity Platform) integration
- SECOM secure communications
- Certificate-based authentication (PKCS12 keystores)

## Common Development Tasks

### Adding a New S-100 Product Type
1. Create new package `dk.dma.baleen.product.sXXX` following existing product structure
2. Implement `ProductDataService` interface
3. Add XML bindings in `support/xml-bindings/java/s-XXX/`
4. Add XML binding dependency to baleen-app pom.xml

### Working with Spatial Data
- Use H2GIS spatial functions for geometry operations
- GML datasets are parsed using JAXB-generated classes
- Spatial queries use JTS (Java Topology Suite) geometries

### Testing
- Unit tests use in-memory H2 database
- Test data located in `src/test/resources/datasets/`
- Spring Boot Test with `@SpringBootTest` annotation
- AssertJ for assertions

## Deployment to Azure

### Prerequisites
- Azure CLI installed and logged in (`az login`)
- Access to Azure Container Registry (`az acr login --name sfs0cr`)
- Database password stored in environment variable

### Deploy to Azure Container Instances
```bash
# Set database password (required)
export DATABASE_PASSWORD="your-password-here"

# Deploy (builds, pushes to ACR, and updates container)
./build-and-upload.sh

# Force recreate container (useful for environment variable changes)
./build-and-upload.sh --recreate
```

### Check Deployment Status
```bash
# View container logs
az container logs --resource-group sfs-enav-dev-rg --name baleen-test-server

# Check container status
az container show --resource-group sfs-enav-dev-rg --name baleen-test-server --query instanceView.state

# Access the application
# URL: http://baleen-test-server.northeurope.azurecontainer.io:8080
```

### Environment Variables
The deployment sets the following environment variables:
- `SPRING_PROFILES_ACTIVE=azure` - Activates Azure-specific configuration
- `SERVER_PORT=8080` - Application port
- `DATABASE_PASSWORD` - PostgreSQL password (from your environment)
- `DDL_AUTO=update` - Hibernate DDL mode (optional, defaults to update)

### Database Configuration
- Azure PostgreSQL with PostGIS extension
- Connection details in `application-azure.properties`
- Hibernate 6.x auto-detects PostgreSQL dialect
- Tables are created automatically on first run

### Troubleshooting Deployment
1. **Database connection issues**: Ensure DATABASE_PASSWORD is set correctly
2. **Container fails to start**: Check logs with `az container logs`
3. **Hibernate dialect errors**: The application uses Hibernate 6.x which auto-detects dialects
4. **PostGIS not available**: Enable PostGIS extension on Azure PostgreSQL instance