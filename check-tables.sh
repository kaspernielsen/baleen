#!/bin/bash

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

DB_HOST="${DATABASE_HOST:-baleen-test-db.postgres.database.azure.com}"
DB_NAME="${DATABASE_NAME:-baleen}"
DB_USER="${DATABASE_USERNAME:-baleendbadmin}"
DB_PASSWORD="${DATABASE_PASSWORD}"

if [ -z "$DB_PASSWORD" ]; then
    echo "Error: DATABASE_PASSWORD not set"
    exit 1
fi

echo "Checking if secom_subscriber table exists..."

PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -d $DB_NAME -U $DB_USER -t -c "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'secom_subscriber');"