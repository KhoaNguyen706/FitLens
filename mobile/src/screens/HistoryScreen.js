import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Image,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import {
  deleteMeal,
  getDashboardForDay,
  getMonthProgress,
  getPhotos,
  utcToday,
} from '../api';
import ProfileAvatar from '../components/ProfileAvatar';
import { colors, radius } from '../theme';

function formatMealType(type) {
  if (!type) return 'Other';
  return type.charAt(0) + type.slice(1).toLowerCase();
}

function mealTypeInitial(type) {
  return formatMealType(type).charAt(0).toUpperCase();
}

function monthLabel(year, month) {
  return new Date(year, month, 1).toLocaleDateString(undefined, {
    month: 'long',
    year: 'numeric',
  });
}

function formatDayHeading(isoDay, todayIso) {
  if (isoDay === todayIso) return 'Today';
  const d = new Date(`${isoDay}T12:00:00`);
  return d.toLocaleDateString(undefined, {
    weekday: 'long',
    month: 'short',
    day: 'numeric',
  });
}

const WEEKDAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

export default function HistoryScreen({ onDataChanged }) {
  const today = utcToday();
  const [selectedDay, setSelectedDay] = useState(today);
  const [dashboard, setDashboard] = useState(null);
  const [monthProgress, setMonthProgress] = useState([]);
  const [photosByMeal, setPhotosByMeal] = useState({});
  const [loading, setLoading] = useState(true);
  const [dayLoading, setDayLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();

  const calendarWeeks = useMemo(() => {
    const first = new Date(year, month, 1);
    const startPad = first.getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const cells = [];
    for (let i = 0; i < startPad; i++) cells.push(null);
    for (let d = 1; d <= daysInMonth; d++) {
      cells.push(
        `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
      );
    }
    while (cells.length % 7 !== 0) cells.push(null);

    const weeks = [];
    for (let i = 0; i < cells.length; i += 7) {
      weeks.push(cells.slice(i, i + 7));
    }
    return weeks;
  }, [year, month]);

  const progressByDay = useMemo(() => {
    const map = {};
    for (const p of monthProgress) map[p.day] = p;
    return map;
  }, [monthProgress]);

  const loadDay = useCallback(async (day) => {
    const [dash, photos] = await Promise.all([getDashboardForDay(day), getPhotos()]);
    setDashboard(dash);
    const map = {};
    for (const p of photos) {
      if (p.mealEntryId && p.localUri) map[p.mealEntryId] = p.localUri;
    }
    setPhotosByMeal(map);
  }, []);

  const load = useCallback(async () => {
    try {
      const progress = await getMonthProgress(year, month);
      setMonthProgress(progress);
      await loadDay(selectedDay);
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setLoading(false);
      setRefreshing(false);
      setDayLoading(false);
    }
  }, [loadDay, selectedDay, year, month]);

  useEffect(() => {
    load();
  }, [load]);

  async function selectDay(day) {
    if (!day || day === selectedDay) return;
    setSelectedDay(day);
    setDayLoading(true);
    try {
      await loadDay(day);
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setDayLoading(false);
    }
  }

  function confirmDelete(meal) {
    Alert.alert('Delete meal', `Remove "${meal.mealName}"?`, [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          try {
            await deleteMeal(meal.id);
            await load();
            onDataChanged();
          } catch (e) {
            Alert.alert('FitLens', e.message);
          }
        },
      },
    ]);
  }

  function renderMeal(item) {
    const photoUri = photosByMeal[item.id];
    const time = new Date(item.loggedAt).toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
    });
    return (
      <TouchableOpacity
        key={item.id}
        style={styles.mealCard}
        onLongPress={() => confirmDelete(item)}
      >
        {photoUri ? (
          <Image source={{ uri: photoUri }} style={styles.mealPhoto} />
        ) : (
          <View style={[styles.mealPhoto, styles.mealPhotoPlaceholder]}>
            <Text style={styles.mealPhotoInitial}>{mealTypeInitial(item.mealType)}</Text>
          </View>
        )}
        <View style={styles.mealInfo}>
          <Text style={styles.mealName} numberOfLines={1}>
            {item.mealName}
          </Text>
          <Text style={styles.mealMeta}>
            {formatMealType(item.mealType)} · {time}
          </Text>
        </View>
        <Text style={styles.mealCalories}>{item.calories} kcal</Text>
      </TouchableOpacity>
    );
  }

  return (
    <View style={styles.root}>
      <ScrollView
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
          <Text style={styles.headerTitle}>History</Text>
          <ProfileAvatar size={40} />
        </View>

        <View style={styles.calendarCard}>
          <Text style={styles.monthTitle}>{monthLabel(year, month)}</Text>

          <View style={styles.weekRow}>
            {WEEKDAYS.map((label) => (
              <View key={label} style={styles.weekCell}>
                <Text style={styles.weekLabel}>{label}</Text>
              </View>
            ))}
          </View>

          {loading ? (
            <ActivityIndicator color={colors.primary} style={{ marginVertical: 24 }} />
          ) : (
            calendarWeeks.map((week, wi) => (
              <View key={`week-${wi}`} style={styles.weekRow}>
                {week.map((day, di) => {
                  if (!day) {
                    return <View key={`empty-${wi}-${di}`} style={styles.weekCell} />;
                  }

                  const dayNum = parseInt(day.slice(8), 10);
                  const entry = progressByDay[day];
                  const hasMeals = (entry?.mealCount ?? 0) > 0;
                  const isSelected = day === selectedDay;
                  const isToday = day === today;

                  return (
                    <View key={day} style={styles.weekCell}>
                      <TouchableOpacity
                        style={[
                          styles.dayTile,
                          hasMeals && !isSelected && styles.dayTileLogged,
                          isToday && !isSelected && styles.dayTileToday,
                          isSelected && styles.dayTileSelected,
                        ]}
                        onPress={() => selectDay(day)}
                        activeOpacity={0.75}
                      >
                        <Text
                          style={[
                            styles.dayNum,
                            isSelected && styles.dayNumSelected,
                            hasMeals && !isSelected && styles.dayNumLogged,
                          ]}
                        >
                          {dayNum}
                        </Text>
                        {hasMeals && (
                          <View
                            style={[styles.dayDot, isSelected && styles.dayDotSelected]}
                          />
                        )}
                      </TouchableOpacity>
                    </View>
                  );
                })}
              </View>
            ))
          )}
        </View>

        <View style={styles.dayHeader}>
          <Text style={styles.dayTitle}>{formatDayHeading(selectedDay, today)}</Text>
          <Text style={styles.dayTotal}>{dashboard?.totalCalories ?? 0} kcal</Text>
        </View>

        {dayLoading ? (
          <ActivityIndicator color={colors.primary} size="large" style={{ marginTop: 24 }} />
        ) : (dashboard?.meals ?? []).length === 0 ? (
          <View style={styles.empty}>
            <Text style={styles.emptyTitle}>No meals</Text>
            <Text style={styles.emptyText}>Nothing logged on this day.</Text>
          </View>
        ) : (
          (dashboard?.meals ?? []).map(renderMeal)
        )}

        <Text style={styles.hint}>Long-press a meal to delete</Text>
      </ScrollView>
    </View>
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
  headerTitle: { fontSize: 28, fontWeight: '800', color: colors.deep },
  calendarCard: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 16,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: colors.border,
    shadowColor: colors.deep,
    shadowOpacity: 0.06,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
  },
  monthTitle: {
    fontSize: 18,
    fontWeight: '800',
    color: colors.deep,
    marginBottom: 14,
  },
  weekRow: { flexDirection: 'row', marginBottom: 6 },
  weekCell: { flex: 1, alignItems: 'center', paddingHorizontal: 3 },
  weekLabel: {
    fontSize: 11,
    fontWeight: '700',
    color: colors.textMuted,
    textTransform: 'uppercase',
  },
  dayTile: {
    width: '100%',
    aspectRatio: 1,
    maxWidth: 44,
    borderRadius: radius.cell,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  dayTileLogged: {
    backgroundColor: colors.skyLight,
    borderColor: colors.sky,
  },
  dayTileToday: {
    borderColor: colors.primary,
    borderWidth: 2,
  },
  dayTileSelected: {
    backgroundColor: colors.primary,
    borderColor: colors.primary,
  },
  dayNum: { fontSize: 15, fontWeight: '700', color: colors.deep },
  dayNumLogged: { color: colors.primaryDark },
  dayNumSelected: { color: colors.white },
  dayDot: {
    width: 5,
    height: 5,
    borderRadius: 3,
    backgroundColor: colors.primary,
    marginTop: 3,
  },
  dayDotSelected: { backgroundColor: colors.white },
  dayHeader: {
    flexDirection: 'row',
    alignItems: 'baseline',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  dayTitle: { fontSize: 17, fontWeight: '800', color: colors.deep, flex: 1, marginRight: 8 },
  dayTotal: { fontSize: 16, fontWeight: '800', color: colors.primaryDark },
  mealCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 12,
    gap: 12,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: colors.border,
  },
  mealPhoto: { width: 56, height: 56, borderRadius: 16 },
  mealPhotoPlaceholder: {
    backgroundColor: colors.skyLight,
    alignItems: 'center',
    justifyContent: 'center',
  },
  mealPhotoInitial: { fontSize: 20, fontWeight: '800', color: colors.primaryDark },
  mealInfo: { flex: 1 },
  mealName: { fontSize: 15, fontWeight: '700', color: colors.deep },
  mealMeta: { fontSize: 12, color: colors.textMuted, marginTop: 2 },
  mealCalories: { fontSize: 15, fontWeight: '800', color: colors.primaryDark },
  empty: { alignItems: 'center', paddingVertical: 32 },
  emptyTitle: { fontSize: 16, fontWeight: '800', color: colors.deep, marginBottom: 6 },
  emptyText: { color: colors.textMuted, fontSize: 14 },
  hint: {
    textAlign: 'center',
    color: colors.textMuted,
    fontSize: 12,
    marginTop: 8,
  },
});
