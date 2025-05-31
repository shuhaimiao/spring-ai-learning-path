-- Table for dogs
CREATE TABLE IF NOT EXISTS dog (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    description TEXT
);

-- Note:
-- 'vector_store' table (for PgVector) and 'chat_message' table (for JDBC Chat Memory)
-- will be created by Spring AI if respective 'initialize-schema' properties are true.
