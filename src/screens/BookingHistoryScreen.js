import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, StyleSheet, Alert, Platform } from 'react-native';
import axios from 'axios';

const showAlert = (title, message) => {
  if (Platform.OS === 'web') {
    console.error(`${title}: ${message}`);
  } else {
    Alert.alert(title, message);
  }
};

const BookingHistoryScreen = () => {
  const [bookings, setBookings] = useState([]);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const response = await axios.get('https://api.example.com/bookings/history');
        setBookings(response.data);
      } catch (error) {
        if (error.response) {
          // Server responded with a status other than 2xx
          showAlert('Error', `Server Error: ${error.response.status} - ${error.response.data}`);
        } else if (error.request) {
          // Request was made but no response received
          showAlert('Error', 'No response from server. Check network or API server.');
        } else {
          // Something else caused the error
          showAlert('Error', `Unexpected Error: ${error.message}`);
        }
        console.error('Fetch Bookings Error:', error);
      }
    };

    fetchBookings();
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Booking History</Text>
      <FlatList
        data={bookings}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.bookingItem}>
            <Text>Court: {item.courtName}</Text>
            <Text>Date: {item.date}</Text>
            <Text>Time: {item.startTime} - {item.endTime}</Text>
            <Text>Status: {item.status}</Text>
          </View>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  bookingItem: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
});

export default BookingHistoryScreen;
