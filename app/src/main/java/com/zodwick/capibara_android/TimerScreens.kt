package com.zodwick.capibara_android

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun AppTimersScreen(navController: NavController) {
    val context = LocalContext.current
    val timerManager = remember { TimerManager(context) }
    val screenTimeManager = remember { ScreenTimeManager(context) }
    
    val appTimers by timerManager.appTimers.collectAsState()
    var screenTimeData by remember { mutableStateOf<DailyScreenTime?>(null) }
    var showAddTimerDialog by remember { mutableStateOf(false) }
    var showEditTimerDialog by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<AppUsage?>(null) }
    var editingTimer by remember { mutableStateOf<AppTimer?>(null) }
    var hasBlockingPermissions by remember { mutableStateOf(false) }
    
    // Check blocking permissions
    LaunchedEffect(Unit) {
        hasBlockingPermissions = PermissionHelper.hasAllBlockingPermissions(context)
    }
    
    // Load screen time data
    LaunchedEffect(Unit) {
        try {
            screenTimeData = screenTimeManager.getDailyScreenTime()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "⏰ App Timers",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
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
                actions = {
                    IconButton(
                        onClick = { showAddTimerDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            )
                    ) {
                        Text(
                            "➕",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TimerOverviewCard(appTimers)
                }
                
                item {
                    BlockingServiceCard(
                        hasPermissions = hasBlockingPermissions,
                        onSetupBlocking = {
                            if (!PermissionHelper.hasAccessibilityPermission(context)) {
                                PermissionHelper.requestAccessibilityPermission(context)
                            } else if (!PermissionHelper.hasSystemAlertWindowPermission(context)) {
                                PermissionHelper.requestSystemAlertWindowPermission(context)
                            }
                        }
                    )
                }
                
                items(appTimers) { timer ->
                    AppTimerCard(
                        timer = timer,
                        onToggle = { enabled -> timerManager.toggleAppTimer(timer.packageName, enabled) },
                        onRemove = { timerManager.removeAppTimer(timer.packageName) },
                        onEdit = { 
                            editingTimer = timer
                            showEditTimerDialog = true
                        }
                    )
                }
                
                item {
                    if (appTimers.isEmpty()) {
                        EmptyTimersState { showAddTimerDialog = true }
                    }
                }
            }
        }
    }
    
    // Add Timer Dialog
    if (showAddTimerDialog) {
        AddTimerDialog(
            availableApps = screenTimeData?.appUsageList?.filter { app ->
                appTimers.none { it.packageName == app.packageName }
            } ?: emptyList(),
            onDismiss = { showAddTimerDialog = false },
            onAddTimer = { packageName, appName, limitMinutes ->
                timerManager.setAppTimer(packageName, appName, limitMinutes)
                showAddTimerDialog = false
            }
        )
    }
    
    // Edit Timer Dialog
    if (showEditTimerDialog && editingTimer != null) {
        EditTimerDialog(
            timer = editingTimer!!,
            onDismiss = { 
                showEditTimerDialog = false
                editingTimer = null
            },
            onUpdateTimer = { packageName, appName, limitMinutes ->
                timerManager.setAppTimer(packageName, appName, limitMinutes)
                showEditTimerDialog = false
                editingTimer = null
            }
        )
    }
}

@Composable
fun TimerOverviewCard(appTimers: List<AppTimer>) {
    val activeTimers = appTimers.count { it.isEnabled }
    val limitReached = appTimers.count { it.isLimitReached && it.isEnabled }
    
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
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 Timer Overview",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimerStatItem(
                    title = "Active",
                    value = activeTimers.toString(),
                    emoji = "⏰",
                    color = MaterialTheme.colorScheme.primary
                )
                
                TimerStatItem(
                    title = "Limits Reached",
                    value = limitReached.toString(),
                    emoji = "🚫",
                    color = if (limitReached > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                )
                
                TimerStatItem(
                    title = "Under Control",
                    value = (activeTimers - limitReached).toString(),
                    emoji = "✅",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AppTimerCard(
    timer: AppTimer,
    onToggle: (Boolean) -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit
) {
    val usagePercentage = (timer.currentUsageToday / (1000 * 60)).toFloat() / timer.dailyLimitMinutes.toFloat()
    val progressColor = when {
        usagePercentage >= 1f -> MaterialTheme.colorScheme.error
        usagePercentage >= 0.8f -> Color(0xFFF57C00) // Orange
        else -> MaterialTheme.colorScheme.primary
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = timer.appName,
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "${timer.dailyLimitMinutes} min limit",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Switch(
                    checked = timer.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Usage Progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Used: ${(timer.currentUsageToday / (1000 * 60)).toInt()} min",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = "${(usagePercentage * 100).toInt()}%",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = usagePercentage.coerceAtMost(1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = progressColor.copy(alpha = 0.2f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("✏️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                
                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("🗑️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
fun FocusSessionScreen(navController: NavController) {
    val context = LocalContext.current
    val timerManager = remember { TimerManager(context) }
    val focusSession by timerManager.focusSession.collectAsState()
    
    var selectedDuration by remember { mutableStateOf(25) }
    var selectedBreakDuration by remember { mutableStateOf(5) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "🧠 Focus Sessions",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
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
                }
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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    if (focusSession != null) {
                        ActiveFocusSessionCard(
                            session = focusSession!!,
                            onPause = { timerManager.pauseFocusSession() },
                            onResume = { timerManager.resumeFocusSession() },
                            onStop = { timerManager.stopFocusSession() },
                            formatTime = { timerManager.formatTimeRemaining(it) }
                        )
                    } else {
                        FocusSessionSetupCard(
                            selectedDuration = selectedDuration,
                            selectedBreakDuration = selectedBreakDuration,
                            onDurationChange = { selectedDuration = it },
                            onBreakDurationChange = { selectedBreakDuration = it },
                            onStart = { 
                                timerManager.startFocusSession(selectedDuration, selectedBreakDuration)
                            }
                        )
                    }
                }
                
                item {
                    FocusSessionInfoCard()
                }
            }
        }
    }
}

@Composable
fun ActiveFocusSessionCard(
    session: FocusSession,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    formatTime: (Long) -> String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f - (session.remainingTimeMs.toFloat() / (session.durationMinutes * 60 * 1000f)),
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phase indicator
            Text(
                text = when (session.currentPhase) {
                    FocusPhase.FOCUS -> "🧠 Focus Time"
                    FocusPhase.BREAK -> "☕ Break Time"
                    FocusPhase.COMPLETED -> "🎉 Completed!"
                },
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Circular Progress
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = when (session.currentPhase) {
                        FocusPhase.FOCUS -> MaterialTheme.colorScheme.primary
                        FocusPhase.BREAK -> MaterialTheme.colorScheme.secondary
                        FocusPhase.COMPLETED -> MaterialTheme.colorScheme.tertiary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(session.remainingTimeMs),
                        fontFamily = FontFamily.Default,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = if (session.currentPhase == FocusPhase.FOCUS) "remaining" else "break time",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Sessions completed: ${session.sessionsCompleted}",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (session.currentPhase != FocusPhase.COMPLETED) {
                    Button(
                        onClick = if (session.isActive) onPause else onResume,
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (session.isActive) "⏸️" else "▶️",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (session.isActive) "Pause" else "Resume")
                    }
                }
                
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("⏹️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
fun FocusSessionSetupCard(
    selectedDuration: Int,
    selectedBreakDuration: Int,
    onDurationChange: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
    onStart: () -> Unit
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
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 Start Focus Session",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Focus duration selector
            Text(
                text = "Focus Duration: $selectedDuration minutes",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Slider(
                value = selectedDuration.toFloat(),
                onValueChange = { onDurationChange(it.toInt()) },
                valueRange = 15f..60f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Break duration selector
            Text(
                text = "Break Duration: $selectedBreakDuration minutes",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Slider(
                value = selectedBreakDuration.toFloat(),
                onValueChange = { onBreakDurationChange(it.toInt()) },
                valueRange = 5f..15f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("▶️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Start Focus Session",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FocusSessionInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "💡 About Focus Sessions",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Focus sessions use the Pomodoro Technique to help you stay productive while giving your capybaras (and your mind) regular rest periods. Work in focused bursts, then take short breaks to recharge!",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun TimerStatItem(
    title: String,
    value: String,
    emoji: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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
            color = color
        )
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyTimersState(onAddTimer: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⏰",
                fontSize = 64.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No App Timers Set",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Set time limits for your apps to help your capybaras stay happy and healthy!",
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAddTimer,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("➕", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Your First Timer")
            }
        }
    }
}

@Composable
fun AddTimerDialog(
    availableApps: List<AppUsage>,
    onDismiss: () -> Unit,
    onAddTimer: (String, String, Int) -> Unit
) {
    var selectedApp by remember { mutableStateOf<AppUsage?>(null) }
    var limitMinutes by remember { mutableStateOf(60) }
    var showAppPicker by remember { mutableStateOf(false) }
    
    // Auto-select first app when dialog opens
    LaunchedEffect(availableApps) {
        if (selectedApp == null && availableApps.isNotEmpty()) {
            selectedApp = availableApps.first()
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add App Timer",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Set a daily time limit for apps to keep your capybaras happy:",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // App Selection Button
                OutlinedButton(
                    onClick = { showAppPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    selectedApp?.let { app ->
                        Column {
                            Text(
                                text = "Selected: ${app.appName}",
                                fontFamily = FontFamily.SansSerif,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap to change app",
                                fontFamily = FontFamily.SansSerif,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } ?: Text("Select an app")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Daily limit: $limitMinutes minutes",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Slider(
                    value = limitMinutes.toFloat(),
                    onValueChange = { limitMinutes = it.toInt() },
                    valueRange = 15f..240f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedApp?.let { app ->
                        onAddTimer(app.packageName, app.appName, limitMinutes)
                    }
                },
                enabled = selectedApp != null
            ) {
                Text("Add Timer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // App Picker Dialog
    if (showAppPicker) {
        AppPickerDialog(
            availableApps = availableApps,
            selectedApp = selectedApp,
            onAppSelected = { app ->
                selectedApp = app
                showAppPicker = false
            },
            onDismiss = { showAppPicker = false }
        )
    }
}

@Composable
fun EditTimerDialog(
    timer: AppTimer,
    onDismiss: () -> Unit,
    onUpdateTimer: (String, String, Int) -> Unit
) {
    var limitMinutes by remember { mutableStateOf(timer.dailyLimitMinutes) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit App Timer",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Adjust the daily time limit for ${timer.appName}:",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = timer.appName,
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Current usage: ${(timer.currentUsageToday / (1000 * 60)).toInt()} minutes today",
                            fontFamily = FontFamily.SansSerif,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Daily limit: $limitMinutes minutes",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Slider(
                    value = limitMinutes.toFloat(),
                    onValueChange = { limitMinutes = it.toInt() },
                    valueRange = 15f..240f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Text(
                    text = "Warning at ${limitMinutes - 5} minutes",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdateTimer(timer.packageName, timer.appName, limitMinutes)
                }
            ) {
                Text("Update Timer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AppPickerDialog(
    availableApps: List<AppUsage>,
    selectedApp: AppUsage?,
    onAppSelected: (AppUsage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Choose App",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (availableApps.isEmpty()) {
                Text(
                    text = "No apps available for timer setup. Make sure you have used some apps today and granted usage access permission.",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableApps) { app ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAppSelected(app) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedApp?.packageName == app.packageName) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = app.appName,
                                    fontFamily = FontFamily.SansSerif,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Used ${(app.timeInForeground / (1000 * 60)).toInt()} minutes today",
                                    fontFamily = FontFamily.SansSerif,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun BlockingServiceCard(
    hasPermissions: Boolean,
    onSetupBlocking: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasPermissions) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (hasPermissions) "🛡️ App Blocking Active" else "⚠️ App Blocking Setup",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (hasPermissions) {
                Text(
                    text = "Apps will be automatically blocked when daily limits are reached. Your capybaras can rest easy! 🦫💤",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 16.sp)
                    Text(
                        "Accessibility Service",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text("✅", fontSize = 16.sp)
                    Text(
                        "Overlay Permission",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = "To block apps when limits are reached, please enable the required permissions. This helps your capybaras enforce healthy digital habits! 🦫",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onSetupBlocking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("🔒", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enable App Blocking")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Note: You'll need to enable accessibility service and overlay permissions in Android Settings.",
                    fontFamily = FontFamily.SansSerif,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
} 