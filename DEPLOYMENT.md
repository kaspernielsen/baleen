# Baleen Deployment Guide

## Manual Deployment Process

Since automated deployment isn't available in your environment, use the manual deployment scripts.

### Prerequisites

1. Azure CLI installed and logged in
2. Access to Azure Container Registry (sfs0cr.azurecr.io)
3. Access to Azure resource group (sfs-enav-dev-rg)

### Deployment URL

Once deployed, the application will be available at:
- **HTTP**: http://baleen-dev-server.northeurope.azurecontainer.io:8080
- **HTTPS (mTLS)**: https://baleen-dev-server.northeurope.azurecontainer.io:8443

### Caddy Configuration

Point your Caddy reverse proxy to the container instance:

```caddy
baleen.yourdomain.com {
    reverse_proxy http://baleen-dev-server.northeurope.azurecontainer.io:8080
}
```

### Manual Deployment Steps

1. **Build and Push Image**:
   ```bash
   # Login to Azure Container Registry (if needed)
   az acr login --name sfs0cr
   
   # Build and push Docker image
   ./build-and-upload.sh
   ```

2. **Deploy to Azure Container Instances**:
   ```bash
   # Deploy the container
   ./deploy-to-aci.sh
   ```

### One-Command Deployment

For convenience, you can run both steps together:
```bash
az acr login --name sfs0cr && ./build-and-upload.sh && ./deploy-to-aci.sh
```

### Monitoring Deployment

View deployment logs:
```bash
az container logs --resource-group sfs-enav-dev-rg --name baleen-dev-server --follow
```

Check container status:
```bash
az container show --resource-group sfs-enav-dev-rg --name baleen-dev-server
```

### mTLS Configuration

The dev environment includes test certificates:
- Keystore: `/app/keystore.p12` (mcp-baleen-test-keystore.p12)
- Truststore: `/app/truststore.p12`
- MCP Truststore: `/app/mcp-truststore.jks`

These are copied from the repository during Docker build for the dev environment.