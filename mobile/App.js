import React, { useCallback, useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View, useWindowDimensions, Platform } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { AuthProvider, useAuth } from './src/context/AuthContext';
import { getTodayDashboard } from './src/api';
import BottomNav from './src/components/BottomNav';
import AddFriendScreen from './src/screens/AddFriendScreen';
import AuthScreen from './src/screens/AuthScreen';
import CameraScreen from './src/screens/CameraScreen';
import CloseFriendsFeedScreen from './src/screens/CloseFriendsFeedScreen';
import ConfirmMealScreen from './src/screens/ConfirmMealScreen';
import DashboardScreen from './src/screens/DashboardScreen';
import FriendChatScreen from './src/screens/FriendChatScreen';
import FriendRequestsScreen from './src/screens/FriendRequestsScreen';
import FriendsScreen from './src/screens/FriendsScreen';
import HistoryScreen from './src/screens/HistoryScreen';
import PostDetailScreen from './src/screens/PostDetailScreen';
import { colors } from './src/theme';

function Main() {
  const { user, loading } = useAuth();
  const [tab, setTab] = useState('camera');
  const [overlay, setOverlay] = useState(null);
  const [friendsView, setFriendsView] = useState(null);
  const [selectedPostId, setSelectedPostId] = useState(null);
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [photoUri, setPhotoUri] = useState(null);
  const [todayCalories, setTodayCalories] = useState(0);
  const [refreshKey, setRefreshKey] = useState(0);

  const refreshCalories = useCallback(async () => {
    try {
      const dash = await getTodayDashboard();
      setTodayCalories(dash.totalCalories);
      setRefreshKey((k) => k + 1);
    } catch (e) {
      // Non-blocking.
    }
  }, []);

  useEffect(() => {
    if (user) {
      refreshCalories();
    } else {
      setTab('camera');
      setOverlay(null);
      setFriendsView(null);
      setSelectedPostId(null);
      setSelectedFriend(null);
      setTodayCalories(0);
    }
  }, [user, refreshCalories]);

  function openFriendsView(view) {
    setFriendsView(view);
    if (view !== 'post') setSelectedPostId(null);
    if (view !== 'chat') setSelectedFriend(null);
  }

  function handleTabChange(nextTab) {
    setTab(nextTab);
    setFriendsView(null);
    setSelectedPostId(null);
    setSelectedFriend(null);
  }

  if (loading) {
    return (
      <View style={styles.splash}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  if (!user) return <AuthScreen />;

  if (overlay === 'confirm') {
    return (
      <ConfirmMealScreen
        photoUri={photoUri}
        onSaved={() => {
          setOverlay(null);
          setPhotoUri(null);
          refreshCalories();
        }}
        onCancel={() => {
          setOverlay(null);
          setPhotoUri(null);
        }}
      />
    );
  }

  function renderFriendsTab() {
    if (friendsView === 'post' && selectedPostId) {
      return (
        <PostDetailScreen
          postId={selectedPostId}
          onBack={() => openFriendsView('feed')}
        />
      );
    }
    if (friendsView === 'feed') {
      return (
        <CloseFriendsFeedScreen
          onBack={() => openFriendsView(null)}
          onOpenPost={(id) => {
            setSelectedPostId(id);
            openFriendsView('post');
          }}
        />
      );
    }
    if (friendsView === 'add') {
      return <AddFriendScreen onBack={() => openFriendsView(null)} />;
    }
    if (friendsView === 'requests') {
      return <FriendRequestsScreen onBack={() => openFriendsView(null)} />;
    }
    if (friendsView === 'chat' && selectedFriend) {
      return (
        <FriendChatScreen
          friend={selectedFriend}
          onBack={() => openFriendsView(null)}
        />
      );
    }
    return (
      <FriendsScreen
        key={refreshKey}
        onNavigate={(view) => openFriendsView(view)}
        onOpenChat={(friend) => {
          setSelectedFriend(friend);
          openFriendsView('chat');
        }}
      />
    );
  }

  const { width: windowWidth } = useWindowDimensions();
  const isLargeWeb = Platform.OS === 'web' && windowWidth > 480;

  const content = (
    <View style={styles.shell}>
      {tab === 'dashboard' && <DashboardScreen key={refreshKey} />}
      {tab === 'camera' && (
        <CameraScreen
          todayCalories={todayCalories}
          onPhotoTaken={(uri) => {
            setPhotoUri(uri);
            setOverlay('confirm');
          }}
          onOpenHistory={() => handleTabChange('history')}
        />
      )}
      {tab === 'friends' && renderFriendsTab()}
      {tab === 'history' && (
        <HistoryScreen key={refreshKey} onDataChanged={refreshCalories} />
      )}
      <BottomNav active={tab} onChange={handleTabChange} />
    </View>
  );

  if (isLargeWeb) {
    return (
      <View style={styles.webContainer}>
        <View style={styles.phoneFrame}>
          {content}
        </View>
      </View>
    );
  }

  return content;
}

export default function App() {
  return (
    <AuthProvider>
      <StatusBar style="dark" />
      <Main />
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  splash: {
    flex: 1,
    backgroundColor: colors.background,
    alignItems: 'center',
    justifyContent: 'center',
  },
  shell: { flex: 1, backgroundColor: colors.background },
  webContainer: {
    flex: 1,
    backgroundColor: '#D6EAF8',
    alignItems: 'center',
    justifyContent: 'center',
  },
  phoneFrame: {
    width: '100%',
    maxWidth: 430,
    height: '92%',
    maxHeight: 880,
    borderRadius: 36,
    overflow: 'hidden',
    backgroundColor: colors.background,
    borderWidth: 8,
    borderColor: '#0B3B66',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 12 },
    shadowOpacity: 0.15,
    shadowRadius: 16,
  },
});
