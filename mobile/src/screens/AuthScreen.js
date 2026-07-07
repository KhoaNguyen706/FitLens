import React, { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import * as AppleAuthentication from 'expo-apple-authentication';
import { useAuth } from '../context/AuthContext';
import { googleSetupHint, isGoogleConfiguredForPlatform } from '../auth/googleConfig';
import GoogleSignInButton from '../components/GoogleSignInButton';
import { colors, radius } from '../theme';

export default function AuthScreen() {
  const { login, register, loginWithGoogle, loginWithApple } = useAuth();
  const [mode, setMode] = useState('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [busy, setBusy] = useState(false);
  const [appleAvailable, setAppleAvailable] = useState(false);

  const isLogin = mode === 'login';
  const googleReady = isGoogleConfiguredForPlatform();

  useEffect(() => {
    let mounted = true;

    AppleAuthentication.isAvailableAsync()
      .then((available) => {
        if (mounted) setAppleAvailable(available);
      })
      .catch(() => {
        if (mounted) setAppleAvailable(false);
      });

    return () => {
      mounted = false;
    };
  }, []);

  async function submit() {
    if (!email.trim() || !password) {
      Alert.alert('FitLens', 'Please fill in email and password.');
      return;
    }
    if (!isLogin && !displayName.trim()) {
      Alert.alert('FitLens', 'Please enter a display name.');
      return;
    }
    setBusy(true);
    try {
      if (isLogin) {
        await login(email.trim(), password);
      } else {
        await register(email.trim(), password, displayName.trim());
      }
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setBusy(false);
    }
  }

  function showGoogleSetupHint() {
    Alert.alert('Google sign-in setup', googleSetupHint());
  }

  async function signInWithApple() {
    setBusy(true);
    try {
      const credential = await AppleAuthentication.signInAsync({
        requestedScopes: [
          AppleAuthentication.AppleAuthenticationScope.FULL_NAME,
          AppleAuthentication.AppleAuthenticationScope.EMAIL,
        ],
      });

      const nameParts = [
        credential.fullName?.givenName,
        credential.fullName?.familyName,
      ].filter(Boolean);
      const appleDisplayName = nameParts.join(' ');

      await loginWithApple(credential.identityToken, appleDisplayName || undefined);
    } catch (e) {
      if (e.code !== 'ERR_REQUEST_CANCELED') {
        Alert.alert('FitLens', e.message || 'Apple sign-in failed.');
      }
    } finally {
      setBusy(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.root}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <Text style={styles.title}>FitLens</Text>
          <View style={styles.titleAccent} />
          <Text style={styles.subtitle}>Snap your food. Know your day.</Text>
        </View>

        <View style={styles.card}>
          {!isLogin && (
            <TextInput
              style={styles.input}
              placeholder="Display name"
              placeholderTextColor={colors.textMuted}
              value={displayName}
              onChangeText={setDisplayName}
              autoCapitalize="words"
            />
          )}
          <TextInput
            style={styles.input}
            placeholder="Email"
            placeholderTextColor={colors.textMuted}
            value={email}
            onChangeText={setEmail}
            autoCapitalize="none"
            keyboardType="email-address"
          />
          <TextInput
            style={styles.input}
            placeholder={isLogin ? 'Password' : 'Password (min 8 characters)'}
            placeholderTextColor={colors.textMuted}
            value={password}
            onChangeText={setPassword}
            secureTextEntry
          />

          <TouchableOpacity style={styles.button} onPress={submit} disabled={busy}>
            {busy ? (
              <ActivityIndicator color={colors.white} />
            ) : (
              <Text style={styles.buttonText}>{isLogin ? 'Log in' : 'Create account'}</Text>
            )}
          </TouchableOpacity>

          <View style={styles.dividerRow}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>or</Text>
            <View style={styles.dividerLine} />
          </View>

          {googleReady ? (
            <GoogleSignInButton busy={busy} setBusy={setBusy} onIdToken={loginWithGoogle} />
          ) : (
            <TouchableOpacity style={styles.oauthButton} onPress={showGoogleSetupHint}>
              <Text style={styles.oauthButtonText}>Continue with Google</Text>
            </TouchableOpacity>
          )}

          {Platform.OS === 'ios' && appleAvailable && (
            <AppleAuthentication.AppleAuthenticationButton
              buttonType={AppleAuthentication.AppleAuthenticationButtonType.CONTINUE}
              buttonStyle={AppleAuthentication.AppleAuthenticationButtonStyle.BLACK}
              cornerRadius={radius.pill}
              style={styles.appleButton}
              onPress={signInWithApple}
            />
          )}
        </View>

        <TouchableOpacity onPress={() => setMode(isLogin ? 'register' : 'login')}>
          <Text style={styles.switchText}>
            {isLogin ? 'New here? Create an account' : 'Already have an account? Log in'}
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background },
  scroll: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 24,
    paddingVertical: 32,
  },
  header: { alignItems: 'center', marginBottom: 28, width: '100%' },
  title: { fontSize: 36, fontWeight: '800', color: colors.deep, letterSpacing: -0.5 },
  titleAccent: {
    width: 40,
    height: 4,
    borderRadius: 2,
    backgroundColor: colors.primary,
    marginTop: 10,
    marginBottom: 10,
  },
  subtitle: { fontSize: 15, color: colors.textMuted, textAlign: 'center' },
  card: {
    width: '100%',
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 20,
    gap: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  input: {
    backgroundColor: colors.surface,
    borderRadius: radius.input,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 16,
    color: colors.deep,
  },
  button: {
    backgroundColor: colors.primary,
    borderRadius: radius.pill,
    paddingVertical: 15,
    alignItems: 'center',
    marginTop: 4,
  },
  buttonText: { color: colors.white, fontSize: 17, fontWeight: '800' },
  dividerRow: { flexDirection: 'row', alignItems: 'center', gap: 10, marginVertical: 4 },
  dividerLine: { flex: 1, height: 1, backgroundColor: colors.border },
  dividerText: { color: colors.textMuted, fontWeight: '600' },
  oauthButton: {
    borderRadius: radius.pill,
    paddingVertical: 14,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: colors.borderLight,
    backgroundColor: colors.skyLight,
  },
  oauthButtonText: { color: colors.deep, fontSize: 16, fontWeight: '700' },
  appleButton: { width: '100%', height: 48 },
  switchText: { marginTop: 20, color: colors.primaryDark, fontSize: 15, fontWeight: '600' },
});
