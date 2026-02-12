import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { API } from '../services/api';

interface Alert {
  id: string;
  type: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  title: string;
  message: string;
  createdAt: string;
  acknowledged: boolean;
}

const Alerts: React.FC = () => {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'CRITICAL' | 'UNACKNOWLEDGED'>('ALL');

  useEffect(() => {
    fetchAlerts();
  }, [filter]);

  const fetchAlerts = async () => {
    try {
      const response = await API.get(`/api/alerting/mobile?filter=${filter}`);
      setAlerts(response.data);
    } catch (error) {
      console.error('Error fetching alerts:', error);
    }
  };

  const acknowledgeAlert = async (id: string) => {
    try {
      await API.patch(`/api/alerting/alerts/${id}/acknowledge`);
      setAlerts((prev) => prev.map((a) => (a.id === id ? { ...a, acknowledged: true } : a)));
    } catch (error) {
      console.error('Error acknowledging alert:', error);
    }
  };

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL': return '#dc2626';
      case 'WARNING': return '#f59e0b';
      default: return '#3b82f6';
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Alerts</Text>
        <Text style={styles.subtitle}>Mobile Alert Management</Text>
      </View>

      <View style={styles.filters}>
        {(['ALL', 'CRITICAL', 'UNACKNOWLEDGED'] as const).map((f) => (
          <TouchableOpacity
            key={f}
            style={[styles.filterButton, filter === f && styles.activeFilter]}
            onPress={() => setFilter(f)}
          >
            <Text style={[styles.filterText, filter === f && styles.activeFilterText]}>{f}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {alerts.map((alert) => (
        <View key={alert.id} style={[styles.alertCard, { borderLeftColor: getSeverityColor(alert.severity) }]}>
          <View style={styles.alertHeader}>
            <Text style={styles.alertTitle}>{alert.title}</Text>
            {!alert.acknowledged && <View style={styles.badge}><Text style={styles.badgeText}>NEW</Text></View>}
          </View>
          <Text style={styles.alertMessage}>{alert.message}</Text>
          <Text style={styles.alertTime}>{new Date(alert.createdAt).toLocaleString()}</Text>
          {!alert.acknowledged && (
            <TouchableOpacity style={styles.ackButton} onPress={() => acknowledgeAlert(alert.id)}>
              <Text style={styles.ackButtonText}>Acknowledge</Text>
            </TouchableOpacity>
          )}
        </View>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#1e40af', padding: 20, paddingTop: 40 },
  title: { fontSize: 28, fontWeight: 'bold', color: 'white' },
  subtitle: { fontSize: 14, color: '#93c5fd' },
  filters: { flexDirection: 'row', padding: 10, gap: 10 },
  filterButton: { flex: 1, backgroundColor: 'white', padding: 10, borderRadius: 8, alignItems: 'center' },
  activeFilter: { backgroundColor: '#1e40af' },
  filterText: { color: '#1f2937', fontWeight: '600' },
  activeFilterText: { color: 'white' },
  alertCard: { backgroundColor: 'white', margin: 10, marginHorizontal: 10, padding: 15, borderRadius: 8, borderLeftWidth: 4, elevation: 2 },
  alertHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 },
  alertTitle: { fontSize: 16, fontWeight: 'bold', color: '#1f2937', flex: 1 },
  badge: { backgroundColor: '#dc2626', paddingHorizontal: 8, paddingVertical: 2, borderRadius: 10 },
  badgeText: { color: 'white', fontSize: 10, fontWeight: 'bold' },
  alertMessage: { fontSize: 14, color: '#4b5563', marginBottom: 8 },
  alertTime: { fontSize: 12, color: '#6b7280', marginBottom: 12 },
  ackButton: { backgroundColor: '#1e40af', padding: 10, borderRadius: 6, alignItems: 'center' },
  ackButtonText: { color: 'white', fontWeight: '600' },
});

export default Alerts;
