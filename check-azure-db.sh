#!/bin/bash

# Script to check existing table structure in Azure PostgreSQL

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
    exit 1
fi

echo "Checking existing table structures..."

# Check existing tables and their columns
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -d $DB_NAME -U $DB_USER << 'EOF'
-- List all tables
\echo "=== EXISTING TABLES ==="
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Check secom_node_entity structure
\echo ""
\echo "=== SECOM_NODE_ENTITY STRUCTURE ==="
\d secom_node_entity

-- Check if secom_subscriber exists
\echo ""
\echo "=== CHECKING SECOM_SUBSCRIBER ==="
\d secom_subscriber

-- Check secom_transactional_entity structure
\echo ""
\echo "=== SECOM_TRANSACTIONAL_ENTITY STRUCTURE ==="
\d secom_transactional_entity

-- Check for PostGIS
\echo ""
\echo "=== CHECKING FOR POSTGIS ==="
SELECT * FROM pg_extension WHERE extname = 'postgis';
EOF