package com.example.offer_generator.Screens.Internship

import OfferLetter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.offer_generator.common.AnimatedButton
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.Screens.ApplicationForm.FormData
import com.example.offer_generator.Screens.OfferLetters.NoOfferLetters
import com.example.offer_generator.Screens.OfferLetters.OfferLetterDetailDialog
import com.example.offer_generator.Screens.OfferLetters.OfferLettersContent
import com.example.offer_generator.common.bottomBar
import kotlinx.coroutines.delay

@Composable
fun CandidateDashboard(navController: NavController, whoLoginViewModel: WhoLoginViewModel) {
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
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
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
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(whoLoginViewModel.isUserLoggedIn.value){
                            Text(
                                "My Applications",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Track your internship applications",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        else if(whoLoginViewModel.isHrLoggedIn.value){
                            Text(
                                "Applications",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Track the internship applications",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                    }
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                ) {
                    EnhancedApplicationsListScreen(navController,whoLoginViewModel)
                }

                bottomBar(navController , whoLoginViewModel)
            }
        }
    }
}

@Composable
fun EnhancedApplicationsListScreen(navController: NavController,viewmodel: WhoLoginViewModel) {
    val applications = remember { InternshipDataManager.applications }
    val offerLetters = remember { OfferLetterDataManager.offerLetters} // You'll need to add this to your data manager
    val statistics = remember { InternshipDataManager.getApplicationStatistics() }

    var selectedApplication by remember { mutableStateOf<InternshipApplication?>(null) }
    var selectedOffer by remember { mutableStateOf<OfferLetter?>(null) } // Create OfferLetter data class
    var showingApplications by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Toggle Button
        ToggleSection(
            showingApplications = showingApplications,
            onToggle = { showingApplications = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val offerstatistics by remember { derivedStateOf { OfferLetterDataManager.getOfferStatistics() } }
        if (showingApplications) {
            // Applications Section
            if (applications.isEmpty()) {
                NoApplication(navController = navController, viewmodel)
            } else {
                ApplicationsContent(
                    applications = applications,
                    statistics = statistics,
                    onViewDetails = { selectedApplication = it },
                    navController = navController,
                    viewModel = viewmodel
                )
            }
        } else {
            // Offer Letters Section
            if (offerLetters.isEmpty()) {
                NoOfferLetters(viewmodel)
            } else {
                OfferLettersContent(
                    offerLetters = offerLetters,
                    statistics = offerstatistics,
                    onViewDetails = { offer ->
                        selectedOffer = offer
                    }
                )
            }
        }
    }

    // Detail Dialogs
    selectedApplication?.let { application ->
        ApplicationDetailDialog(
            application = application,
            onDismiss = { selectedApplication = null },
            navController = navController,
            viewModel = viewmodel
        )
    }

    // Show offer details dialog
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
fun ApplicationsContent(
    applications: List<InternshipApplication>,
    statistics: ApplicationStatistics,
    onViewDetails: (InternshipApplication) -> Unit,
    navController: NavController,
    viewModel : WhoLoginViewModel
) {
    // Statistics Cards
    Text(
        "Application Overview",
        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = Color.DarkGray
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if(viewModel.isUserLoggedIn.value){
            EnhancedStatCard("Total", statistics.total.toString(), Icons.Default.Assignment, Color(0xFF6C63FF),viewModel)
            EnhancedStatCard("Pending", statistics.submitted.toString(), Icons.Default.HourglassEmpty, Color(0xFFFF9500),viewModel)
            EnhancedStatCard("Accepted", statistics.accepted.toString(), Icons.Default.CheckCircle, Color(0xFF4CAF50),viewModel)
        }
       else{
           Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
               Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween){
                   EnhancedStatCard("Total", statistics.total.toString(), Icons.Default.Assignment, Color(0xFF6C63FF),viewModel,150.dp)
                   EnhancedStatCard("Pending", statistics.submitted.toString(), Icons.Default.HourglassEmpty, Color(0xFFFF9500),viewModel,150.dp)
               }
               Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween){
                   EnhancedStatCard("Accepted", statistics.accepted.toString(), Icons.Default.CheckCircle, Color(0xFF4CAF50),viewModel,150.dp)
                   EnhancedStatCard("Offer generated", statistics.accepted.toString(), Icons.Default.CheckCircle, color = colorResource(R.color.purple_200),viewModel,150.dp)
               }
           }


        }

    }

    Spacer(modifier = Modifier.height(32.dp))

    // Applications Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Your Applications",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Gray.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            androidx.compose.material3.Icon(
                Icons.Default.List,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${applications.size} Applications",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Applications List Header
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Role",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Status",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center
            )
            Text(
                "Date",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center
            )
            Text(
                "Detail",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Applications List
    applications.forEach { application ->
        EnhancedApplicationCard(
            application = application,
            onViewDetails = onViewDetails
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    if(viewModel.isUserLoggedIn.value){
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.width(250.dp).padding(16.dp)) {
                AnimatedButton(
                    onclick = {
                        resetForm(formData = FormData())
                        navController.navigate(Screen.ApplicationForm.route)
                    },
                    text = "Apply For New Internship",
                    delay = 400,
                    fontsize = 16.sp,
                    filled = true
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }

}


@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f)
        )
    }
}
@Composable
fun EnhancedStatCard(title: String, value: String, icon: ImageVector, color: Color, viewModel: WhoLoginViewModel = viewModel(), width : Dp = 100.dp) {
    Card(
        modifier = Modifier
            .width(width)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun EnhancedApplicationCard(
    application: InternshipApplication,
    onViewDetails: (InternshipApplication) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Role
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = application.internshipRole,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status
            Box(
                modifier = Modifier
                    .weight(1.5f),
                contentAlignment = Alignment.Center
            ) {
                StatusChip(status = application.status)
            }

            // Date
            Column(
                modifier = Modifier.weight(1.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dateParts = application.submissionDate.split(" ")[0].split("/")
                Text(
                    text = "${dateParts[0]}/${dateParts[1]}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = dateParts[2],
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }

            // View Details Icon
            androidx.compose.material3.IconButton(
                onClick = { onViewDetails(application) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.purple).copy(alpha = 0.1f))
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.Visibility,
                    contentDescription = "View Details",
                    tint = colorResource(id = R.color.purple),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: ApplicationStatus) {
    val (backgroundColor, textColor) = when (status) {
        ApplicationStatus.SUBMITTED -> Color(0xFFFF9500).copy(alpha = 0.1f) to Color(0xFFFF9500)
        ApplicationStatus.UNDER_REVIEW -> Color(0xFF2196F3).copy(alpha = 0.1f) to Color(0xFF2196F3)
        ApplicationStatus.ACCEPTED -> Color(0xFF4CAF50).copy(alpha = 0.1f) to Color(0xFF4CAF50)
        ApplicationStatus.REJECTED -> Color(0xFFF44336).copy(alpha = 0.1f) to Color(0xFFF44336)
        ApplicationStatus.WITHDRAWN -> Color(0xFF9E9E9E).copy(alpha = 0.1f) to Color(0xFF9E9E9E)
        ApplicationStatus.OFFER_GENERATED -> Color(0xFF9E9E9E).copy(alpha = 0.1f) to Color(0xFF9E9E9E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name.replace("_", " "),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailDialog(
    application: InternshipApplication,
    navController: NavController, // Add NavController parameter
    onDismiss: () -> Unit,
    viewModel: WhoLoginViewModel
) {

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Column {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.purple),
                                    colorResource(id = R.color.blue)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Application Details",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                application.internshipRole,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        DetailSection(
                            title = "Personal Information",
                            icon = Icons.Default.Person
                        ) {
                            DetailRow("Full Name", application.fullName)
                            DetailRow("Date of Birth", application.dateOfBirth)
                            DetailRow("Year of Study", application.yearOfStudy)
                            DetailRow("College", application.college)
                            DetailRow("Branch", application.branch)
                        }
                    }

                    item {
                        DetailSection(
                            title = "Internship Details",
                            icon = Icons.Default.Work
                        ) {
                            DetailRow("Role", application.internshipRole)
                            DetailRow("Available From", application.availableFrom)
                            DetailRow("Available Until", application.availableUntil)
                            DetailRow("Status", application.status.name.replace("_", " "))
                            DetailRow("Submission Date", application.submissionDate)
                        }
                    }

                    item {
                        DetailSection(
                            title = "Skills",
                            icon = Icons.Default.Star
                        ) {
                            Text(
                                application.skills.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                    }

                    item {
                        DetailSection(
                            title = "Contact Information",
                            icon = Icons.Default.Phone
                        ) {
                            DetailRow("Mobile", application.mobileNumber)
                            DetailRow("Email", application.email)
                            DetailRow("LinkedIn", application.linkedinProfile)
                            DetailRow("GitHub", application.githubLink)
                            if (application.portfolioWebsite.isNotEmpty()) {
                                DetailRow("Portfolio", application.portfolioWebsite)
                            }
                        }
                    }

                    item {
                        DocumentsSection(application)
                    }

                    if (application.personalStatement.isNotEmpty()) {
                        item {
                            DetailSection(
                                title = "Personal Statement",
                                icon = Icons.Default.Notes
                            ) {
                                Text(
                                    application.personalStatement,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun DocumentsSection(application: InternshipApplication) {
    var showCVViewer by remember { mutableStateOf(false) }

    DetailSection(
        title = "Documents",
        icon = Icons.Default.Description
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (application.cvFileName.isNotEmpty()) {
                        showCVViewer = true
                    }
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    application.cvFileName.ifEmpty { "CV not uploaded" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (application.cvFileName.isNotEmpty()) Color.Black else Color.Gray
                )
            }

            if (application.cvFileName.isNotEmpty()) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "View CV",
                    tint = colorResource(id = R.color.purple),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (application.cvFileName.isNotEmpty()) {
            Text(
                "Tap to view CV",
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(id = R.color.purple),
                modifier = Modifier.padding(start = 28.dp)
            )
        }
    }

    // Show CV Viewer Dialog
    if (showCVViewer) {
        CVViewerDialog(
            cvUri = application.cvUri,
            cvFilePath = application.cvFilePath,
            cvFileName = application.cvFileName,
            onDismiss = { showCVViewer = false }
        )
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(
                    icon,
                    contentDescription = null,
                    tint = colorResource(id = R.color.purple),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}


@Composable
fun NoApplication(navController: NavController,viewModel : WhoLoginViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = colorResource(id = R.color.blue).copy(alpha = 0.9f),
                spotColor = colorResource(id = R.color.blue).copy(alpha = 0.9f)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white),
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Applications Yet",
                    style = androidx.compose.material.MaterialTheme.typography.h5,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when {
                        viewModel.isUserLoggedIn.value -> "You haven't applied for any internship yet"
                        viewModel.isHrLoggedIn.value -> "Nobody has applied for any internship yet"
                        else -> ""
                    }
                    ,
                    style = androidx.compose.material.MaterialTheme.typography.body2,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )

                if(viewModel.isUserLoggedIn.value){
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Get started by applying for an internship opportunity",
                        style = androidx.compose.material.MaterialTheme.typography.body1,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(Modifier.width(120.dp)) {
                    if(viewModel.isUserLoggedIn.value){
                        AnimatedButton(
                            onclick = {
                                resetForm(formData = FormData())
                                navController.navigate(Screen.ApplicationForm.route)
                            },
                            text = "Apply Now",
                            delay = 500,
                            filled = true,
                            fontsize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// Utility function to reset form
fun resetForm(formData: FormData) {
    formData.fullName = ""
    formData.dateOfBirth = ""
    formData.yearOfStudy = ""
    formData.college = ""
    formData.branch = ""
    formData.yearsOfExperience = ""
    formData.currentLocation = ""
    formData.availabilityStatus = ""
    formData.availableFrom = ""
    formData.availableUntil = ""
    formData.internshipRole = ""
    formData.skillsList.clear()
    formData.cvFileName = ""
    formData.cvUri = null
    formData.cvFilePath = ""
    formData.projectStartDate = ""
    formData.projectEndDate = ""
    formData.serviceCategory = ""
    formData.portfolioFileName = ""
    formData.portfolioFileUri = null
    formData.portfolioFilePath = ""
    formData.mobileNumber = ""
    formData.email = ""
    formData.linkedinProfile = ""
    formData.githubLink = ""
    formData.portfolioWebsite = ""
    formData.references = ""
    formData.personalStatement = ""
    formData.clientReferences = ""
    formData.professionalSummary = ""
}