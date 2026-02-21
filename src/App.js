import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import LoginScreen from './screens/LoginScreen';
import RegisterScreen from './screens/RegisterScreen';
import SearchCourtsScreen from './screens/SearchCourtsScreen';
import CourtBookingScreen from './screens/CourtBookingScreen';
import BookingHistoryScreen from './screens/BookingHistoryScreen';

const Stack = createNativeStackNavigator();

const App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Register" component={RegisterScreen} />
        <Stack.Screen name="SearchCourts" component={SearchCourtsScreen} options={{ title: 'Search Courts' }} />
        <Stack.Screen name="CourtBooking" component={CourtBookingScreen} options={{ title: 'Book Court' }} />
        <Stack.Screen name="BookingHistory" component={BookingHistoryScreen} options={{ title: 'My Bookings' }} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default App;
