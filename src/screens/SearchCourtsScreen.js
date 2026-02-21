import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, FlatList, Alert } from 'react-native';
import axios from 'axios';

const SearchCourtsScreen = ({ navigation }) => {
  const [sport, setSport] = useState('');
  const [date, setDate] = useState('');
  const [time, setTime] = useState('');
  const [courts, setCourts] = useState([]);

  const searchCourts = async () => {
    try {
      const response = await axios.get('https://api.example.com/courts/available', {
        params: { sport, date, time },
      });
      setCourts(response.data);
    } catch (error) {
      Alert.alert('Error', 'Failed to fetch courts');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.label}>Sport</Text>
      <TextInput
        style={styles.input}
        value={sport}
        onChangeText={setSport}
        placeholder="Enter sport"
      />
      <Text style={styles.label}>Date</Text>
      <TextInput
        style={styles.input}
        value={date}
        onChangeText={setDate}
        placeholder="YYYY-MM-DD"
      />
      <Text style={styles.label}>Time</Text>
      <TextInput
        style={styles.input}
        value={time}
        onChangeText={setTime}
        placeholder="HH:MM"
      />
      <Button title="Search" onPress={searchCourts} />

      <FlatList
        data={courts}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.courtItem}>
            <Text>{item.name}</Text>
            <Button title="Book" onPress={() => navigation.navigate('CourtBooking', { court: item })} />
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
  label: {
    fontSize: 16,
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 4,
    padding: 8,
    marginBottom: 16,
  },
  courtItem: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
});

export default SearchCourtsScreen;
