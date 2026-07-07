import React from 'react';
import { Alert, StyleSheet, Text, TouchableOpacity } from 'react-native';
import { useAuth } from '../context/AuthContext';
import { colors } from '../theme';

export default function ProfileAvatar({ size = 44 }) {
  const { user, logout } = useAuth();
  const initial = (user?.displayName || '?').charAt(0).toUpperCase();
  const radius = size / 2;

  return (
    <TouchableOpacity
      style={[styles.avatar, { width: size, height: size, borderRadius: radius }]}
      onPress={() =>
        Alert.alert('FitLens', `Signed in as ${user?.displayName}\n${user?.email}`, [
          { text: 'Log out', style: 'destructive', onPress: logout },
          { text: 'Close', style: 'cancel' },
        ])
      }
    >
      <Text style={[styles.text, { fontSize: size * 0.38 }]}>{initial}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  avatar: {
    backgroundColor: colors.cardElevated,
    borderWidth: 2,
    borderColor: colors.sky,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: { fontWeight: '800', color: colors.primaryDark },
});
