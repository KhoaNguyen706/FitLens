import React, { useEffect } from 'react';
import { ActivityIndicator, Alert, StyleSheet, Text, TouchableOpacity } from 'react-native';
import * as Application from 'expo-application';
import * as Google from 'expo-auth-session/providers/google';
import * as WebBrowser from 'expo-web-browser';
import { getExpoGoogleRedirectUri } from '../auth/googleConfig';
import { GOOGLE_OAUTH } from '../config';
import { colors, radius } from '../theme';

WebBrowser.maybeCompleteAuthSession();

export default function GoogleSignInButton({ busy, setBusy, onIdToken }) {
  const redirectUri = getExpoGoogleRedirectUri();

  const [googleRequest, googleResponse, promptGoogleSignIn] = Google.useIdTokenAuthRequest(
    {
      webClientId: GOOGLE_OAUTH.webClientId,
      iosClientId: GOOGLE_OAUTH.iosClientId,
      androidClientId: GOOGLE_OAUTH.androidClientId || undefined,
      redirectUri,
      selectAccount: true,
    },
    { native: `${Application.applicationId || 'host.exp.Exponent'}:/oauthredirect` }
  );

  useEffect(() => {
    if (!googleResponse) return;

    if (googleResponse.type === 'error') {
      Alert.alert(
        'Google sign-in',
        googleResponse.error?.message ||
          googleResponse.params?.error_description ||
          'Google blocked sign-in. Check Test users + redirect URIs in Google Cloud.'
      );
      return;
    }

    if (googleResponse.type !== 'success') return;

    const idToken = googleResponse.params?.id_token || googleResponse.authentication?.idToken;
    if (!idToken) {
      Alert.alert('FitLens', 'Google sign-in did not return an ID token.');
      return;
    }

    (async () => {
      setBusy(true);
      try {
        await onIdToken(idToken);
      } catch (e) {
        Alert.alert('FitLens', e.message);
      } finally {
        setBusy(false);
      }
    })();
  }, [googleResponse, onIdToken, setBusy]);

  async function press() {
    setBusy(true);
    try {
      await promptGoogleSignIn();
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <TouchableOpacity
      style={styles.oauthButton}
      onPress={press}
      disabled={busy || !googleRequest}
    >
      {busy ? (
        <ActivityIndicator color={colors.primary} />
      ) : (
        <Text style={styles.oauthButtonText}>Continue with Google</Text>
      )}
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  oauthButton: {
    borderRadius: radius.pill,
    paddingVertical: 14,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.skyLight,
  },
  oauthButtonText: { color: colors.deep, fontSize: 16, fontWeight: '700' },
});
