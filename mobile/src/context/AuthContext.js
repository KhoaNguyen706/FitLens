import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as SecureStore from 'expo-secure-store';
import {
  setAuthToken,
  setAuthUpdatedHandler,
  setRefreshToken,
  setSessionExpiredHandler,
} from '../api/client';
import * as apiCalls from '../api';

const AuthContext = createContext(null);
const STORAGE_KEY = 'fitlens.auth';

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const clearSession = useCallback(async () => {
    setAuthToken(null);
    setRefreshToken(null);
    setUser(null);
    await SecureStore.deleteItemAsync(STORAGE_KEY);
  }, []);

  const persist = useCallback(async (auth) => {
    setAuthToken(auth.token);
    setRefreshToken(auth.refreshToken);
    setUser(auth.user);
    await SecureStore.setItemAsync(STORAGE_KEY, JSON.stringify(auth));
  }, []);

  useEffect(() => {
    setAuthUpdatedHandler(async (auth) => {
      await persist({
        token: auth.token,
        refreshToken: auth.refreshToken,
        user: auth.user,
      });
    });
    setSessionExpiredHandler(clearSession);

    (async () => {
      try {
        let raw = await SecureStore.getItemAsync(STORAGE_KEY);
        if (!raw) {
          const legacy = await AsyncStorage.getItem(STORAGE_KEY);
          if (legacy) {
            raw = legacy;
            await SecureStore.setItemAsync(STORAGE_KEY, legacy);
            await AsyncStorage.removeItem(STORAGE_KEY);
          }
        }
        if (raw) {
          const saved = JSON.parse(raw);
          setAuthToken(saved.token);
          setRefreshToken(saved.refreshToken);
          setUser(saved.user);
        }
      } finally {
        setLoading(false);
      }
    })();

    return () => {
      setAuthUpdatedHandler(null);
      setSessionExpiredHandler(null);
    };
  }, [clearSession, persist]);

  const login = async (email, password) => persist(await apiCalls.login(email, password));

  const register = async (email, password, displayName) =>
    persist(await apiCalls.register(email, password, displayName));

  const loginWithGoogle = async (idToken) =>
    persist(await apiCalls.loginWithGoogle(idToken));

  const loginWithApple = async (idToken, displayName) =>
    persist(await apiCalls.loginWithApple(idToken, displayName));

  const logout = async () => {
    try {
      await apiCalls.logout();
    } catch (e) {
      // Still clear local session if the server is unreachable.
    }
    await clearSession();
  };

  return (
    <AuthContext.Provider
      value={{ user, loading, login, register, loginWithGoogle, loginWithApple, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
