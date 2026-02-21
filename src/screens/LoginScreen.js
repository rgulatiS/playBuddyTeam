import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert } from 'react-native';
import axios from 'axios';

const LoginScreen = ({ navigation }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const login = async () => {
    try {
      // Pointing to local backend
      const response = await axios.post('http://localhost:8080/api/auth/login/email', { username: email, password });
      if (response.data.accessToken) {
        // Use localStorage for web compatibility
        localStorage.setItem('jwt_token', response.data.accessToken);
        localStorage.setItem('user_id', response.data.userId);
        localStorage.setItem('user_name', response.data.userName);
        localStorage.setItem('user_email', response.data.userEmail);
        Alert.alert('Success', 'Login successful');
        navigation.navigate('SearchCourts');
      }
    } catch (error) {
      console.error(error);
      Alert.alert('Error', 'Invalid email or password');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.label}>Email</Text>
      <TextInput
        style={styles.input}
        value={email}
        onChangeText={setEmail}
        placeholder="Enter your email"
        keyboardType="email-address"
      />
      <Text style={styles.label}>Password</Text>
      <TextInput
        style={styles.input}
        value={password}
        onChangeText={setPassword}
        placeholder="Enter your password"
        secureTextEntry
      />
      <Button title="Login" onPress={login} />
      <View style={{ marginTop: 16 }}>
        <Button title="Don't have an account? Register" onPress={() => navigation.navigate('Register')} color="#666" />
      </View>
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

export default LoginScreen;
