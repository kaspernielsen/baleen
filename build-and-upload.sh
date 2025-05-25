#!/bin/bash
# Remember to login to the repository before pushing
# az acr login --name sfs0cr

# Build the application
mvn -DskipTests clean install

# Build Docker image
docker build --platform linux/amd64 -f Dockerfile -t sfs0cr.azurecr.io/baleen-server .

# Push to Azure Container Registry
docker push sfs0cr.azurecr.io/baleen-server