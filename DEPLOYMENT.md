# FitLens Deployment

FitLens is split into two deploys:

- Backend: Spring Boot on Heroku.
- Frontend: Expo Web from `mobile/` on Vercel.

## Backend on Heroku

Create the Heroku app from the repository root. Heroku detects Maven from `pom.xml`, uses `system.properties` for Java, and starts the app with `Procfile`.

Required config vars:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `OPENAI_API_KEY`

Useful optional config vars:

- `OPENAI_MODEL=gpt-5.4-mini`
- `FITLENS_AI_ENABLED=true`
- `GOOGLE_OAUTH_CLIENT_IDS`
- `APPLE_OAUTH_CLIENT_ID`
- `FITLENS_UPLOADS_DIR`

Deploy:

```powershell
heroku create your-fitlens-api
heroku config:set JWT_SECRET="change-this"
heroku config:set OPENAI_API_KEY="sk-..."
git push heroku main
```

## Frontend on Vercel

Create a Vercel project with the root directory set to `mobile`.

Set this Vercel environment variable:

- `EXPO_PUBLIC_API_BASE_URL=https://your-fitlens-api.herokuapp.com`

Vercel uses `mobile/vercel.json`:

- Build command: `npm run build`
- Output directory: `dist`

## Local Web Check

```powershell
cd mobile
npm run build
```

The generated web app is written to `mobile/dist`.
