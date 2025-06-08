package com.zodwick.capibara_android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zodwick.capibara_android.ui.theme.CapibaraAndroidTheme
import kotlinx.coroutines.delay

class BlockingOverlayActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val blockedAppName = intent.getStringExtra("blocked_app_name") ?: "App"
        val blockedPackageName = intent.getStringExtra("blocked_package_name") ?: ""
        val dailyLimit = intent.getIntExtra("daily_limit", 60)
        val currentUsage = intent.getIntExtra("current_usage", 0)
        
        setContent {
            CapibaraAndroidTheme {
                AppBlockedScreen(
                    appName = blockedAppName,
                    packageName = blockedPackageName,
                    dailyLimit = dailyLimit,
                    currentUsage = currentUsage,
                    onDismiss = { 
                        goToHome()
                        finish()
                    },
                    onSettings = {
                        openAppSettings()
                        finish()
                    }
                )
            }
        }
    }
    
    private fun goToHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }
    
    private fun openAppSettings() {
        val settingsIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(settingsIntent)
    }
    
    override fun onBackPressed() {
        // Prevent back button from bypassing the block
        goToHome()
        super.onBackPressed()
    }
}

@Composable
fun AppBlockedScreen(
    appName: String,
    packageName: String,
    dailyLimit: Int,
    currentUsage: Int,
    onDismiss: () -> Unit,
    onSettings: () -> Unit
) {
    var animationTime by remember { mutableStateOf(0f) }
    
    // Continuous animation
    LaunchedEffect(Unit) {
        while (true) {
            animationTime += 0.02f
            delay(50)
        }
    }
    
    // Animated background
    val animatedAlpha by animateFloatAsState(
        targetValue = 0.7f + 0.1f * kotlin.math.sin(animationTime),
        animationSpec = tween(durationMillis = 1000),
        label = "background_alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6B4423).copy(alpha = animatedAlpha),
                        Color(0xFF4A2C17).copy(alpha = 0.95f)
                    ),
                    radius = 1000f
                )
            )
    ) {
        // Background pattern
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            for (i in 0..20) {
                for (j in 0..30) {
                    val x = (i * 60f) + (30f * kotlin.math.sin(animationTime + i * 0.5f)).toFloat()
                    val y = (j * 60f) + (20f * kotlin.math.cos(animationTime + j * 0.3f)).toFloat()
                    
                    if (x < canvasWidth && y < canvasHeight) {
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main blocking card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sleepy capybara emoji
                    Text(
                        text = "💤",
                        fontSize = 72.sp,
                        modifier = Modifier
                            .alpha(0.7f + 0.3f * kotlin.math.sin(animationTime * 2))
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Time for a Break!",
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B4423),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Your capybaras are tired! You've reached your daily limit for $appName.",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF4A2C17),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Usage stats
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5E6D3).copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Used Today:",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6B4423)
                                )
                                Text(
                                    text = "$currentUsage min",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B4423)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Daily Limit:",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6B4423)
                                )
                                Text(
                                    text = "$dailyLimit min",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B4423)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Progress bar
                            LinearProgressIndicator(
                                progress = 1f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFD84315),
                                trackColor = Color(0xFFFFCCBC)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Action buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B4423)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("🏠", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Go to Home",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        OutlinedButton(
                            onClick = onSettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B4423)
                            ),
                            border = BorderStroke(2.dp, Color(0xFF6B4423)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("⚙️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Adjust Timer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "🦫 Your capybaras appreciate your self-care! Try reading, walking, or spending time with friends instead.",
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B4423).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
} 