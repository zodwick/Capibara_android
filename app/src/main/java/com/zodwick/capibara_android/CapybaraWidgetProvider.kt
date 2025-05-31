package com.zodwick.capibara_android

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.content.Intent
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import java.util.concurrent.TimeUnit
import kotlin.math.max

class CapybaraWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, getWidgetLayout(context, appWidgetId))
        
        // Get screen time data
        val screenTimeManager = ScreenTimeManager(context)
        val screenTimeData = screenTimeManager.getDailyScreenTime()
        val settingsManager = SettingsManager(context)
        val userSettings = settingsManager.loadSettings()
        
        // Calculate remaining time
        val totalMinutes = (screenTimeData?.totalScreenTime ?: 0) / (1000 * 60)
        val targetMinutes = (userSettings.dailyTargetHours * 60).toInt()
        val remainingMinutes = max(0, targetMinutes - totalMinutes)
        
        // Set the time text
        val timeText = when {
            remainingMinutes <= 0 -> "Time's up!"
            remainingMinutes < 60 -> "${remainingMinutes}m left"
            else -> {
                val hours = remainingMinutes / 60
                val minutes = remainingMinutes % 60
                "${hours}h ${minutes}m left"
            }
        }
        views.setTextViewText(R.id.widget_time_text, timeText)
        
        // Set the capybara image based on screen time
        val healthPercentage = if (totalMinutes > 0) {
            (targetMinutes - totalMinutes).toFloat() / targetMinutes
        } else {
            1f
        }
        
        val imageResId = when {
            healthPercentage >= 0.8f -> R.drawable.capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg
            healthPercentage >= 0.5f -> R.drawable.capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg
            healthPercentage >= 0.2f -> R.drawable.capybara_sitting_with_drooped_ears_tired_expression_holding_a_small_wilted_flower_white_bg
            else -> R.drawable.capybara_hungry_and_in_despair_white_bg
        }
        views.setImageViewResource(R.id.widget_capybara_image, imageResId)
        
        // Set up click intent to open the app
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        views.setOnClickPendingIntent(R.id.widget_capybara_image, pendingIntent)
        
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getWidgetLayout(context: Context, appWidgetId: Int): Int {
        // Only 2x2 widget is supported
        return R.layout.widget_capybara_2x2
    }

    override fun onEnabled(context: Context) {
        // Start periodic updates
        val updateIntent = Intent(context, CapybaraWidgetProvider::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            updateIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        // Update every 15 minutes
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.setRepeating(
            android.app.AlarmManager.RTC,
            System.currentTimeMillis(),
            TimeUnit.MINUTES.toMillis(15),
            pendingIntent
        )
    }

    override fun onDisabled(context: Context) {
        // Stop periodic updates
        val updateIntent = Intent(context, CapybaraWidgetProvider::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            updateIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.cancel(pendingIntent)
    }
} 