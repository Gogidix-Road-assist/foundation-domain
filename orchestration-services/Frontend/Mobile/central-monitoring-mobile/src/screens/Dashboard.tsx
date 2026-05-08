import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, StyleSheet, RefreshControl } from 'react-native';
import { API } from '../services/api';
import { WebSocketService } from '../services/websocket';

interface DashboardStats {
  activeRequests: number;
  availablePartners: number;
  criticalAlerts: number;
  avgResponseTime: number;
}

interface RecentActivity {
  id: string;
  type: 'REQUEST' | 'ALERT' | 'ASSIGNMENT';
  message: string;
  timestamp: string;
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [activities, setActivities] = useState<RecentActivity[]>([]);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchData();
    const ws = WebSocketService.getInstance();
    ws.subscribe('/topic/mobile-updates', (message) => {
      const update = JSON.parse(message.body);
      if (update.type === 'STATS') setStats(update.data);
    });
    return () => ws.unsubscribe('/topic/mobile-updates');
  }, []);

  const fetchData = async () => {
    try {
      const [statsRes, activityRes] = await Promise.all([
        API.get('/api/monitoring/mobile/stats'),
        API.get('/api/monitoring/mobile/activity'),
      ]);
      setStats(statsRes.data);
      setActivities(activityRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchData();
    setRefreshing(false);
  };

  return (
    <ScrollView
      style={styles.container}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
    >
      <View style={styles.header}>
        <Text style={styles.title}>RapidAssist</Text>
        <Text style={styles.subtitle}>Mobile Dashboard</Text>
      </View>

      {stats && (
        <View style={styles.statsGrid}>
          <View style={[styles.statCard, { borderLeftColor: '#3b82f6' }]}>
            <Text style={styles.statValue}>{stats.activeRequests}</Text>
            <Text style={styles.statLabel}>Active Requests</Text>
          </View>
          <View style={[styles.statCard, { borderLeftColor: '#10b981' }]}>
            <Text style={styles.statValue}>{stats.availablePartners}</Text>
            <Text style={styles.statLabel}>Available Partners</Text>
          </View>
          <View style={[styles.statCard, { borderLeftColor: '#ef4444' }]}>
            <Text style={styles.statValue}>{stats.criticalAlerts}</Text>
            <Text style={styles.statLabel}>Critical Alerts</Text>
          </View>
          <View style={[styles.statCard, { borderLeftColor: '#f59e0b' }]}>
            <Text style={styles.statValue}>{stats.avgResponseTime.toFixed(1)}m</Text>
            <Text style={styles.statLabel}>Avg Response</Text>
          </View>
        </View>
      )}

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Recent Activity</Text>
        {activities.map((activity) => (
          <View key={activity.id} style={styles.activityItem}>
            <View style={styles.activityDot} />
            <View style={styles.activityContent}>
              <Text style={styles.activityMessage}>{activity.message}</Text>
              <Text style={styles.activityTime}>
                {new Date(activity.timestamp).toLocaleTimeString()}
              </Text>
            </View>
          </View>
        ))}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#1e40af', padding: 20, paddingTop: 40 },
  title: { fontSize: 28, fontWeight: 'bold', color: 'white' },
  subtitle: { fontSize: 14, color: '#93c5fd' },
  statsGrid: { flexDirection: 'row', flexWrap: 'wrap', padding: 10, gap: 10 },
  statCard: { flex: 1, minWidth: '45%', backgroundColor: 'white', padding: 15, borderRadius: 8, borderLeftWidth: 4, elevation: 2, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.2, shadowRadius: 1.41 },
  statValue: { fontSize: 32, fontWeight: 'bold', color: '#1f2937' },
  statLabel: { fontSize: 12, color: '#6b7280', marginTop: 4 },
  section: { backgroundColor: 'white', margin: 10, padding: 15, borderRadius: 8, elevation: 2 },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', marginBottom: 15, color: '#1f2937' },
  activityItem: { flexDirection: 'row', alignItems: 'center', marginBottom: 12 },
  activityDot: { width: 10, height: 10, borderRadius: 5, backgroundColor: '#3b82f6', marginRight: 12 },
  activityContent: { flex: 1 },
  activityMessage: { fontSize: 14, color: '#1f2937' },
  activityTime: { fontSize: 12, color: '#6b7280', marginTop: 2 },
});

export default Dashboard;
