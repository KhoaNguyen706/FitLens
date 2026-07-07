import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import Svg, { Circle, Defs, LinearGradient, Path, Stop } from 'react-native-svg';
import { colors } from '../theme';

function formatDayLabel(isoDay) {
  const d = new Date(`${isoDay}T12:00:00Z`);
  return d.toLocaleDateString(undefined, { weekday: 'short' }).slice(0, 3);
}

export default function CalorieLineChart({ data, width = 320, height = 180 }) {
  const padding = { top: 16, right: 12, bottom: 32, left: 12 };
  const chartW = width - padding.left - padding.right;
  const chartH = height - padding.top - padding.bottom;

  const values = data.map((d) => d.totalCalories);
  const maxVal = Math.max(...values, 400);
  const minVal = 0;

  const points = data.map((d, i) => {
    const x = padding.left + (data.length <= 1 ? chartW / 2 : (i / (data.length - 1)) * chartW);
    const ratio = maxVal === minVal ? 0 : (d.totalCalories - minVal) / (maxVal - minVal);
    const y = padding.top + chartH - ratio * chartH;
    return { x, y, ...d };
  });

  const linePath = points
    .map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x.toFixed(1)} ${p.y.toFixed(1)}`)
    .join(' ');

  const areaPath =
    linePath +
    ` L ${points[points.length - 1].x.toFixed(1)} ${(padding.top + chartH).toFixed(1)}` +
    ` L ${points[0].x.toFixed(1)} ${(padding.top + chartH).toFixed(1)} Z`;

  return (
    <View style={styles.wrap}>
      <Svg width={width} height={height}>
        <Defs>
          <LinearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
            <Stop offset="0" stopColor={colors.primary} stopOpacity="0.3" />
            <Stop offset="1" stopColor={colors.primary} stopOpacity="0" />
          </LinearGradient>
        </Defs>
        <Path d={areaPath} fill="url(#areaGrad)" />
        <Path
          d={linePath}
          fill="none"
          stroke={colors.primary}
          strokeWidth={3}
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        {points.map((p) => (
          <Circle
            key={p.day}
            cx={p.x}
            cy={p.y}
            r={p.totalCalories > 0 ? 5 : 3}
            fill={p.totalCalories > 0 ? colors.primary : colors.border}
            stroke={colors.card}
            strokeWidth={2}
          />
        ))}
      </Svg>
      <View style={[styles.labels, { width }]}>
        {data.map((d) => (
          <Text key={d.day} style={styles.label}>
            {formatDayLabel(d.day)}
          </Text>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { alignItems: 'center' },
  labels: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 8,
    marginTop: -4,
  },
  label: {
    fontSize: 11,
    fontWeight: '600',
    color: colors.textMuted,
    textTransform: 'uppercase',
  },
});
