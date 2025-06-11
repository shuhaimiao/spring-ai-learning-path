-- Enable required extensions for Spring AI
CREATE EXTENSION IF NOT EXISTS vector;

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE postgresml TO myappuser;
GRANT ALL ON SCHEMA public TO myappuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO myappuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO myappuser;

-- Create tables if they don't exist (will be handled by Spring AI auto-configuration)
-- The vector_store and chat_message tables will be created automatically by Spring AI 