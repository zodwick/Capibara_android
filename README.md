# Digital Wellbeing App

A simple Android app that tracks and displays your daily screen time using Android's UsageStatsManager API.

## Features

- 📱 **Daily Screen Time Tracking**: View your total screen time for today
- 📊 **App Usage Breakdown**: See which apps you use most and for how long
- 🎨 **Modern Material Design UI**: Beautiful, intuitive interface using Jetpack Compose
- 🔒 **Privacy-Focused**: All data stays on your device - no data collection or sharing

## Screenshots

The app displays:
- Total daily screen time in a prominent card
- List of your most-used apps with usage duration
- Clean, modern Material Design 3 interface

## Setup Requirements

### Prerequisites
- Android Studio (latest version recommended)
- Java Development Kit (JDK) 11 or higher
- Android SDK with API level 24+ (Android 7.0+)

### Installation

1. **Clone or open the project** in Android Studio
2. **Sync the project** with Gradle files
3. **Build and run** the app on a device or emulator

### First-Time Setup

When you first open the app, you'll need to grant usage access permission:

1. The app will show a permission request screen
2. Tap "Grant Permission" to open Android Settings
3. Find "Digital Wellbeing" in the list of apps
4. Toggle on "Permit usage access"
5. Return to the app to see your screen time data

## Technical Details

### Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35

### Key Components
- `MainActivity.kt`: Main UI using Jetpack Compose
- `ScreenTimeManager.kt`: Handles UsageStatsManager API calls
- `PermissionHelper.kt`: Manages usage access permissions
- `ScreenTimeData.kt`: Data classes for app usage information

### Permissions
- `PACKAGE_USAGE_STATS`: Required to access app usage statistics

## How It Works

1. **Permission Check**: App checks if usage access permission is granted
2. **Data Retrieval**: Uses UsageStatsManager to get today's app usage data
3. **Data Processing**: Filters out system apps and calculates total screen time
4. **UI Display**: Shows formatted data in a beautiful, scrollable interface

## Privacy

This app:
- ✅ Only accesses data locally on your device
- ✅ Does not collect or transmit any personal data
- ✅ Does not require internet permissions
- ✅ Shows only today's usage data

## Building the App

```bash
# Windows
gradlew.bat build

# macOS/Linux
./gradlew build
```

## Running the App

1. Connect an Android device or start an emulator
2. Run the app from Android Studio or use:

```bash
# Windows
gradlew.bat installDebug

# macOS/Linux
./gradlew installDebug
```

## Troubleshooting

### "No usage data available"
- Make sure you've granted usage access permission
- Use the device for a while to generate usage data
- Some emulators may not have realistic usage data

### Permission not working
- Go to Settings > Apps > Special access > Usage access
- Find "Digital Wellbeing" and enable it
- Restart the app

## Future Enhancements

Potential features for future versions:
- Weekly and monthly usage trends
- App usage limits and notifications
- Usage goals and achievements
- Export usage data
- Dark/light theme toggle

## License

This project is open source and available under the MIT License. 