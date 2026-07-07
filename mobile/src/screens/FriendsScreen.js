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
import { getFriendRequests, getFriends } from '../api';
import ProfileAvatar from '../components/ProfileAvatar';
import { colors, radius } from '../theme';

export default function FriendsScreen({ onNavigate, onOpenChat }) {
  const [friends, setFriends] = useState([]);
  const [requestCount, setRequestCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async () => {
    try {
      const friendsList = await getFriends();
      setFriends(friendsList);
    } catch (e) {
      Alert.alert('FitLens', e.message);
    }
    try {
      const requests = await getFriendRequests();
      setRequestCount(requests.length);
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
        <Text style={styles.title}>Friends</Text>
        <ProfileAvatar size={40} />
      </View>

      <TouchableOpacity style={styles.primaryCard} onPress={() => onNavigate('feed')}>
        <Text style={styles.primaryCardTitle}>Journey feed</Text>
        <Text style={styles.primaryCardSub}>Food snaps and gym progress from friends</Text>
      </TouchableOpacity>

      <View style={styles.row}>
        <TouchableOpacity style={styles.actionCard} onPress={() => onNavigate('add')}>
          <Text style={styles.actionTitle}>Add friend</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.actionCard} onPress={() => onNavigate('requests')}>
          <Text style={styles.actionTitle}>Requests</Text>
          {requestCount > 0 && (
            <View style={styles.badge}>
              <Text style={styles.badgeText}>{requestCount}</Text>
            </View>
          )}
        </TouchableOpacity>
      </View>

      <Text style={styles.sectionTitle}>Friends ({friends.length})</Text>

      {loading ? (
        <ActivityIndicator color={colors.primary} style={{ marginTop: 24 }} />
      ) : friends.length === 0 ? (
        <Text style={styles.empty}>No friends yet. Send a request to start sharing progress.</Text>
      ) : (
        friends.map((friend) => (
          <TouchableOpacity
            key={friend.friendUserId}
            style={styles.friendRow}
            onPress={() => onOpenChat(friend)}
            activeOpacity={0.85}
          >
            <View style={styles.friendAvatar}>
              <Text style={styles.friendInitial}>
                {friend.displayName.charAt(0).toUpperCase()}
              </Text>
            </View>
            <View style={styles.friendInfo}>
              <Text style={styles.friendName}>{friend.displayName}</Text>
              <Text style={styles.friendEmail}>{friend.email}</Text>
            </View>
            <Text style={styles.chatHint}>Chat</Text>
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
  title: { fontSize: 28, fontWeight: '800', color: colors.deep },
  primaryCard: {
    backgroundColor: colors.primary,
    borderRadius: radius.card,
    padding: 20,
    marginBottom: 14,
  },
  primaryCardTitle: { fontSize: 18, fontWeight: '800', color: colors.white },
  primaryCardSub: { fontSize: 14, color: colors.skyLight, marginTop: 4 },
  row: { flexDirection: 'row', gap: 10, marginBottom: 20 },
  actionCard: {
    flex: 1,
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 16,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
  },
  actionTitle: { fontSize: 15, fontWeight: '700', color: colors.deep },
  badge: {
    position: 'absolute',
    top: 8,
    right: 8,
    backgroundColor: colors.primary,
    borderRadius: 10,
    minWidth: 20,
    height: 20,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 6,
  },
  badgeText: { color: colors.white, fontSize: 11, fontWeight: '800' },
  sectionTitle: { fontSize: 16, fontWeight: '800', color: colors.deep, marginBottom: 10 },
  friendRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 12,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: colors.border,
    gap: 12,
  },
  friendAvatar: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.skyLight,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: colors.sky,
  },
  friendInitial: { fontSize: 18, fontWeight: '800', color: colors.primaryDark },
  friendInfo: { flex: 1 },
  friendName: { fontSize: 15, fontWeight: '700', color: colors.deep },
  friendEmail: { fontSize: 12, color: colors.textMuted, marginTop: 2 },
  chatHint: { fontSize: 13, fontWeight: '700', color: colors.primaryDark },
  empty: { color: colors.textMuted, fontSize: 14, lineHeight: 20 },
});
