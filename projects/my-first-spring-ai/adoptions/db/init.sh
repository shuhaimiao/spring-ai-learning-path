#!/bin/bash
# This script should initialize the database with an application user.
# The tutorial does not provide the exact commands.
# Typical commands using psql might look like:
# Ensure the container 'postgres-ai' is running first.

# Example for creating database if it doesn't exist (some images create a default db)
# docker exec -i postgres-ai psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'postgresml'" | grep -q 1 || \
#   docker exec -i postgres-ai psql -U postgres -c "CREATE DATABASE postgresml"

# Example for creating user and granting privileges:
# docker exec -i postgres-ai psql -U postgres -d postgresml <<EOF
# CREATE USER myappuser WITH PASSWORD 'mypassword';
# GRANT ALL PRIVILEGES ON DATABASE postgresml TO myappuser;
# GRANT ALL ON SCHEMA public TO myappuser;
# EOF

echo "TODO: Replace with actual psql or other commands to initialize the user 'myappuser'"
echo "The user 'myappuser' with password 'mypassword' should be created and granted permissions on the 'postgresml' database."
echo "This may include creating the 'postgresml' database itself if not done by the Docker image setup."
