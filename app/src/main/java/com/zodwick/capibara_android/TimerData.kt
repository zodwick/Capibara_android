package com.zodwick.capibara_android

import java.util.*

data class AppTimer(
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int,
    val isEnabled: Boolean = true,
    val warningAtMinutes: Int = dailyLimitMinutes - 5,
    val currentUsageToday: Long = 0L,
    val isLimitReached: Boolean = false
)

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val durationMinutes: Int,
    val breakDurationMinutes: Int = 5,
    val currentPhase: FocusPhase = FocusPhase.FOCUS,
    val remainingTimeMs: Long = durationMinutes * 60 * 1000L,
    val isActive: Boolean = false,
    val sessionsCompleted: Int = 0
)

enum class FocusPhase {
    FOCUS, BREAK, COMPLETED
}

data class BreakReminder(
    val intervalMinutes: Int = 30,
    val isEnabled: Boolean = true,
    val lastReminderTime: Long = 0L,
    val reminderMessage: String = "Time for a break! Your capybaras need rest 🦫"
) 