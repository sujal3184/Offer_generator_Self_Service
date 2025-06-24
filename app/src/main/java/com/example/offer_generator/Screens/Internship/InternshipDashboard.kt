package com.example.offer_generator.Screens.Internship

import OfferLetter
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
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
import androidx.core.net.toUri
import firebase.com.protolitewrapper.BuildConfig
import java.io.File
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InternDashboard(navController: NavController, whoLoginViewModel: WhoLoginViewModel) {
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
fun EnhancedApplicationsListScreen(navController: NavController, viewmodel: WhoLoginViewModel) {
    val applications = remember { InternshipDataManager.applications }

    // Use mutableStateOf to trigger recomposition when offer letters change
    var offerLetters by remember { mutableStateOf(OfferLetterDataManager.offerLetters) }
    val statistics = remember { InternshipDataManager.getApplicationStatistics() }

    var selectedApplication by remember { mutableStateOf<InternshipApplication?>(null) }
    var selectedOffer by remember { mutableStateOf<OfferLetter?>(null) }
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
                    },
                    viewmodel
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
                // Update the local state to trigger recomposition
                offerLetters = OfferLetterDataManager.offerLetters.toList()
                selectedOffer = null
            },
            onDecline = {
                OfferLetterDataManager.updateOfferStatus(offer.id, OfferStatus.REJECTED)
                // Update the local state to trigger recomposition
                offerLetters = OfferLetterDataManager.offerLetters.toList()
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
    // FIXED: Use applications parameter instead of InternshipDataManager.applications
    var filteredApplications by remember { mutableStateOf(applications) }

    // Update filteredApplications when applications change
    LaunchedEffect(applications) {
        filteredApplications = applications
    }

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
            FilterButton(
                onFilterApplied = { criteria ->
                    // FIXED: Apply filter to visible applications instead of all applications
                    filteredApplications = applications.applyFilters(criteria)
                },
                modifier = Modifier.padding(0.dp) // Removed extra padding
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

    // Applications List - FIXED: Use filteredApplications instead of applications
    filteredApplications.forEach { application ->
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
    val context = LocalContext.current

    // Function to open PDF with default viewer
    fun openPDFWithDefaultViewer() {
        try {

            var intent: Intent? = null

            when {
                // Priority 1: Content URI (from document picker)
                application.cvUri.toString().isNotEmpty() && application.cvUri.toString().startsWith("content://") -> {
                    try {
                        val uri = Uri.parse(application.cvUri.toString())
                        intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                    } catch (e: Exception) {
                        Log.e("PDF_DEBUG", "Error with content URI", e)
                    }
                }

                // Priority 2: File path exists
                application.cvFilePath.isNotEmpty() && File(application.cvFilePath).exists() -> {
                    try {
                        val file = File(application.cvFilePath)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                    } catch (e: Exception) {
                        Log.e("PDF_DEBUG", "Error with file path", e)
                    }
                }

                // Priority 3: File URI
                application.cvUri.toString().isNotEmpty() && application.cvUri.toString().startsWith("file://") -> {
                    try {
                        val filePath = Uri.parse(application.cvUri.toString()).path
                        if (filePath != null && File(filePath).exists()) {
                            val file = File(filePath)
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("PDF_DEBUG", "Error with file URI", e)
                    }
                }

                // Priority 4: Try to find file in app directories
                application.cvFileName.isNotEmpty() -> {
                    try {
                        // Check internal files directory
                        val internalFile = File(context.filesDir, application.cvFileName)
                        val externalFile = File(context.getExternalFilesDir(null), application.cvFileName)

                        val targetFile = when {
                            internalFile.exists() -> internalFile
                            externalFile.exists() -> externalFile
                            else -> null
                        }

                        targetFile?.let { file ->
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            Log.d("PDF_DEBUG", "Found file in app directory: ${file.absolutePath}")
                        }
                    } catch (e: Exception) {
                        Log.e("PDF_DEBUG", "Error searching app directories", e)
                    }
                }
            }

            // Launch the intent if we have one
            intent?.let {
                try {
                    // Check if there's an app that can handle this intent
                    val resolveInfo = context.packageManager.queryIntentActivities(it, 0)
                    if (resolveInfo.isNotEmpty()) {
                        context.startActivity(it)
                    } else {
                        // Try with a chooser
                        val chooserIntent = Intent.createChooser(it, "Open PDF with...")
                        context.startActivity(chooserIntent)
                    }
                } catch (e: Exception) {
                    Log.e("PDF_DEBUG", "Error launching intent", e)
                    Toast.makeText(context, "Error opening PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(context, "Unable to locate PDF file", Toast.LENGTH_SHORT).show()
                Log.e("PDF_DEBUG", "No valid intent created")
            }

        } catch (e: Exception) {
            Log.e("PDF_DEBUG", "General error opening PDF", e)
            Toast.makeText(context, "Error opening PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    DetailSection(
        title = "Documents",
        icon = Icons.Default.Description
    ) {
        // CV Section
        DocumentItem(
            fileName = application.cvFileName,
            displayName = "CV/Resume",
            isAvailable = application.cvFileName.isNotEmpty() ||
                    application.cvUri.toString().isNotEmpty() ||
                    application.cvFilePath.isNotEmpty(),
            onClick = {
                if (application.cvFileName.isNotEmpty() ||
                    application.cvUri.toString().isNotEmpty() ||
                    application.cvFilePath.isNotEmpty()) {
                    openPDFWithDefaultViewer()
                } else {
                    Toast.makeText(context, "No CV uploaded", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Add spacing between documents
        Spacer(modifier = Modifier.height(8.dp))


        // Debug information (remove in production)
        if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Debug Info:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                "CV URI: ${application.cvUri}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "CV Path: ${application.cvFilePath}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "CV Name: ${application.cvFileName}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun DocumentItem(
    fileName: String,
    displayName: String,
    isAvailable: Boolean,
    isWebLink: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isAvailable) Color.Transparent
                else Color.Gray.copy(alpha = 0.1f)
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                if (isWebLink) Icons.Default.Link else Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = if (isAvailable) {
                    if (isWebLink) colorResource(id = R.color.blue) else Color.Red
                } else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isAvailable) Color.Black else Color.Gray
                )
                if (isAvailable && fileName.isNotEmpty() && fileName != displayName) {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (isAvailable) {
            Icon(
                Icons.Default.Visibility,
                contentDescription = if (isWebLink) "Open Link" else "View Document",
                tint = colorResource(id = R.color.purple),
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                "Not uploaded",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }

    if (isAvailable) {
        Text(
            text = if (isWebLink) "Tap to open link" else "Tap to view document",
            style = MaterialTheme.typography.bodySmall,
            color = colorResource(id = R.color.purple),
            modifier = Modifier.padding(start = 28.dp, top = 2.dp)
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
                    },
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
@Composable
fun FilterButton(
    onFilterApplied: (FilterCriteria) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    // Enhanced Filter Button with better styling
    Button(
        onClick = { showFilterDialog = true },
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter Applications",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Filter",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onApplyFilter = { criteria ->
                onFilterApplied(criteria)
                showFilterDialog = false
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilter: (FilterCriteria) -> Unit
) {
    var selectedStatus by remember { mutableStateOf<ApplicationStatus?>(null) }
    var collegeFilter by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("") }
    var dateFromFilter by remember { mutableStateOf("") }
    var dateToFilter by remember { mutableStateOf("") }

    // Date picker states
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    // Date picker dialogs
    if (showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            onDateSelected = { date ->
                dateFromFilter = date
                showFromDatePicker = false
            }
        )
    }

    if (showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            onDateSelected = { date ->
                dateToFilter = date
                showToDatePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        title = null,
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Custom header that spans full width
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF6A5ACD), // Purple
                                        Color(0xFF4169E1)  // Blue
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter Applications",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    // Content area
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Status Filter
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8F9FA)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Application Status",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF6A5ACD),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        item {
                                            FilterChip(
                                                selected = selectedStatus == null,
                                                onClick = { selectedStatus = null },
                                                label = { Text("All") },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF6A5ACD),
                                                    selectedLabelColor = Color.White,
                                                    containerColor = Color.White,
                                                    labelColor = Color(0xFF6A5ACD)
                                                )
                                            )
                                        }
                                        items(ApplicationStatus.values()) { status ->
                                            FilterChip(
                                                selected = selectedStatus == status,
                                                onClick = {
                                                    selectedStatus =
                                                        if (selectedStatus == status) null else status
                                                },
                                                label = {
                                                    Text(
                                                        text = status.name.replace("_", " ")
                                                            .lowercase()
                                                            .replaceFirstChar { it.uppercase() }
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF4169E1),
                                                    selectedLabelColor = Color.White,
                                                    containerColor = Color.White,
                                                    labelColor = Color(0xFF4169E1)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // College Filter
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8F9FA)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "College",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF6A5ACD),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    OutlinedTextField(
                                        value = collegeFilter,
                                        onValueChange = { collegeFilter = it },
                                        placeholder = {
                                            Text(
                                                "Enter college name...",
                                                color = Color.Gray
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6A5ACD),
                                            unfocusedBorderColor = Color(0xFF6A5ACD).copy(alpha = 0.5f),
                                            cursorColor = Color(0xFF6A5ACD),
                                            focusedTextColor = Color(0xFF2E7D32), // Dark Green for entered text
                                            unfocusedTextColor = Color(0xFF2E7D32), // Dark Green for entered text
                                        ),
                                        textStyle = LocalTextStyle.current.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = "College",
                                                tint = Color(0xFF6A5ACD)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Role Filter
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8F9FA)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Internship Role",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF4169E1),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    OutlinedTextField(
                                        value = roleFilter,
                                        onValueChange = { roleFilter = it },
                                        placeholder = {
                                            Text(
                                                "Enter role...",
                                                color = Color.Gray
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF4169E1),
                                            unfocusedBorderColor = Color(0xFF4169E1).copy(alpha = 0.5f),
                                            cursorColor = Color(0xFF4169E1),
                                            focusedTextColor = Color(0xFFD32F2F), // Red for entered text
                                            unfocusedTextColor = Color(0xFFD32F2F), // Red for entered text
                                        ),
                                        textStyle = LocalTextStyle.current.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Work,
                                                contentDescription = "Role",
                                                tint = Color(0xFF4169E1)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Date Range Filter
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8F9FA)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Submission Date Range",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6A5ACD),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // From Date Field
                                OutlinedTextField(
                                    value = dateFromFilter,
                                    onValueChange = { }, // Read-only
                                    label = {
                                        Text(
                                            "From Date",
                                            color = Color(0xFF6A5ACD),
                                            fontSize = 14.sp
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            "Select start date (DD/MM/YYYY)",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showFromDatePicker = true },
                                    readOnly = true,
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF6A5ACD),
                                        unfocusedBorderColor = Color(0xFF6A5ACD).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF6A5ACD),
                                        unfocusedLabelColor = Color(0xFF6A5ACD).copy(alpha = 0.7f),
                                        cursorColor = Color(0xFF6A5ACD),
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                    ),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "From Date",
                                            tint = Color(0xFF6A5ACD)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { showFromDatePicker = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = "Open Calendar",
                                                tint = Color(0xFF6A5ACD)
                                            )
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // To Date Field
                                OutlinedTextField(
                                    value = dateToFilter,
                                    onValueChange = { }, // Read-only
                                    label = {
                                        Text(
                                            "To Date",
                                            color = Color(0xFF4169E1),
                                            fontSize = 14.sp
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            "Select end date (DD/MM/YYYY)",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showToDatePicker = true },
                                    readOnly = true,
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4169E1),
                                        unfocusedBorderColor = Color(0xFF4169E1).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF4169E1),
                                        unfocusedLabelColor = Color(0xFF4169E1).copy(alpha = 0.7f),
                                        cursorColor = Color(0xFF4169E1),
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                    ),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "To Date",
                                            tint = Color(0xFF4169E1)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { showToDatePicker = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = "Open Calendar",
                                                tint = Color(0xFF4169E1)
                                            )
                                        }
                                    }
                                )
                            }
                            }
                        }
                    }

                    // Action buttons at the bottom
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Clear all filters
                                selectedStatus = null
                                collegeFilter = ""
                                roleFilter = ""
                                dateFromFilter = ""
                                dateToFilter = ""
                                onApplyFilter(FilterCriteria())
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6A5ACD)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF6A5ACD)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear All", fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                onApplyFilter(
                                    FilterCriteria(
                                        status = selectedStatus,
                                        college = collegeFilter.takeIf { it.isNotBlank() },
                                        role = roleFilter.takeIf { it.isNotBlank() },
                                        dateFrom = dateFromFilter.takeIf { it.isNotBlank() },
                                        dateTo = dateToFilter.takeIf { it.isNotBlank() }
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4169E1),
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Apply Filter", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = { onDismissRequest },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val date = Date(millis)
                        onDateSelected(formatter.format(date))
                    }
                }
            ) {
                Text("OK", color = Color(0xFF6A5ACD), fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                headlineContentColor = Color.Black,
                weekdayContentColor = Color.Black,
                subheadContentColor = Color.Black,
                yearContentColor = Color.Black,
                currentYearContentColor = Color.Black,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Color.Black,
                dayContentColor = Color.Black,
                disabledDayContentColor = Color.Black.copy(alpha = 0.3f),
                selectedDayContentColor = Color.White,
                disabledSelectedDayContentColor = Color.White.copy(alpha = 0.3f),
                selectedDayContainerColor = Color.Black,
                disabledSelectedDayContainerColor = Color.Black.copy(alpha = 0.3f),
                todayContentColor = Color.Black,
                todayDateBorderColor = Color.Black,
                navigationContentColor = Color.Black,
                dividerColor = Color.Black.copy(alpha = 0.1f),
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.DarkGray,
                    errorIndicatorColor = Color.Red,
                    errorTextColor = Color.Black,
                    errorContainerColor = Color.White,
                    cursorColor = Color.Black
                )
            )
        )
    }
}
// Data class to hold filter criteria
data class FilterCriteria(
    val status: ApplicationStatus? = null,
    val college: String? = null,
    val role: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null
)

// Extension function to apply filters to your applications list
fun List<InternshipApplication>.applyFilters(criteria: FilterCriteria): List<InternshipApplication> {
    return this.filter { application ->
        val statusMatch = criteria.status?.let { application.status == it } ?: true
        val collegeMatch = criteria.college?.let {
            application.college.contains(it, ignoreCase = true)
        } ?: true
        val roleMatch = criteria.role?.let {
            application.internshipRole.contains(it, ignoreCase = true)
        } ?: true

        // Basic date filtering (you might want to improve this with proper date parsing)
        val dateFromMatch = criteria.dateFrom?.let { fromDate ->
            application.submissionDate >= fromDate
        } ?: true
        val dateToMatch = criteria.dateTo?.let { toDate ->
            application.submissionDate <= toDate
        } ?: true

        statusMatch && collegeMatch && roleMatch && dateFromMatch && dateToMatch
    }
}
