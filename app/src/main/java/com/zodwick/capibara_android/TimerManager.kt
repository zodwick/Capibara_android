package com.zodwick.capibara_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import java.util.Timer
import java.util.TimerTask
import kotlin.math.max

class TimerManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_timers", Context.MODE_PRIVATE)
    private val notificationManager = NotificationManagerCompat.from(context)
    private val screenTimeManager = ScreenTimeManager(context)
    
    // State flows for reactive UI updates
    private val _appTimers = MutableStateFlow<List<AppTimer>>(emptyList())
    val appTimers: StateFlow<List<AppTimer>> = _appTimers.asStateFlow()
    
    private val _focusSession = MutableStateFlow<FocusSession?>(null)
    val focusSession: StateFlow<FocusSession?> = _focusSession.asStateFlow()
    
    private val _breakReminder = MutableStateFlow<BreakReminder>(BreakReminder())
    val breakReminder: StateFlow<BreakReminder> = _breakReminder.asStateFlow()
    
    // Timer instances
    private var focusCountdownTimer: CountDownTimer? = null
    private var breakReminderTimer: Timer? = null
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    companion object {
        private const val CHANNEL_ID_APP_LIMITS = "app_limits"
        private const val CHANNEL_ID_FOCUS = "focus_sessions"
        private const val CHANNEL_ID_BREAKS = "break_reminders"
        private const val NOTIFICATION_ID_APP_LIMIT = 1001
        private const val NOTIFICATION_ID_FOCUS = 1002
        private const val NOTIFICATION_ID_BREAK = 1003
    }
    
    init {
        createNotificationChannels()
        loadAppTimers()
        loadBreakReminder()
        startBreakReminderTimer()
        startUsageMonitoring()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_APP_LIMITS,
                    "App Usage Limits",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications when app usage limits are reached"
                },
                NotificationChannel(
                    CHANNEL_ID_FOCUS,
                    "Focus Sessions",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Focus session and break notifications"
                },
                NotificationChannel(
                    CHANNEL_ID_BREAKS,
                    "Break Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Regular break reminder notifications"
                }
            )
            
            val manager = context.getSystemService(NotificationManager::class.java)
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }
    
    // App Usage Limits
    fun setAppTimer(packageName: String, appName: String, dailyLimitMinutes: Int) {
        val existingTimers = _appTimers.value.toMutableList()
        val existingIndex = existingTimers.indexOfFirst { it.packageName == packageName }
        
        val newTimer = AppTimer(
            packageName = packageName,
            appName = appName,
            dailyLimitMinutes = dailyLimitMinutes,
            warningAtMinutes = max(1, dailyLimitMinutes - 5)
        )
        
        if (existingIndex >= 0) {
            existingTimers[existingIndex] = newTimer
        } else {
            existingTimers.add(newTimer)
        }
        
        _appTimers.value = existingTimers
        saveAppTimers()
    }
    
    fun removeAppTimer(packageName: String) {
        val updatedTimers = _appTimers.value.filter { it.packageName != packageName }
        _appTimers.value = updatedTimers
        saveAppTimers()
    }
    
    fun toggleAppTimer(packageName: String, enabled: Boolean) {
        val updatedTimers = _appTimers.value.map { timer ->
            if (timer.packageName == packageName) {
                timer.copy(isEnabled = enabled)
            } else timer
        }
        _appTimers.value = updatedTimers
        saveAppTimers()
    }
    
    // Focus Sessions (Pomodoro-style)
    fun startFocusSession(durationMinutes: Int, breakDurationMinutes: Int = 5) {
        stopFocusSession() // Stop any existing session
        
        val session = FocusSession(
            durationMinutes = durationMinutes,
            breakDurationMinutes = breakDurationMinutes,
            isActive = true
        )
        _focusSession.value = session
        
        startFocusCountdown(session)
        showFocusNotification("Focus session started", "Stay focused for $durationMinutes minutes! 🧠")
    }
    
    private fun startFocusCountdown(session: FocusSession) {
        focusCountdownTimer?.cancel()
        
        focusCountdownTimer = object : CountDownTimer(session.remainingTimeMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _focusSession.value = session.copy(remainingTimeMs = millisUntilFinished)
            }
            
            override fun onFinish() {
                when (session.currentPhase) {
                    FocusPhase.FOCUS -> {
                        // Start break phase
                        val breakSession = session.copy(
                            currentPhase = FocusPhase.BREAK,
                            remainingTimeMs = session.breakDurationMinutes * 60 * 1000L,
                            sessionsCompleted = session.sessionsCompleted + 1
                        )
                        _focusSession.value = breakSession
                        startFocusCountdown(breakSession)
                        showFocusNotification(
                            "Break time! 🦫", 
                            "Your capybaras are proud! Take a ${session.breakDurationMinutes} minute break."
                        )
                    }
                    FocusPhase.BREAK -> {
                        // Session completed
                        _focusSession.value = session.copy(
                            currentPhase = FocusPhase.COMPLETED,
                            isActive = false,
                            remainingTimeMs = 0L
                        )
                        showFocusNotification(
                            "Session complete! 🎉", 
                            "Great job! You've completed ${session.sessionsCompleted + 1} focus sessions today."
                        )
                    }
                    FocusPhase.COMPLETED -> {
                        // Already completed
                    }
                }
            }
        }.start()
    }
    
    fun pauseFocusSession() {
        _focusSession.value?.let { session ->
            if (session.isActive) {
                focusCountdownTimer?.cancel()
                _focusSession.value = session.copy(isActive = false)
            }
        }
    }
    
    fun resumeFocusSession() {
        _focusSession.value?.let { session ->
            if (!session.isActive && session.remainingTimeMs > 0) {
                val resumedSession = session.copy(isActive = true)
                _focusSession.value = resumedSession
                startFocusCountdown(resumedSession)
            }
        }
    }
    
    fun stopFocusSession() {
        focusCountdownTimer?.cancel()
        _focusSession.value = null
        notificationManager.cancel(NOTIFICATION_ID_FOCUS)
    }
    
    // Break Reminders
    fun setBreakReminder(intervalMinutes: Int, enabled: Boolean) {
        val reminder = BreakReminder(
            intervalMinutes = intervalMinutes,
            isEnabled = enabled
        )
        _breakReminder.value = reminder
        saveBreakReminder()
        
        if (enabled) {
            startBreakReminderTimer()
        } else {
            stopBreakReminderTimer()
        }
    }
    
    private fun startBreakReminderTimer() {
        stopBreakReminderTimer()
        
        val reminder = _breakReminder.value
        if (!reminder.isEnabled) return
        
        breakReminderTimer = Timer().apply {
            scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        showBreakReminderNotification()
                    }
                },
                reminder.intervalMinutes * 60 * 1000L, // Initial delay
                reminder.intervalMinutes * 60 * 1000L  // Repeat interval
            )
        }
    }
    
    private fun stopBreakReminderTimer() {
        breakReminderTimer?.cancel()
        breakReminderTimer = null
    }
    
    // Usage Monitoring
    private fun startUsageMonitoring() {
        scope.launch {
            while (true) {
                updateAppUsage()
                delay(30000) // Check every 30 seconds
            }
        }
    }
    
    private suspend fun updateAppUsage() {
        try {
            val dailyScreenTime = screenTimeManager.getDailyScreenTime()
            val updatedTimers = _appTimers.value.map { timer ->
                val currentUsage = dailyScreenTime.appUsageList.find { 
                    it.packageName == timer.packageName 
                }?.timeInForeground ?: 0L
                
                val usageMinutes = (currentUsage / (1000 * 60)).toInt()
                val limitReached = usageMinutes >= timer.dailyLimitMinutes
                val shouldWarn = usageMinutes >= timer.warningAtMinutes && !timer.isLimitReached
                
                // Show warning notification
                if (shouldWarn && timer.isEnabled) {
                    showAppLimitWarning(timer, usageMinutes)
                }
                
                // Show limit reached notification
                if (limitReached && !timer.isLimitReached && timer.isEnabled) {
                    showAppLimitReached(timer, usageMinutes)
                }
                
                timer.copy(
                    currentUsageToday = currentUsage,
                    isLimitReached = limitReached
                )
            }
            
            _appTimers.value = updatedTimers
        } catch (e: Exception) {
            // Handle monitoring errors gracefully
        }
    }
    
    // Notifications
    private fun showAppLimitWarning(timer: AppTimer, currentMinutes: Int) {
        val remainingMinutes = timer.dailyLimitMinutes - currentMinutes
        showNotification(
            CHANNEL_ID_APP_LIMITS,
            NOTIFICATION_ID_APP_LIMIT,
            "⚠️ ${timer.appName} Limit Warning",
            "You have $remainingMinutes minutes left today. Your capybaras are getting tired! 🦫"
        )
    }
    
    private fun showAppLimitReached(timer: AppTimer, currentMinutes: Int) {
        showNotification(
            CHANNEL_ID_APP_LIMITS,
            NOTIFICATION_ID_APP_LIMIT,
            "🚫 ${timer.appName} Limit Reached",
            "Daily limit of ${timer.dailyLimitMinutes} minutes reached. Time to rest! Your capybaras need a break 💤"
        )
    }
    
    private fun showFocusNotification(title: String, message: String) {
        showNotification(CHANNEL_ID_FOCUS, NOTIFICATION_ID_FOCUS, title, message)
    }
    
    private fun showBreakReminderNotification() {
        val reminder = _breakReminder.value
        showNotification(
            CHANNEL_ID_BREAKS,
            NOTIFICATION_ID_BREAK,
            "🦫 Break Time!",
            reminder.reminderMessage
        )
    }
    
    private fun showNotification(channelId: String, notificationId: Int, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to add this icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
    
    // Persistence
    private fun saveAppTimers() {
        val editor = prefs.edit()
        val timersJson = _appTimers.value.joinToString("|") { timer ->
            "${timer.packageName},${timer.appName},${timer.dailyLimitMinutes},${timer.isEnabled},${timer.warningAtMinutes}"
        }
        editor.putString("app_timers", timersJson)
        editor.apply()
    }
    
    private fun loadAppTimers() {
        val timersJson = prefs.getString("app_timers", "") ?: ""
        if (timersJson.isNotEmpty()) {
            val timers = timersJson.split("|").mapNotNull { timerString ->
                val parts = timerString.split(",")
                if (parts.size >= 5) {
                    AppTimer(
                        packageName = parts[0],
                        appName = parts[1],
                        dailyLimitMinutes = parts[2].toIntOrNull() ?: 60,
                        isEnabled = parts[3].toBooleanStrictOrNull() ?: true,
                        warningAtMinutes = parts[4].toIntOrNull() ?: 55
                    )
                } else null
            }
            _appTimers.value = timers
        }
    }
    
    private fun saveBreakReminder() {
        val reminder = _breakReminder.value
        prefs.edit().apply {
            putInt("break_interval", reminder.intervalMinutes)
            putBoolean("break_enabled", reminder.isEnabled)
            putString("break_message", reminder.reminderMessage)
            apply()
        }
    }
    
    private fun loadBreakReminder() {
        val reminder = BreakReminder(
            intervalMinutes = prefs.getInt("break_interval", 30),
            isEnabled = prefs.getBoolean("break_enabled", true),
            reminderMessage = prefs.getString("break_message", "Time for a break! Your capybaras need rest 🦫") ?: ""
        )
        _breakReminder.value = reminder
    }
    
    fun cleanup() {
        scope.cancel()
        focusCountdownTimer?.cancel()
        stopBreakReminderTimer()
    }
    
    // Utility functions
    fun formatTimeRemaining(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    fun getUsagePercentage(timer: AppTimer): Float {
        val usageMinutes = (timer.currentUsageToday / (1000 * 60)).toFloat()
        return (usageMinutes / timer.dailyLimitMinutes.toFloat()).coerceAtMost(1f)
    }
} 