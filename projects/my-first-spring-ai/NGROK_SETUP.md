# Ngrok Setup for MCP Integration

This guide explains how to set up ngrok to expose the scheduler service publicly so that OpenAI can call it via MCP (Model Control Protocol).

## Prerequisites

1. Install ngrok: https://ngrok.com/download
2. Create a free ngrok account: https://ngrok.com/signup
3. Get your authtoken from the ngrok dashboard

## Setup Steps

### 1. Install and Configure Ngrok

```bash
# Install ngrok (macOS with Homebrew)
brew install ngrok/ngrok/ngrok

# Or download from https://ngrok.com/download

# Configure your authtoken (get this from your ngrok dashboard)
ngrok config add-authtoken YOUR_AUTHTOKEN_HERE
```

### 2. Start the Scheduler Service

```bash
# Start the scheduler service on port 8081
cd projects/my-first-spring-ai/scheduler
mvn spring-boot:run
```

### 3. Expose the Service with Ngrok

In a new terminal window:

```bash
# Expose port 8081 publicly
ngrok http 8081
```

You should see output like:
```
ngrok                                                          
Session Status                online                           
Account                       your-email@example.com (Plan: Free)
Version                       3.x.x                            
Region                        United States (us)               
Latency                       -                                
Web Interface                 http://127.0.0.1:4040            
Forwarding                    https://abc123.ngrok-free.app -> http://localhost:8081

Connections                   ttl     opn     rt1     rt5     p50     p90  
                              0       0       0.00    0.00    0.00    0.00 
```

### 4. Test the Public URL

```bash
# Test the health endpoint
curl https://abc123.ngrok-free.app/health

# Test scheduling
curl -X POST https://abc123.ngrok-free.app/schedule \
  -H "Content-Type: application/json" \
  -d '{"dogId": 1, "dogName": "Buddy"}'
```

### 5. Configure the Adoptions Service

Update the scheduler URL in your application:

```bash
# Set the public ngrok URL as a system property
export SCHEDULER_URL=https://abc123.ngrok-free.app

# Or set it when running the adoptions service
cd ../adoptions
mvn spring-boot:run -Dscheduler.url=https://abc123.ngrok-free.app
```

## For Production

### Paid Ngrok Plans

For production use, consider upgrading to a paid ngrok plan for:
- Custom domains
- No bandwidth limits
- Better reliability
- No interstitial pages

### Alternative Deployment Options

Instead of ngrok, you can deploy the scheduler service to:
- **Cloud platforms**: AWS, Google Cloud, Azure
- **Container platforms**: Docker, Kubernetes
- **Platform-as-a-Service**: Heroku, Railway, Render

## Security Considerations

1. **API Keys**: Never expose API keys in public URLs
2. **Authentication**: Consider adding authentication to the scheduler service
3. **Rate Limiting**: Implement rate limiting for production use
4. **HTTPS**: Always use HTTPS in production (ngrok provides this automatically)

## Troubleshooting

### Common Issues

1. **Port already in use**: Make sure port 8081 is free
2. **Ngrok tunnel closed**: Restart ngrok if the tunnel closes
3. **Network issues**: Check firewall settings

### Monitoring

- **Ngrok Web Interface**: http://127.0.0.1:4040 (shows request logs)
- **Application Logs**: Monitor both services for errors
- **Health Checks**: Use the `/health` endpoint to verify service status

## Testing the Complete Flow

1. Start PostgreSQL: `docker-compose up -d`
2. Start scheduler: `mvn spring-boot:run` (in scheduler directory)
3. Start ngrok: `ngrok http 8081`
4. Start adoptions: `mvn spring-boot:run -Dscheduler.url=YOUR_NGROK_URL` (in adoptions directory)
5. Test the AI assistant with scheduling requests 