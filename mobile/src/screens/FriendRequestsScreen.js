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
import { acceptFriendRequest, getFriendRequests } from '../api';
import { colors, radius } from '../theme';

export default function FriendRequestsScreen({ onBack }) {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [acceptingId, setAcceptingId] = useState(null);

  const load = useCallback(async () => {
    try {
      setRequests(await getFriendRequests());
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

  async function accept(id) {
    setAcceptingId(id);
    try {
      await acceptFriendRequest(id);
      await load();
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setAcceptingId(null);
    }
  }

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
        <Text style={styles.title}>Requests</Text>
        <View style={styles.spacer} />
      </View>

      {loading ? (
        <ActivityIndicator color={colors.primary} size="large" style={{ marginTop: 40 }} />
      ) : requests.length === 0 ? (
        <Text style={styles.empty}>No pending friend requests.</Text>
      ) : (
        requests.map((req) => (
          <View key={req.id} style={styles.card}>
            <View style={styles.info}>
              <Text style={styles.name}>{req.requester.displayName}</Text>
              <Text style={styles.email}>{req.requester.email}</Text>
            </View>
            <TouchableOpacity
              style={styles.acceptButton}
              onPress={() => accept(req.id)}
              disabled={acceptingId === req.id}
            >
              {acceptingId === req.id ? (
                <ActivityIndicator color={colors.white} size="small" />
              ) : (
                <Text style={styles.acceptText}>Accept</Text>
              )}
            </TouchableOpacity>
          </View>
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
  title: { fontSize: 20, fontWeight: '800', color: colors.deep },
  spacer: { minWidth: 50 },
  empty: { color: colors.textMuted, fontSize: 15, textAlign: 'center', marginTop: 40 },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 14,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: colors.border,
    gap: 12,
  },
  info: { flex: 1 },
  name: { fontSize: 16, fontWeight: '700', color: colors.deep },
  email: { fontSize: 13, color: colors.textMuted, marginTop: 2 },
  acceptButton: {
    backgroundColor: colors.primary,
    borderRadius: radius.pill,
    paddingHorizontal: 16,
    paddingVertical: 10,
    minWidth: 80,
    alignItems: 'center',
  },
  acceptText: { color: colors.white, fontWeight: '800', fontSize: 14 },
});
