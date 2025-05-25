package com.zodwick.capibara_android

data class AppUsage(
    val appName: String,
    val packageName: String,
    val timeInForeground: Long, // in milliseconds
    val lastTimeUsed: Long
)

data class DailyScreenTime(
    val totalScreenTime: Long, // in milliseconds
    val appUsageList: List<AppUsage>
) 