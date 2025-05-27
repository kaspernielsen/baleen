#!/bin/bash

# Script to initialize Azure PostgreSQL database for Baleen

# Load environment variables from .env file if it exists
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Database connection parameters
DB_HOST="${DATABASE_HOST:-baleen-test-db.postgres.database.azure.com}"
DB_PORT="${DATABASE_PORT:-5432}"
DB_NAME="${DATABASE_NAME:-baleen}"
DB_USER="${DATABASE_USERNAME:-baleendbadmin}"
DB_PASSWORD="${DATABASE_PASSWORD}"

# Check if password is provided
if [ -z "$DB_PASSWORD" ]; then
    echo "Error: DATABASE_PASSWORD environment variable is not set"
    echo "Please set it or create a .env file with DATABASE_PASSWORD=your_password"
    exit 1
fi

echo "Initializing Azure PostgreSQL database..."
echo "Host: $DB_HOST"
echo "Database: $DB_NAME"
echo "User: $DB_USER"

# Execute the initialization script (using non-PostGIS version for Azure)
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -d $DB_NAME -U $DB_USER -f src/main/resources/schema-azure-init-no-postgis.sql

if [ $? -eq 0 ]; then
    echo "Database initialization completed successfully!"
else
    echo "Database initialization failed!"
    exit 1
fi