import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { colors, radius } from '../theme';

const TABS = [
  { id: 'dashboard', label: 'Progress' },
  { id: 'camera', label: 'Snap' },
  { id: 'friends', label: 'Friends' },
  { id: 'history', label: 'History' },
];

function TabIcon({ tab, active }) {
  const fill = active ? colors.primaryDark : colors.textMuted;
  if (tab === 'dashboard') {
    return (
      <View style={styles.gridIcon}>
        {[0, 1, 2, 3].map((i) => (
          <View key={i} style={[styles.gridDot, { backgroundColor: fill }]} />
        ))}
      </View>
    );
  }
  if (tab === 'camera') {
    return (
      <View style={styles.homeIcon}>
        <View style={[styles.homeRoof, { borderBottomColor: fill }]} />
        <View style={[styles.homeBase, { backgroundColor: fill }]} />
      </View>
    );
  }
  if (tab === 'friends') {
    return (
      <View style={styles.friendsIcon}>
        <View style={[styles.friendDot, { backgroundColor: fill }]} />
        <View style={[styles.friendDot, styles.friendDotRight, { backgroundColor: fill }]} />
      </View>
    );
  }
  return (
    <View style={styles.listIcon}>
      <View style={[styles.listLine, { backgroundColor: fill }]} />
      <View style={[styles.listLine, styles.listLineShort, { backgroundColor: fill }]} />
      <View style={[styles.listLine, { backgroundColor: fill }]} />
    </View>
  );
}

export default function BottomNav({ active, onChange }) {
  return (
    <View style={styles.wrap}>
      <View style={styles.bar}>
        {TABS.map((tab) => {
          const isActive = active === tab.id;
          return (
            <TouchableOpacity
              key={tab.id}
              style={[styles.tab, isActive && styles.tabActive]}
              onPress={() => onChange(tab.id)}
              activeOpacity={0.8}
            >
              <TabIcon tab={tab.id} active={isActive} />
              <Text style={[styles.tabLabel, isActive && styles.tabLabelActive]}>{tab.label}</Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 28,
    alignItems: 'center',
    paddingHorizontal: 12,
  },
  bar: {
    flexDirection: 'row',
    backgroundColor: colors.navBg,
    borderRadius: radius.nav,
    paddingVertical: 8,
    paddingHorizontal: 6,
    borderWidth: 1,
    borderColor: colors.border,
    gap: 2,
  },
  tab: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 8,
    paddingHorizontal: 4,
    borderRadius: 22,
    minWidth: 72,
  },
  tabActive: { backgroundColor: colors.overlay },
  tabLabel: { fontSize: 10, fontWeight: '600', color: colors.textMuted, marginTop: 5 },
  tabLabelActive: { color: colors.primaryDark, fontWeight: '700' },
  gridIcon: { width: 18, height: 18, flexDirection: 'row', flexWrap: 'wrap', gap: 4 },
  gridDot: { width: 7, height: 7, borderRadius: 2 },
  homeIcon: { width: 22, height: 18, alignItems: 'center', justifyContent: 'flex-end' },
  homeRoof: {
    width: 0,
    height: 0,
    borderLeftWidth: 11,
    borderRightWidth: 11,
    borderBottomWidth: 9,
    borderLeftColor: 'transparent',
    borderRightColor: 'transparent',
    marginBottom: 1,
  },
  homeBase: { width: 14, height: 8, borderRadius: 2 },
  friendsIcon: { width: 22, height: 16, flexDirection: 'row', alignItems: 'flex-end', gap: 4 },
  friendDot: { width: 9, height: 9, borderRadius: 5 },
  friendDotRight: { marginBottom: 4 },
  listIcon: { width: 18, height: 16, justifyContent: 'space-between' },
  listLine: { height: 2.5, borderRadius: 2, width: '100%' },
  listLineShort: { width: '70%' },
});
