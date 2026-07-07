import React, { useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Image,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import * as FileSystem from 'expo-file-system/legacy';
import { createMeal, createPost, estimateMealPhoto, saveLocalPhoto } from '../api';
import { colors, radius } from '../theme';

const MEAL_TYPES = [
  { value: 'BREAKFAST', label: 'Breakfast' },
  { value: 'LUNCH', label: 'Lunch' },
  { value: 'DINNER', label: 'Dinner' },
  { value: 'SNACK', label: 'Snack' },
  { value: 'OTHER', label: 'Other' },
];

const VISIBILITY_OPTIONS = [
  { value: 'PRIVATE', label: 'Private' },
  { value: 'FRIENDS', label: 'Friends' },
  { value: 'CLOSE_FRIENDS', label: 'Close friends' },
];

async function readPhotoForUpload(uri) {
  if (uri?.startsWith('data:')) {
    const [header, data] = uri.split(',');
    const mimeType = header.match(/^data:(.*);base64$/)?.[1] || 'image/jpeg';
    return { photoBase64: data, mimeType };
  }

  return {
    photoBase64: await FileSystem.readAsStringAsync(uri, {
      encoding: FileSystem.EncodingType.Base64,
    }),
    mimeType: 'image/jpeg',
  };
}

function guessMealType() {
  const h = new Date().getHours();
  if (h < 11) return 'BREAKFAST';
  if (h < 15) return 'LUNCH';
  if (h < 21) return 'DINNER';
  return 'SNACK';
}

export default function ConfirmMealScreen({ photoUri, onSaved, onCancel }) {
  const [mealName, setMealName] = useState('');
  const [mealType, setMealType] = useState(guessMealType());
  const [calories, setCalories] = useState('');
  const [visibility, setVisibility] = useState('PRIVATE');
  const [estimateNote, setEstimateNote] = useState('');
  const [estimating, setEstimating] = useState(false);
  const [saving, setSaving] = useState(false);

  async function estimateWithAi() {
    if (!photoUri) return;
    setEstimating(true);
    setEstimateNote('');
    try {
      const estimate = await estimateMealPhoto(await readPhotoForUpload(photoUri));
      if (estimate.mealName && !mealName.trim()) {
        setMealName(estimate.mealName);
      }
      if (estimate.mealType) {
        setMealType(estimate.mealType);
      }
      if (estimate.calories != null) {
        setCalories(String(estimate.calories));
      }
      const confidence = estimate.confidencePercent != null
        ? `${estimate.confidencePercent}% confidence`
        : 'Review before saving';
      setEstimateNote(`${confidence}. ${estimate.notes || ''}`.trim());
    } catch (e) {
      Alert.alert('FitLens', e.message);
    } finally {
      setEstimating(false);
    }
  }

  async function save() {
    if (!mealName.trim()) {
      Alert.alert('FitLens', 'Give your meal a name.');
      return;
    }
    const kcal = parseInt(calories, 10);
    if (Number.isNaN(kcal) || kcal < 0) {
      Alert.alert('FitLens', 'Enter the calories (a number).');
      return;
    }
    setSaving(true);
    try {
      const meal = await createMeal({
        mealName: mealName.trim(),
        mealType,
        calories: kcal,
        loggedAt: new Date().toISOString(),
      });
      if (Platform.OS !== 'web') {
        try {
          await saveLocalPhoto(meal.id, photoUri);
        } catch (e) {
          console.warn('Photo metadata not saved:', e.message);
        }
      }
      const postBody = {
        mealEntryId: meal.id,
        caption: mealName.trim(),
        visibility,
      };
      if (visibility !== 'PRIVATE' && photoUri) {
        postBody.photoBase64 = (await readPhotoForUpload(photoUri)).photoBase64;
      }
      await createPost(postBody);
      onSaved();
    } catch (e) {
      Alert.alert('FitLens', e.message);
      setSaving(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={styles.root}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
        <Text style={styles.screenTitle}>Log meal</Text>

        <View style={styles.photoWrap}>
          <Image source={{ uri: photoUri }} style={styles.photo} />
        </View>

        <View style={styles.card}>
          <TextInput
            style={styles.input}
            placeholder="What did you eat?"
            placeholderTextColor={colors.textMuted}
            value={mealName}
            onChangeText={setMealName}
          />

          <Text style={styles.sectionLabel}>Meal type</Text>
          <View style={styles.chipRow}>
            {MEAL_TYPES.map((t) => (
              <TouchableOpacity
                key={t.value}
                style={[styles.chip, mealType === t.value && styles.chipActive]}
                onPress={() => setMealType(t.value)}
              >
                <Text
                  style={[styles.chipText, mealType === t.value && styles.chipTextActive]}
                >
                  {t.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          <Text style={styles.sectionLabel}>Calories</Text>
          <View style={styles.calorieRow}>
            <TextInput
              style={[styles.input, styles.calorieInput]}
              placeholder="0"
              placeholderTextColor={colors.textMuted}
              value={calories}
              onChangeText={setCalories}
              keyboardType="number-pad"
            />
            <TouchableOpacity
              style={[styles.estimateButton, estimating && { opacity: 0.55 }]}
              onPress={estimateWithAi}
              disabled={estimating || saving}
            >
              {estimating ? (
                <ActivityIndicator color={colors.primary} />
              ) : (
                <Text style={styles.estimateButtonText}>AI</Text>
              )}
            </TouchableOpacity>
          </View>
          {estimateNote ? <Text style={styles.estimateNote}>{estimateNote}</Text> : null}

          <Text style={styles.sectionLabel}>Share with</Text>
          <View style={styles.chipRow}>
            {VISIBILITY_OPTIONS.map((option) => (
              <TouchableOpacity
                key={option.value}
                style={[styles.chip, visibility === option.value && styles.chipActive]}
                onPress={() => setVisibility(option.value)}
              >
                <Text
                  style={[
                    styles.chipText,
                    visibility === option.value && styles.chipTextActive,
                  ]}
                >
                  {option.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
          <Text style={styles.visibilityNote}>
            Friends posts go to your journey feed. Private stays on your log only.
          </Text>

          <TouchableOpacity style={styles.saveButton} onPress={save} disabled={saving}>
            {saving ? (
              <ActivityIndicator color={colors.white} />
            ) : (
              <Text style={styles.saveButtonText}>Save to today</Text>
            )}
          </TouchableOpacity>

          <TouchableOpacity style={styles.cancelButton} onPress={onCancel} disabled={saving}>
            <Text style={styles.cancelButtonText}>Retake</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background },
  scroll: { flexGrow: 1, padding: 20, paddingTop: 60, paddingBottom: 40 },
  screenTitle: {
    fontSize: 28,
    fontWeight: '800',
    color: colors.deep,
    marginBottom: 16,
  },
  photoWrap: {
    aspectRatio: 1,
    borderRadius: radius.camera,
    overflow: 'hidden',
    marginBottom: 16,
    backgroundColor: colors.cardElevated,
    borderWidth: 1,
    borderColor: colors.border,
  },
  photo: { flex: 1, width: '100%' },
  card: {
    backgroundColor: colors.card,
    borderRadius: radius.card,
    padding: 18,
    gap: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  sectionLabel: {
    fontSize: 12,
    fontWeight: '700',
    color: colors.textMuted,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  input: {
    backgroundColor: colors.surface,
    borderRadius: radius.input,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 16,
    color: colors.text,
  },
  chipRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  chip: {
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: radius.pill,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
  },
  chipActive: { backgroundColor: colors.primary, borderColor: colors.primary },
  chipText: { color: colors.textMuted, fontWeight: '600', fontSize: 14 },
  chipTextActive: { color: colors.white, fontWeight: '800' },
  calorieRow: { flexDirection: 'row', gap: 10 },
  calorieInput: { flex: 1 },
  estimateButton: {
    backgroundColor: colors.surface,
    borderRadius: radius.input,
    paddingHorizontal: 16,
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: colors.borderLight,
  },
  estimateButtonText: { color: colors.deep, fontWeight: '700', fontSize: 15 },
  estimateNote: { fontSize: 12, color: colors.textMuted, marginTop: -4, lineHeight: 18 },
  visibilityNote: { fontSize: 12, color: colors.textMuted, marginTop: -4, lineHeight: 18 },
  saveButton: {
    backgroundColor: colors.primary,
    borderRadius: radius.pill,
    paddingVertical: 15,
    alignItems: 'center',
    marginTop: 4,
  },
  saveButtonText: { color: colors.white, fontSize: 17, fontWeight: '800' },
  cancelButton: { alignItems: 'center', paddingVertical: 8 },
  cancelButtonText: { color: colors.textMuted, fontSize: 15, fontWeight: '600' },
});
