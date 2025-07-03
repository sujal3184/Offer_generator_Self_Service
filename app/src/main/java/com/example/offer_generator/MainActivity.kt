package com.example.offer_generator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.offer_generator.Navigation.Navigation
import com.example.offer_generator.ui.theme.Offer_generatorTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.lerp

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
                            delay(5000) // 5 second splash screen
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
    var showSecondaryElements by remember { mutableStateOf(false) }

    // Professional color palette
    val primaryBlue = Color(0xFF0F172A)      // Slate 900
    val secondaryBlue = Color(0xFF1E293B)    // Slate 800
    val accentBlue = Color(0xFF3B82F6)       // Blue 500
    val lightBlue = Color(0xFF60A5FA)        // Blue 400
    val softWhite = Color(0xFFF8FAFC)        // Slate 50
    val mutedWhite = Color(0xFFE2E8F0)       // Slate 200
    val glowColor = Color(0xFF06B6D4)        // Cyan 500

    // Multiple infinite transitions for complex animations
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val secondaryTransition = rememberInfiniteTransition(label = "secondaryTransition")

    // Logo entrance animation with sophisticated easing
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logoScale"
    )

    // Logo fade-in animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = 300,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        ),
        label = "logoAlpha"
    )

    // Logo rotation animation - increased speed
    val logoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "logoRotation"
    )

    // Logo pulse animation
    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    // Logo glow animation
    val logoGlow by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoGlow"
    )

    // Text slide-up animation
    val textOffsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 80f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = 800,
            easing = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f)
        ),
        label = "textOffsetY"
    )

    // Text fade-in animation
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "textAlpha"
    )

    // Text scale animation for emphasis
    val textScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textScale"
    )

    // Subtitle slide animation
    val subtitleOffsetX by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -100f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 1200,
            easing = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
        ),
        label = "subtitleOffsetX"
    )

    // Loading indicator animation
    val loadingAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "loadingAlpha"
    )

    // Loading scale animation
    val loadingScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingScale"
    )

    // Dynamic gradient animation
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    // Wave animation for background
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    // Ripple effect animation
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale"
    )

    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha"
    )

    // Additional wave animations for layered effects
    val secondWaveOffset by secondaryTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondWaveOffset"
    )

    // Spiral animation
    val spiralRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spiralRotation"
    )

    // Zoom pulse for dramatic effect
    val zoomPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoomPulse"
    )

    // Color shift animation
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorShift"
    )

    // Orbit animation for decorative elements
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation"
    )

    // Morphing animation for background shapes
    val morphOffset by secondaryTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morphOffset"
    )

    // Start animations when composable launches
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1000)
        showSecondaryElements = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(zoomPulse) // Apply zoom pulse to entire screen
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryBlue,
                        secondaryBlue.copy(alpha = 0.9f + morphOffset * 0.1f),
                        Color(0xFF334155).copy(alpha = 0.8f + morphOffset * 0.2f),
                        secondaryBlue.copy(alpha = 0.9f + morphOffset * 0.1f),
                        primaryBlue
                    ),
                    start = Offset(
                        sin(waveOffset) * 500f,
                        cos(waveOffset) * 300f
                    ),
                    end = Offset(
                        cos(secondWaveOffset) * 800f,
                        sin(secondWaveOffset) * 600f
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // Wave-based ripple effects using waveOffset
        repeat(4) { index ->
            val waveRadius = 100f + index * 50f
            val waveX = cos(waveOffset + index * PI.toFloat() / 2) * 80f
            val waveY = sin(waveOffset + index * PI.toFloat() / 2) * 60f

            Box(
                modifier = Modifier
                    .offset(x = waveX.dp, y = waveY.dp)
                    .size((120 + index * 30).dp)
                    .scale(rippleScale * 0.3f)
                    .alpha(rippleAlpha * 0.4f * logoAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                lerp(
                                    accentBlue,
                                    glowColor,
                                    colorShift
                                ).copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Spiral pattern using spiralRotation
        repeat(8) { index ->
            val spiralAngle = spiralRotation + index * 45f
            val spiralRadius = 180f + sin(waveOffset + index) * 40f
            val spiralX = cos(Math.toRadians(spiralAngle.toDouble())) * spiralRadius
            val spiralY = sin(Math.toRadians(spiralAngle.toDouble())) * spiralRadius

            Box(
                modifier = Modifier
                    .offset(x = spiralX.dp, y = spiralY.dp)
                    .size((8 + index * 2).dp)
                    .alpha(0.6f * logoAlpha * (0.5f + sin(waveOffset * 2 + index) * 0.5f))
                    .background(
                        lerp(lightBlue, glowColor, colorShift),
                        shape = CircleShape
                    )
            )
        }

        // Animated ripple effects behind logo
        repeat(3) { index ->
            val delayedRippleScale by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 2f + index * 0.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000,
                        delayMillis = index * 600,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "delayedRipple$index"
            )

            val delayedRippleAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000,
                        delayMillis = index * 600,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "delayedRippleAlpha$index"
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(delayedRippleScale)
                    .alpha(delayedRippleAlpha * logoAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                accentBlue.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Morphing background shapes with wave integration
        repeat(4) { index ->
            val shapeRotation = orbitRotation + index * 90f
            val waveInfluence = sin(waveOffset + index * PI.toFloat() / 2) * 30f
            val shapeScale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + index * 800,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "shapeScale$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(Math.toRadians(shapeRotation.toDouble())) * (150f + waveInfluence)).dp,
                        y = (sin(Math.toRadians(shapeRotation.toDouble())) * (150f + waveInfluence)).dp
                    )
                    .size((80 + index * 20).dp)
                    .scale(shapeScale * 0.3f * (1f + rippleScale * 0.1f))
                    .rotate(shapeRotation * 0.5f + waveOffset * 10f)
                    .alpha(0.08f + morphOffset * 0.04f + rippleAlpha * 0.02f)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                lerp(glowColor, accentBlue, colorShift).copy(alpha = 0.4f),
                                lerp(accentBlue, lightBlue, colorShift).copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            start = Offset(sin(waveOffset) * 100f, cos(waveOffset) * 100f),
                            end = Offset(cos(waveOffset) * 100f, sin(waveOffset) * 100f)
                        ),
                        shape = RoundedCornerShape((16 + sin(waveOffset + index) * 8).dp)
                    )
            )
        }

        // Orbiting particles around logo
        repeat(6) { index ->
            val orbitRadius = 120f + index * 15f
            val particleRotation = orbitRotation * (1f + index * 0.1f) + index * 60f

            val particlePulse by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500 + index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particlePulse$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(Math.toRadians(particleRotation.toDouble())) * orbitRadius).dp,
                        y = (sin(Math.toRadians(particleRotation.toDouble())) * orbitRadius).dp
                    )
                    .size((6 + index * 2).dp)
                    .scale(particlePulse)
                    .alpha(0.6f * logoAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (index % 2 == 0) lightBlue else glowColor,
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Enhanced Logo Container with wave-influenced effects
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(logoScale * logoPulse * (1f + sin(waveOffset) * 0.02f))
                    .alpha(logoAlpha)
                    .rotate(logoRotation * 0.2f + sin(waveOffset) * 5f)
            ) {
                // Dynamic ripple effect using defined animations
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(rippleScale)
                        .alpha(rippleAlpha * 0.3f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    lerp(glowColor, accentBlue, colorShift).copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Multiple glow layers with wave influence
                repeat(3) { glowIndex ->
                    val waveGlow = sin(waveOffset * (2f + glowIndex)) * 0.1f + 0.9f
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1f + glowIndex * 0.1f + sin(secondWaveOffset + glowIndex) * 0.05f)
                            .alpha(logoGlow * (0.3f - glowIndex * 0.1f) * waveGlow)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        lerp(glowColor, lightBlue, colorShift).copy(alpha = 0.4f),
                                        lerp(accentBlue, glowColor, colorShift).copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                // Logo background with wave-animated border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    softWhite.copy(alpha = 0.15f + sin(waveOffset) * 0.05f),
                                    softWhite.copy(alpha = 0.05f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = (3f + sin(waveOffset * 2) * 1f).dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    lerp(glowColor, accentBlue, colorShift),
                                    lerp(accentBlue, lightBlue, colorShift),
                                    lerp(lightBlue, glowColor, colorShift),
                                    lerp(glowColor, accentBlue, colorShift)
                                ),
                                center = Offset(
                                    0.5f + sin(waveOffset) * 0.1f,
                                    0.5f + cos(waveOffset) * 0.1f
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lokachakralogo3),
                        contentDescription = "LOKACHAKRA Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .clip(CircleShape)
                            .rotate(sin(waveOffset * 0.5f) * 3f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(72.dp))

            // Enhanced App Name with multiple effects
            Text(
                text = "LOKACHAKRA",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = softWhite,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .offset(y = textOffsetY.dp)
                    .alpha(textAlpha)
                    .scale(textScale)
                    .drawWithContent {
                        drawContent()
                    },
                style = TextStyle(
                    shadow = Shadow(
                        color = glowColor.copy(alpha = 0.8f),
                        offset = Offset(0f, 6f),
                        blurRadius = 12f
                    ),
                    brush = Brush.linearGradient(
                        colors = listOf(
                            softWhite,
                            mutedWhite,
                            lightBlue.copy(alpha = 0.9f)
                        )
                    )
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Animated subtitle with slide effect
            Text(
                text = "Connecting Communities",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = mutedWhite,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .offset(
                        x = subtitleOffsetX.dp,
                        y = (textOffsetY * 0.3f).dp
                    )
                    .alpha(textAlpha * 0.9f)
                    .scale(if (showSecondaryElements) 1f else 0.8f),
                style = TextStyle(
                    shadow = Shadow(
                        color = accentBlue.copy(alpha = 0.4f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                )
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Multi-layered loading indicator
            Box(
                modifier = Modifier
                    .alpha(loadingAlpha)
                    .scale(loadingScale),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating ring
                CircularProgressIndicator(
                    color = glowColor.copy(alpha = 0.4f),
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(64.dp)
                        .rotate(orbitRotation * 0.5f)
                )
                // Middle ring
                CircularProgressIndicator(
                    color = accentBlue,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(-orbitRotation * 0.3f)
                )
                // Inner pulsing dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(logoPulse * 0.8f)
                        .background(
                            lightBlue,
                            shape = CircleShape
                        )
                )
            }
        }

        // Advanced floating elements with wave-based complex paths
        repeat(16) { index ->
            val elementSize = (3..10).random().dp
            val animationDelay = index * 200
            val baseRadius = 200f + index * 25f

            // Complex wave-based movement
            val waveX by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 2 * PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 6000 + animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "waveX$index"
            )

            val waveY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 2 * PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 8000 + animationDelay * 2,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "waveY$index"
            )

            val elementAlpha by infiniteTransition.animateFloat(
                initialValue = 0.1f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000 + animationDelay,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "elementAlpha$index"
            )

            // Calculate position using multiple wave functions
            val posX = sin(waveX + waveOffset) * baseRadius * 0.4f +
                    cos(waveY + secondWaveOffset) * baseRadius * 0.2f
            val posY = cos(waveX + waveOffset) * baseRadius * 0.3f +
                    sin(waveY + secondWaveOffset) * baseRadius * 0.25f

            Box(
                modifier = Modifier
                    .offset(x = posX.dp, y = posY.dp)
                    .size(elementSize)
                    .alpha(elementAlpha * if (showSecondaryElements) 1f else 0f)
                    .rotate(waveX * 57.3f + waveY * 28.6f) // Convert radians to degrees
                    .scale(
                        logoPulse * 0.8f *
                                (1f + sin(waveOffset * 3 + index) * 0.3f) *
                                (1f + rippleScale * 0.1f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                lerp(
                                    when (index % 4) {
                                        0 -> glowColor
                                        1 -> lightBlue
                                        2 -> accentBlue
                                        else -> lerp(glowColor, lightBlue, 0.5f)
                                    },
                                    when (index % 3) {
                                        0 -> accentBlue
                                        1 -> glowColor
                                        else -> lightBlue
                                    },
                                    colorShift
                                ),
                                Color.Transparent
                            )
                        ),
                        shape = when (index % 5) {
                            0 -> CircleShape
                            1 -> RoundedCornerShape(1.dp)
                            2 -> RoundedCornerShape(50)
                            3 -> RoundedCornerShape(2.dp)
                            else -> RoundedCornerShape((sin(waveOffset + index) * 5 + 5).dp)
                        }
                    )
            )
        }
    }
}