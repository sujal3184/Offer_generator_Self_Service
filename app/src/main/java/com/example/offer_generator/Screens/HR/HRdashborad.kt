package com.example.offer_generator.Screens.HR

import OfferLetter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.Screens.Freelancer.FreelancerApplication
import com.example.offer_generator.Screens.Freelancer.FreelancerDataManager
import com.example.offer_generator.Screens.FulltimeJob.FullTimeApplication
import com.example.offer_generator.Screens.FulltimeJob.FullTimeDatamanger
import com.example.offer_generator.Screens.Internship.InternshipApplication
import com.example.offer_generator.Screens.Internship.InternshipDataManager
import com.example.offer_generator.Screens.OfferLetters.OfferLetterDetailDialog
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.common.bottomBar
import kotlinx.coroutines.delay

enum class UserType(val displayName: String, val icon: ImageVector, val color: Color) {
    INTERN("Interns", Icons.Default.School, Color(0xFF1976D2)),
    FREELANCER("Freelancers", Icons.Default.Person, Color(0xFFFF8F00)  ),
    FULLTIME("Full-time", Icons.Default.Business, Color(0xFF388E3C)  )
}

@Composable
fun HRDashboard(navController: NavController, whoLoginViewModel: WhoLoginViewModel) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    AppNavigationDrawer(
        navController = navController,
        currentScreen = Screen.ApplicationForm.route,
        onBackPressed = { navController.popBackStack() },
        whoLoginViewModel = whoLoginViewModel
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Enhanced Header with gradient and better typography
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.purple),
                                    colorResource(id = R.color.blue)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Work,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "HR Dashboard",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Manage applications across all departments",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                ) {
                    EnhancedApplicationsListScreen(navController, whoLoginViewModel)
                }

                bottomBar(navController, whoLoginViewModel)
            }
        }
    }
}

@Composable
fun EnhancedApplicationsListScreen(navController: NavController, viewmodel: WhoLoginViewModel) {
    val userType = viewmodel.getCurrentUserType()

    val internApplications = remember { InternshipDataManager.applications }
    val freelancerApplications = remember { FreelancerDataManager.applications }
    val fulltimeApplications = remember { FullTimeDatamanger.applications }
    val internOfferLetters = remember {
        OfferLetterDataManager.getInternOfferLetters()
    }
    val freelancerOfferLetters = remember {
        OfferLetterDataManager.getFreelancerOfferLetters()
    }
    val fulltimeOfferLetters = remember {
        OfferLetterDataManager.getFullTimeOfferLetters()
    }

    val internStatistics = remember { InternshipDataManager.getApplicationStatistics() }
    val freelancerStatistics = remember { FreelancerDataManager.getApplicationStatistics() }
    val fulltimeStatistics = remember { FullTimeDatamanger.getApplicationStatistics() }

    var selectedInternApplication by remember { mutableStateOf<InternshipApplication?>(null) }
    var selectedFreelancerApplication by remember { mutableStateOf<FreelancerApplication?>(null) }
    var selectedFulltimeApplication by remember { mutableStateOf<FullTimeApplication?>(null) }
    var selectedOffer by remember { mutableStateOf<OfferLetter?>(null) }
    var selectedUserType by remember { mutableStateOf(UserType.INTERN) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Enhanced User Type Toggle
        EnhancedUserTypeSelector(
            selectedUserType = selectedUserType,
            onUserTypeSelected = { selectedUserType = it },
            internApplicationCount = internApplications.size,
            internOfferCount = internOfferLetters.size,
            freelancerApplicationCount = freelancerApplications.size,
            freelancerOfferCount = freelancerOfferLetters.size,
            fulltimeApplicationCount = fulltimeApplications.size,
            fulltimeOfferCount = fulltimeOfferLetters.size
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Content based on selected user type
        when (selectedUserType) {
            UserType.INTERN -> {
                InternSectionContent(
                    applications = internApplications,
                    offerLetters = internOfferLetters,
                    statistics = internStatistics,
                    onViewApplicationDetails = { selectedInternApplication = it },
                    onViewOfferDetails = { selectedOffer = it },
                    navController = navController,
                    viewModel = viewmodel
                )
            }
            UserType.FREELANCER -> {
                FreelancerSectionContent(
                    applications = freelancerApplications,
                    offerLetters = freelancerOfferLetters,
                    statistics = freelancerStatistics,
                    onViewApplicationDetails = { selectedFreelancerApplication = it },
                    onViewOfferDetails = { selectedOffer = it },
                    navController = navController,
                    viewModel = viewmodel
                )
            }
            UserType.FULLTIME -> {
                FulltimeSectionContent(
                    applications = fulltimeApplications,
                    offerLetters = fulltimeOfferLetters,
                    statistics = fulltimeStatistics,
                    onViewApplicationDetails = { selectedFulltimeApplication = it },
                    onViewOfferDetails = { selectedOffer = it },
                    navController = navController,
                    viewModel = viewmodel
                )
            }
        }
    }

    // Detail Dialogs
    selectedInternApplication?.let { application ->
        ApplicationDetailDialog(
            application = application,
            onDismiss = { selectedInternApplication = null },
            navController = navController,
            viewModel = viewmodel
        )
    }

    selectedFreelancerApplication?.let { application ->
        FreelancerApplicationDetailDialog(
            application = application,
            onDismiss = { selectedFreelancerApplication = null },
            navController = navController,
            viewModel = viewmodel
        )
    }

    selectedFulltimeApplication?.let { application ->
        FulltimeApplicationDetailDialog(
            application = application,
            onDismiss = { selectedFulltimeApplication = null },
            navController = navController,
            viewModel = viewmodel
        )
    }

    selectedOffer?.let { offer ->
        OfferLetterDetailDialog(
            offer = offer,
            onDismiss = { selectedOffer = null },
            onAccept = {
                OfferLetterDataManager.updateOfferStatus(offer.id, OfferStatus.ACCEPTED)
                selectedOffer = null
            },
            onDecline = {
                OfferLetterDataManager.updateOfferStatus(offer.id, OfferStatus.REJECTED)
                selectedOffer = null
            },
            viewModel = viewmodel
        )
    }
}

@Composable
fun EnhancedUserTypeSelector(
    selectedUserType: UserType,
    onUserTypeSelected: (UserType) -> Unit,
    internApplicationCount: Int,
    internOfferCount: Int,
    freelancerApplicationCount: Int,
    freelancerOfferCount: Int,
    fulltimeApplicationCount: Int,
    fulltimeOfferCount: Int,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UserType.values().forEach { userType ->
                val isSelected = selectedUserType == userType
                val (appCount, offerCount) = when (userType) {
                    UserType.INTERN -> internApplicationCount to internOfferCount
                    UserType.FREELANCER -> freelancerApplicationCount to freelancerOfferCount
                    UserType.FULLTIME -> fulltimeApplicationCount to fulltimeOfferCount
                }

                EnhancedUserTypeTab(
                    userType = userType,
                    isSelected = isSelected,
                    applicationCount = appCount,
                    offerCount = offerCount,
                    onClick = { onUserTypeSelected(userType) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EnhancedUserTypeTab(
    userType: UserType,
    isSelected: Boolean,
    applicationCount: Int,
    offerCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            userType.color.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColor"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) {
            userType.color
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    Surface(
        modifier = modifier
            .scale(animatedScale)
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = animatedBackgroundColor,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, animatedBorderColor)
        } else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            // Icon with animation
            Icon(
                imageVector = userType.icon,
                contentDescription = null,
                tint = if (isSelected) userType.color else Color.Gray,
                modifier = Modifier
                    .size(28.dp)
                    .scale(if (isSelected) 1.1f else 1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User type name
            Text(
                text = userType.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) userType.color else Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

        }
    }
}


@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = colorResource(id = R.color.purple),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.purple)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun EnhancedStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    viewModel: WhoLoginViewModel,
    width: Dp = 140.dp
) {
    Card(
        modifier = Modifier
            .width(width)
            .height(100.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NoApplication(
    navController: NavController,
    viewModel: WhoLoginViewModel,
    type: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Assignment,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "No ${type.capitalize()} Applications",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            "Applications will appear here once submitted",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}