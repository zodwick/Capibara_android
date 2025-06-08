package com.zodwick.capibara_android

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class AppBlockingService : AccessibilityService() {
    
    private lateinit var timerManager: TimerManager
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    private var lastCheckedPackage: String? = null
    private var lastCheckTime = 0L
    
    companion object {
        private const val CHECK_COOLDOWN = 2000L // 2 seconds cooldown between checks
        var isServiceRunning = false
            private set
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceRunning = true
        timerManager = TimerManager(this)
        
        // Log service start
        android.util.Log.d("AppBlockingService", "Accessibility service connected")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        scope.cancel()
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString()
                if (packageName != null && packageName != applicationContext.packageName) {
                    checkAppLimit(packageName)
                }
            }
        }
    }
    
    override fun onInterrupt() {
        // Required override but not used
    }
    
    private fun checkAppLimit(packageName: String) {
        val currentTime = System.currentTimeMillis()
        
        // Avoid checking the same app too frequently
        if (packageName == lastCheckedPackage && 
            (currentTime - lastCheckTime) < CHECK_COOLDOWN) {
            return
        }
        
        lastCheckedPackage = packageName
        lastCheckTime = currentTime
        
        scope.launch {
            try {
                val appTimers = timerManager.appTimers.first()
                val blockedTimer = appTimers.find { timer ->
                    timer.packageName == packageName && 
                    timer.isEnabled && 
                    timer.isLimitReached
                }
                
                if (blockedTimer != null) {
                    blockApp(blockedTimer)
                }
            } catch (e: Exception) {
                android.util.Log.e("AppBlockingService", "Error checking app limit", e)
            }
        }
    }
    
    private fun blockApp(timer: AppTimer) {
        // Start the blocking overlay activity
        val intent = Intent(this, BlockingOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("blocked_app_name", timer.appName)
            putExtra("blocked_package_name", timer.packageName)
            putExtra("daily_limit", timer.dailyLimitMinutes)
            putExtra("current_usage", (timer.currentUsageToday / (1000 * 60)).toInt())
        }
        startActivity(intent)
        
        // Also try to minimize the blocked app (best effort)
        try {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.moveTaskToFront(intent.hashCode(), 0)
        } catch (e: Exception) {
            // Ignore if this fails
        }
        
        android.util.Log.d("AppBlockingService", "Blocked app: ${timer.appName}")
    }
}

// Extension function to check if accessibility service is enabled
fun Context.isAccessibilityServiceEnabled(): Boolean {
    return try {
        val accessibilityEnabled = android.provider.Settings.Secure.getInt(
            contentResolver,
            android.provider.Settings.Secure.ACCESSIBILITY_ENABLED
        )
        
        if (accessibilityEnabled == 1) {
            val services = android.provider.Settings.Secure.getString(
                contentResolver,
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val serviceName = "${packageName}/${AppBlockingService::class.java.canonicalName}"
            services?.contains(serviceName) == true
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
} 