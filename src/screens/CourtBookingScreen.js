import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Alert, TouchableOpacity, ActivityIndicator, ScrollView, Dimensions } from 'react-native';
import axios from 'axios';

const { height: screenHeight } = Dimensions.get('window');

const CourtBookingScreen = ({ route, navigation }) => {
  const { court, selectedSlot, bookingDate } = route.params;
  const [isLoading, setIsLoading] = useState(false);
  const [isBooked, setIsBooked] = useState(false);
  const [bookingDetails, setBookingDetails] = useState(null);
  const [userName, setUserName] = useState('');

  useEffect(() => {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        const name = window.localStorage.getItem('user_name');
        setUserName(name || 'User');
      }
    } catch (e) {
      console.warn('LocalStorage not available');
    }
  }, []);

  const confirmBooking = async () => {
    setIsLoading(true);
    try {
      let token = null;
      let userId = null;
      let storedUserName = 'Customer';

      if (typeof window !== 'undefined' && window.localStorage) {
        token = window.localStorage.getItem('jwt_token');
        userId = window.localStorage.getItem('user_id');
        storedUserName = window.localStorage.getItem('user_name') || 'Customer';
      }

      if (!token || !userId) {
        const msg = 'You must be logged in to book a court. Please log in again.';
        if (typeof window !== 'undefined') {
          window.alert(msg);
        } else {
          Alert.alert('Session Error', msg);
        }
        navigation.navigate('Login');
        return;
      }

      // Calculate endTime (1 hour after start)
      let startParts = selectedSlot.split(':');
      let startHour = parseInt(startParts[0]);
      let endHour = startHour + 1;
      if (endHour >= 24) endHour = 0;
      const endTime = `${endHour.toString().padStart(2, '0')}:${startParts[1]}`;

      const bookingPayload = {
        courtId: court.id,
        userId: parseInt(userId),
        bookedBy: storedUserName,
        bookedById: parseInt(userId),
        date: bookingDate,
        startTime: `${selectedSlot}:00`,
        endTime: `${endTime}:00`,
        amountCents: Math.round((court.pricePerHour || 0) * 100)
      };

      console.log('--- BOOKING ATTEMPT ---', bookingPayload);

      const response = await axios.post('http://localhost:8080/api/bookings/create', bookingPayload, {
        headers: { Authorization: `Bearer ${token}` }
      });

      console.log('Booking Response Success:', response.data);

      if (response.data && response.data.bookingId) {
        setBookingDetails(response.data);
        setIsBooked(true);
        // Using window.alert for better Web compatibility
        if (typeof window !== 'undefined') {
          window.alert('Success! Your booking is confirmed.');
        } else {
          Alert.alert('Success', 'Your booking is confirmed!');
        }
      } else {
        const msg = response.data?.message || 'Booking failed without a specific error message.';
        if (typeof window !== 'undefined') window.alert('Booking Failed: ' + msg);
        else Alert.alert('Booking Failed', msg);
      }
    } catch (error) {
      console.error('--- BOOKING ERROR ---', error);
      const errorData = error.response?.data;
      let errorMessage = 'Failed to confirm booking. ';
      if (errorData) {
        if (typeof errorData === 'string') errorMessage += errorData;
        else if (errorData.message) errorMessage += errorData.message;
        else errorMessage += JSON.stringify(errorData);
      } else {
        errorMessage += error.message;
      }

      if (typeof window !== 'undefined') window.alert('Error: ' + errorMessage);
      else Alert.alert('Booking Error', errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      {/* Header with Logo/Home functionality */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.navigate('SearchCourts')} style={styles.logoButton}>
          <Text style={styles.logoText}>PlayBuddy</Text>
        </TouchableOpacity>
      </View>

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.card}>
          {isBooked ? (
            <View style={styles.successContainer}>
              <View style={styles.successIconBadge}>
                <Text style={{ fontSize: 40 }}>✅</Text>
              </View>
              <Text style={styles.successTitle}>Booking Confirmed!</Text>
              <Text style={styles.successSubtitle}>Session ID: #{bookingDetails?.bookingId}</Text>

              <View style={styles.detailDivider} />

              <View style={styles.detailRow}>
                <Text style={styles.detailLabel}>Venue</Text>
                <Text style={styles.detailValue}>{court.venue?.name || 'Local Venue'}</Text>
              </View>
              <View style={styles.detailRow}>
                <Text style={styles.detailLabel}>Court</Text>
                <Text style={styles.detailValue}>{court.name}</Text>
              </View>
              <View style={styles.detailRow}>
                <Text style={styles.detailLabel}>Status</Text>
                <Text style={[styles.detailValue, { color: '#4CAF50' }]}>{bookingDetails?.status || 'CONFIRMED'}</Text>
              </View>

              <TouchableOpacity
                style={[styles.confirmButton, { marginTop: 32 }]}
                onPress={() => navigation.navigate('SearchCourts')}
              >
                <Text style={styles.confirmButtonText}>Explore More Courts</Text>
              </TouchableOpacity>
            </View>
          ) : (
            <View style={styles.bookingFormContainer}>
              <Text style={styles.label}>Review Your Booking</Text>
              <Text style={styles.venueName}>{court.venue?.name || 'Venue'}</Text>
              <Text style={styles.courtName}>{court.name}</Text>

              <View style={styles.divider} />

              <View style={styles.detailRow}>
                <Text style={styles.detailLabel}>Date</Text>
                <Text style={styles.detailValue}>{bookingDate}</Text>
              </View>

              <View style={styles.detailRow}>
                <Text style={styles.detailLabel}>Time</Text>
                <Text style={styles.detailValue}>{selectedSlot.substring(0, 5)} - 1 hour session</Text>
              </View>

              <View style={styles.detailRow}>
                <Text style={styles.detailLabel}>Customer</Text>
                <Text style={styles.detailValue}>{userName}</Text>
              </View>

              <View style={styles.divider} />

              <View style={styles.priceRow}>
                <Text style={styles.totalLabel}>Total Amount</Text>
                <Text style={styles.totalValue}>₹{court.pricePerHour}</Text>
              </View>

              <TouchableOpacity
                style={styles.confirmButton}
                onPress={confirmBooking}
                disabled={isLoading}
              >
                {isLoading ? (
                  <ActivityIndicator color="#fff" />
                ) : (
                  <Text style={styles.confirmButtonText}>Confirm Booking</Text>
                )}
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.cancelButton}
                onPress={() => navigation.goBack()}
                disabled={isLoading}
              >
                <Text style={styles.cancelButtonText}>Change Selection</Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    height: '100%',
    minHeight: screenHeight,
    backgroundColor: '#F8F9FB',
  },
  header: {
    height: 60,
    backgroundColor: '#fff',
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    zIndex: 10,
  },
  logoButton: {
    paddingVertical: 8,
  },
  logoText: {
    fontSize: 22,
    fontWeight: '900',
    color: '#007AFF',
    letterSpacing: -0.5,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    paddingBottom: 40,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 20,
    margin: 20,
    padding: 24,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.1,
    shadowRadius: 20,
    elevation: 5,
    minHeight: 400, // Ensure card is visible even if content is loading
  },
  successContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 20,
    flex: 1,
  },
  bookingFormContainer: {
    flex: 1,
  },
  successIconBadge: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#E8F5E9',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
  },
  successTitle: {
    fontSize: 26,
    fontWeight: '800',
    color: '#1A1A1A',
    marginBottom: 8,
    textAlign: 'center',
  },
  successSubtitle: {
    fontSize: 16,
    color: '#666',
    marginBottom: 24,
    textAlign: 'center',
  },
  label: {
    fontSize: 14,
    fontWeight: '700',
    color: '#007AFF',
    textTransform: 'uppercase',
    letterSpacing: 1,
    marginBottom: 12,
  },
  venueName: {
    fontSize: 24,
    fontWeight: '800',
    color: '#1A1A1A',
  },
  courtName: {
    fontSize: 16,
    color: '#666',
    marginBottom: 24,
  },
  divider: {
    height: 1,
    backgroundColor: '#F0F0F0',
    marginVertical: 20,
    width: '100%',
  },
  detailDivider: {
    height: 1,
    backgroundColor: '#F0F0F0',
    marginVertical: 20,
    width: '100%',
  },
  detailRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
    width: '100%',
  },
  detailLabel: {
    fontSize: 15,
    color: '#888',
    fontWeight: '600',
  },
  detailValue: {
    fontSize: 15,
    color: '#333',
    fontWeight: '700',
    flex: 1,
    textAlign: 'right',
    marginLeft: 10,
  },
  priceRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 24,
    width: '100%',
  },
  totalLabel: {
    fontSize: 18,
    fontWeight: '700',
    color: '#1A1A1A',
  },
  totalValue: {
    fontSize: 28,
    fontWeight: '800',
    color: '#007AFF',
  },
  confirmButton: {
    backgroundColor: '#007AFF',
    height: 56,
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#007AFF',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 4,
    marginBottom: 12,
    width: '100%',
  },
  confirmButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: '700',
  },
  cancelButton: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
  },
  cancelButtonText: {
    color: '#999',
    fontSize: 16,
    fontWeight: '600',
  }
});

export default CourtBookingScreen;
