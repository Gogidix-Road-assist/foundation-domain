import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function ServiceCard({ service }) {
  const getStatusColor = (status) => {
    switch (status) {
      case 'HEALTHY': return '#28a745';
      case 'WARNING': return '#ffc107';
      case 'CRITICAL': return '#dc3545';
      default: return '#6c757d';
    }
  };

  return (
    <View style={styles.card}>
      <View style={styles.header}>
        <Text style={styles.name}>{service.name}</Text>
        <View style={[styles.statusBadge, { backgroundColor: getStatusColor(service.status) }]}>
          <Text style={styles.statusText}>{service.status}</Text>
        </View>
      </View>
      <View style={styles.metrics}>
        <Text style={styles.metric}>Uptime: {service.uptime}</Text>
        <Text style={styles.metric}>Requests: {service.requests.toLocaleString()}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
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
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  name: {
    fontSize: 15,
    fontWeight: '600',
  },
  statusBadge: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 10,
  },
  statusText: {
    color: '#fff',
    fontSize: 10,
    fontWeight: '600',
  },
  metrics: {
    gap: 4,
  },
  metric: {
    fontSize: 12,
    color: '#6c757d',
  },
});
