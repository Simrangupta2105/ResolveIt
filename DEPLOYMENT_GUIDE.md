# Deployment Guide

## Vercel Deployment (Frontend)

### Prerequisites
1. Install Vercel CLI: `npm i -g vercel`
2. Create a Vercel account at https://vercel.com
3. Deploy your backend to a cloud service (Heroku, Railway, etc.)

### Frontend Deployment Steps

1. **Navigate to the frontend directory:**
   ```bash
   cd frontend
   ```

2. **Login to Vercel:**
   ```bash
   vercel login
   ```

3. **Deploy to Vercel:**
   ```bash
   vercel --prod
   ```

4. **Set Environment Variables in Vercel Dashboard:**
   - Go to your project in Vercel dashboard
   - Navigate to Settings > Environment Variables
   - Add: `REACT_APP_API_URL` = `https://your-backend-url.herokuapp.com`

### Backend Deployment Options

#### Option 1: Heroku
1. Create a Heroku account
2. Install Heroku CLI
3. Navigate to backend directory:
   ```bash
   cd backend
   ```
4. Create Heroku app:
   ```bash
   heroku create your-app-name
   ```
5. Deploy:
   ```bash
   git add .
   git commit -m "Deploy to Heroku"
   git push heroku main
   ```

#### Option 2: Railway
1. Create Railway account at https://railway.app
2. Connect your GitHub repository
3. Select the backend folder
4. Railway will auto-deploy

#### Option 3: Render
1. Create Render account at https://render.com
2. Connect GitHub repository
3. Create new Web Service
4. Set build command: `./mvnw clean package`
5. Set start command: `java -jar target/*.jar`

### Environment Configuration

#### Frontend (.env.production)
```
REACT_APP_API_URL=https://your-backend-url.herokuapp.com
GENERATE_SOURCEMAP=false
```

#### Backend (application-prod.yml)
```yaml
server:
  port: ${PORT:8080}

spring:
  datasource:
    url: ${DATABASE_URL:jdbc:h2:mem:testdb}
    driver-class-name: ${DB_DRIVER:org.h2.Driver}
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
  
  jpa:
    hibernate:
      ddl-auto: ${DDL_AUTO:create-drop}
    show-sql: false
    
  h2:
    console:
      enabled: false

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-make-it-very-long-and-secure-for-production-use}
  expiration: 86400000

logging:
  level:
    com.resolve.complaint: INFO
```

### Post-Deployment Steps

1. **Update CORS Configuration:**
   Update `WebSecurityConfig.java` to allow your Vercel domain:
   ```java
   @CrossOrigin(origins = {"http://localhost:3001", "https://your-vercel-app.vercel.app"})
   ```

2. **Test the Application:**
   - Visit your Vercel URL
   - Test login functionality
   - Verify API calls work correctly
   - Test file uploads and downloads

3. **Set up Custom Domain (Optional):**
   - In Vercel dashboard, go to Settings > Domains
   - Add your custom domain
   - Update DNS records as instructed

### Troubleshooting

#### Common Issues:
1. **CORS Errors:** Update backend CORS configuration
2. **API Not Found:** Check REACT_APP_API_URL environment variable
3. **Build Failures:** Ensure all dependencies are in package.json
4. **File Upload Issues:** Configure backend for cloud file storage

#### Logs:
- **Vercel:** Check Function Logs in Vercel dashboard
- **Heroku:** `heroku logs --tail`
- **Railway:** Check deployment logs in Railway dashboard

### Production Checklist

- [ ] Backend deployed and accessible
- [ ] Frontend deployed to Vercel
- [ ] Environment variables configured
- [ ] CORS settings updated
- [ ] Database configured (if using external DB)
- [ ] File storage configured (if needed)
- [ ] SSL certificates active
- [ ] Custom domain configured (optional)
- [ ] Error monitoring set up (optional)

### Security Considerations

1. **JWT Secret:** Use a strong, unique secret in production
2. **Database:** Use a production database (PostgreSQL, MySQL)
3. **File Storage:** Use cloud storage (AWS S3, Cloudinary)
4. **HTTPS:** Ensure all communications are encrypted
5. **Environment Variables:** Never commit secrets to version control