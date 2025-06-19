package com.example.offer_generator.Screens.HR

import OfferLetter
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.Screens.Internship.ApplicationStatistics
import com.example.offer_generator.Screens.Internship.ApplicationStatus
import com.example.offer_generator.Screens.Internship.CVViewerDialog
import com.example.offer_generator.Screens.Internship.DocumentsSection
import com.example.offer_generator.Screens.Internship.InternshipApplication
import com.example.offer_generator.Screens.Internship.StatusChip
import com.example.offer_generator.Screens.Internship.ToggleSection
import com.example.offer_generator.Screens.OfferLetters.NoOfferLetters
import com.example.offer_generator.Screens.OfferLetters.OfferLettersContent
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.common.AnimatedButton

@Composable
fun InternSectionContent(
    applications: List<InternshipApplication>,
    offerLetters: List<OfferLetter>,
    statistics: ApplicationStatistics, // Replace with actual statistics type
    onViewApplicationDetails: (InternshipApplication) -> Unit,
    onViewOfferDetails: (OfferLetter) -> Unit,
    navController: NavController,
    viewModel: WhoLoginViewModel
) {
    var showingApplications by remember { mutableStateOf(true) }

    Column {
        // Applications vs Offer Letters Toggle
        ToggleSection(
            showingApplications = showingApplications,
            onToggle = { showingApplications = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (showingApplications) {
            // Show Applications
            if (applications.isEmpty()) {
                NoApplication(navController = navController, viewModel, "intern")
            } else {
                InternApplicationsContent(
                    applications = applications,
                    statistics = statistics,
                    onViewDetails = onViewApplicationDetails,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        } else {
            // Show Offer Letters
            if (offerLetters.isEmpty()) {
                NoOfferLetters(viewModel)
            } else {
                val offerStatistics = remember {
                    OfferLetterDataManager.getOfferStatistics().copy(
                        // Filter statistics for intern offers only
                    )
                }
                OfferLettersContent(
                    offerLetters = offerLetters,
                    statistics = offerStatistics,
                    onViewDetails = onViewOfferDetails,
                    viewModel
                )
            }
        }
    }
}

@Composable
fun InternApplicationsContent(
    applications: List<InternshipApplication>,
    statistics: ApplicationStatistics,
    onViewDetails: (InternshipApplication) -> Unit,
    navController: NavController,
    viewModel: WhoLoginViewModel
) {
    // Statistics Cards
    Text(
        "Internship Overview",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = Color.DarkGray
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                EnhancedStatCard("Total", statistics.total.toString(), Icons.Default.Assignment, Color(0xFF6C63FF), viewModel, 150.dp)
                EnhancedStatCard("Pending", statistics.submitted.toString(), Icons.Default.HourglassEmpty, Color(0xFFFF9500), viewModel, 150.dp)
            }
            Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                EnhancedStatCard("Accepted", statistics.accepted.toString(), Icons.Default.CheckCircle, Color(0xFF4CAF50), viewModel, 150.dp)
                EnhancedStatCard("Offers", statistics.accepted.toString(), Icons.Default.CheckCircle, color = colorResource(
                    R.color.purple_200), viewModel, 150.dp)
            }
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Applications List
    ApplicationsList(
        title = "Internship Applications",
        applications = applications,
        onViewDetails = onViewDetails
    )
}

@Composable
fun ApplicationsList(
    title: String,
    applications: List<InternshipApplication>,
    onViewDetails: (InternshipApplication) -> Unit
) {
    // Applications Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Gray.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                Icons.Default.List,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${applications.size} Applications",
                style = MaterialTheme.typography.bodySmall,
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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Status",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center
            )
            Text(
                "Date",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center
            )
            Text(
                "Detail",
                style = MaterialTheme.typography.bodyMedium,
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status
            Box(
                modifier = Modifier.weight(1.5f),
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = dateParts[2],
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }

            // View Details Icon
            IconButton(
                onClick = { onViewDetails(application) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.purple).copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "View Details",
                    tint = colorResource(id = R.color.purple),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailDialog(
    application: InternshipApplication,
    navController: NavController,
    onDismiss: () -> Unit,
    viewModel: WhoLoginViewModel
) {
    var showCVViewer by remember { mutableStateOf(false) }

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
                                "Internship Application",
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
//                            DetailRow("Education Level", application.educationLevel)
                        }
                    }

                    item {
                        DetailSection(
                            title = "Application Details",
                            icon = Icons.Default.Work
                        ) {
                            DetailRow("Role", application.internshipRole)
//                            DetailRow("Department", application.department)
//                            DetailRow("Duration", application.duration)
                            DetailRow("Status", application.status.name)
                        }
                    }

                    item {
                        DetailSection(
                            title = "Contact Information",
                            icon = Icons.Default.Phone
                        ) {
                            DetailRow("Email", application.email)
                            DetailRow("Phone", application.mobileNumber)
//                            DetailRow("Address", application.address)
                        }
                    }

                    item {
                        DetailSection(
                            title = "Academic Information",
                            icon = Icons.Default.Star
                        ) {
                            DetailRow("Institution", application.college)
                            DetailRow("Field of Study", application.branch)
//                            DetailRow("GPA", application.gpa.toString())
//                            DetailRow("Graduation Year", application.graduationYear.toString())
                        }
                    }

                    item {
                        DetailSection(
                            title = "Application Info",
                            icon = Icons.Default.Description
                        ) {
                            DetailRow("Submission Date", application.submissionDate)
                            DetailRow("Skills", application.skills.toString())
//                            DetailRow("Motivation", application.motivation)
                        }
                    }

                    item {
                        DocumentsSection(application)
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))

                        InternAcceptRejectButtons(
                            application = application,
                            navController = navController,
                            onDismiss = onDismiss
                        )
                    }
                }
            }
        }
    }

    if (showCVViewer) {
        DocumentsSection(application)
    }
}
@Composable
fun InternAcceptRejectButtons(
    application: InternshipApplication,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var showOfferLetterButton by remember { mutableStateOf(application.status == ApplicationStatus.ACCEPTED) }
    var applicationStatus by remember { mutableStateOf(application.status) }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Application Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Check if application is already processed
            when (applicationStatus) {
                ApplicationStatus.ACCEPTED -> {
                    // Show Generate Offer Letter button after acceptance
                    Column {
                        Text(
                            text = "Application Accepted ✓",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Green,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            AnimatedButton(onclick = {
                                navController.navigate("offer_generator_screen/${application.id}/internship")
                                                     onDismiss()},
                                text = "Generate Offer Letter", delay = 300, filled = true)
                        }
                    }
                }

                ApplicationStatus.REJECTED -> {
                    // Show rejection status
                    Text(
                        text = "Application Rejected ✗",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }

                else -> {
                    // Show Accept and Reject buttons for pending applications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedButton(
                                onclick = {
                                    applicationStatus = ApplicationStatus.REJECTED
                                    application.status = ApplicationStatus.REJECTED
                                    // You might want to persist this change to your data source here
                                    onDismiss()
                                    navController.navigate(Screen.HrDashboard.route)
                                },
                                text = "Reject",
                                delay = 300,
                                filled = true,
                                color = Color(0xFFE53E3E)
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedButton(
                                onclick = {
                                    applicationStatus = ApplicationStatus.ACCEPTED
                                    application.status = ApplicationStatus.ACCEPTED
                                    showOfferLetterButton = true
                                    // You might want to persist this change to your data source here
                                },
                                text = "Accept",
                                delay = 300,
                                filled = true,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}