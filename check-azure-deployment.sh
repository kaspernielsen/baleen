#!/bin/bash

# Script to check Azure Container Instance status and logs

RESOURCE_GROUP="sfs-enav-dev-rg"
CONTAINER_NAME="baleen-test-server"

echo "=== Checking Container Status ==="
az container show \
  --resource-group $RESOURCE_GROUP \
  --name $CONTAINER_NAME \
  --query "{Status:instanceView.state, Events:instanceView.events[*].{Time:firstTimestamp, Message:message, Type:type}}" \
  --output table

echo ""
echo "=== Container Details ==="
az container show \
  --resource-group $RESOURCE_GROUP \
  --name $CONTAINER_NAME \
  --query "{State:containers[0].instanceView.currentState.state, ExitCode:containers[0].instanceView.currentState.exitCode, StartTime:containers[0].instanceView.currentState.startTime, RestartCount:containers[0].instanceView.restartCount}" \
  --output table

echo ""
echo "=== Environment Variables ==="
az container show \
  --resource-group $RESOURCE_GROUP \
  --name $CONTAINER_NAME \
  --query "containers[0].environmentVariables[*].{Name:name, Value:value}" \
  --output table

echo ""
echo "=== Container Logs ==="
az container logs \
  --resource-group $RESOURCE_GROUP \
  --name $CONTAINER_NAME \
  --follow=false

echo ""
echo "=== Container Events (Last 10) ==="
az container show \
  --resource-group $RESOURCE_GROUP \
  --name $CONTAINER_NAME \
  --query "instanceView.events[-10:].{Time:firstTimestamp, Message:message}" \
  --output table