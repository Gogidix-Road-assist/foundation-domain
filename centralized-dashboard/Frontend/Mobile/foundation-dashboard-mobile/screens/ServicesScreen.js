import React, { useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
} from 'react-native';
import { useDashboardStore } from '../store/dashboardStore';

export default function ServicesScreen() {
  const { services, initializeSampleData } = useDashboardStore();

  useEffect(() => {
    initializeSampleData();
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'HEALTHY': return '#28a745';
      case 'WARNING': return '#ffc107';
      case 'CRITICAL': return '#dc3545';
      default: return '#6c757d';
    }
  };

  const renderService = ({ item }) => (
    <View style={styles.serviceCard}>
      <View style={styles.serviceHeader}>
        <Text style={styles.serviceName}>{item.name}</Text>
        <View style={[styles.statusBadge, { backgroundColor: getStatusColor(item.status) }]}>
          <Text style={styles.statusText}>{item.status}</Text>
        </View>
      </View>
      <View style={styles.serviceMetrics}>
        <Text style={styles.metric}>Uptime: {item.uptime}</Text>
        <Text style={styles.metric}>Requests: {item.requests.toLocaleString()}</Text>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Service Health</Text>
      <FlatList
        data={services}
        renderItem={renderService}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    padding: 20,
    backgroundColor: '#fff',
  },
  list: {
    padding: 20,
  },
  serviceCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  serviceHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  serviceName: {
    fontSize: 16,
    fontWeight: '600',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statusText: {
    color: '#fff',
    fontSize: 11,
    fontWeight: '600',
  },
  serviceMetrics: {
    gap: 4,
  },
  metric: {
    fontSize: 13,
    color: '#6c757d',
  },
});
