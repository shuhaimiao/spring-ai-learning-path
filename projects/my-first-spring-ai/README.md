# Spring AI Learning Path - My First Spring AI Project

This project demonstrates Spring AI capabilities with PostgresML for embeddings and vector search.

## Prerequisites

- Docker and Docker Compose
- Java 17+ 
- Maven 3.6+

## Getting Started

### 1. Start the Database

```bash
# Navigate to the project directory
cd projects/my-first-spring-ai

# Start PostgreSQL with PostgresML
docker-compose up -d

# Check if the database is running
docker-compose ps
```

### 2. Configure API Keys

Set your OpenAI API key as an environment variable:

**Option A: Export in your shell**
```bash
export SPRING_AI_OPENAI_API_KEY=your_actual_openai_api_key_here
```

**Option B: Create a .env file** (recommended for development)
```bash
# Copy the example file
cp env.example .env

# Edit .env file and add your actual API key
SPRING_AI_OPENAI_API_KEY=your_actual_openai_api_key_here

# The app will automatically load .env files on startup
```

**Option C: Set when running Maven**
```bash
SPRING_AI_OPENAI_API_KEY=your_key_here mvn spring-boot:run
```

Get your OpenAI API key from: https://platform.openai.com/api-keys

### 3. Run the Applications

#### Standard Setup (Local Testing)
```bash
# Run the Scheduler service (port 8081)
cd scheduler
mvn spring-boot:run

# In another terminal, run the Adoptions service (port 8080)
cd ../adoptions
mvn spring-boot:run
```

#### MCP Setup with Ngrok (For OpenAI Function Calling)

For OpenAI to call the scheduler service as a function, you need to expose it publicly:

```bash
# 1. Install and configure ngrok (see NGROK_SETUP.md for details)
brew install ngrok/ngrok/ngrok
ngrok config add-authtoken YOUR_AUTHTOKEN

# 2. Start the scheduler service
cd scheduler
mvn spring-boot:run

# 3. In another terminal, expose the scheduler publicly
ngrok http 8081
# Note the public URL (e.g., https://abc123.ngrok-free.app)

# 4. In a third terminal, start adoptions with the ngrok URL
cd ../adoptions
mvn spring-boot:run -Dscheduler.url=https://abc123.ngrok-free.app
```

See `NGROK_SETUP.md` for complete ngrok installation and configuration instructions.

## Services

### Adoptions Service (Port 8080)
- **Endpoint**: `GET /{user}/assistant?question={question}`
- **Example**: `http://localhost:8080/john/assistant?question=What dogs are available?`

### Scheduler Service (Port 8081)
- **Health Check**: `GET /health`
- **Schedule Appointment**: `POST /schedule`
  ```json
  {
    "dogId": 1,
    "dogName": "Buddy"
  }
  ```

## Database Management

```bash
# Stop the database
docker-compose down

# Stop and remove all data (⚠️ destroys all data)
docker-compose down -v

# View database logs
docker-compose logs postgresml

# Connect to database directly
docker exec -it spring-ai-postgresml psql -U myappuser -d postgresml
```

## Security Best Practices

### API Key Management
- ✅ **Never commit API keys** to version control
- ✅ **Use environment variables** for sensitive configuration
- ✅ **Keep .env files local** (they're in .gitignore)
- ✅ **Rotate keys regularly** for production applications

### Environment Files
```bash
# .env files are ignored by git for security
echo ".env" >> .gitignore

# Use env.example as a template
cp env.example .env
# Edit .env with your actual values
```

## Troubleshooting

### Database Connection Issues
1. Ensure Docker is running
2. Check if port 5433 is available: `lsof -i :5433`
3. Wait for database health check to pass: `docker-compose logs postgres`

### Missing API Key
Set your OpenAI API key as an environment variable:
```bash
export SPRING_AI_OPENAI_API_KEY=your_key_here
```

### Port Already in Use
If you get "Port 8080 was already in use":
```bash
# Find and kill the process using port 8080
lsof -ti:8080 | xargs kill -9

# Or run on a different port
mvn spring-boot:run -Dserver.port=8081
```

# Reference 

https://spring.io/blog/2025/05/20/your-first-spring-ai-1