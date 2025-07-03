package com.example.offer_generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import kotlinx.coroutines.delay

object ProfessionalTheme {
    // Purple Colors for buttons and accents
    val Purple = Color(0xFF9C27B0)              // Purple
    val PurpleLight = Color(0xFFBA68C8)         // Light purple
    val PurpleDark = Color(0xFF7B1FA2)          // Dark purple

    // Blue for shadows
    val BlueShadow = Color(0xFF2196F3)          // Blue for shadows
    val BlueShadowLight = Color(0x1A2196F3)     // Light blue shadow

    // White backgrounds
    val White = Color(0xFFFFFFFF)               // Pure white
    val Background = Color(0xFFFFFFFF)          // White background
    val Surface = Color(0xFFFFFFFF)             // White surface

    // Text Colors
    val OnSurface = Color(0xFF212121)           // Dark text
    val OnSurfaceVariant = Color(0xFF757575)    // Medium gray text
    val OnPrimary = Color(0xFFFFFFFF)           // White text

    // Border Colors
    val PurpleBorder = Color(0xFF9C27B0)        // Purple border
    val PurpleBorderLight = Color(0x4D9C27B0)   // Light purple border

    // Additional Professional Colors
    val Blue = Color(0xFF1976D2)                // Professional blue
    val BlueLight = Color(0xFF64B5F6)           // Light blue
    val Green = Color(0xFF388E3C)               // Professional green
    val GreenLight = Color(0xFF81C784)          // Light green
    val Orange = Color(0xFFFF8F00)              // Professional orange
    val OrangeLight = Color(0xFFFFB74D)         // Light orange
    val Teal = Color(0xFF00695C)                // Professional teal
    val TealLight = Color(0xFF4DB6AC)           // Light teal
    val Indigo = Color(0xFF303F9F)              // Professional indigo
    val IndigoLight = Color(0xFF7986CB)         // Light indigo
}

data class UserRole(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val gradientColors: List<Color>
)

// Update the StartScreen composable - only change the updateViewModelForRole function and the onContinue callback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navController: NavController,
    onRoleSelected: (String) -> Unit = {},
    viewModel: WhoLoginViewModel
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var headerVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")

    // Logo rotation animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Top bar gradient animation
    val topBarColorAnimation by infiniteTransition.animateColor(
        initialValue = ProfessionalTheme.Purple,
        targetValue = ProfessionalTheme.PurpleLight,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "topBarColor"
    )

    // Button animation - color cycling
    val buttonColorAnimation by infiniteTransition.animateColor(
        initialValue = colorResource(R.color.topbarbackgound),
        targetValue = colorResource(R.color.Lightpurple),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonColor"
    )

    // Floating animation for welcome card
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Pulsing animation for selected role indicator
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val roles = listOf(
        UserRole(
            id = "intern",
            title = "Intern",
            description = "Apply for internship opportunities and accelerate your career growth with mentorship",
            icon = Icons.Default.School,
            color = ProfessionalTheme.Blue,
            gradientColors = listOf(ProfessionalTheme.Blue, ProfessionalTheme.BlueLight)
        ),
        UserRole(
            id = "fulltime",
            title = "Full Time Employee",
            description = "Access full-time positions and advance your professional journey with comprehensive benefits",
            icon = Icons.Default.Work,
            color = ProfessionalTheme.Green,
            gradientColors = listOf(ProfessionalTheme.Green, ProfessionalTheme.GreenLight)
        ),
        UserRole(
            id = "freelancer",
            title = "Freelancer",
            description = "Browse freelance projects and enjoy flexible work opportunities with competitive rates",
            icon = Icons.Default.Computer,
            color = ProfessionalTheme.Orange,
            gradientColors = listOf(ProfessionalTheme.Orange, ProfessionalTheme.OrangeLight)
        ),
        UserRole(
            id = "hr",
            title = "HR Manager",
            description = "Manage applications, generate offers and build exceptional teams efficiently",
            icon = Icons.Default.Group,
            color = ProfessionalTheme.Teal,
            gradientColors = listOf(ProfessionalTheme.Teal, ProfessionalTheme.TealLight)
        )
    )

    fun updateViewModelForRole(roleId: String) {
        viewModel.setSelectedRole(roleId)
        navController.navigate(Screen.HomeScreen.route)
    }

    LaunchedEffect(Unit) {
        delay(200)
        headerVisible = true
        delay(400)
        cardsVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Animated title with gradient effect
                        Text(
                            text = "Lokachakra",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.topbarbackgound)
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.6f),
                        ambientColor = ProfessionalTheme.BlueShadowLight
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                topBarColorAnimation,
                                topBarColorAnimation.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ProfessionalTheme.White,
                            ProfessionalTheme.Background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        selectedRole = null
                    }
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Enhanced Animated Header Section with floating effect
                AnimatedVisibility(
                    visible = headerVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it * 2 },
                        animationSpec = tween(1000, easing = EaseOutBounce)
                    ) + fadeIn(animationSpec = tween(800, easing = EaseOutCubic)) +
                            scaleIn(
                                initialScale = 0.8f,
                                animationSpec = tween(800, easing = EaseOutBack)
                            )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = floatingOffset.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.4f),
                                ambientColor = ProfessionalTheme.BlueShadowLight
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.Lightpurple)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            colorResource(R.color.topbarbackgound),
                                            colorResource(R.color.Lightpurple),
                                            ProfessionalTheme.Purple
                                        ),
                                        start = Offset.Zero,
                                        end = Offset.Infinite
                                    )
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                // Enhanced Icon Section with pulsing effect
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .shadow(
                                            elevation = 12.dp,
                                            shape = CircleShape,
                                            spotColor = Color.White.copy(alpha = 0.5f)
                                        )
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    ProfessionalTheme.White,
                                                    ProfessionalTheme.Background
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    ProfessionalTheme.White.copy(alpha = 0.8f),
                                                    ProfessionalTheme.White.copy(alpha = 0.4f)
                                                )
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.lokachakralogo3),
                                        contentDescription = "Welcome",
                                        modifier = Modifier
                                            .size(55.dp)
                                            .clip(CircleShape)
                                            .rotate(rotation)
                                            .scale(
                                                animateFloatAsState(
                                                    targetValue = if (headerVisible) 1f else 0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    ),
                                                    label = "logoScale"
                                                ).value
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.width(20.dp))

                                // Enhanced Text Section with staggered animation
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Welcome to Lokachakra",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProfessionalTheme.White,
                                        modifier = Modifier.animateContentSize()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Select your role to get started",
                                        fontSize = 15.sp,
                                        color = ProfessionalTheme.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Enhanced Role Selection Grid with staggered animations
                AnimatedVisibility(
                    visible = cardsVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + fadeIn(animationSpec = tween(400))
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(roles) { index, role ->
                            AnimatedRoleCard(
                                role = role,
                                isSelected = selectedRole == role.id,
                                delay = index * 150L,
                                selectedcolor = role.color,
                                unselectedcolor = role.color.copy(alpha = 0.7f),
                                pulseScale = if (selectedRole == role.id) pulseScale else 1f,
                                onRoleClick = {
                                    selectedRole = if (selectedRole == role.id) null else role.id
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom section with enhanced animation
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    AnimatedActionSection(
                        selectedRole = selectedRole,
                        onContinue = {
                            selectedRole?.let { roleId ->
                                updateViewModelForRole(roleId)
                                onRoleSelected(roleId)
                            }
                        },
                        delay = 1200L,
                        buttonColor = buttonColorAnimation
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AnimatedRoleCard(
    role: UserRole,
    isSelected: Boolean,
    delay: Long,
    selectedcolor: Color,
    unselectedcolor: Color,
    pulseScale: Float = 1f,
    onRoleClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    // Add delay before showing the card
    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.005f * pulseScale
            isPressed -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 20f else 8f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "elevation"
    )

    val borderWidth by animateFloatAsState(
        targetValue = if (isSelected) 3f else 1.5f,
        animationSpec = tween(300),
        label = "borderWidth"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it * 2 },
            animationSpec = tween<IntOffset>(600, easing = EaseOutBack)
        ) + fadeIn(
            animationSpec = tween<Float>(600, easing = EaseOutBack)
        ) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween<Float>(600, easing = EaseOutBack)
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .scale(scale)
                .shadow(
                    elevation = elevation.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = selectedcolor.copy(alpha = 0.2f),
                    spotColor = selectedcolor.copy(alpha = if (isSelected) 0.5f else 0.3f)
                )
                .border(
                    width = borderWidth.dp,
                    brush = if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(
                                selectedcolor,
                                selectedcolor.copy(alpha = 0.7f),
                                selectedcolor
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                unselectedcolor,
                                unselectedcolor.copy(alpha = 0.5f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPressed = true
                    onRoleClick()
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (isSelected) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    selectedcolor.copy(alpha = 0.05f),
                                    Color.White
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    ProfessionalTheme.White,
                                    ProfessionalTheme.Background
                                )
                            )
                        }
                    )
            ) {
                // Enhanced Selection Indicator with animation
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset((-12).dp, 12.dp)
                            .size(32.dp)
                            .scale(pulseScale)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                spotColor = selectedcolor.copy(alpha = 0.6f)
                            )
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        selectedcolor,
                                        selectedcolor.copy(alpha = 0.8f)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = ProfessionalTheme.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Enhanced Professional Icon Container
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(
                                elevation = if (isSelected) 8.dp else 4.dp,
                                shape = CircleShape,
                                spotColor = selectedcolor.copy(alpha = 0.4f)
                            )
                            .background(
                                brush = if (isSelected) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White,
                                            selectedcolor.copy(alpha = 0.1f)
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            ProfessionalTheme.White,
                                            ProfessionalTheme.Background
                                        )
                                    )
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 2.dp,
                                brush = if (isSelected) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            selectedcolor,
                                            selectedcolor.copy(alpha = 0.6f)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            unselectedcolor,
                                            unselectedcolor.copy(alpha = 0.4f)
                                        )
                                    )
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = role.icon,
                            contentDescription = role.title,
                            tint = if (isSelected) selectedcolor else unselectedcolor,
                            modifier = Modifier
                                .size(30.dp)
                                .scale(
                                    animateFloatAsState(
                                        targetValue = if (isSelected) 1.1f else 1f,
                                        animationSpec = spring(dampingRatio = 0.6f),
                                        label = "iconScale"
                                    ).value
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = role.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) selectedcolor else ProfessionalTheme.OnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = role.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ProfessionalTheme.OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun AnimatedActionSection(
    selectedRole: String?,
    onContinue: () -> Unit,
    delay: Long,
    buttonColor: Color
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(800, easing = EaseOutBack)
        ) + fadeIn(animationSpec = tween(800)) +
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(800, easing = EaseOutBack)
                )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(16.dp)
        ) {
            // Enhanced Animated Action Button
            Button(
                onClick = onContinue,
                enabled = selectedRole != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole != null) {
                        Color.Transparent
                    } else {
                        ProfessionalTheme.OnSurfaceVariant.copy(alpha = 0.6f)
                    },
                    contentColor = if (selectedRole != null) {
                        ProfessionalTheme.White
                    } else {
                        ProfessionalTheme.OnSurface
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (selectedRole != null) 16.dp else 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = if (selectedRole != null) {
                            buttonColor.copy(alpha = 0.5f)
                        } else {
                            Color.Gray.copy(alpha = 0.2f)
                        },
                        ambientColor = if (selectedRole != null) {
                            buttonColor.copy(alpha = 0.3f)
                        } else {
                            Color.Gray.copy(alpha = 0.1f)
                        }
                    )
                    .background(
                        brush = if (selectedRole != null) {
                            Brush.linearGradient(
                                colors = listOf(
                                    buttonColor,
                                    buttonColor.copy(alpha = 0.8f),
                                    buttonColor
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    ProfessionalTheme.OnSurfaceVariant.copy(alpha = 0.6f),
                                    ProfessionalTheme.OnSurfaceVariant.copy(alpha = 0.8f)
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = if (selectedRole != null) 1.dp else 0.dp,
                        brush = if (selectedRole != null) {
                            Brush.linearGradient(
                                colors = listOf(
                                    ProfessionalTheme.White.copy(alpha = 0.3f),
                                    ProfessionalTheme.White.copy(alpha = 0.1f)
                                )
                            )
                        } else {
                            Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (selectedRole != null) "Continue" else "Select a Role to Continue",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedRole == null) ProfessionalTheme.OnSurface else ProfessionalTheme.White
                    )
                    if (selectedRole != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Continue",
                            modifier = Modifier
                                .size(22.dp)
                                .scale(
                                    animateFloatAsState(
                                        targetValue = if (selectedRole != null) 1f else 0f,
                                        animationSpec = spring(dampingRatio = 0.6f),
                                        label = "arrowScale"
                                    ).value
                                ),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}