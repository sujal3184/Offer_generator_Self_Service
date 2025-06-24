package com.example.offer_generator

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.offer_generator.ui.theme.Offer_generatorTheme
import com.example.offer_generator.Navigation.Navigation
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.widget.Toast
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    private var splashShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if splash was already shown
        if (savedInstanceState != null) {
            splashShown = savedInstanceState.getBoolean("splash_shown", false)
        }

        setContent {
            Offer_generatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(!splashShown) }

                    LaunchedEffect(Unit) {
                        if (!splashShown) {
                            delay(3000) // 3 second splash screen
                            showSplash = false
                            splashShown = true
                        }
                    }

                    if (showSplash) {
                        SplashScreen()
                    } else {
                        Navigation()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("splash_shown", splashShown)
    }
}





@Composable
fun SplashScreen() {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Logo scale animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    // Logo rotation animation (continuous)
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Fade in animation for text
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            delayMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "textAlpha"
    )

    // Pulsing animation for background
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundAlpha"
    )

    // Start animations when composable launches
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B).copy(alpha = backgroundAlpha),           // Deep navy blue center
                        Color(0xFF3730A3).copy(alpha = backgroundAlpha * 0.9f),   // Rich indigo
                        Color(0xFF5B21B6).copy(alpha = backgroundAlpha * 0.8f),   // Deep purple
                        Color(0xFF7C3AED).copy(alpha = backgroundAlpha * 0.6f),   // Vibrant purple
                        Color(0xFF1F2937).copy(alpha = backgroundAlpha * 0.4f)    // Dark slate edge
                    ),
                    radius = 1800f, // Adjust based on your screen size
                    center = Offset(0.3f, 0.2f) // Slightly off-center for more dynamic look
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale)
                    .rotate(rotation * 0.3f) // Slower rotation for logo
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)) // Optional background
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lokachakralogo3),
                    contentDescription = "LOKACHAKRA Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Animated App Name
            Text(
                text = "LOKACHAKRA",
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            Box(
                modifier = Modifier.alpha(textAlpha)
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Floating particles effect (optional decorative elements)
        repeat(5) { index ->
            val particleScale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000 + index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particleScale$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = ((-100..100).random()).dp,
                        y = ((-200..200).random()).dp
                    )
                    .size(8.dp)
                    .scale(particleScale)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}