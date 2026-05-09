import React, { useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  RefreshControl,
} from 'react-native';
import { useDashboardStore } from '../store/dashboardStore';
import { useAuthStore } from '../store/authStore';
import MetricCard from '../components/MetricCard';
import ServiceCard from '../components/ServiceCard';
import AlertCard from '../components/AlertCard';

export default function DashboardScreen() {
  const { metrics, services, alerts, isConnected, initializeSampleData } = useDashboardStore();
  const user = useAuthStore((state) => state.user);

  useEffect(() => {
    initializeSampleData();
  }, []);

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={false} onRefresh={initializeSampleData} />
      }
    >
      <View style={styles.header}>
        <Text style={styles.welcome}>Welcome back, {user?.name || 'Admin'}</Text>
        <View style={styles.statusRow}>
          <View style={[styles.statusDot, isConnected ? styles.online : styles.offline]} />
          <Text style={styles.statusText}>
            {isConnected ? 'Connected' : 'Disconnected'}
          </Text>
        </View>
      </View>

      <View style={styles.metricsGrid}>
        <MetricCard title="Total Requests" value={metrics.totalRequests?.toLocaleString()} change="+12.5%" positive />
        <MetricCard title="Success Rate" value={`${metrics.successRate}%`} change="+0.3%" positive />
        <MetricCard title="Avg Response" value={`${metrics.avgResponseTime}ms`} change="-5ms" positive />
        <MetricCard title="Active Services" value={metrics.activeServices} change="Stable" />
      </View>

      <Text style={styles.sectionTitle}>Service Health</Text>
      <View style={styles.section}>
        {services.slice(0, 3).map((service) => (
          <ServiceCard key={service.id} service={service} />
        ))}
      </View>

      <Text style={styles.sectionTitle}>Recent Alerts</Text>
      <View style={styles.section}>
        {alerts.slice(0, 3).map((alert) => (
          <AlertCard key={alert.id} alert={alert} />
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
  },
  welcome: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  statusRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: 8,
  },
  online: {
    backgroundColor: '#28a745',
  },
  offline: {
    backgroundColor: '#dc3545',
  },
  statusText: {
    fontSize: 14,
    color: '#6c757d',
  },
  metricsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: 10,
    gap: 10,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    padding: 20,
    paddingBottom: 10,
  },
  section: {
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
});
