-- ==============================================================================
-- DATABASE INITIALIZATION SCRIPT FOR POSTGRESQL
-- Execute this script in your PostgreSQL server (pgAdmin or psql) before starting backend
-- ==============================================================================

-- 1. Create auth_db database for Auth Microservice
CREATE DATABASE auth_db;

-- 2. Create main_db database for Monolith Core
CREATE DATABASE main_db;

-- Grant all privileges to user postgres
GRANT ALL PRIVILEGES ON DATABASE auth_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE main_db TO postgres;
