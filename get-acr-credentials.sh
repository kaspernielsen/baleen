#!/bin/bash

# Get Azure Container Registry credentials for GitHub Actions setup

echo "Fetching ACR credentials for sfs0cr..."
echo

# Get the credentials
CREDS=$(az acr credential show --name sfs0cr 2>&1)

if [ $? -eq 0 ]; then
    echo "ACR Credentials retrieved successfully!"
    echo
    echo "Add these as GitHub Secrets:"
    echo "=========================="
    echo
    echo "AZURE_REGISTRY_USERNAME:"
    echo "$CREDS" | jq -r '.username'
    echo
    echo "AZURE_REGISTRY_PASSWORD (use one of these):"
    echo "$CREDS" | jq -r '.passwords[0].value'
    echo
else
    echo "Error: Unable to fetch ACR credentials"
    echo "$CREDS"
    echo
    echo "You may need to:"
    echo "1. Login to Azure: az login"
    echo "2. Set the correct subscription: az account set --subscription 5526fd5e-4acf-4374-ac85-3e3138968df9"
    echo "3. Ensure you have access to the sfs0cr container registry"
fi