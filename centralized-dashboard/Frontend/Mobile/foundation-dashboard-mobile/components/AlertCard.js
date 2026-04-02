import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function AlertCard({ alert }) {
  const getAlertStyle = (type) => {
    switch (type) {
      case 'WARNING':
        return {
          bg: '#fff3cd',
          border: '#ffc107',
          icon: '⚠️',
        };
      case 'ERROR':
      case 'CRITICAL':
        return {
          bg: '#f8d7da',
          border: '#dc3545',
          icon: '🔴',
        };
      case 'SUCCESS':
        return {
          bg: '#d4edda',
          border: '#28a745',
          icon: '✓',
        };
      default:
        return {
          bg: '#d1ecf1',
          border: '#17a2b8',
          icon: 'ℹ️',
        };
    }
  };

  const style = getAlertStyle(alert.type);

  return (
    <View style={[styles.card, { backgroundColor: style.bg, borderLeftColor: style.border }]}>
      <Text style={styles.icon}>{style.icon}</Text>
      <View style={styles.content}>
        <Text style={styles.message}>{alert.message}</Text>
        <Text style={styles.time}>{alert.time}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    borderRadius: 8,
    padding: 12,
    marginBottom: 8,
    borderLeftWidth: 4,
  },
  icon: {
    fontSize: 20,
    marginRight: 12,
  },
  content: {
    flex: 1,
  },
  message: {
    fontSize: 14,
    fontWeight: '500',
    marginBottom: 4,
  },
  time: {
    fontSize: 12,
    color: '#6c757d',
  },
});
