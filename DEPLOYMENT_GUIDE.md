# Deployment Guide for I-TO-SAI

This guide will help you deploy your application to Vercel (frontend) and Render (backend with PostgreSQL).

## Prerequisites

1. **GitHub Account**: Your code should be in a GitHub repository
2. **Vercel Account**: Sign up at [vercel.com](https://vercel.com)
3. **Render Account**: Sign up at [render.com](https://render.com)
4. **OpenAI API Key**: For the AI features

## Step 1: Deploy Backend to Render

### 1.1 Create Render Account and Connect GitHub
1. Go to [render.com](https://render.com) and sign up
2. Connect your GitHub account
3. Import your repository

### 1.2 Create PostgreSQL Database
1. In your Render dashboard, click "New +"
2. Select "PostgreSQL"
3. Configure:
   - **Name**: `itosai-database`
   - **Database**: `itosai`
   - **User**: `itosai_user`
   - **Plan**: Free
4. Click "Create Database"
5. Note down the connection details (you'll need these later)

### 1.3 Deploy Backend Service
1. In your Render dashboard, click "New +"
2. Select "Web Service"
3. Connect your GitHub repository
4. Configure the service:
   - **Name**: `itosai-backend`
   - **Environment**: Java
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/I-TO-SAI-BE-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free

### 1.4 Configure Environment Variables
In your backend service settings, add these environment variables:

```
JAVA_VERSION=24
OPENAI_API_KEY=your_openai_api_key_here
SPRING_PROFILES_ACTIVE=production
```

**Note**: The DATABASE_URL, DB_USERNAME, and DB_PASSWORD will be automatically configured by Render when you connect the database to your service.

### 1.5 Deploy
1. Click "Create Web Service"
2. Wait for the build to complete
3. Note the service URL (e.g., `https://itosai-backend.onrender.com`)

## Step 2: Deploy Frontend to Vercel

### 2.1 Connect to Vercel
1. Go to [vercel.com](https://vercel.com) and sign up
2. Import your GitHub repository
3. Configure the project:
   - **Framework Preset**: Vite
   - **Root Directory**: `I-TO-SAI-FE`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`

### 2.2 Configure Environment Variables
In your Vercel project settings, add:
```
VITE_API_URL=https://your-backend-url.onrender.com
```

Replace `your-backend-url.onrender.com` with your actual Render backend URL.

### 2.3 Deploy
1. Click "Deploy"
2. Wait for the build to complete
3. Your frontend will be available at `https://your-project.vercel.app`

## Step 3: Update Frontend API Configuration

You'll need to update your frontend to use the environment variable for the API URL. If you haven't already, create a configuration file:

```typescript
// src/config/api.ts
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
```

Then update your API calls to use this base URL.

## Step 4: Test Your Deployment

1. **Test Backend**: Visit your Render backend URL + `/login` to ensure it's working
2. **Test Frontend**: Visit your Vercel URL to test the frontend
3. **Test Database**: Try registering a user to ensure the database is working

## Troubleshooting

### Common Issues:

1. **Database Connection Errors**: 
   - Ensure the PostgreSQL database is created in Render before deploying the backend
   - Check that the database is connected to your backend service in Render
   - Verify that SPRING_PROFILES_ACTIVE=production is set

2. **CORS Errors**: The backend is configured to allow all origins. If you still get CORS errors, check that the CORS configuration is properly applied.

3. **Build Failures**: 
   - Check that all dependencies are in package.json (frontend) or pom.xml (backend)
   - Ensure Java 24 is available in Render
   - Verify that the PostgreSQL dependency is in pom.xml

4. **Environment Variables**: Make sure all required environment variables are set in both Vercel and Render.

### Useful Commands:

**Local Testing**:
```bash
# Backend
cd I-TO-SAI-BE
./mvnw spring-boot:run

# Frontend
cd I-TO-SAI-FE
npm run dev
```

**Check Logs**:
- Render: Go to your service dashboard and check the logs
- Vercel: Check the deployment logs in your project dashboard

## Security Notes

1. **Environment Variables**: Never commit sensitive information like API keys to your repository
2. **Database**: The free PostgreSQL database on Render has limitations - consider upgrading for production
3. **HTTPS**: Both Vercel and Render provide HTTPS by default

## Cost Considerations

- **Vercel**: Free tier includes 100GB bandwidth and 100 serverless function invocations
- **Render**: Free tier includes 750 hours/month for web services and 1GB PostgreSQL database
- **OpenAI**: You'll be charged based on your API usage

## Next Steps

1. Set up a custom domain (optional)
2. Configure monitoring and logging
3. Set up CI/CD pipelines
4. Consider upgrading to paid plans for production use 