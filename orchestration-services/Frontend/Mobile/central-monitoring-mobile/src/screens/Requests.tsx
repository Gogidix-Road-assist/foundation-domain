import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';
import { API } from '../services/api';

interface Request {
  id: string;
  customerName: string;
  status: string;
  priority: string;
  vehicle: string;
  issue: string;
  location: string;
  createdAt: string;
}

const Requests: React.FC = () => {
  const [requests, setRequests] = useState<Request[]>([]);

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      const response = await API.get('/api/monitoring/mobile/requests');
      setRequests(response.data);
    } catch (error) {
      console.error('Error fetching requests:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return '#f59e0b';
      case 'IN_PROGRESS': return '#3b82f6';
      case 'COMPLETED': return '#10b981';
      default: return '#6b7280';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'CRITICAL': return '#dc2626';
      case 'HIGH': return '#f97316';
      case 'MEDIUM': return '#eab308';
      default: return '#22c55e';
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Requests</Text>
        <Text style={styles.subtitle}>Mobile Request Tracking</Text>
      </View>

      {requests.map((request) => (
        <View key={request.id} style={styles.requestCard}>
          <View style={styles.requestHeader}>
            <View style={[styles.statusBadge, { backgroundColor: getStatusColor(request.status) }]}>
              <Text style={styles.statusText}>{request.status}</Text>
            </View>
            <View style={[styles.priorityBadge, { backgroundColor: getPriorityColor(request.priority) }]}>
              <Text style={styles.priorityText}>{request.priority}</Text>
            </View>
          </View>
          <Text style={styles.customerName}>{request.customerName}</Text>
          <Text style={styles.detail}>{request.vehicle}</Text>
          <Text style={styles.issue}>{request.issue}</Text>
          <Text style={styles.location}>{request.location}</Text>
          <Text style={styles.timestamp}>{new Date(request.createdAt).toLocaleString()}</Text>
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
  requestCard: { backgroundColor: 'white', margin: 10, padding: 15, borderRadius: 8, elevation: 2 },
  requestHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  statusBadge: { paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 },
  statusText: { color: 'white', fontSize: 11, fontWeight: 'bold' },
  priorityBadge: { paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 },
  priorityText: { color: 'white', fontSize: 11, fontWeight: 'bold' },
  customerName: { fontSize: 16, fontWeight: 'bold', color: '#1f2937', marginBottom: 4 },
  detail: { fontSize: 14, color: '#4b5563', marginBottom: 2 },
  issue: { fontSize: 14, color: '#1f2937', marginBottom: 6 },
  location: { fontSize: 12, color: '#6b7280', marginBottom: 4 },
  timestamp: { fontSize: 11, color: '#9ca3af' },
});

export default Requests;
