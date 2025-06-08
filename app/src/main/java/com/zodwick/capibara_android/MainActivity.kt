package com.zodwick.capibara_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zodwick.capibara_android.ui.theme.SanctuaryTheme
import java.util.*
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.cos
import kotlin.random.Random
import android.appwidget.AppWidgetManager
import android.content.ComponentName

data class Capybara(
    val id: Int,
    val isAlive: Boolean = true,
    val mood: CapybaraMood = CapybaraMood.HAPPY,
    val lastSeenTime: Long = System.currentTimeMillis(),
    val animationOffset: Float = Random.nextFloat() * 1000f,
    val isSelected: Boolean = false
)

enum class CapybaraMood {
    HAPPY, CONTENT, SLEEPY, WORRIED, ANGRY, PEACEFUL, EXCITED
}

data class UserSettings(
    val dailyTargetHours: Float = 3f,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

class SettingsManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("capybara_settings", Context.MODE_PRIVATE)
    
    fun saveSettings(settings: UserSettings) {
        prefs.edit().apply {
            putFloat("daily_target_hours", settings.dailyTargetHours)
            putBoolean("notifications_enabled", settings.notificationsEnabled)
            putBoolean("sound_enabled", settings.soundEnabled)
            apply()
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, CapybaraWidgetProvider::class.java))
        if (ids.isNotEmpty()) {
            CapybaraWidgetProvider().onUpdate(context, appWidgetManager, ids)
        }
    }
    
    fun loadSettings(): UserSettings {
        return UserSettings(
            dailyTargetHours = prefs.getFloat("daily_target_hours", 3f),
            notificationsEnabled = prefs.getBoolean("notifications_enabled", true),
            soundEnabled = prefs.getBoolean("sound_enabled", true)
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContent {
            SanctuaryTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "sanctuary"
                ) {
                    composable("sanctuary") {
                        CapybaraSanctuaryScreen(navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(navController = navController)
                    }
                    composable("detailed_usage") {
                        DetailedUsageScreen(navController = navController)
                    }
                    composable("app_timers") {
                        AppTimersScreen(navController = navController)
                    }
                    composable("focus_sessions") {
                        FocusSessionScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun CapybaraSanctuaryScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    
    var hasPermission by remember { mutableStateOf(false) }
    var screenTimeData by remember { mutableStateOf<DailyScreenTime?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var capybaras by remember { mutableStateOf(generateDailyCapybaras()) }
    var userSettings by remember { mutableStateOf(settingsManager.loadSettings()) }
    var animationTime by remember { mutableStateOf(0f) }
    var selectedCapybara by remember { mutableStateOf<Int?>(null) }
    
    // Continuous animation for living capybaras
    LaunchedEffect(Unit) {
        while (true) {
            animationTime += 0.05f
            delay(50)
        }
    }
    
    // Load settings on start
    LaunchedEffect(Unit) {
        userSettings = settingsManager.loadSettings()
    }
    
    // Check for permission and get screen time
    LaunchedEffect(Unit) {
        hasPermission = PermissionHelper.hasUsageStatsPermission(context)
        if (hasPermission) {
            isLoading = true
            try {
                val manager = ScreenTimeManager(context)
                screenTimeData = manager.getDailyScreenTime()
            } catch (e: Exception) {
                // Handle error gracefully
            } finally {
                isLoading = false
            }
        }
    }
    
    // Update capybaras based on screen time and user settings
    LaunchedEffect(screenTimeData, userSettings) {
        screenTimeData?.let { data ->
            val hoursUsed = (data.totalScreenTime / (1000 * 60 * 60)).toFloat()
            val capybarasToKill = ((hoursUsed / userSettings.dailyTargetHours) * 30).toInt()
            capybaras = updateCapybarasBasedOnUsage(capybaras, capybarasToKill, hoursUsed, userSettings.dailyTargetHours)
        }
    }
    
    // Auto-refresh every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // 1 minute
            if (hasPermission) {
                try {
                    val manager = ScreenTimeManager(context)
                    screenTimeData = manager.getDailyScreenTime()
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo),
                            contentDescription = "Sanctuary icon",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            ";;Sanctuary",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("detailed_usage") },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Detailed Usage",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            // Enhanced background with parallax
            Image(
                painter = painterResource(id = R.drawable.sunset_wide_1536_1024_ghibly),
                contentDescription = "Peaceful background",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.2f)
                    .scale(1.1f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                                item {
        if (!hasPermission) {
                        EnhancedPermissionCard {
                    PermissionHelper.requestUsageStatsPermission(context)
                }
                    } else {
                        BeautifulSanctuaryHeader(
                            capybaras = capybaras,
                            screenTimeData = screenTimeData,
                            userSettings = userSettings,
                            isLoading = isLoading,
                            animationTime = animationTime
                        )
                    }
                }
                
                if (hasPermission) {
                    item {
                        SanctuaryStatsCard(
                            capybaras = capybaras,
                            screenTimeData = screenTimeData,
                            userSettings = userSettings,
                            animationTime = animationTime
                        )
                    }
                }
                
                if (hasPermission) {
                    item {
                        InteractiveCapybaraGrid(
                            capybaras = capybaras,
                            animationTime = animationTime,
                            selectedCapybara = selectedCapybara,
                            onCapybaraClick = { capybaraId ->
                                selectedCapybara = if (selectedCapybara == capybaraId) null else capybaraId
                            }
                        )
                    }
                    
                    item {
                        BeautifulWellnessInsights(
                            screenTimeData = screenTimeData,
                            capybaras = capybaras,
                            userSettings = userSettings,
                            animationTime = animationTime
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    
    var userSettings by remember { mutableStateOf(settingsManager.loadSettings()) }
    var targetHours by remember { mutableStateOf(userSettings.dailyTargetHours) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    
    // Auto-hide success message
    LaunchedEffect(showSaveSuccess) {
        if (showSaveSuccess) {
            delay(2000)
            showSaveSuccess = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                modifier = Modifier
                            .size(48.dp)
                    .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        CircleShape
                            )
            ) {
                Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    BeautifulSettingsCard(
                        title = "🎯 Daily Screen Time Goal",
                        description = "Set your ideal daily screen time to keep your capybaras happy",
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Column {
            Text(
                                text = "${String.format("%.1f", targetHours)} hours",
                                fontFamily = FontFamily.SansSerif,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Slider(
                                value = targetHours,
                                onValueChange = { targetHours = it },
                                valueRange = 1f..8f,
                                steps = 13,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
            Button(
                                onClick = {
                                    userSettings = userSettings.copy(dailyTargetHours = targetHours)
                                    settingsManager.saveSettings(userSettings)
                                    showSaveSuccess = true
                                },
                modifier = Modifier
                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                                    if (showSaveSuccess) "✓ Saved!" else "Save Goal",
                                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

                item {
                    BeautifulSettingsCard(
                        title = "🔔 Notifications",
                        description = "Customize your gentle reminders",
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        val context = LocalContext.current
                        val timerManager = remember { TimerManager(context) }
                        val breakReminder by timerManager.breakReminder.collectAsState()
                        var breakInterval by remember { mutableStateOf(breakReminder.intervalMinutes) }
                        
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            SettingsToggle(
                                title = "Gentle Reminders",
                                description = "Get notified when capybaras need rest",
                                checked = userSettings.notificationsEnabled,
                                onCheckedChange = { 
                                    userSettings = userSettings.copy(notificationsEnabled = it)
                                    settingsManager.saveSettings(userSettings)
                                }
                            )
                            
                            SettingsToggle(
                                title = "Break Reminders",
                                description = "Regular reminders to take screen breaks",
                                checked = breakReminder.isEnabled,
                                onCheckedChange = { enabled ->
                                    timerManager.setBreakReminder(breakInterval, enabled)
                                }
                            )
                            
                            if (breakReminder.isEnabled) {
                                Column {
                                    Text(
                                        text = "Break interval: $breakInterval minutes",
                                        fontFamily = FontFamily.SansSerif,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Slider(
                                        value = breakInterval.toFloat(),
                                        onValueChange = { 
                                            breakInterval = it.toInt()
                                            timerManager.setBreakReminder(breakInterval, breakReminder.isEnabled)
                                        },
                                        valueRange = 15f..120f,
                                        steps = 6,
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.secondary,
                                            activeTrackColor = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }
                            }
                            
                            SettingsToggle(
                                title = "Sound Effects",
                                description = "Peaceful sounds for interactions",
                                checked = userSettings.soundEnabled,
                                onCheckedChange = { 
                                    userSettings = userSettings.copy(soundEnabled = it)
                                    settingsManager.saveSettings(userSettings)
                                }
                            )
                        }
                    }
                }
                
                item {
                    BeautifulSettingsCard(
                        title = "⏰ App Timers",
                        description = "Set time limits for specific apps to keep your capybaras happy",
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { navController.navigate("app_timers") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("⏰", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Manage App Timers", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                            
                            OutlinedButton(
                                onClick = { navController.navigate("focus_sessions") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("🧠", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Focus Sessions", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                
                item {
                    BeautifulSettingsCard(
                        title = "🌱 About Capybara Sanctuary",
                        description = "Learn about your digital wellness journey",
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(
                            text = "Each day you receive 30 adorable capybaras. The more you use your phone beyond your daily goal, the more capybaras need to rest. Find balance and keep your digital friends happy! 🌸\n\nTap on capybaras to interact with them and see their moods change based on your digital wellness.",
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BeautifulSettingsCard(
    title: String,
    description: String,
    color: Color,
    content: @Composable () -> Unit
) {
            Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
            ) {
                Column(
            modifier = Modifier.padding(28.dp)
                ) {
                    Text(
                text = title,
                fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
                    Spacer(modifier = Modifier.height(8.dp))
            
                    Text(
                text = description,
                fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            content()
        }
    }
}

@Composable
fun SettingsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun BeautifulSanctuaryHeader(
    capybaras: List<Capybara>,
    screenTimeData: DailyScreenTime?,
    userSettings: UserSettings,
    isLoading: Boolean,
    animationTime: Float
) {
    val aliveCount = capybaras.count { it.isAlive }
    val restingCount = capybaras.count { !it.isAlive }
    
    val healthPercentage = (aliveCount / 30f)
    val healthColor by animateColorAsState(
        targetValue = when {
            healthPercentage >= 0.8f -> MaterialTheme.colorScheme.primary
            healthPercentage >= 0.5f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }
    )
    
        // Main capybara card with white background
    Card(
                modifier = Modifier
                    .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                        // Enlarged capybara without animation
                Box(
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                Image(
                    painter = painterResource(id = getSanctuaryWellnessCapybara(healthPercentage)),
                    contentDescription = "Sanctuary wellness indicator",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
                Text(
                text = when {
                    healthPercentage >= 0.8f -> "Perfect Harmony"
                    healthPercentage >= 0.5f -> "Needs Attention"
                    healthPercentage >= 0.2f -> "In Distress"
                    else -> "In Crisis"
                },
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SanctuaryStatsCard(
    capybaras: List<Capybara>,
    screenTimeData: DailyScreenTime?,
    userSettings: UserSettings,
    animationTime: Float
) {
    val aliveCount = capybaras.count { it.isAlive }
    val restingCount = capybaras.count { !it.isAlive }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BeautifulStatCard(
                title = "Happy",
                value = aliveCount.toString(),
                emoji = "💚",
                color = MaterialTheme.colorScheme.primary,
                isAnimated = true,
                animationTime = animationTime
            )
            
            BeautifulStatCard(
                title = "Resting",
                value = restingCount.toString(),
                emoji = "💤",
                color = MaterialTheme.colorScheme.secondary,
                isAnimated = false,
                animationTime = animationTime
            )
            
            screenTimeData?.let { data ->
                val totalMinutes = (data.totalScreenTime / (1000 * 60)).toInt()
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                val timeString = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
                val targetMinutes = (userSettings.dailyTargetHours * 60).toInt()
                
                BeautifulStatCard(
                    title = "Screen Time",
                    value = timeString,
                    emoji = "📱",
                    color = if (totalMinutes > targetMinutes) 
                            MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.tertiary,
                    isAnimated = totalMinutes > targetMinutes,
                    animationTime = animationTime
                )
            }
        }
    }
}

@Composable
fun BeautifulStatCard(
    title: String,
    value: String,
    emoji: String,
    color: Color,
    isAnimated: Boolean = false,
    animationTime: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontFamily = FontFamily.SansSerif,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SimpleMoodSummary(capybaras: List<Capybara>) {
    val aliveCount = capybaras.count { it.isAlive }
    val alivePercentage = (aliveCount / 30f) * 100f
    
    val summaryText = when {
        alivePercentage >= 80f -> "Your capybaras are thriving! Excellent digital wellness!"
        alivePercentage >= 60f -> "Most capybaras are content with your screen time habits"
        alivePercentage >= 40f -> "Some capybaras are getting tired from screen time"
        alivePercentage >= 20f -> "Many capybaras need rest - consider reducing screen time"
        else -> "Your capybaras are exhausted - time for a digital detox!"
    }
    
    Text(
        text = summaryText,
        fontFamily = FontFamily.SansSerif,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun OrganicCapybaraLayout(
    capybaras: List<Capybara>,
    selectedCapybara: Int?,
    onCapybaraClick: (Int) -> Unit
) {
    // Select representatives - always 5 capybaras with proper alive/resting ratio
    val representatives = selectRepresentativeCapybaras(capybaras)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Always show in 3+2 format for consistency
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            representatives.take(3).forEach { capybara ->
                BigCapybaraItem(
                    capybara = capybara,
                    isSelected = selectedCapybara == capybara.id,
                    onClick = { onCapybaraClick(capybara.id) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            representatives.drop(3).forEach { capybara ->
                BigCapybaraItem(
                    capybara = capybara,
                    isSelected = selectedCapybara == capybara.id,
                    onClick = { onCapybaraClick(capybara.id) }
                )
            }
        }
    }
}

@Composable
fun BigCapybaraItem(
    capybara: Capybara,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (capybara.isAlive) 1f else 0.8f,
        animationSpec = tween(durationMillis = 300)
    )
    
    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .alpha(alpha)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (capybara.isAlive) {
            Image(
                painter = painterResource(id = getCapybaraDrawable(capybara.mood)),
                contentDescription = "Capybara ${capybara.id} - ${capybara.mood.name.lowercase()}",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
        } else {
            // Show small sleeping capybara for resting capybaras
            Image(
                painter = painterResource(id = R.drawable.capybara_cartoon_style_sleeping_curled_up_with_zzz_facing_right_solo),
                contentDescription = "Sleeping capybara",
                modifier = Modifier
                    .size(56.dp)
                    .alpha(0.7f)
                    .clip(CircleShape)
            )
        }
    }
}

// Helper function to select representative capybaras based on percentage
fun selectRepresentativeCapybaras(capybaras: List<Capybara>): List<Capybara> {
    val aliveCount = capybaras.count { it.isAlive }
    val deadCount = capybaras.count { !it.isAlive }
    val alivePercentage = (aliveCount / 30f) * 100f
    
    // Always show 5 capybaras total, but mix alive and resting based on actual ratios
    val totalToShow = 5
    val aliveToShow = ((aliveCount / 30f) * totalToShow).toInt().coerceAtLeast(if (aliveCount > 0) 1 else 0)
    val restingToShow = totalToShow - aliveToShow
    
    val aliveCapybaras = capybaras.filter { it.isAlive }
    val deadCapybaras = capybaras.filter { !it.isAlive }
    
    val selected = mutableListOf<Capybara>()
    
    // Add alive capybaras with diverse moods
    if (aliveCapybaras.isNotEmpty() && aliveToShow > 0) {
        val moodGroups = aliveCapybaras.groupBy { it.mood }
        moodGroups.values.forEach { group ->
            if (selected.size < aliveToShow) {
                selected.add(group.first())
            }
        }
        
        // Fill remaining alive slots
        val remaining = aliveCapybaras.filter { it !in selected }
        val aliveNeeded = aliveToShow - selected.size
        selected.addAll(remaining.take(aliveNeeded))
    }
    
    // Add resting capybaras
    if (deadCapybaras.isNotEmpty() && restingToShow > 0) {
        selected.addAll(deadCapybaras.take(restingToShow))
    }
    
    return selected.take(totalToShow)
}

@Composable
fun InteractiveCapybaraGrid(
    capybaras: List<Capybara>,
    animationTime: Float,
    selectedCapybara: Int?,
    onCapybaraClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Text(
                text = "Your Capybara Friends",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple mood summary
            SimpleMoodSummary(capybaras = capybaras)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Organic capybara layout
            OrganicCapybaraLayout(
                capybaras = capybaras,
                selectedCapybara = selectedCapybara,
                onCapybaraClick = onCapybaraClick
            )
        }
    }
}

@Composable
fun InteractiveCapybaraItem(
    capybara: Capybara,
    animationTime: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = when {
            !capybara.isAlive -> 0.8f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (capybara.isAlive) 1f else 0.5f,
        animationSpec = tween(durationMillis = 500)
    )
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .alpha(alpha)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (capybara.isAlive) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ),
                modifier = Modifier.shadow(
                    elevation = if (isSelected) 8.dp else 2.dp,
                    shape = CircleShape
                )
            ) {
                    Image(
                    painter = painterResource(id = getCapybaraDrawable(capybara.mood)),
                    contentDescription = "Capybara ${capybara.id} - ${capybara.mood.name.lowercase()}",
                        modifier = Modifier
                        .size(75.dp)
                        .padding(8.dp)
                            .clip(CircleShape)
                    )
            }
                } else {
                    Image(
                painter = painterResource(id = R.drawable.capybara_cartoon_style_sleeping_curled_up_with_zzz_facing_right_solo),
                contentDescription = "Sleeping capybara",
                modifier = Modifier
                    .size(40.dp)
                    .alpha(0.7f)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun BeautifulWellnessInsights(
    screenTimeData: DailyScreenTime?,
    capybaras: List<Capybara>,
    userSettings: UserSettings,
    animationTime: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.capybara_cartoon_style_sitting_content_happy__eating_lettuce_with_heart_facing_left_solo),
                    contentDescription = "Wellness capybara",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Wellness Insights",
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            val aliveCount = capybaras.count { it.isAlive }
            val message = when {
                aliveCount >= 25 -> "Your capybaras are thriving! You're maintaining excellent digital balance. Keep up the mindful usage!"
                aliveCount >= 20 -> "Most of your capybaras are content. You're doing well with your screen time goals."
                aliveCount >= 15 -> "Some capybaras are getting tired. Consider taking more breaks throughout the day."
                aliveCount >= 10 -> "Your capybaras need more rest. Try reducing screen time or taking longer breaks."
                else -> "Your capybaras are very tired. Time for a digital detox to restore balance!"
            }
            
            Text(
                text = message,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp
            )
            
            screenTimeData?.let { data ->
                Spacer(modifier = Modifier.height(24.dp))
                val totalMinutes = (data.totalScreenTime / (1000 * 60)).toInt()
                val targetMinutes = (userSettings.dailyTargetHours * 60).toInt()
                val progress = (totalMinutes.toFloat() / targetMinutes.toFloat()).coerceAtMost(1.5f)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Goal Progress",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "${totalMinutes}m / ${targetMinutes}m",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (totalMinutes > targetMinutes) 
                                MaterialTheme.colorScheme.error 
                                else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                    color = if (totalMinutes > targetMinutes) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun EnhancedPermissionCard(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo),
                contentDescription = "Welcome capybara",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
                Text(
                text = "Welcome to Capybara Sanctuary",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
                )
            
            Spacer(modifier = Modifier.height(20.dp))
            
                Text(
                text = "To care for your 30 daily capybaras and track your digital wellness journey, we need permission to gently monitor your screen time.",
                fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Begin Sanctuary Journey",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper functions
fun generateDailyCapybaras(): List<Capybara> {
    return (1..30).map { id ->
        Capybara(
            id = id,
            isAlive = true,
            mood = CapybaraMood.HAPPY, // Start all happy
            animationOffset = Random.nextFloat() * 1000f
        )
    }
}

fun updateCapybarasBasedOnUsage(
    capybaras: List<Capybara>,
    capybarasToKill: Int,
    hoursUsed: Float,
    targetHours: Float
): List<Capybara> {
    val aliveCapybaras = capybaras.filter { it.isAlive }
    val deadCapybaras = capybaras.filter { !it.isAlive }
    
    val actualKillCount = max(0, capybarasToKill.coerceAtMost(aliveCapybaras.size))
    
    // Intelligently distribute moods based on usage
    val usageRatio = hoursUsed / targetHours
    val totalAlive = aliveCapybaras.size
    
    val happyCount = when {
        usageRatio <= 0.5f -> (totalAlive * 0.8f).toInt() // 80% happy when usage is low
        usageRatio <= 0.8f -> (totalAlive * 0.6f).toInt() // 60% happy when usage is moderate
        usageRatio <= 1.0f -> (totalAlive * 0.3f).toInt() // 30% happy when near limit
        usageRatio <= 1.5f -> (totalAlive * 0.1f).toInt() // 10% happy when over limit
        else -> 0 // No happy capybaras when severely over limit
    }
    
    val worriedCount = when {
        usageRatio <= 0.5f -> (totalAlive * 0.1f).toInt()
        usageRatio <= 0.8f -> (totalAlive * 0.2f).toInt()
        usageRatio <= 1.0f -> (totalAlive * 0.4f).toInt()
        usageRatio <= 1.5f -> (totalAlive * 0.5f).toInt()
        else -> (totalAlive * 0.3f).toInt()
    }
    
    val angryCount = when {
        usageRatio <= 0.8f -> 0
        usageRatio <= 1.0f -> (totalAlive * 0.1f).toInt()
        usageRatio <= 1.5f -> (totalAlive * 0.3f).toInt()
        else -> (totalAlive * 0.6f).toInt()
    }
    
    val contentCount = totalAlive - happyCount - worriedCount - angryCount
    
    // Assign moods to capybaras
    val updatedAlive = aliveCapybaras.mapIndexed { index, capybara ->
        val newMood = when {
            index < happyCount -> if (usageRatio <= 0.3f) CapybaraMood.EXCITED else CapybaraMood.HAPPY
            index < happyCount + contentCount -> if (usageRatio <= 0.6f) CapybaraMood.PEACEFUL else CapybaraMood.CONTENT
            index < happyCount + contentCount + worriedCount -> CapybaraMood.WORRIED
            else -> CapybaraMood.ANGRY
        }
        capybara.copy(mood = newMood)
    }
    
    val newlyDead = updatedAlive.take(actualKillCount).map { it.copy(isAlive = false, mood = CapybaraMood.SLEEPY) }
    val stillAlive = updatedAlive.drop(actualKillCount)
    
    return stillAlive + newlyDead + deadCapybaras
}

fun getCapybaraDrawable(mood: CapybaraMood): Int {
    return when (mood) {
        CapybaraMood.HAPPY, CapybaraMood.EXCITED -> R.drawable.capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo
        CapybaraMood.CONTENT, CapybaraMood.PEACEFUL -> R.drawable.capybara_cartoon_style_sitting_content_happy__eating_lettuce_with_heart_facing_left_solo
        CapybaraMood.SLEEPY -> R.drawable.capybara_cartoon_style_sleeping_curled_up_with_zzz_facing_right_solo
        CapybaraMood.WORRIED -> R.drawable.capybara_cartoon_style_sitting_confused_bread_costume_eyes_closed_question_mark_facing_left_solo
        CapybaraMood.ANGRY -> R.drawable.capybara_cartoon_style_sitting_annoyed_holding_knife_steam_angry_face_facing_right_solo
    }
}

fun getSanctuaryWellnessCapybara(healthPercentage: Float): Int {
    return when {
        healthPercentage >= 0.8f -> R.drawable.capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg
        healthPercentage >= 0.5f -> R.drawable.capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg
        healthPercentage >= 0.2f -> R.drawable.capybara_sitting_with_drooped_ears_tired_expression_holding_a_small_wilted_flower_white_bg
        else -> R.drawable.capybara_hungry_and_in_despair_white_bg
    }
}

@Composable
fun DetailedUsageScreen(navController: NavController) {
    val context = LocalContext.current
    
    var hasPermission by remember { mutableStateOf(false) }
    var screenTimeData by remember { mutableStateOf<DailyScreenTime?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Check for permission and get screen time
    LaunchedEffect(Unit) {
        hasPermission = PermissionHelper.hasUsageStatsPermission(context)
        if (hasPermission) {
            isLoading = true
            try {
                val manager = ScreenTimeManager(context)
                screenTimeData = manager.getDailyScreenTime()
            } catch (e: Exception) {
                // Handle error gracefully
            } finally {
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detailed Usage", 
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            // Enhanced background with parallax - same as main page
            Image(
                painter = painterResource(id = R.drawable.sunset_wide_1536_1024_ghibly),
                contentDescription = "Peaceful background",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.2f)
                    .scale(1.1f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            
            if (!hasPermission) {
                // Show permission request
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EnhancedPermissionCard {
                        PermissionHelper.requestUsageStatsPermission(context)
                    }
                }
            } else if (isLoading) {
                // Show loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Show detailed usage
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    item {
                        UsageOverviewCard(screenTimeData = screenTimeData)
                    }
                    
                    item {
                        AppUsageBreakdownCard(screenTimeData = screenTimeData)
                    }
                }
            }
        }
    }
}

@Composable
fun UsageOverviewCard(screenTimeData: DailyScreenTime?) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val userSettings = remember { settingsManager.loadSettings() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today's Overview",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            screenTimeData?.let { data ->
                val totalHours = data.totalScreenTime / (1000 * 60 * 60)
                val totalMinutes = (data.totalScreenTime % (1000 * 60 * 60)) / (1000 * 60)
                val timeString = if (totalHours > 0) "${totalHours}h ${totalMinutes}m" else "${totalMinutes}m"
                
                // Show total time
                Text(
                    text = timeString,
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Screen Time",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress bar
                val totalMinutesUsed = (data.totalScreenTime / (1000 * 60)).toInt()
                val targetMinutes = (userSettings.dailyTargetHours * 60).toInt()
                val progress = (totalMinutesUsed.toFloat() / targetMinutes.toFloat()).coerceAtMost(1.5f)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Goal",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "${totalMinutesUsed}m / ${targetMinutes}m",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (totalMinutesUsed > targetMinutes) 
                                MaterialTheme.colorScheme.error 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (totalMinutesUsed > targetMinutes) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                )
                
            } ?: run {
                Text(
                    text = "No usage data available for today",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AppUsageBreakdownCard(screenTimeData: DailyScreenTime?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp)
        ) {
            screenTimeData?.let { data ->
                if (data.appUsageList.isNotEmpty()) {
                    // Show top 10 apps
                    data.appUsageList.take(10).forEachIndexed { index, appUsage ->
                        AppUsageItem(
                            appUsage = appUsage,
                            rank = index + 1,
                            totalScreenTime = data.totalScreenTime
                        )
                        
                        if (index < minOf(9, data.appUsageList.size - 1)) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    if (data.appUsageList.size > 10) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "... and ${data.appUsageList.size - 10} more apps",
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Text(
                        text = "No app usage data available",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } ?: run {
                Text(
                    text = "Loading app usage data...",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AppUsageItem(
    appUsage: AppUsage,
    rank: Int,
    totalScreenTime: Long
) {
    val hours = appUsage.timeInForeground / (1000 * 60 * 60)
    val minutes = (appUsage.timeInForeground % (1000 * 60 * 60)) / (1000 * 60)
    
    val timeString = when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "<1m"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = appUsage.appName,
            fontFamily = FontFamily.SansSerif,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = timeString,
            fontFamily = FontFamily.SansSerif,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CapybaraSanctuaryPreview() {
    SanctuaryTheme {
        val navController = rememberNavController()
        CapybaraSanctuaryScreen(navController = navController)
    }
}