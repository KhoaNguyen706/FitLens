import React, { useRef, useState } from 'react';
import {
  Alert,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import ProfileAvatar from '../components/ProfileAvatar';
import { colors, radius } from '../theme';

export default function CameraScreen({ todayCalories, onPhotoTaken, onOpenHistory }) {
  const [permission, requestPermission] = useCameraPermissions();
  const [facing, setFacing] = useState('back');
  const [capturing, setCapturing] = useState(false);
  const cameraRef = useRef(null);

  async function snap() {
    if (!cameraRef.current || capturing) return;
    setCapturing(true);
    try {
      const photo = await cameraRef.current.takePictureAsync({ quality: 0.7 });
      onPhotoTaken(photo.uri);
    } catch (e) {
      Alert.alert('FitLens', 'Could not take the photo: ' + e.message);
    } finally {
      setCapturing(false);
    }
  }

  return (
    <View style={styles.root}>
      <View style={styles.topBar}>
        <View style={styles.topSpacer} />

        <View style={styles.caloriePill}>
          <Text style={styles.caloriePillText}>{todayCalories} kcal today</Text>
        </View>

        <ProfileAvatar size={42} />
      </View>

      <View style={styles.cameraWrap}>
        {permission?.granted ? (
          <CameraView ref={cameraRef} style={styles.camera} facing={facing} />
        ) : (
          <View style={styles.permissionBox}>
            <Text style={styles.permissionTitle}>Camera access</Text>
            <Text style={styles.permissionText}>
              FitLens needs the camera to snap your meals.
            </Text>
            <TouchableOpacity style={styles.permissionButton} onPress={requestPermission}>
              <Text style={styles.permissionButtonText}>Allow camera</Text>
            </TouchableOpacity>
          </View>
        )}
      </View>

      <View style={styles.shutterRow}>
        <View style={styles.sideButtonSpacer} />
        <TouchableOpacity
          style={[styles.shutterOuter, capturing && { opacity: 0.5 }]}
          onPress={snap}
          disabled={!permission?.granted}
        >
          <View style={styles.shutterInner} />
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.sideButton}
          onPress={() => setFacing((f) => (f === 'back' ? 'front' : 'back'))}
        >
          <Text style={styles.sideButtonText}>Flip</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.historyHint} onPress={onOpenHistory}>
        <Text style={styles.historyHintText}>History</Text>
        <Text style={styles.historyChevron}>⌄</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background, paddingTop: 56, paddingBottom: 100 },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  topSpacer: { width: 42 },
  caloriePill: {
    backgroundColor: colors.card,
    borderRadius: radius.pill,
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderWidth: 1,
    borderColor: colors.border,
  },
  caloriePillText: { fontSize: 15, fontWeight: '700', color: colors.deep },
  cameraWrap: {
    marginHorizontal: 20,
    aspectRatio: 1,
    borderRadius: radius.camera,
    overflow: 'hidden',
    backgroundColor: colors.cardElevated,
    borderWidth: 1,
    borderColor: colors.border,
  },
  camera: { flex: 1 },
  permissionBox: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
    backgroundColor: colors.card,
  },
  permissionTitle: {
    color: colors.deep,
    fontSize: 18,
    fontWeight: '800',
    marginBottom: 8,
  },
  permissionText: {
    color: colors.textMuted,
    fontSize: 15,
    textAlign: 'center',
    marginBottom: 16,
    lineHeight: 22,
  },
  permissionButton: {
    backgroundColor: colors.primary,
    borderRadius: radius.pill,
    paddingHorizontal: 24,
    paddingVertical: 12,
  },
  permissionButtonText: { color: colors.white, fontWeight: '700', fontSize: 16 },
  shutterRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-evenly',
    marginTop: 32,
  },
  sideButtonSpacer: { width: 54, height: 54 },
  sideButton: {
    width: 54,
    height: 54,
    borderRadius: 27,
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  sideButtonText: { fontSize: 12, fontWeight: '700', color: colors.primaryDark },
  shutterOuter: {
    width: 84,
    height: 84,
    borderRadius: 42,
    borderWidth: 5,
    borderColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  shutterInner: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: colors.white,
  },
  historyHint: { alignItems: 'center', marginTop: 'auto', marginBottom: 8 },
  historyHintText: { color: colors.textMuted, fontSize: 15, fontWeight: '600' },
  historyChevron: { color: colors.textMuted, fontSize: 18, marginTop: -4 },
});
