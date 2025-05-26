#!/bin/bash
# Build and Deploy Baleen to Azure Container Instances
# Remember to login to the repository before running: az acr login --name sfs0cr

set -e

RESOURCE_GROUP="sfs-enav-dev-rg"
CONTAINER_NAME="baleen-test-server"
IMAGE="sfs0cr.azurecr.io/baleen-server"
DNS_LABEL="baleen-test-server"
LOCATION="northeurope"

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

# Check if container exists
if az container show --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME --query "id" -o tsv >/dev/null 2>&1; then
    echo "Container exists, restarting with new image..."
    az container restart --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME
else
    echo "Container doesn't exist, creating new one..."
    az container create \
      --resource-group $RESOURCE_GROUP \
      --name $CONTAINER_NAME \
      --image $IMAGE \
      --cpu 1 \
      --memory 2 \
      --registry-login-server sfs0cr.azurecr.io \
      --dns-name-label $DNS_LABEL \
      --ports 8080 8443 \
      --environment-variables \
        SPRING_PROFILES_ACTIVE=docker \
        SERVER_PORT=8080 \
      --restart-policy Always \
      --location $LOCATION
fi

echo
echo "✅ Deployment complete!"
echo "HTTP:  http://$DNS_LABEL.northeurope.azurecontainer.io:8080"
echo "HTTPS: https://$DNS_LABEL.northeurope.azurecontainer.io:8443"