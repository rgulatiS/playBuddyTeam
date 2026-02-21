import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Alert } from 'react-native';
import axios from 'axios';

const CourtBookingScreen = ({ route, navigation }) => {
  const { court } = route.params;
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [pricing, setPricing] = useState(null);

  const fetchPricing = async () => {
    try {
      const response = await axios.get(`https://api.example.com/courts/${court.id}/pricing`);
      setPricing(response.data);
    } catch (error) {
      Alert.alert('Error', 'Failed to fetch pricing');
    }
  };

  const confirmBooking = async () => {
    try {
      const response = await axios.post('https://api.example.com/bookings/create', {
        courtId: court.id,
        slot: selectedSlot,
        amountCents: pricing?.amountCents,
      });
      if (response.data.status === 'CONFIRMED') {
        Alert.alert('Success', 'Booking confirmed');
        navigation.navigate('BookingHistory');
      } else {
        Alert.alert('Error', 'Booking failed');
      }
    } catch (error) {
      Alert.alert('Error', 'Failed to confirm booking');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Book Court: {court.name}</Text>
      <Button title="Fetch Pricing" onPress={fetchPricing} />
      {pricing && (
        <View>
          <Text>Pricing: ${pricing.amountCents / 100}</Text>
          <Button title="Select Slot" onPress={() => setSelectedSlot('10:00 AM - 11:00 AM')} />
          <Button title="Confirm Booking" onPress={confirmBooking} disabled={!selectedSlot} />
        </View>
      )}
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
});

export default CourtBookingScreen;
