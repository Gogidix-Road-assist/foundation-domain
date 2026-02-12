import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { API } from '../services/api';

interface Partner {
  id: string;
  name: string;
  type: 'TOWING' | 'MECHANIC' | 'INDEPENDENT';
  status: 'AVAILABLE' | 'BUSY' | 'OFFLINE';
  rating: number;
  completedJobs: number;
  location: string;
  phone: string;
}

const Partners: React.FC = () => {
  const [partners, setPartners] = useState<Partner[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'TOWING' | 'MECHANIC' | 'INDEPENDENT'>('ALL');

  useEffect(() => {
    fetchPartners();
  }, [filter]);

  const fetchPartners = async () => {
    try {
      const response = await API.get(`/api/monitoring/mobile/partners?type=${filter}`);
      setPartners(response.data);
    } catch (error) {
      console.error('Error fetching partners:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return '#10b981';
      case 'BUSY': return '#f59e0b';
      default: return '#6b7280';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'TOWING': return '🚗';
      case 'MECHANIC': return '🔧';
      default: return '👤';
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Partners</Text>
        <Text style={styles.subtitle}>Mobile Partner View</Text>
      </View>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.filters}>
        {(['ALL', 'TOWING', 'MECHANIC', 'INDEPENDENT'] as const).map((f) => (
          <TouchableOpacity
            key={f}
            style={[styles.filterButton, filter === f && styles.activeFilter]}
            onPress={() => setFilter(f)}
          >
            <Text style={[styles.filterText, filter === f && styles.activeFilterText]}>{f}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      {partners.map((partner) => (
        <View key={partner.id} style={styles.partnerCard}>
          <View style={styles.partnerHeader}>
            <Text style={styles.partnerIcon}>{getTypeIcon(partner.type)}</Text>
            <View style={styles.partnerInfo}>
              <Text style={styles.partnerName}>{partner.name}</Text>
              <Text style={styles.partnerType}>{partner.type}</Text>
            </View>
            <View style={[styles.statusDot, { backgroundColor: getStatusColor(partner.status) }]} />
          </View>
          <View style={styles.partnerStats}>
            <View style={styles.stat}>
              <Text style={styles.statValue}>{partner.rating.toFixed(1)} ★</Text>
              <Text style={styles.statLabel}>Rating</Text>
            </View>
            <View style={styles.stat}>
              <Text style={styles.statValue}>{partner.completedJobs}</Text>
              <Text style={styles.statLabel}>Jobs</Text>
            </View>
          </View>
          <Text style={styles.location}>{partner.location}</Text>
          <Text style={styles.phone}>{partner.phone}</Text>
          <TouchableOpacity style={styles.callButton}>
            <Text style={styles.callButtonText}>Call Partner</Text>
          </TouchableOpacity>
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
  filters: { padding: 10 },
  filterButton: { backgroundColor: 'white', paddingHorizontal: 16, paddingVertical: 8, borderRadius: 20, marginRight: 8 },
  activeFilter: { backgroundColor: '#1e40af' },
  filterText: { color: '#1f2937', fontWeight: '600' },
  activeFilterText: { color: 'white' },
  partnerCard: { backgroundColor: 'white', margin: 10, padding: 15, borderRadius: 8, elevation: 2 },
  partnerHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 12 },
  partnerIcon: { fontSize: 32, marginRight: 12 },
  partnerInfo: { flex: 1 },
  partnerName: { fontSize: 16, fontWeight: 'bold', color: '#1f2937' },
  partnerType: { fontSize: 12, color: '#6b7280' },
  statusDot: { width: 12, height: 12, borderRadius: 6 },
  partnerStats: { flexDirection: 'row', marginBottom: 12 },
  stat: { flex: 1 },
  statValue: { fontSize: 18, fontWeight: 'bold', color: '#1f2937' },
  statLabel: { fontSize: 11, color: '#6b7280' },
  location: { fontSize: 12, color: '#6b7280', marginBottom: 4 },
  phone: { fontSize: 12, color: '#3b82f6', marginBottom: 12 },
  callButton: { backgroundColor: '#10b981', padding: 12, borderRadius: 6, alignItems: 'center' },
  callButtonText: { color: 'white', fontWeight: '600' },
});

export default Partners;
