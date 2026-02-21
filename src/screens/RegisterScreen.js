import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert } from 'react-native';
import axios from 'axios';

const RegisterScreen = ({ navigation }) => {
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [mobile, setMobile] = useState('');
  const [otp, setOtp] = useState('');
  const [debugOtp, setDebugOtp] = useState(''); // New state for debug display
  const [step, setStep] = useState(1); // 1: Enter details, 2: Enter OTP

  const requestOtp = async () => {
    console.log('Requesting OTP for:', { email, mobile, name });
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register/mobile', {
        email: email,
        mobileNumber: mobile,
        name: name
      });

      console.log('OTP Response:', response.data);

      if (response.data.sent) {
        setStep(2);
        const code = response.data.debugCode;
        setDebugOtp(code || ''); // Save code to state
        const msg = code ? `OTP sent! (Debug Code: ${code})` : 'OTP sent successfully';

        console.log('Final Success Message:', msg);
        if (typeof window !== 'undefined' && window.alert) window.alert(msg);
        else Alert.alert('Success', msg);
      } else {
        const msg = 'Server reported OTP not sent. Check mobile number or email.';
        console.warn('Fail Alert:', msg);
        if (typeof window !== 'undefined' && window.alert) window.alert(msg);
        else Alert.alert('Error', msg);
      }
    } catch (error) {
      console.error('Request OTP Error Details:', error);
      const errorMsg = error.response?.data?.message || error.message;
      const finalMsg = `Failed to send OTP: ${errorMsg}`;
      if (typeof window !== 'undefined' && window.alert) window.alert(finalMsg);
      else Alert.alert('Error', finalMsg);
    }
  };

  const verifyOtp = async () => {
    try {
      // Use local backend - matching VerifyOtpRequest DTO
      const response = await axios.post('http://localhost:8080/api/auth/verify-otp', {
        email: email,
        mobileNumber: mobile,
        name: name,
        password: password,
        code: otp
      });

      if (response.data.accessToken) {
        localStorage.setItem('jwt_token', response.data.accessToken);
        Alert.alert('Success', 'Registration successful');
        navigation.navigate('Login');
      }
    } catch (error) {
      console.error(error);
      const errorMsg = error.response?.data?.message || 'Invalid OTP';
      Alert.alert('Error', errorMsg);
    }
  };

  return (
    <View style={styles.container}>
      {step === 1 ? (
        <>
          <Text style={styles.label}>Full Name</Text>
          <TextInput
            style={styles.input}
            value={name}
            onChangeText={setName}
            placeholder="Enter your name"
          />
          <Text style={styles.label}>Email Address (For OTP)</Text>
          <TextInput
            style={styles.input}
            value={email}
            onChangeText={setEmail}
            placeholder="Enter your email"
            keyboardType="email-address"
          />
          <Text style={styles.label}>Mobile Number</Text>
          <TextInput
            style={styles.input}
            value={mobile}
            onChangeText={setMobile}
            placeholder="Enter your mobile number"
            keyboardType="phone-pad"
          />
          <Text style={styles.label}>Set Password</Text>
          <TextInput
            style={styles.input}
            value={password}
            onChangeText={setPassword}
            placeholder="Enter your password"
            secureTextEntry
          />
          <Button title="Request OTP" onPress={requestOtp} />
        </>
      ) : (
        <>
          <Text style={styles.label}>Enter OTP</Text>
          {debugOtp ? (
            <View style={styles.debugBox}>
              <Text style={styles.debugText}>DEBUG: Use OTP code {debugOtp}</Text>
            </View>
          ) : null}
          <TextInput
            style={styles.input}
            value={otp}
            onChangeText={setOtp}
            placeholder="Enter the OTP"
            keyboardType="number-pad"
          />
          <Button title="Verify OTP" onPress={verifyOtp} />
          <View style={{ marginTop: 16 }}>
            <Button title="Back" onPress={() => setStep(1)} color="#666" />
          </View>
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
  debugBox: {
    backgroundColor: '#e3f2fd',
    padding: 12,
    borderRadius: 8,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#2196f3',
  },
  debugText: {
    color: '#1565c0',
    fontWeight: 'bold',
    textAlign: 'center',
  },
});

export default RegisterScreen;
