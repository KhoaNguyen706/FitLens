# FitLens Deployment

FitLens is split into two deploys:

- Backend: Spring Boot on Heroku.
- Frontend: Expo Web from `mobile/` on Vercel.

## Backend on Heroku

Create the Heroku app from the repository root. Heroku detects Maven from `pom.xml`, uses `system.properties` for Java, and starts the app with `Procfile`.

### Required config vars

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | Supabase Postgres JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `JWT_SECRET` | Secret key for signing JWTs |
| `OPENAI_API_KEY` | OpenAI API key for AI features |

### Optional config vars

| Variable | Default | Description |
|---|---|---|
| `OPENAI_MODEL` | `gpt-5.4-mini` | AI model to use |
| `FITLENS_AI_ENABLED` | `true` | Toggle AI features |
| `GOOGLE_OAUTH_CLIENT_IDS` | (empty) | Comma-separated Google OAuth client IDs |
| `APPLE_OAUTH_CLIENT_ID` | `host.exp.Exponent` | Apple Sign-In client ID |
| `FITLENS_UPLOADS_DIR` | `./uploads` | Directory for uploaded files |
| `CORS_ALLOWED_ORIGINS` | (all origins) | Comma-separated frontend URLs for CORS |

### Manual deploy

```powershell
heroku create your-fitlens-api
heroku config:set JWT_SECRET="change-this"
heroku config:set OPENAI_API_KEY="sk-..."
heroku config:set CORS_ALLOWED_ORIGINS="https://your-vercel-app.vercel.app"
git push heroku main
```

### CORS

In production, set `CORS_ALLOWED_ORIGINS` on Heroku to the Vercel frontend URL:

```powershell
heroku config:set CORS_ALLOWED_ORIGINS="https://your-vercel-app.vercel.app"
```

Multiple origins can be comma-separated (no spaces). When the variable is unset, all origins are allowed for local development.

### GitHub Actions auto-deploy

The CI/CD pipeline (`.github/workflows/ci-cd.yml`) automatically deploys to Heroku on every push to `main` after the backend tests pass.

Add these GitHub repository secrets (Settings → Secrets and variables → Actions):

| Secret | Value |
|---|---|
| `HEROKU_API_KEY` | Your Heroku API key (Account → API Key) |
| `HEROKU_EMAIL` | The email on your Heroku account |

## Frontend on Vercel

Create a Vercel project with the root directory set to `mobile`.

Set this Vercel environment variable:

- `EXPO_PUBLIC_API_BASE_URL=https://your-fitlens-api.herokuapp.com`

Replace with your actual Heroku app URL (find it with `heroku info` or in the Heroku dashboard).

Vercel uses `mobile/vercel.json`:

- Build command: `npm run build`
- Output directory: `dist`

## Local Web Check

```powershell
cd mobile
npm run build
```

The generated web app is written to `mobile/dist`.
