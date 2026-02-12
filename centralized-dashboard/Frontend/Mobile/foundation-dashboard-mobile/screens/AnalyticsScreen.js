import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
} from 'react-native';
import { LineChart } from 'react-native-chart-kit';

export default function AnalyticsScreen() {
  const data = {
    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    datasets: [
      {
        data: [820, 910, 750, 880, 920, 850, 890],
        color: (opacity = 1) => `rgba(26, 84, 144, ${opacity})`,
      },
    ],
  };

  const chartConfig = {
    backgroundColor: '#ffffff',
    backgroundGradientFrom: '#ffffff',
    backgroundGradientTo: '#ffffff',
    decimalPlaces: 0,
    color: (opacity = 1) => `rgba(26, 84, 144, ${opacity})`,
    labelColor: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
    style: {
      borderRadius: 16,
    },
    propsForDots: {
      r: '4',
      strokeWidth: '2',
      stroke: '#1a5490',
    },
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Analytics</Text>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Request Trends</Text>
        <LineChart
          data={data}
          width={undefined}
          height={220}
          chartConfig={chartConfig}
          bezier
          style={styles.chart}
        />
      </View>

      <View style={styles.metricsRow}>
        <View style={styles.metricBox}>
          <Text style={styles.metricLabel}>p50</Text>
          <Text style={styles.metricValue}>45ms</Text>
        </View>
        <View style={styles.metricBox}>
          <Text style={styles.metricLabel}>p95</Text>
          <Text style={styles.metricValue}>120ms</Text>
        </View>
        <View style={styles.metricBox}>
          <Text style={styles.metricLabel}>p99</Text>
          <Text style={styles.metricValue}>250ms</Text>
        </View>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Error Rate</Text>
        <View style={styles.errorRateContainer}>
          <Text style={styles.errorRateValue}>0.02%</Text>
          <Text style={styles.errorRateLabel}>Current Error Rate</Text>
        </View>
      </View>
    </ScrollView>
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
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    margin: 20,
    marginTop: 0,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 16,
  },
  chart: {
    borderRadius: 16,
  },
  metricsRow: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    gap: 12,
  },
  metricBox: {
    flex: 1,
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  metricLabel: {
    fontSize: 14,
    color: '#6c757d',
    marginBottom: 8,
  },
  metricValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#1a5490',
  },
  errorRateContainer: {
    alignItems: 'center',
    paddingVertical: 20,
  },
  errorRateValue: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#28a745',
  },
  errorRateLabel: {
    fontSize: 14,
    color: '#6c757d',
    marginTop: 8,
  },
});
