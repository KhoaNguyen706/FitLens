import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Image,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { addReaction, getPhotos, getPost, postPhotoUrl, removeReaction } from '../api';
import AuthenticatedImage from '../components/AuthenticatedImage';
import { useAuth } from '../context/AuthContext';
import { colors, radius } from '../theme';

const REACTIONS = ['❤️', '👍', '🔥', '💪'];

function formatTime(iso) {
  return new Date(iso).toLocaleString(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function visibilityLabel(visibility) {
  if (visibility === 'PRIVATE') return 'Private';
  if (visibility === 'FRIENDS') return 'Shared with friends';
  return 'Shared with close friends';
}

export default function PostDetailScreen({ postId, onBack }) {
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [photoUri, setPhotoUri] = useState(null);
  const [loading, setLoading] = useState(true);
  const [reacting, setReacting] = useState(false);

  const load = useCallback(async () => {
    try {
      const [detail, photos] = await Promise.all([getPost(postId), getPhotos()]);
      setPost(detail);
      if (detail.mealEntryId) {
        const match = photos.find((p) => p.mealEntryId === detail.mealEntryId);
        if (match?.localUri) setPhotoUri(match.localUri);
      }
    } catch (e) {
      Alert.alert('FitLens', e.message);
      onBack();
    } finally {
      setLoading(false);
    }
  }, [postId, onBack]);

  useEffect(() => {
    load();
  }, [load]);

  async function toggleReaction(emoji) {
    if (!post || reacting) return;
    setReacting(true);
    try {
      if (post.myReaction === emoji) {
        const updated = await removeReaction(postId);
        setPost(updated);
      } else {
        const updated = await addReaction(postId, emoji);
        setPost(updated);
      }
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setReacting(false);
    }
  }

  const isOwnPost = post?.authorId === user?.id;

  return (
    <ScrollView style={styles.root} contentContainerStyle={styles.scroll}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}>
          <Text style={styles.back}>Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Post</Text>
        <View style={styles.spacer} />
      </View>

      {loading ? (
        <ActivityIndicator color={colors.primary} size="large" style={{ marginTop: 40 }} />
      ) : (
        <>
          <View style={styles.card}>
            <Text style={styles.author}>{post.authorDisplayName}</Text>
            <Text style={styles.time}>{formatTime(post.createdAt)}</Text>

            {post.hasPhoto ? (
              <AuthenticatedImage uri={postPhotoUrl(postId)} style={styles.photo} />
            ) : photoUri ? (
              <Image source={{ uri: photoUri }} style={styles.photo} />
            ) : null}

            {post.mealName && (
              <Text style={styles.mealLine}>
                {post.mealName}
                {post.mealCalories != null ? ` · ${post.mealCalories} kcal` : ''}
              </Text>
            )}
            {post.caption ? <Text style={styles.caption}>{post.caption}</Text> : null}

            <Text style={styles.visibility}>{visibilityLabel(post.visibility)}</Text>
          </View>

          <Text style={styles.sectionTitle}>React</Text>
          <View style={styles.reactionRow}>
            {REACTIONS.map((emoji) => {
              const active = post.myReaction === emoji;
              return (
                <TouchableOpacity
                  key={emoji}
                  style={[styles.reactionButton, active && styles.reactionButtonActive]}
                  onPress={() => toggleReaction(emoji)}
                  disabled={reacting}
                >
                  <Text style={styles.reactionEmoji}>{emoji}</Text>
                </TouchableOpacity>
              );
            })}
          </View>

          <Text style={styles.sectionTitle}>
            Reactions {isOwnPost ? 'on your post' : ''}
          </Text>
          {(post.reactions ?? []).length === 0 ? (
            <Text style={styles.emptyReactions}>No reactions yet.</Text>
          ) : (
            post.reactions.map((r) => (
              <View key={r.id} style={styles.reactionItem}>
                <Text style={styles.reactionItemEmoji}>{r.emoji}</Text>
                <Text style={styles.reactionItemName}>{r.displayName}</Text>
              </View>
            ))
          )}
        </>
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
  title: { fontSize: 20, fontWeight: '800', color: colors.deep },
  spacer: { minWidth: 50 },
  card: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 16,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 20,
  },
  author: { fontSize: 17, fontWeight: '800', color: colors.deep },
  time: { fontSize: 13, color: colors.textMuted, marginTop: 2, marginBottom: 12 },
  photo: {
    width: '100%',
    aspectRatio: 1,
    borderRadius: radius.camera,
    marginBottom: 12,
  },
  mealLine: { fontSize: 16, fontWeight: '700', color: colors.deep, marginBottom: 6 },
  caption: { fontSize: 15, color: colors.textMuted, lineHeight: 22, marginBottom: 8 },
  visibility: { fontSize: 12, fontWeight: '700', color: colors.primaryDark, marginTop: 4 },
  sectionTitle: { fontSize: 14, fontWeight: '800', color: colors.deep, marginBottom: 10 },
  reactionRow: { flexDirection: 'row', gap: 10, marginBottom: 24 },
  reactionButton: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  reactionButtonActive: {
    backgroundColor: colors.skyLight,
    borderColor: colors.primary,
    borderWidth: 2,
  },
  reactionEmoji: { fontSize: 24 },
  emptyReactions: { color: colors.textMuted, fontSize: 14, marginBottom: 20 },
  reactionItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    backgroundColor: colors.card,
    borderRadius: radius.input,
    padding: 12,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: colors.border,
  },
  reactionItemEmoji: { fontSize: 20 },
  reactionItemName: { fontSize: 15, fontWeight: '600', color: colors.deep },
});
