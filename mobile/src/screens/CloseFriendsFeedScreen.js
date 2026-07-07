import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { getFeed, postPhotoUrl } from '../api';
import AuthenticatedImage from '../components/AuthenticatedImage';
import { colors, radius } from '../theme';

function formatTime(iso) {
  return new Date(iso).toLocaleString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function reactionSummary(reactionCounts) {
  if (!reactionCounts) return '';
  return Object.entries(reactionCounts)
    .map(([emoji, count]) => `${emoji} ${count}`)
    .join('  ');
}

export default function CloseFriendsFeedScreen({ onBack, onOpenPost }) {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async () => {
    try {
      setPosts(await getFeed());
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <ScrollView
      style={styles.root}
      contentContainerStyle={styles.scroll}
      refreshControl={
        <RefreshControl
          refreshing={refreshing}
          onRefresh={() => {
            setRefreshing(true);
            load();
          }}
          tintColor={colors.primary}
        />
      }
    >
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}>
          <Text style={styles.back}>Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Journey feed</Text>
        <View style={styles.spacer} />
      </View>

      {loading ? (
        <ActivityIndicator color={colors.primary} size="large" style={{ marginTop: 40 }} />
      ) : posts.length === 0 ? (
        <Text style={styles.empty}>
          No shared posts yet.{'\n'}Share a food snap or gym update with friends.
        </Text>
      ) : (
        posts.map((post) => (
          <TouchableOpacity
            key={post.id}
            style={styles.card}
            onPress={() => onOpenPost(post.id)}
            activeOpacity={0.85}
          >
            <View style={styles.cardHeader}>
              <View style={styles.avatar}>
                <Text style={styles.avatarText}>
                  {post.authorDisplayName.charAt(0).toUpperCase()}
                </Text>
              </View>
              <View style={styles.meta}>
                <Text style={styles.author}>{post.authorDisplayName}</Text>
                <Text style={styles.time}>{formatTime(post.createdAt)}</Text>
              </View>
            </View>

            {post.hasPhoto ? (
              <AuthenticatedImage
                uri={postPhotoUrl(post.id)}
                style={styles.photo}
              />
            ) : null}

            {post.mealName && (
              <Text style={styles.mealLine}>
                {post.mealName}
                {post.mealCalories != null ? ` - ${post.mealCalories} kcal` : ''}
              </Text>
            )}
            {post.caption ? <Text style={styles.caption}>{post.caption}</Text> : null}

            {reactionSummary(post.reactionCounts) ? (
              <Text style={styles.reactions}>{reactionSummary(post.reactionCounts)}</Text>
            ) : (
              <Text style={styles.reactionsMuted}>Tap to react</Text>
            )}
          </TouchableOpacity>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background },
  scroll: { paddingTop: 56, paddingHorizontal: 20, paddingBottom: 120 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 20,
  },
  back: { fontSize: 15, fontWeight: '700', color: colors.primaryDark, minWidth: 50 },
  title: { fontSize: 18, fontWeight: '800', color: colors.deep, flex: 1, textAlign: 'center' },
  spacer: { minWidth: 50 },
  empty: {
    color: colors.textMuted,
    fontSize: 15,
    textAlign: 'center',
    lineHeight: 22,
    marginTop: 40,
    paddingHorizontal: 12,
  },
  card: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  cardHeader: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 10 },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.skyLight,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: colors.sky,
  },
  avatarText: { fontSize: 16, fontWeight: '800', color: colors.primaryDark },
  meta: { flex: 1 },
  author: { fontSize: 15, fontWeight: '700', color: colors.deep },
  time: { fontSize: 12, color: colors.textMuted, marginTop: 2 },
  photo: {
    width: '100%',
    aspectRatio: 1,
    borderRadius: radius.camera,
    marginBottom: 10,
    backgroundColor: colors.cardElevated,
  },
  mealLine: { fontSize: 16, fontWeight: '700', color: colors.deep, marginBottom: 4 },
  caption: { fontSize: 14, color: colors.textMuted, lineHeight: 20, marginBottom: 8 },
  reactions: { fontSize: 14, fontWeight: '700', color: colors.primaryDark },
  reactionsMuted: { fontSize: 13, color: colors.textMuted },
});
