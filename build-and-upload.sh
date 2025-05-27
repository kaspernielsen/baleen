#!/bin/bash
# Build and Deploy Baleen to Azure Container Instances
# Remember to login to the repository before running: az acr login --name sfs0cr
# Usage: ./build-and-upload.sh [--recreate]

set -e

RESOURCE_GROUP="sfs-enav-dev-rg"
CONTAINER_NAME="baleen-test-server"
IMAGE="sfs0cr.azurecr.io/baleen-server"
DNS_LABEL="baleen-test-server"
LOCATION="northeurope"

# Check for --recreate flag
RECREATE=false
if [[ "$1" == "--recreate" ]]; then
    RECREATE=true
    echo "🔄 Recreation mode enabled - will delete and recreate container if it exists"
fi

echo "🔨 Building Baleen application..."

# Build the application
mvn -DskipTests clean install

echo "🐳 Building Docker image..."

# Build Docker image
docker build --platform linux/amd64 -f Dockerfile -t $IMAGE .

echo "📤 Pushing to Azure Container Registry..."

# Push to Azure Container Registry
docker push $IMAGE

echo "🚀 Updating Azure Container Instance..."

# Check if DATABASE_PASSWORD is set
if [ -z "$DATABASE_PASSWORD" ]; then
    echo "❌ ERROR: DATABASE_PASSWORD environment variable is not set!"
    echo "Please set it before running this script:"
    echo "  export DATABASE_PASSWORD=your-password"
    exit 1
fi

# Check if container exists
if az container show --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME --query "id" -o tsv >/dev/null 2>&1; then
    if [ "$RECREATE" = true ]; then
        echo "Container exists, deleting and recreating..."
        # Delete and recreate to update environment variables
        az container delete --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME --yes
        az container create \
          --resource-group $RESOURCE_GROUP \
          --name $CONTAINER_NAME \
          --image $IMAGE \
          --cpu 1 \
          --memory 2 \
          --os-type Linux \
          --registry-login-server sfs0cr.azurecr.io \
          --registry-username sfs0cr \
          --registry-password $(az acr credential show --name sfs0cr --query passwords[0].value -o tsv) \
          --dns-name-label $DNS_LABEL \
          --ports 8080 8443 \
          --environment-variables \
            SPRING_PROFILES_ACTIVE=azure \
            SERVER_PORT=8080 \
            DATABASE_PASSWORD="$DATABASE_PASSWORD" \
            DDL_AUTO="${DDL_AUTO:-update}" \
          --restart-policy Always \
          --location $LOCATION
    else
        echo "Container exists, restarting to pull new image..."
        az container restart --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME
        echo
        echo "ℹ️  Note: Container restarted with new image."
        echo "   Environment variables were NOT updated."
        echo "   To update environment variables, run with --recreate flag:"
        echo "   ./build-and-upload.sh --recreate"
    fi
else
    echo "Container doesn't exist, creating new one..."
    az container create \
      --resource-group $RESOURCE_GROUP \
      --name $CONTAINER_NAME \
      --image $IMAGE \
      --cpu 1 \
      --memory 2 \
      --os-type Linux \
      --registry-login-server sfs0cr.azurecr.io \
      --registry-username sfs0cr \
      --registry-password $(az acr credential show --name sfs0cr --query passwords[0].value -o tsv) \
      --dns-name-label $DNS_LABEL \
      --ports 8080 8443 \
      --environment-variables \
        SPRING_PROFILES_ACTIVE=azure \
        SERVER_PORT=8080 \
        DATABASE_PASSWORD="$DATABASE_PASSWORD" \
        DDL_AUTO="${DDL_AUTO:-update}" \
      --restart-policy Always \
      --location $LOCATION
fi

echo
echo "✅ Deployment complete!"
echo "HTTP:  http://$DNS_LABEL.northeurope.azurecontainer.io:8080"
echo "HTTPS: https://$DNS_LABEL.northeurope.azurecontainer.io:8443"