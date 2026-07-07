import * as Application from 'expo-application';
import { Platform } from 'react-native';
import { GOOGLE_OAUTH } from '../config';

/** Redirect URI Expo Go uses on iOS (must be added to Web OAuth client in Google Cloud). */
export function getExpoGoogleRedirectUri() {
  const appId = Application.applicationId || 'host.exp.Exponent';
  return `${appId}:/oauthredirect`;
}

/** Optional second redirect Google accepts for the web client ID. */
export function getWebClientRedirectUri() {
  const id = GOOGLE_OAUTH.webClientId;
  if (!id || !id.includes('.apps.googleusercontent.com')) return null;
  const prefix = id.replace('.apps.googleusercontent.com', '');
  return `com.googleusercontent.apps.${prefix}:/oauthredirect`;
}

export function isGoogleConfiguredForPlatform() {
  if (Platform.OS === 'ios') {
    return Boolean(GOOGLE_OAUTH.iosClientId && GOOGLE_OAUTH.webClientId);
  }
  if (Platform.OS === 'android') {
    return Boolean(GOOGLE_OAUTH.androidClientId && GOOGLE_OAUTH.webClientId);
  }
  return Boolean(GOOGLE_OAUTH.webClientId);
}

export function googleSetupHint() {
  const redirect = getExpoGoogleRedirectUri();
  const webRedirect = getWebClientRedirectUri();

  const redirectLines = [redirect, webRedirect].filter(Boolean).join('\n• ');

  return (
    'Google Cloud fixes (do ALL of these):\n\n' +
    '1. OAuth consent screen → External → add YOUR Gmail under Test users\n' +
    '2. OAuth consent screen → add a Privacy policy URL (any public link works for testing)\n' +
    '3. Credentials → Web client → Authorized redirect URIs → add:\n' +
    '• ' +
    redirectLines +
    '\n\n4. Wait 5–10 minutes, restart Expo (r), try again with the same Gmail you added as test user.'
  );
}

export function googleRedirectUriList() {
  return [getExpoGoogleRedirectUri(), getWebClientRedirectUri()].filter(Boolean);
}
