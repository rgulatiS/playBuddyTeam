import { AppRegistry } from 'react-native';
import App from './App';
import appConfig from './app.json'; // Import as default export

AppRegistry.registerComponent(appConfig.name, () => App);
