import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Dimensions,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { getProgressRange, getTodayDashboard } from '../api';
import CalorieLineChart from '../components/CalorieLineChart';
import ProfileAvatar from '../components/ProfileAvatar';
import { colors, radius } from '../theme';

const CHART_DAYS = 7;

function computeStreak(days) {
  let streak = 0;
  for (let i = days.length - 1; i >= 0; i--) {
    if (days[i].mealCount > 0) streak += 1;
    else break;
  }
  return streak;
}

export default function DashboardScreen() {
  const [progress, setProgress] = useState([]);
  const [today, setToday] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const chartWidth = Dimensions.get('window').width - 64;

  const load = useCallback(async () => {
    try {
      const [range, dash] = await Promise.all([
        getProgressRange(CHART_DAYS),
        getTodayDashboard(),
      ]);
      setProgress(range);
      setToday(dash);
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

  const totalMeals = progress.reduce((sum, d) => sum + d.mealCount, 0);
  const daysWithMeals = progress.filter((d) => d.mealCount > 0).length;
  const avgCalories =
    daysWithMeals > 0
      ? Math.round(progress.reduce((sum, d) => sum + d.totalCalories, 0) / daysWithMeals)
      : 0;
  const streak = computeStreak(progress);

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
        <Text style={styles.headerTitle}>Progress</Text>
        <ProfileAvatar />
      </View>

      {loading ? (
        <ActivityIndicator color={colors.primary} size="large" style={{ marginTop: 80 }} />
      ) : (
        <>
          <View style={styles.heroCard}>
            <Text style={styles.heroLabel}>Today</Text>
            <Text style={styles.heroValue}>{today?.totalCalories ?? 0}</Text>
            <Text style={styles.heroUnit}>kcal logged</Text>
          </View>

          <View style={styles.chartCard}>
            <Text style={styles.cardTitle}>Last {CHART_DAYS} days</Text>
            <Text style={styles.cardSubtitle}>Daily calorie intake</Text>
            <CalorieLineChart data={progress} width={chartWidth} height={190} />
          </View>

          <View style={styles.statsRow}>
            <View style={styles.statPill}>
              <Text style={styles.statValue}>{totalMeals}</Text>
              <Text style={styles.statLabel}>Meals</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statPill}>
              <Text style={styles.statValue}>{streak}d</Text>
              <Text style={styles.statLabel}>Streak</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statPill}>
              <Text style={styles.statValue}>{avgCalories}</Text>
              <Text style={styles.statLabel}>Avg kcal</Text>
            </View>
          </View>

          <View style={styles.trendCard}>
            <Text style={styles.cardTitle}>This week</Text>
            {progress.map((d) => {
              const max = Math.max(...progress.map((p) => p.totalCalories), 1);
              const pct = Math.round((d.totalCalories / max) * 100);
              const label = new Date(`${d.day}T12:00:00Z`).toLocaleDateString(undefined, {
                month: 'short',
                day: 'numeric',
              });
              return (
                <View key={d.day} style={styles.barRow}>
                  <Text style={styles.barLabel}>{label}</Text>
                  <View style={styles.barTrack}>
                    <View style={[styles.barFill, { width: `${pct}%` }]} />
                  </View>
                  <Text style={styles.barValue}>{d.totalCalories}</Text>
                </View>
              );
            })}
          </View>
        </>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background },
  scroll: { paddingTop: 60, paddingHorizontal: 20, paddingBottom: 120 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 24,
  },
  headerTitle: { fontSize: 28, fontWeight: '800', color: colors.deep, letterSpacing: -0.5 },
  heroCard: {
    backgroundColor: colors.primary,
    borderRadius: radius.card,
    paddingVertical: 28,
    paddingHorizontal: 24,
    alignItems: 'center',
    marginBottom: 16,
    shadowColor: colors.primary,
    shadowOpacity: 0.3,
    shadowRadius: 16,
    shadowOffset: { width: 0, height: 6 },
    elevation: 6,
  },
  heroLabel: {
    fontSize: 13,
    fontWeight: '700',
    color: colors.skyLight,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  heroValue: {
    fontSize: 56,
    fontWeight: '900',
    color: colors.white,
    lineHeight: 62,
    marginTop: 4,
  },
  heroUnit: { fontSize: 15, fontWeight: '600', color: colors.skyLight, marginTop: 2 },
  chartCard: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 20,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '800',
    color: colors.deep,
    alignSelf: 'flex-start',
  },
  cardSubtitle: {
    fontSize: 13,
    color: colors.textMuted,
    alignSelf: 'flex-start',
    marginTop: 2,
    marginBottom: 12,
  },
  statsRow: {
    flexDirection: 'row',
    backgroundColor: colors.card,
    borderRadius: radius.pill,
    paddingVertical: 16,
    paddingHorizontal: 8,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
  },
  statPill: { flex: 1, alignItems: 'center' },
  statDivider: { width: 1, height: 32, backgroundColor: colors.border },
  statValue: { fontSize: 20, fontWeight: '800', color: colors.deep },
  statLabel: { fontSize: 12, fontWeight: '600', color: colors.textMuted, marginTop: 2 },
  trendCard: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 20,
    gap: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  barRow: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  barLabel: { width: 52, fontSize: 12, fontWeight: '600', color: colors.textMuted },
  barTrack: {
    flex: 1,
    height: 8,
    backgroundColor: colors.surface,
    borderRadius: 4,
    overflow: 'hidden',
  },
  barFill: {
    height: '100%',
    backgroundColor: colors.primary,
    borderRadius: 4,
  },
  barValue: { width: 36, fontSize: 13, fontWeight: '700', color: colors.deep, textAlign: 'right' },
});
