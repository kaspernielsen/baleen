-- Enable PostGIS extension for PostgreSQL (required for geometry support)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Note: The tables will be created automatically by Hibernate DDL auto-update
-- This file ensures PostGIS is available before table creation