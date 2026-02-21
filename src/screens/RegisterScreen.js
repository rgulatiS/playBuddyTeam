import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert } from 'react-native';
import EncryptedStorage from 'react-native-encrypted-storage';
import axios from 'axios';

const RegisterScreen = ({ navigation }) => {
  const [email, setEmail] = useState('');
  const [mobile, setMobile] = useState('');
  const [otp, setOtp] = useState('');
  const [step, setStep] = useState(1); // 1: Enter email/mobile, 2: Enter OTP

  const requestOtp = async () => {
    try {
      const response = await axios.post('https://api.example.com/auth/request-otp', { email, mobile });
      if (response.data.success) {
        setStep(2);
        Alert.alert('OTP sent successfully');
      }
    } catch (error) {
      Alert.alert('Error', 'Failed to send OTP');
    }
  };

  const verifyOtp = async () => {
    try {
      const response = await axios.post('https://api.example.com/auth/verify-otp', { email, mobile, otp });
      if (response.data.token) {
        await EncryptedStorage.setItem('jwt_token', response.data.token);
        Alert.alert('Success', 'Registration successful');
        navigation.navigate('Login');
      }
    } catch (error) {
      Alert.alert('Error', 'Invalid OTP');
    }
  };

  return (
    <View style={styles.container}>
      {step === 1 ? (
        <>
          <Text style={styles.label}>Email</Text>
          <TextInput
            style={styles.input}
            value={email}
            onChangeText={setEmail}
            placeholder="Enter your email"
            keyboardType="email-address"
          />
          <Text style={styles.label}>Mobile</Text>
          <TextInput
            style={styles.input}
            value={mobile}
            onChangeText={setMobile}
            placeholder="Enter your mobile number"
            keyboardType="phone-pad"
          />
          <Button title="Request OTP" onPress={requestOtp} />
        </>
      ) : (
        <>
          <Text style={styles.label}>Enter OTP</Text>
          <TextInput
            style={styles.input}
            value={otp}
            onChangeText={setOtp}
            placeholder="Enter the OTP"
            keyboardType="number-pad"
          />
          <Button title="Verify OTP" onPress={verifyOtp} />
        </>
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
});

export default RegisterScreen;
