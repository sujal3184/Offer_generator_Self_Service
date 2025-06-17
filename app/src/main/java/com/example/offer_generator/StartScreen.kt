package com.example.offer_generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOutSine
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navController: NavController,
    onRoleSelected: (String) -> Unit = {},
    viewModel: WhoLoginViewModel
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var headerVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
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

    // Function to update ViewModel based on selected role
    fun updateViewModelForRole(roleId: String) {
        // Reset all login states first
        viewModel.logoutAll()

        // Set the appropriate role to true
        when (roleId) {
            "intern" -> viewModel.setUserLoggedIn(true)
            "fulltime" -> viewModel.setFulltimeEmployeeLoggedIn(true)
            "freelancer" -> viewModel.setFreeLancerLoggedIn(true)
            "hr" -> viewModel.setHrLoggedIn(true)
            "admin" -> viewModel.setAdminLoggedIn(true)
        }
        navController.navigate(Screen.HomeScreen.route)
    }

    LaunchedEffect(Unit) {
        delay(300)
        headerVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center){
                        Text(
                            text = "Lokachakra   ",
                            color = ProfessionalTheme.OnPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.topbarbackgound)
                ),
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.4f),
                    ambientColor = ProfessionalTheme.BlueShadowLight
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfessionalTheme.White)
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

                // Enhanced Animated Header Section
                AnimatedVisibility(
                    visible = headerVisible,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(800, easing = EaseOutCubic))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.3f),
                                ambientColor = ProfessionalTheme.BlueShadowLight
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.Lightpurple)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Icon Section
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = CircleShape,
                                        spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.3f)
                                    )
                                    .background(
                                        color = Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = ProfessionalTheme.PurpleBorder,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.lokachakralogo3),
                                    contentDescription = "Welcome",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .rotate(rotation)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Text Section
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Welcome to lokachakra",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Select your role to get started",
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Role Selection Grid
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
                            delay = 0,
                            selectedcolor = role.color,
                            unselectedcolor = role.color.copy(alpha = 0.5f),
                            onRoleClick = {
                                selectedRole = if (selectedRole == role.id) null else role.id
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom section with white background
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ProfessionalTheme.White
                ) {
                    AnimatedActionSection(
                        selectedRole = selectedRole,
                        onContinue = {
                            selectedRole?.let { roleId ->
                                // Update ViewModel first
                                updateViewModelForRole(roleId)
                                // Then call the original callback
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
    unselectedcolor : Color,
    onRoleClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.05f
            isPressed -> 0.98f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 16f else 6f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "elevation"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(600, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .scale(scale)
                .shadow(
                    elevation = elevation.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = ProfessionalTheme.BlueShadowLight,
                    spotColor = ProfessionalTheme.BlueShadow.copy(alpha = if (isSelected) 0.4f else 0.2f)
                )
                .border(
                    width = if (isSelected) 3.dp else 2.dp,
                    color = if (isSelected) {
                        selectedcolor
                    } else {
                        unselectedcolor
                    },
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPressed = true
                    onRoleClick()
                },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = ProfessionalTheme.White
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Selection Indicator
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset((-12).dp, 12.dp)
                            .size(28.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.5f)
                            )
                            .background(
                                color = selectedcolor,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = ProfessionalTheme.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = ProfessionalTheme.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Professional Icon Container
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                spotColor = ProfessionalTheme.BlueShadow.copy(alpha = 0.3f)
                            )
                            .background(
                                color = ProfessionalTheme.White,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected) {
                                    selectedcolor
                                } else {
                                    unselectedcolor
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = role.icon,
                            contentDescription = role.title,
                            tint = if (isSelected) selectedcolor else unselectedcolor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = role.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) selectedcolor else ProfessionalTheme.OnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = role.description,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ProfessionalTheme.OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
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
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfessionalTheme.White)
                .padding(16.dp)
        ) {
            // Enhanced Animated Action Button
            Button(
                onClick = onContinue,
                enabled = selectedRole != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole != null) {
                        buttonColor // Animated purple color
                    } else {
                        Color.DarkGray// Light gray when disabled
                    },
                    contentColor = if (selectedRole != null) {
                        ProfessionalTheme.White
                    } else {
                        Color.Black// Gray text when disabled
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = if (selectedRole != null) 12.dp else 3.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = if (selectedRole != null) {
                            ProfessionalTheme.BlueShadow.copy(alpha = 0.4f)
                        } else {
                            Color.Gray.copy(alpha = 0.2f)
                        },
                        ambientColor = ProfessionalTheme.BlueShadowLight
                    ),
                border = if (selectedRole != null) {
                    BorderStroke(
                        width = 1.dp,
                        color = ProfessionalTheme.Purple.copy(alpha = 0.3f)
                    )
                } else null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (selectedRole != null) "Continue" else "Select a Role to Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedRole == null) Color.DarkGray else Color.White
                    )
                    if (selectedRole != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Continue",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
