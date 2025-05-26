#!/bin/bash

# Run Baleen locally on port 9090 with login enabled

echo "🔨 Building Baleen application..."
mvn -DskipTests clean install

echo "🚀 Starting Baleen on localhost:9090..."
echo "Login with demo/demo"
echo

java -jar target/baleen-0.1-SNAPSHOT.jar --spring.profiles.active=local