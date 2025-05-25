package com.zodwick.capibara_android

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import java.util.*

class ScreenTimeManager(private val context: Context) {
    
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager
    
    fun getDailyScreenTime(): DailyScreenTime {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        
        val appUsageList = mutableListOf<AppUsage>()
        var totalScreenTime = 0L
        
        for (usageStat in usageStats) {
            if (usageStat.totalTimeInForeground > 0) {
                val appName = getAppName(usageStat.packageName)
                if (appName.isNotEmpty() && !isSystemApp(usageStat.packageName)) {
                    appUsageList.add(
                        AppUsage(
                            appName = appName,
                            packageName = usageStat.packageName,
                            timeInForeground = usageStat.totalTimeInForeground,
                            lastTimeUsed = usageStat.lastTimeUsed
                        )
                    )
                    totalScreenTime += usageStat.totalTimeInForeground
                }
            }
        }
        
        // Sort by usage time (descending)
        appUsageList.sortByDescending { it.timeInForeground }
        
        return DailyScreenTime(totalScreenTime, appUsageList)
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            
            // Handle common system apps with user-friendly names
            when {
                packageName.contains("launcher") -> "Home Screen"
                packageName.contains("nexuslauncher") -> "Pixel Launcher"
                packageName.contains("systemui") -> "System UI"
                packageName.contains("settings") -> "Settings"
                packageName.contains("chrome") -> "Chrome"
                packageName.contains("photos") -> "Google Photos"
                packageName.contains("gmail") -> "Gmail"
                packageName.contains("maps") -> "Google Maps"
                packageName.contains("youtube") -> "YouTube"
                packageName.contains("whatsapp") -> "WhatsApp"
                packageName.contains("instagram") -> "Instagram"
                packageName.contains("facebook") -> "Facebook"
                packageName.contains("twitter") -> "Twitter"
                packageName.contains("spotify") -> "Spotify"
                packageName.contains("netflix") -> "Netflix"
                else -> appName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // Fallback for unknown packages
            packageName.substringAfterLast(".").replaceFirstChar { it.uppercase() }
        }
    }
    
    fun getAppIcon(packageName: String): android.graphics.drawable.Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    private fun isSystemApp(packageName: String): Boolean {
        // Filter out some common system packages
        val systemPackages = listOf(
            "android",
            "com.android.systemui",
            "com.android.launcher",
            "com.google.android.gms"
        )
        return systemPackages.any { packageName.startsWith(it) }
    }
    
    fun formatTime(milliseconds: Long): String {
        val hours = milliseconds / (1000 * 60 * 60)
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 -> "${hours}h ${minutes}min"
            minutes > 0 -> "${minutes} min"
            else -> "<1 min"
        }
    }
    
    fun formatTimeShort(milliseconds: Long): String {
        val hours = milliseconds / (1000 * 60 * 60)
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "<1m"
        }
    }
} 