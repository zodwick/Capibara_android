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
        try {
            val views = RemoteViews(context.packageName, getWidgetLayout(context, appWidgetId))
            
            // Get screen time data
            val screenTimeManager = ScreenTimeManager(context)
            val screenTimeData = try {
                screenTimeManager.getDailyScreenTime()
            } catch (e: Exception) {
                Log.e("CapybaraWidget", "Error getting screen time data", e)
                null
            }
            
            val settingsManager = SettingsManager(context)
            val userSettings = try {
                settingsManager.loadSettings()
            } catch (e: Exception) {
                Log.e("CapybaraWidget", "Error loading settings", e)
                UserSettings() // Use default settings
            }
            
            // Calculate remaining time
            val totalMinutes = (screenTimeData?.totalScreenTime ?: 0) / (1000 * 60)
            val targetMinutes = (userSettings.dailyTargetHours * 60).toInt()
            
            // Calculate health percentage for capybara mood
            val healthPercentage = if (totalMinutes > 0) {
                (targetMinutes - totalMinutes).toFloat() / targetMinutes
            } else {
                1f
            }
            
            // Set capybara image based on screen time - let the image speak for itself
            val imageResId = when {
                healthPercentage >= 0.8f -> 
                    R.drawable.capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg
                healthPercentage >= 0.5f -> 
                    R.drawable.capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg
                healthPercentage >= 0.2f -> 
                    R.drawable.capybara_sitting_with_drooped_ears_tired_expression_holding_a_small_wilted_flower_white_bg
                else -> 
                    R.drawable.capybara_hungry_and_in_despair_white_bg
            }
            
            try {
                views.setImageViewResource(R.id.widget_capybara_image, imageResId)
            } catch (e: Exception) {
                Log.e("CapybaraWidget", "Error setting capybara image", e)
            }
            
            // Set up click intent to open the app
            try {
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
                
                // Make the entire widget clickable
                views.setOnClickPendingIntent(R.id.widget_capybara_image, pendingIntent)
            } catch (e: Exception) {
                Log.e("CapybaraWidget", "Error setting click intent", e)
            }
            
            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d("CapybaraWidget", "Minimal widget updated successfully")
        } catch (e: Exception) {
            Log.e("CapybaraWidget", "Error updating widget", e)
        }
    }

    private fun getWidgetLayout(context: Context, appWidgetId: Int): Int {
        // Use minimal layout - just the capybara for emotional connection
        return R.layout.widget_minimal
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