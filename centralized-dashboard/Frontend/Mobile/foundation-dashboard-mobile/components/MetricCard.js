import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function MetricCard({ title, value, change, positive }) {
  return (
    <View style={styles.card}>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.value}>{value}</Text>
      {change && (
        <Text style={[styles.change, positive ? styles.positive : styles.negative]}>
          {positive ? '↑' : '↓'} {change}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    width: '48%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  title: {
    fontSize: 12,
    color: '#6c757d',
    marginBottom: 8,
  },
  value: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  change: {
    fontSize: 12,
    fontWeight: '600',
  },
  positive: {
    color: '#28a745',
  },
  negative: {
    color: '#dc3545',
  },
});
