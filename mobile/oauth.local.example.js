/**
 * 1. Copy this file to oauth.local.js (same folder):
 *      copy oauth.local.example.js oauth.local.js
 *
 * 2. Fill in BOTH IDs from Google Cloud Console → Credentials → OAuth 2.0 Client IDs
 *
 * 3. Restart Expo (npm start) and reload the app
 *
 * iOS client bundle ID for Expo Go: host.exp.Exponent
 */
export const GOOGLE_OAUTH = {
  webClientId: 'PASTE-WEB-CLIENT-ID.apps.googleusercontent.com',
  iosClientId: 'PASTE-IOS-CLIENT-ID.apps.googleusercontent.com',
  androidClientId: '',
};
