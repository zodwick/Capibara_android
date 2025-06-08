# 🦫 Capybara Sanctuary - App Timers Guide

## Overview
Your Capybara Sanctuary app now includes comprehensive **App Timer** functionality to help you maintain digital wellness while keeping your capybaras happy! 

## Features

### 🎯 **App Usage Limits**
Set daily time limits for specific apps to prevent overuse:

- **Set Limits**: Choose any app and set a daily time limit (15-240 minutes)
- **Smart Warnings**: Get notified 5 minutes before reaching your limit
- **Limit Notifications**: Receive gentle reminders when limits are reached
- **Real-time Tracking**: See your current usage vs. your set limits
- **Easy Management**: Toggle timers on/off, edit limits, or remove timers

**How to use:**
1. Go to Settings → App Timers
2. Tap "+" to add a new timer
3. Select an app and set your daily limit
4. The app will monitor usage and send notifications

### 🧠 **Focus Sessions (Pomodoro)**
Productive work sessions with built-in breaks:

- **Customizable Duration**: 15-60 minute focus sessions
- **Smart Breaks**: 5-15 minute break periods
- **Visual Timer**: Beautiful circular progress indicator
- **Session Tracking**: Count completed focus sessions
- **Pause/Resume**: Full control over your sessions
- **Notifications**: Get notified when focus time ends and breaks begin

**How to use:**
1. Go to Settings → Focus Sessions
2. Set your focus duration (default: 25 minutes)
3. Set your break duration (default: 5 minutes)
4. Tap "Start Focus Session"
5. Stay focused! The app will guide you through focus and break periods

### ⏰ **Break Reminders**
Regular reminders to take healthy screen breaks:

- **Customizable Intervals**: 15-120 minute intervals
- **Gentle Notifications**: Friendly capybara-themed reminders
- **Easy Toggle**: Enable/disable from notification settings
- **Smart Timing**: Reminders respect your usage patterns

**How to use:**
1. Go to Settings → Notifications
2. Enable "Break Reminders"
3. Set your preferred interval (default: 30 minutes)
4. Receive regular break notifications

## Android APIs Used

### **UsageStatsManager**
- Tracks real-time app usage
- Monitors daily screen time per app
- Provides usage events for accurate timing

### **NotificationManager**
- Creates notification channels for different timer types
- Sends contextual notifications for limits and reminders
- Handles notification permissions

### **CountDownTimer**
- Powers focus session timers
- Provides accurate countdown functionality
- Updates UI in real-time

### **SharedPreferences**
- Saves timer settings persistently
- Stores user preferences
- Maintains timer states across app restarts

### **Coroutines & StateFlow**
- Real-time UI updates
- Background monitoring
- Reactive state management

## Permissions Required

The app uses these permissions for timer functionality:
- `PACKAGE_USAGE_STATS`: Monitor app usage (requires manual permission)
- `POST_NOTIFICATIONS`: Send timer notifications
- `WAKE_LOCK`: Keep timers running
- `USE_EXACT_ALARM`: Precise break reminders

## Integration with Capybara System

The app timers integrate seamlessly with your capybara sanctuary:

- **Capybara Health**: Exceeding limits affects capybara moods
- **Notification Themes**: All notifications use capybara-friendly language
- **Visual Design**: Timer UIs match the sanctuary's peaceful aesthetic
- **Settings Integration**: All timer controls accessible from main settings

## Tips for Success

1. **Start Small**: Begin with reasonable limits (60-90 minutes for social apps)
2. **Use Focus Sessions**: Combine with app limits for productive work
3. **Enable Break Reminders**: Regular breaks keep both you and capybaras healthy
4. **Monitor Progress**: Check your timer overview regularly
5. **Adjust as Needed**: Modify limits based on your digital wellness goals

## Technical Features

- **Real-time Monitoring**: Usage tracked every 30 seconds
- **Progressive Warnings**: 5-minute warnings before limits
- **State Persistence**: Settings saved across app restarts
- **Background Processing**: Timers work even when app is closed
- **Notification Channels**: Organized notifications by type
- **Error Handling**: Graceful fallbacks for edge cases

Keep your capybaras happy and maintain healthy digital habits! 🌸 