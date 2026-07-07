import React from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';
import { Image } from 'expo-image';
import { getAuthToken } from '../api/client';
import { colors } from '../theme';

export default function AuthenticatedImage({ uri, style, contentFit = 'cover' }) {
  const token = getAuthToken();
  if (!uri || !token) return null;

  return (
    <Image
      source={{ uri, headers: { Authorization: `Bearer ${token}` } }}
      style={style}
      contentFit={contentFit}
      placeholderContentFit="cover"
      transition={200}
    />
  );
}

export function AuthenticatedImagePlaceholder({ style }) {
  return (
    <View style={[styles.placeholder, style]}>
      <ActivityIndicator color={colors.primary} />
    </View>
  );
}

const styles = StyleSheet.create({
  placeholder: {
    backgroundColor: colors.cardElevated,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
