import { API_BASE_URL } from '../config';
import { api, getRefreshToken } from './client';

// The backend groups meals by UTC day, so "today" is computed in UTC
// to keep the dashboard consistent with what the server stores.
export function utcToday() {
  return new Date().toISOString().slice(0, 10);
}

// --- Auth ---
export const register = (email, password, displayName) =>
  api('/api/auth/register', { method: 'POST', body: { email, password, displayName } });

export const login = (email, password) =>
  api('/api/auth/login', { method: 'POST', body: { email, password } });

export const loginWithGoogle = (idToken) =>
  api('/api/auth/oauth/google', { method: 'POST', body: { idToken } });

export const loginWithApple = (idToken, displayName) =>
  api('/api/auth/oauth/apple', { method: 'POST', body: { idToken, displayName } });

export const refreshSession = (refreshToken) =>
  api('/api/auth/refresh', { method: 'POST', body: { refreshToken }, skipRefresh: true });

export const logout = () => {
  const token = getRefreshToken();
  if (!token) return Promise.resolve();
  return api('/api/auth/logout', {
    method: 'POST',
    body: { refreshToken: token },
    skipRefresh: true,
  });
};

// --- Meals ---
export const createMeal = (meal) => api('/api/meals', { method: 'POST', body: meal });

export const getMealsForDay = (day) => api(`/api/meals?day=${day}`);

export const deleteMeal = (mealEntryId) =>
  api(`/api/meals/${mealEntryId}`, { method: 'DELETE' });

export const estimateMealPhoto = ({ photoBase64, mimeType }) =>
  api('/api/ai/meal-estimate', {
    method: 'POST',
    body: { photoBase64, mimeType },
  });

// --- Dashboard ---
export const getDashboardForDay = (day) => api(`/api/dashboard/today?day=${day}`);

export const getTodayDashboard = () => getDashboardForDay(utcToday());

/** Fetch daily totals for every day in a calendar month. */
export async function getMonthProgress(year, monthIndex) {
  const daysInMonth = new Date(year, monthIndex + 1, 0).getDate();
  const days = [];
  for (let d = 1; d <= daysInMonth; d++) {
    days.push(
      `${year}-${String(monthIndex + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    );
  }
  return Promise.all(
    days.map(async (day) => {
      const dash = await getDashboardForDay(day);
      return {
        day,
        totalCalories: dash.totalCalories,
        mealCount: dash.meals?.length ?? 0,
      };
    })
  );
}

/** Fetch daily totals for the last N UTC days (oldest first). */
export async function getProgressRange(dayCount = 7) {
  const now = new Date();
  const days = [];
  for (let i = dayCount - 1; i >= 0; i--) {
    const d = new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate() - i));
    days.push(d.toISOString().slice(0, 10));
  }
  const results = await Promise.all(
    days.map(async (day) => {
      const dash = await getDashboardForDay(day);
      return {
        day,
        totalCalories: dash.totalCalories,
        mealCount: dash.meals?.length ?? 0,
      };
    })
  );
  return results;
}

// --- Meal photos (local URIs stored server-side, files stay on the phone) ---
export const saveLocalPhoto = (mealEntryId, localUri) =>
  api('/api/meal-photos/local', { method: 'POST', body: { mealEntryId, localUri } });

export const getPhotos = () => api('/api/meal-photos');

// --- Friends (v2) ---
export const getFriends = () => api('/api/friends');

export const sendFriendRequest = (email) =>
  api('/api/friends/requests', { method: 'POST', body: { email } });

export const getFriendRequests = () => api('/api/friends/requests');

export const acceptFriendRequest = (requestId) =>
  api(`/api/friends/requests/${requestId}/accept`, { method: 'POST' });

export const removeFriend = (friendUserId) =>
  api(`/api/friends/${friendUserId}`, { method: 'DELETE' });

export const getFriendMessages = (friendUserId) =>
  api(`/api/friends/${friendUserId}/messages`);

export const sendFriendMessage = (friendUserId, body) =>
  api(`/api/friends/${friendUserId}/messages`, { method: 'POST', body: { body } });

// --- Posts & feed (v2) ---
export const createPost = (body) => api('/api/posts', { method: 'POST', body });

export const getFeed = () => api('/api/feed');

export const getPost = (postId) => api(`/api/posts/${postId}`);

export const addReaction = (postId, emoji) =>
  api(`/api/posts/${postId}/reactions`, { method: 'POST', body: { emoji } });

export const removeReaction = (postId) =>
  api(`/api/posts/${postId}/reactions`, { method: 'DELETE' });

export function postPhotoUrl(postId) {
  return `${API_BASE_URL}/api/posts/${postId}/photo`;
}
