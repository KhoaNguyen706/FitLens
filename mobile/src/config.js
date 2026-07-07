import Constants from 'expo-constants';
import { Platform } from 'react-native';

function stripTrailingSlash(value) {
  return value.replace(/\/$/, '');
}

function hostFromExpo() {
  const hostUri =
    Constants.expoConfig?.hostUri ||
    Constants.manifest2?.extra?.expoClient?.hostUri ||
    Constants.manifest?.debuggerHost;

  const host = hostUri?.split(':')[0];
  if (!host || host.includes('exp.host')) return null;

  if (Platform.OS === 'android' && (host === 'localhost' || host === '127.0.0.1')) {
    return '10.0.2.2';
  }
  return host;
}

function defaultApiBaseUrl() {
  const override = process.env.EXPO_PUBLIC_API_BASE_URL;
  if (override) return stripTrailingSlash(override);

  const expoHost = hostFromExpo();
  if (expoHost) return `http://${expoHost}`;

  return Platform.OS === 'android' ? 'http://10.0.2.2' : 'http://localhost';
}

export const API_BASE_URL = defaultApiBaseUrl();

let googleOAuth = { webClientId: '', iosClientId: '', androidClientId: '' };

try {
  googleOAuth = require('../oauth.local.js').GOOGLE_OAUTH;
} catch (e) {
  // Keep local email/password auth usable when OAuth is not configured.
}

export const GOOGLE_OAUTH = googleOAuth;
