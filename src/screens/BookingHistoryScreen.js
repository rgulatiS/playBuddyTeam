import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, StyleSheet, Alert, TouchableOpacity, ActivityIndicator, ScrollView } from 'react-native';
import axios from 'axios';

const BookingHistoryScreen = ({ navigation }) => {
  const [bookings, setBookings] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    setIsLoading(true);
    try {
      const token = localStorage.getItem('jwt_token');
      const userId = localStorage.getItem('user_id');
      const response = await axios.get(`http://localhost:8080/api/bookings`, {
        params: { userId: userId },
        headers: { Authorization: `Bearer ${token}` }
      });
      setBookings(response.data);
    } catch (error) {
      console.error('Fetch Bookings Error:', error.response?.data || error.message);
      Alert.alert('Error', 'Failed to load your bookings. Please check your connection.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancelBooking = async (bookingId) => {
    Alert.alert(
      'Cancel Booking',
      'Are you sure you want to cancel this booking? This action cannot be undone.',
      [
        { text: 'No', style: 'cancel' },
        {
          text: 'Yes, Cancel',
          style: 'destructive',
          onPress: async () => {
            try {
              const token = localStorage.getItem('jwt_token');
              const userId = localStorage.getItem('user_id');
              await axios.delete(`http://localhost:8080/api/bookings/cancel`, {
                data: { bookingId: bookingId, userId: parseInt(userId) },
                headers: { Authorization: `Bearer ${token}` }
              });
              Alert.alert('Success', 'Booking cancelled successfully.');
              fetchBookings(); // Refresh list
            } catch (error) {
              console.error('Cancel Error:', error.response?.data || error.message);
              Alert.alert('Error', error.response?.data?.message || 'Failed to cancel booking.');
            }
          }
        }
      ]
    );
  };

  const getStatusStyle = (status) => {
    switch (status) {
      case 'CONFIRMED': return { bg: '#e8f5e9', text: '#2e7d32' };
      case 'PENDING': return { bg: '#fff3e0', text: '#e65100' };
      case 'CANCELLED': return { bg: '#ffebee', text: '#c62828' };
      default: return { bg: '#f5f5f5', text: '#666' };
    }
  };

  const renderBookingCard = ({ item }) => {
    const statusStyle = getStatusStyle(item.status);
    const canCancel = item.status !== 'CANCELLED';

    return (
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <View>
            <Text style={styles.venueName}>{item.court?.venue?.name || 'Sports Venue'}</Text>
            <Text style={styles.courtName}>{item.court?.name || 'Court'}</Text>
          </View>
          <View style={[styles.statusBadge, { backgroundColor: statusStyle.bg }]}>
            <Text style={[styles.statusText, { color: statusStyle.text }]}>{item.status}</Text>
          </View>
        </View>

        <View style={styles.divider} />

        <View style={styles.infoGrid}>
          <View style={styles.infoItem}>
            <Text style={styles.infoLabel}>DATE</Text>
            <Text style={styles.infoValue}>{item.date}</Text>
          </View>
          <View style={styles.infoItem}>
            <Text style={styles.infoLabel}>TIME</Text>
            <Text style={styles.infoValue}>{item.startTime.substring(0, 5)} - {item.endTime.substring(0, 5)}</Text>
          </View>
        </View>

        <View style={styles.infoRow}>
          <Text style={styles.addressText}>📍 {item.court?.venue?.address || 'Location details not available'}</Text>
        </View>

        {canCancel && (
          <TouchableOpacity
            style={styles.cancelButton}
            onPress={() => handleCancelBooking(item.id)}
          >
            <Text style={styles.cancelButtonText}>Cancel Booking</Text>
          </TouchableOpacity>
        )}
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.header}>My Bookings</Text>
        <Text style={styles.subHeader}>Manage your upcoming and past court sessions</Text>
      </View>

      {isLoading ? (
        <View style={styles.centerContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
        </View>
      ) : (
        <FlatList
          data={bookings}
          keyExtractor={(item) => item.id.toString()}
          renderItem={renderBookingCard}
          contentContainerStyle={styles.listContent}
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Text style={styles.emptyTitle}>No bookings found</Text>
              <Text style={styles.emptySubTitle}>Looks like you haven't booked any courts yet.</Text>
              <TouchableOpacity
                style={styles.searchButton}
                onPress={() => navigation.navigate('SearchCourts')}
              >
                <Text style={styles.searchButtonText}>Browse Courts</Text>
              </TouchableOpacity>
            </View>
          }
          refreshing={isLoading}
          onRefresh={fetchBookings}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8F9FB',
  },
  headerContainer: {
    padding: 24,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#F0F0F0',
  },
  header: {
    fontSize: 28,
    fontWeight: '800',
    color: '#1A1A1A',
    marginBottom: 4,
  },
  subHeader: {
    fontSize: 16,
    color: '#666',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  listContent: {
    padding: 20,
    paddingBottom: 40,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.05,
    shadowRadius: 10,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
  },
  venueName: {
    fontSize: 18,
    fontWeight: '700',
    color: '#1A1A1A',
  },
  courtName: {
    fontSize: 14,
    color: '#666',
    marginTop: 2,
  },
  statusBadge: {
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 8,
  },
  statusText: {
    fontSize: 12,
    fontWeight: '700',
  },
  divider: {
    height: 1,
    backgroundColor: '#F0F0F0',
    marginVertical: 16,
  },
  infoGrid: {
    flexDirection: 'row',
    marginBottom: 12,
  },
  infoItem: {
    flex: 1,
  },
  infoLabel: {
    fontSize: 10,
    fontWeight: '700',
    color: '#999',
    letterSpacing: 0.5,
    marginBottom: 4,
  },
  infoValue: {
    fontSize: 14,
    fontWeight: '700',
    color: '#333',
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  addressText: {
    fontSize: 13,
    color: '#888',
    flex: 1,
  },
  cancelButton: {
    borderWidth: 1,
    borderColor: '#FF3B30',
    height: 40,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
  },
  cancelButtonText: {
    color: '#FF3B30',
    fontSize: 14,
    fontWeight: '600',
  },
  emptyContainer: {
    alignItems: 'center',
    marginTop: 60,
    paddingHorizontal: 40,
  },
  emptyTitle: {
    fontSize: 20,
    fontWeight: '700',
    color: '#333',
    marginBottom: 8,
  },
  emptySubTitle: {
    fontSize: 15,
    color: '#666',
    textAlign: 'center',
    marginBottom: 24,
    lineHeight: 22,
  },
  searchButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 12,
  },
  searchButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '700',
  }
});

export default BookingHistoryScreen;
