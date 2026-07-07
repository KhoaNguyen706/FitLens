import React, { useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { sendFriendRequest } from '../api';
import { colors, radius } from '../theme';

export default function AddFriendScreen({ onBack }) {
  const [email, setEmail] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit() {
    if (!email.trim()) {
      Alert.alert('FitLens', 'Enter your friend\'s email.');
      return;
    }
    setBusy(true);
    try {
      await sendFriendRequest(email.trim());
      Alert.alert('FitLens', 'Friend request sent.');
      setEmail('');
      onBack();
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.root}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}>
          <Text style={styles.back}>Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Add friend</Text>
        <View style={styles.spacer} />
      </View>

      <View style={styles.card}>
        <Text style={styles.label}>Friend&apos;s email</Text>
        <TextInput
          style={styles.input}
          placeholder="friend@example.com"
          placeholderTextColor={colors.textMuted}
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
        />
        <TouchableOpacity style={styles.button} onPress={submit} disabled={busy}>
          {busy ? (
            <ActivityIndicator color={colors.white} />
          ) : (
            <Text style={styles.buttonText}>Send request</Text>
          )}
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background, paddingTop: 56, paddingHorizontal: 20 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 24,
  },
  back: { fontSize: 15, fontWeight: '700', color: colors.primaryDark, minWidth: 50 },
  title: { fontSize: 20, fontWeight: '800', color: colors.deep },
  spacer: { minWidth: 50 },
  card: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 20,
    gap: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  label: { fontSize: 13, fontWeight: '700', color: colors.textMuted, textTransform: 'uppercase' },
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
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 4,
  },
  buttonText: { color: colors.white, fontSize: 16, fontWeight: '800' },
});
