#!/bin/bash

# Script to enable PostGIS on Azure Database for PostgreSQL

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Azure Database parameters
SERVER_NAME="${AZURE_DB_SERVER:-baleen-test-db}"
RESOURCE_GROUP="${RESOURCE_GROUP:-sfs-enav-dev-rg}"
DB_NAME="${DATABASE_NAME:-baleen}"
DB_USER="${DATABASE_USERNAME:-baleendbadmin}"
DB_PASSWORD="${DATABASE_PASSWORD}"

if [ -z "$DB_PASSWORD" ]; then
    echo "Error: DATABASE_PASSWORD environment variable is not set"
    exit 1
fi

echo "Enabling PostGIS on Azure Database for PostgreSQL..."

# Step 1: Enable PostGIS extension via Azure CLI
echo "1. Configuring server to allow PostGIS extension..."
az postgres server configuration set \
    --resource-group $RESOURCE_GROUP \
    --server-name $SERVER_NAME \
    --name azure.extensions \
    --value postgis

echo "2. Restarting server to apply configuration..."
az postgres server restart \
    --resource-group $RESOURCE_GROUP \
    --name $SERVER_NAME

echo "3. Waiting for server to be ready (this may take a few minutes)..."
sleep 60

# Step 2: Create PostGIS extension in the database
echo "4. Creating PostGIS extension in database..."
PGPASSWORD=$DB_PASSWORD psql -h $SERVER_NAME.postgres.database.azure.com -p 5432 -d $DB_NAME -U $DB_USER -c "CREATE EXTENSION IF NOT EXISTS postgis;"

if [ $? -eq 0 ]; then
    echo "✅ PostGIS extension enabled successfully!"
    
    # Now run the original initialization script with PostGIS support
    echo "5. Running database initialization with PostGIS support..."
    PGPASSWORD=$DB_PASSWORD psql -h $SERVER_NAME.postgres.database.azure.com -p 5432 -d $DB_NAME -U $DB_USER -f src/main/resources/schema-azure-init.sql
    
    if [ $? -eq 0 ]; then
        echo "✅ Database initialization completed successfully!"
    else
        echo "❌ Database initialization failed!"
        exit 1
    fi
else
    echo "❌ Failed to create PostGIS extension!"
    echo ""
    echo "Alternative: Enable PostGIS via Azure Portal:"
    echo "1. Go to Azure Portal > Your PostgreSQL Server"
    echo "2. Navigate to 'Server parameters'"
    echo "3. Search for 'azure.extensions'"
    echo "4. Add 'POSTGIS' to the list of allowed extensions"
    echo "5. Save and restart the server"
    echo ""
    echo "Then run: ./init-azure-db.sh"
    exit 1
fi