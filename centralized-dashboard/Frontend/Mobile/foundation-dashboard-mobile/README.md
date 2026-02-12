# Foundation Dashboard Mobile

React Native mobile app for Foundation Domain centralized monitoring dashboard.

## Features

- Real-time service health monitoring
- Metrics and analytics visualization
- Alert notifications
- Demo authentication mode
- Offline-capable sample data

## Tech Stack

- React Native 0.74
- Expo SDK 51
- React Navigation 6
- Zustand (state management)
- Axios (HTTP client)
- Chart Kit (visualizations)

## Getting Started

### Prerequisites

- Node.js 18+
- Expo CLI
- iOS Simulator (Mac) or Android Emulator

### Installation

```bash
npm install
```

### Development

```bash
# Start Expo development server
npm start

# Run on iOS
npm run ios

# Run on Android
npm run android

# Run in web browser
npm run web
```

### Demo Credentials

- Email: admin@foundation.local
- Password: admin123

## Project Structure

```
src/
├── App.js                 # Main app component
├── screens/               # Screen components
│   ├── LoginScreen.js
│   ├── DashboardScreen.js
│   ├── ServicesScreen.js
│   ├── AnalyticsScreen.js
│   └── SettingsScreen.js
├── components/            # Reusable components
│   ├── MetricCard.js
│   ├── ServiceCard.js
│   └── AlertCard.js
├── store/                 # State management
│   ├── authStore.js
│   └── dashboardStore.js
└── services/              # API services
    └── api.js
```

## API Configuration

Create `app.config.js`:

```javascript
export default {
  expo: {
    extra: {
      apiUrl: 'http://your-api-url:3000/api',
    },
  },
};
```

## Building for Production

### iOS

```bash
eas build --platform ios
```

### Android

```bash
eas build --platform android
```

## License

Proprietary - Gogidix Foundation Domain Platform
