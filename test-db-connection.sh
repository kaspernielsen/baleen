#!/bin/bash

# Database connection test script for Azure PostgreSQL

# Default values
DB_HOST="baleen-test-db.postgres.database.azure.com"
DB_PORT="5432"
DB_NAME="baleen"
DB_USER="baleendbadmin"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "Azure PostgreSQL Connection Test"
echo "================================"
echo "Host: $DB_HOST"
echo "Port: $DB_PORT"
echo "Database: $DB_NAME"
echo ""

# Function to test connection
test_connection() {
    local username=$1
    local password=$2
    
    echo -e "${YELLOW}Testing connection with username: $username${NC}"
    
    # Try with psql if available
    if command -v psql &> /dev/null; then
        PGPASSWORD="$password" psql -h "$DB_HOST" -p "$DB_PORT" -U "$username" -d "$DB_NAME" -c "SELECT version();" 2>&1
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ Connection successful with psql!${NC}"
            return 0
        else
            echo -e "${RED}✗ Connection failed with psql${NC}"
        fi
    fi
    
    # Try with JDBC URL format
    echo ""
    echo "JDBC URL formats to test:"
    echo "1. jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME?sslmode=require"
    echo "2. jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory"
    
    return 1
}

# Main script
if [ $# -eq 0 ]; then
    echo "Usage: $0 <password> [username]"
    echo "Example: $0 'mypassword'"
    echo "Example: $0 'mypassword' 'baleendbadmin@baleen-test-db'"
    exit 1
fi

PASSWORD="$1"
CUSTOM_USER="${2:-$DB_USER}"

echo ""
echo "Testing with provided credentials..."
echo "===================================="

# Test 1: Plain username
test_connection "$DB_USER" "$PASSWORD"

echo ""
echo "===================================="

# Test 2: Username with @servername
test_connection "${DB_USER}@baleen-test-db" "$PASSWORD"

echo ""
echo "===================================="

# Test 3: Custom username if provided
if [ "$CUSTOM_USER" != "$DB_USER" ]; then
    test_connection "$CUSTOM_USER" "$PASSWORD"
fi

echo ""
echo "Alternative connection test using curl (if psql not available):"
echo "==============================================="
echo "You can also test with a simple Java program:"
echo ""
cat << 'EOF'
import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        String[] usernames = {"baleendbadmin", "baleendbadmin@baleen-test-db"};
        String password = args[0];
        
        for (String username : usernames) {
            System.out.println("\nTesting with username: " + username);
            String url = "jdbc:postgresql://baleen-test-db.postgres.database.azure.com:5432/baleen?sslmode=require";
            
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                System.out.println("✓ Connection successful!");
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            } catch (SQLException e) {
                System.out.println("✗ Connection failed: " + e.getMessage());
            }
        }
    }
}
EOF

echo ""
echo "To run the Java test:"
echo "1. Save the above code to TestConnection.java"
echo "2. Compile: javac TestConnection.java"
echo "3. Run: java -cp .:postgresql-42.x.x.jar TestConnection 'yourpassword'"