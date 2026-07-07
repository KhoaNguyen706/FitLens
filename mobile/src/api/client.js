import { API_BASE_URL } from '../config';

let authToken = null;
let refreshToken = null;
let onAuthUpdated = null;
let onSessionExpired = null;
let refreshPromise = null;

export function setAuthToken(token) {
  authToken = token;
}

export function getAuthToken() {
  return authToken;
}

export function setRefreshToken(token) {
  refreshToken = token;
}

export function getRefreshToken() {
  return refreshToken;
}

export function setAuthUpdatedHandler(handler) {
  onAuthUpdated = handler;
}

export function setSessionExpiredHandler(handler) {
  onSessionExpired = handler;
}

async function refreshAccessToken() {
  if (!refreshToken) {
    return false;
  }

  let response;
  try {
    response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
  } catch (e) {
    return false;
  }

  let json = null;
  try {
    json = await response.json();
  } catch (e) {
    return false;
  }

  if (!response.ok || !json?.success || !json?.data?.token) {
    return false;
  }

  authToken = json.data.token;
  refreshToken = json.data.refreshToken;
  if (onAuthUpdated) {
    await onAuthUpdated(json.data);
  }
  return true;
}

async function tryRefreshOnce() {
  if (refreshPromise) {
    return refreshPromise;
  }
  refreshPromise = refreshAccessToken().finally(() => {
    refreshPromise = null;
  });
  return refreshPromise;
}

async function executeFetch(path, { method = 'GET', body, includeAuth = true } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (includeAuth && authToken) {
    headers.Authorization = `Bearer ${authToken}`;
  }

  return fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
}

/**
 * Thin fetch wrapper around the Spring Boot API.
 * On 401/403 it silently refreshes the access token once and retries.
 */
export async function api(path, { method = 'GET', body, skipRefresh = false } = {}) {
  const isAuthRoute =
    path.startsWith('/api/auth/login') ||
    path.startsWith('/api/auth/register') ||
    path.startsWith('/api/auth/oauth/') ||
    path.startsWith('/api/auth/refresh') ||
    path.startsWith('/api/auth/logout');

  let response;
  try {
    response = await executeFetch(path, { method, body, includeAuth: !isAuthRoute });
  } catch (e) {
    throw new Error(
      `Cannot reach server at ${API_BASE_URL}. Is the backend running and the IP correct? (src/config.js)`
    );
  }

  if (
    !skipRefresh &&
    !isAuthRoute &&
    (response.status === 401 || response.status === 403) &&
    refreshToken
  ) {
    const refreshed = await tryRefreshOnce();
    if (refreshed) {
      try {
        response = await executeFetch(path, { method, body });
      } catch (e) {
        throw new Error(
          `Cannot reach server at ${API_BASE_URL}. Is the backend running and the IP correct? (src/config.js)`
        );
      }
    } else if (onSessionExpired) {
      await onSessionExpired();
    }
  }

  let json = null;
  try {
    json = await response.json();
  } catch (e) {
    // no body
  }

  if (!response.ok || (json && json.success === false)) {
    if (response.status === 502) {
      throw new Error(
        'Backend is not running. Start it with .\\start.bat from the project root, then try again.'
      );
    }
    const message =
      (json && (json.message || json.error)) || `Request failed (${response.status})`;
    const details = json && json.details ? `\n${json.details.join('\n')}` : '';
    throw new Error(message + details);
  }

  return json ? json.data : null;
}
