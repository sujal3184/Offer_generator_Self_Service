package com.example.offer_generator.Screens.OfferLetters

import OfferLetter
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.R
import com.example.offer_generator.Screens.Internship.*
import com.example.offer_generator.Screens.Freelancer.*
import com.example.offer_generator.Screens.FulltimeJob.FullTimeDatamanger
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Enum to identify application type
enum class ApplicationType {
    INTERNSHIP, FREELANCER, FULLTIME
}

// Enum for offer letter templates
enum class OfferLetterTemplate(val displayName: String) {
    FORMAL("Formal & Traditional"),
    MODERN("Modern & Professional"),
    TECH("Tech-Focused")
}

// Generic data class to hold common application properties
data class GenericApplication(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val branch: String? = null,
    val skills: List<String>,
    val availableFrom: String,
    val availableUntil: String? = "",
    val type: ApplicationType
)

// Template generator class
// Minimized Template Generator Class - One Page Max
object OfferTemplateGenerator {

    fun generateOfferContent(
        template: OfferLetterTemplate,
        application: GenericApplication,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String?,
        location: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String
    ): String {
        return when (template) {
            OfferLetterTemplate.FORMAL -> generateFormalTemplate(
                application, companyName, position, salary, startDate, endDate, location, benefits, terms, validUntil, generatedBy
            )
            OfferLetterTemplate.MODERN -> generateModernTemplate(
                application, companyName, position, salary, startDate, endDate, location, benefits, terms, validUntil, generatedBy
            )
            OfferLetterTemplate.TECH -> generateTechTemplate(
                application, companyName, position, salary, startDate, endDate, location, benefits, terms, validUntil, generatedBy
            )
        }
    }

    private fun generateFormalTemplate(
        application: GenericApplication,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String?,
        location: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String
    ): String {
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val positionType = when (application.type) {
            ApplicationType.INTERNSHIP -> "Internship"
            ApplicationType.FREELANCER -> "Contract"
            ApplicationType.FULLTIME -> "Employment"
        }

        return """
            |$companyName
            |Date: $currentDate
            |
            |Dear ${application.fullName},
            |
            |OFFER OF $positionType - $position
            |
            |We are pleased to offer you the position of $position at $companyName.
            |
            |POSITION DETAILS:
            |Position: $position | Type: $positionType | Salary: $salary
            |Start: $startDate${if (endDate != null && application.type != ApplicationType.FULLTIME) " | End: $endDate" else ""} | Location: $location
            |
            |RESPONSIBILITIES: ${application.role} with expertise in ${application.skills.take(3).joinToString(", ")}.
            |
            |BENEFITS: ${benefits.split(",").take(4).joinToString(" • ") { it.trim() }}
            |
            |TERMS: $terms
            |
            |This offer is valid until $validUntil and subject to background verification.
            |
            |Please confirm acceptance by signing below.
            |
            |Sincerely,
            |$generatedBy, $companyName
            |
            |ACCEPTANCE:
            |I, ${application.fullName}, accept this offer.
            |Signature: _________________ Date: _________________
        """.trimMargin()
    }

    private fun generateModernTemplate(
        application: GenericApplication,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String?,
        location: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String
    ): String {
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val emoji = when (application.type) {
            ApplicationType.INTERNSHIP -> "🎓"
            ApplicationType.FREELANCER -> "💼"
            ApplicationType.FULLTIME -> "🚀"
        }

        return """
            |$emoji $companyName
            |$currentDate
            |
            |Hi ${application.fullName}!
            |
            |CONGRATULATIONS! We're excited to offer you the $position role!
            |
            |🎯 THE OPPORTUNITY
            |Role: $position | Compensation: $salary | Start: $startDate
            |${if (endDate != null && application.type != ApplicationType.FULLTIME) "Duration: Until $endDate | " else ""}Location: $location
            |
            |💪 WHAT YOU'LL DO
            |Join as our $position, leveraging your skills in ${application.skills.take(3).joinToString(", ")} to drive results.
            |
            |✨ WHAT WE OFFER
            |${benefits.split(",").take(4).joinToString(" • ") { it.trim() }}
            |
            |📋 TERMS
            |$terms
            |
            |Valid until: $validUntil
            |
            |Ready to join? Sign below and let's get started!
            |
            |Cheers,
            |$generatedBy
            |$companyName Team
            |
            |✅ ACCEPTANCE
            |I accept this offer: ${application.fullName}
            |Signature: _________________ Date: _________
        """.trimMargin()
    }

    private fun generateTechTemplate(
        application: GenericApplication,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String?,
        location: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String
    ): String {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return """
            |#!/usr/bin/offer
            |# $companyName Offer Letter v2.0
            |# Generated: $currentDate
            |
            |Hello ${application.fullName} 👋
            |
            |const offer = {
            |  candidate: "${application.fullName}",
            |  position: "$position",
            |  company: "$companyName",
            |  status: "PENDING_ACCEPTANCE"
            |};
            |
            |💻 CONFIG
            |Role: $position | Package: $salary | Start: $startDate
            |${if (endDate != null && application.type != ApplicationType.FULLTIME) "End: $endDate | " else ""}Environment: $location
            |Tech Stack: ${application.skills.take(4).joinToString(" | ")}
            |
            |🔧 FEATURES
            |${benefits.split(",").take(4).joinToString(" • ") { it.trim() }}
            |
            |📝 TERMS
            |$terms
            |
            |⏰ EXPIRES: $validUntil
            |
            |To accept:
            |```
            |git commit -m "Accept: $position"
            |git push origin main
            |```
            |
            |$generatedBy - Tech Lead @ $companyName
            |
            |// ACCEPTANCE
            |// Signed: ${application.fullName} | Date: ________
        """.trimMargin()
    }
}

@Composable
fun OfferLetterGenerator(
    navController: NavController,
    whoLoginViewModel: WhoLoginViewModel,
    applicationId: String? = null,
    applicationType: String? = null // "internship", "freelancer", "fulltime"
) {
    //    Column(Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
//        if(applicationId != null) Text("$applicationId and $applicationType")
//    }
    val application = remember(applicationId, applicationType) {
        applicationId?.let { id ->
            when (applicationType?.lowercase()) {
                "internship" -> {
                    InternshipDataManager.applications.find { it.id == id }?.let { app ->
                        GenericApplication(
                            id = app.id,
                            fullName = app.fullName,
                            email = app.email,
                            role = app.internshipRole,
                            branch = app.branch,
                            skills = app.skills,
                            availableFrom = app.availableFrom,
                            availableUntil = app.availableUntil,
                            type = ApplicationType.INTERNSHIP
                        )
                    }
                }
                "freelancer" -> {
                    FreelancerDataManager.applications.find { it.id == id }?.let { app ->
                        GenericApplication(
                            id = app.id,
                            fullName = app.fullName,
                            email = app.email,
                            role = app.serviceCategory,
                            skills = app.skills,
                            availableFrom = app.projectStartDate,
                            availableUntil = app.projectEndDate,
                            type = ApplicationType.FREELANCER
                        )
                    }
                }
                "fulltime" -> {
                    FullTimeDatamanger.applications.find { it.id == id }?.let { app ->
                        GenericApplication(
                            id = app.id,
                            fullName = app.fullName,
                            email = app.email,
                            role = app.jobRole,
                            branch = app.branch,
                            skills = app.skills,
                            availableFrom = app.availableFrom,
                            type = ApplicationType.FULLTIME
                        )
                    }
                }
                else -> null
            }
        }
    }


    var isVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedOffer by remember { mutableStateOf<OfferLetter?>(null) }

    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        isVisible = true
        delay(300)
        showContent = true
        // Show the generation dialog only if we have a valid application
        if (application != null) {
            showDialog = true
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    AppNavigationDrawer(
        navController = navController,
        currentScreen = "Offer Letter",
        onBackPressed = {
            navController.navigateUp()
        },
        whoLoginViewModel = whoLoginViewModel
    ) {
        when {
            // Show generation dialog first
            application != null && showDialog -> {
                OfferGenerationDialog(
                    application = application,
                    onDismiss = {
                        showDialog = false
                        navController.navigateUp()
                    },
                    onGenerate = { offerLetter ->
                        // Generate the offer letter using the appropriate method based on type
                        val generatedOfferLetter = when (application.type) {
                            ApplicationType.INTERNSHIP -> {
                                OfferLetterDataManager.generateInternOfferLetter(
                                    applicationId = offerLetter.applicationId,
                                    companyName = offerLetter.companyName,
                                    position = offerLetter.position,
                                    salary = offerLetter.salary,
                                    startDate = offerLetter.startDate,
                                    endDate = offerLetter.endDate ?: "",
                                    location = offerLetter.location,
                                    description = offerLetter.description,
                                    benefits = offerLetter.benefits,
                                    terms = offerLetter.terms,
                                    validUntil = offerLetter.validUntil,
                                    generatedBy = offerLetter.generatedBy
                                )
                            }
                            ApplicationType.FREELANCER -> {
                                OfferLetterDataManager.generateFreelancerOfferLetter(
                                    applicationId = offerLetter.applicationId,
                                    companyName = offerLetter.companyName,
                                    position = offerLetter.position,
                                    salary = offerLetter.salary,
                                    startDate = offerLetter.startDate,
                                    endDate = offerLetter.endDate ?: "",
                                    location = offerLetter.location,
                                    description = offerLetter.description,
                                    benefits = offerLetter.benefits,
                                    terms = offerLetter.terms,
                                    validUntil = offerLetter.validUntil,
                                    generatedBy = offerLetter.generatedBy
                                )
                            }
                            ApplicationType.FULLTIME -> {
                                offerLetter.endDate?.let {
                                    OfferLetterDataManager.generateFullTimeOfferLetter(
                                        applicationId = offerLetter.applicationId,
                                        companyName = offerLetter.companyName,
                                        position = offerLetter.position,
                                        salary = offerLetter.salary,
                                        startDate = offerLetter.startDate,
                                        endDate = it,
                                        location = offerLetter.location,
                                        description = offerLetter.description,
                                        benefits = offerLetter.benefits,
                                        terms = offerLetter.terms,
                                        validUntil = offerLetter.validUntil,
                                        generatedBy = offerLetter.generatedBy
                                    )
                                }
                            }
                        }

                        generatedOffer = generatedOfferLetter
                        showDialog = false
                        showSuccessDialog = true
                    }
                )
            }
            // Show success dialog after generation
            showSuccessDialog -> {
                OfferSuccessDialog(
                    offer = generatedOffer,
                    whoLoginViewModel = whoLoginViewModel,
                    onDismiss = {
                        showSuccessDialog = false
                        navController.navigateUp()
                    },
                    onViewOffers = {
                        showSuccessDialog = false
                        navController.navigate("candidate_offer_letters")
                    }
                )
            }
            // Show error state if no application found
            else -> {
                ErrorState(navController = navController)
            }
        }
    }
}

@Composable
private fun OfferSuccessDialog(
    offer: OfferLetter?,
    whoLoginViewModel: WhoLoginViewModel,
    onDismiss: () -> Unit,
    onViewOffers: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Offer Letter Generated!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "The offer letter has been successfully generated and sent to the candidate.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = Color(0xFF424242),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Show offer details if available
                offer?.let {
                    val typeIcon = when (it.type) {
                        OfferType.INTERN -> "🎓"
                        OfferType.FREELANCER -> "💼"
                        OfferType.FULLTIME -> "🏢"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "$typeIcon ${it.position} at ${it.companyName}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = "💰 ${it.salary}",
                                fontSize = 13.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "📅 Valid until: ${it.validUntil}",
                                fontSize = 12.sp,
                                color = Color(0xFF888888),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "The candidate can now view and respond to the offer in their dashboard.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = Color(0xFF757575)
                )
            }
        },
        confirmButton = {
            // Fixed logic: Show View Offers button only if user is NOT HR (candidates can view offers)
            if (!whoLoginViewModel.isHrLoggedIn.value) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewOffers,
                        border = BorderStroke(1.dp, colorResource(id = R.color.purple)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = colorResource(id = R.color.purple),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "View Offers",
                                color = colorResource(id = R.color.purple),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.purple)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        ),
                        modifier = Modifier
                            .height(44.dp)
                            .weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Done",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                // Show only a single "OK" button when user is HR
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.purple)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        ),
                        modifier = Modifier
                            .height(44.dp)
                            .widthIn(min = 120.dp)
                    ) {
                        Text(
                            text = "OK",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun ErrorState(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Application Data Found",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please select an application to generate offer letter",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigateUp() }
            ) {
                Text("Go Back")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferGenerationDialog(
    application: GenericApplication,
    onDismiss: () -> Unit,
    onGenerate: (OfferLetter) -> Unit
) {
    var selectedTemplate by remember { mutableStateOf(OfferLetterTemplate.FORMAL) }
    var companyName by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(application.role) }
    var salary by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(application.availableFrom) }
    var endDate by remember { mutableStateOf(application.availableUntil ?: "") }
    var location by remember { mutableStateOf("") }
    var benefits by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf("") }
    var validUntil by remember { mutableStateOf("") }
    var generatedBy by remember { mutableStateOf("HR Manager") }
    var showPreview by remember { mutableStateOf(false) }

    // Focus state tracking for text fields
    var companyNameFocused by remember { mutableStateOf(false) }
    var positionFocused by remember { mutableStateOf(false) }
    var salaryFocused by remember { mutableStateOf(false) }
    var startDateFocused by remember { mutableStateOf(false) }
    var endDateFocused by remember { mutableStateOf(false) }
    var locationFocused by remember { mutableStateOf(false) }
    var benefitsFocused by remember { mutableStateOf(false) }
    var termsFocused by remember { mutableStateOf(false) }
    var validUntilFocused by remember { mutableStateOf(false) }
    var generatedByFocused by remember { mutableStateOf(false) }

    // Set default values based on application type
    LaunchedEffect(application.type) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, 2)
        validUntil = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

        when (application.type) {
            ApplicationType.INTERNSHIP -> {
                benefits = "Health Insurance, Learning Stipend, Flexible Hours, Mentorship Program"
                terms = "Standard internship terms and conditions apply. Performance evaluation will be conducted monthly."
            }
            ApplicationType.FREELANCER -> {
                benefits = "Project-based Payment, Flexible Schedule, Remote Work, Portfolio Enhancement"
                terms = "Freelance contract terms apply. Payment will be made upon milestone completion. Intellectual property rights as per agreement."
            }
            ApplicationType.FULLTIME -> {
                benefits = "Health Insurance, Provident Fund, Annual Leave, Performance Bonus, Professional Development"
                terms = "Standard employment terms and conditions apply. Probation period of 6 months. Notice period as per company policy."
            }
        }
    }

    // Generate preview content when template changes
    val previewContent = remember(selectedTemplate, companyName, position, salary, startDate, endDate, location, benefits, terms, validUntil, generatedBy) {
        if (companyName.isNotBlank() && position.isNotBlank()) {
            OfferTemplateGenerator.generateOfferContent(
                template = selectedTemplate,
                application = application,
                companyName = companyName.ifBlank { "Your Company" },
                position = position,
                salary = salary.ifBlank { "Competitive Salary" },
                startDate = startDate,
                endDate = if (application.type != ApplicationType.FULLTIME) endDate else null,
                location = location.ifBlank { "To be decided" },
                benefits = benefits,
                terms = terms,
                validUntil = validUntil,
                generatedBy = generatedBy
            ).take(500) + "..." // Show first 500 chars as preview
        } else ""
    }

    val typeEmoji = when (application.type) {
        ApplicationType.INTERNSHIP -> "🎓"
        ApplicationType.FREELANCER -> "💼"
        ApplicationType.FULLTIME -> "🏢"
    }

    val typeText = when (application.type) {
        ApplicationType.INTERNSHIP -> "Internship"
        ApplicationType.FREELANCER -> "Freelancer"
        ApplicationType.FULLTIME -> "Full-Time"
    }

    // Check if required fields are filled
    val isFormValid = companyName.isNotBlank() &&
            position.isNotBlank() &&
            salary.isNotBlank() &&
            startDate.isNotBlank() &&
            location.isNotBlank() &&
            validUntil.isNotBlank() &&
            generatedBy.isNotBlank()

    // Custom text field style
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF4A5568),
        unfocusedBorderColor = Color(0xFF9CA3AF),
        focusedLabelColor = Color(0xFF2D3748),
        unfocusedLabelColor = Color(0xFF718096),
        focusedTextColor = Color(0xFF1A202C),
        unfocusedTextColor = Color(0xFF1A202C),
        cursorColor = Color(0xFF4A5568),
        errorBorderColor = Color(0xFFE53E3E),
        errorLabelColor = Color(0xFFE53E3E)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f), // Use 95% of screen height instead of fixed height
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Gradient Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA855F7)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = typeEmoji,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column {
                                Text(
                                    text = "Generate Offer Letter",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "$typeText • ${application.fullName}",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Template Selection with better visual feedback
                    Text(
                        text = "Select Template Style",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        OfferLetterTemplate.values().forEach { template ->
                            val isSelected = selectedTemplate == template
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTemplate = template },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFE0E7FF) else Color(0xFFF8FAFC)
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedTemplate = template },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF6366F1)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = template.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151)
                                        )
                                        Text(
                                            text = when (template) {
                                                OfferLetterTemplate.FORMAL -> "Traditional business format with formal language"
                                                OfferLetterTemplate.MODERN -> "Contemporary style with friendly tone"
                                                OfferLetterTemplate.TECH -> "Developer-friendly with code-style formatting"
                                            },
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Preview Button
                    if (companyName.isNotBlank() && position.isNotBlank()) {
                        OutlinedButton(
                            onClick = { showPreview = !showPreview },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6366F1)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF6366F1))
                        ) {
                            Icon(
                                imageVector = if (showPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (showPreview) "Hide Preview" else "Show Template Preview")
                        }
                    }

                    // Template Preview
                    if (showPreview && previewContent.isNotBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8FAFC)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Template Preview (${selectedTemplate.displayName})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = previewContent,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color.White,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp)
                                )
                            }
                        }
                    }

                    // Form Fields with improved styling
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = {
                            Text(
                                "Company Name *",
                                fontWeight = if (companyNameFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onFocusChanged { companyNameFocused = it.isFocused },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF1A202C),
                            fontWeight = if (companyNameFocused) FontWeight.Bold else FontWeight.Normal
                        ),
                        isError = companyName.isBlank()
                    )

                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = {
                            Text(
                                "Position *",
                                fontWeight = if (positionFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onFocusChanged { positionFocused = it.isFocused },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF1A202C),
                            fontWeight = if (positionFocused) FontWeight.Bold else FontWeight.Normal
                        ),
                        isError = position.isBlank()
                    )

                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = {
                            Text(
                                "Salary/Compensation *",
                                fontWeight = if (salaryFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onFocusChanged { salaryFocused = it.isFocused },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF1A202C),
                            fontWeight = if (salaryFocused) FontWeight.Bold else FontWeight.Normal
                        ),
                        placeholder = { Text("e.g., ₹50,000/month", color = Color(0xFF9CA3AF)) },
                        isError = salary.isBlank()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = {
                                Text(
                                    "Start Date *",
                                    fontWeight = if (startDateFocused) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { startDateFocused = it.isFocused },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = textFieldColors,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xFF1A202C),
                                fontWeight = if (startDateFocused) FontWeight.Bold else FontWeight.Normal
                            ),
                            isError = startDate.isBlank()
                        )

                        if (application.type != ApplicationType.FULLTIME) {
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = { endDate = it },
                                label = {
                                    Text(
                                        "End Date",
                                        fontWeight = if (endDateFocused) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { endDateFocused = it.isFocused },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = textFieldColors,
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color(0xFF1A202C),
                                    fontWeight = if (endDateFocused) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = {
                            Text(
                                "Work Location *",
                                fontWeight = if (locationFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onFocusChanged { locationFocused = it.isFocused },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF1A202C),
                            fontWeight = if (locationFocused) FontWeight.Bold else FontWeight.Normal
                        ),
                        placeholder = { Text("e.g., Remote, Bangalore, Hybrid", color = Color(0xFF9CA3AF)) },
                        isError = location.isBlank()
                    )

                    OutlinedTextField(
                        value = benefits,
                        onValueChange = { benefits = it },
                        label = {
                            Text(
                                "Benefits",
                                fontWeight = if (benefitsFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onFocusChanged { benefitsFocused = it.isFocused },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF1A202C),
                            fontWeight = if (benefitsFocused) FontWeight.Bold else FontWeight.Normal
                        )
                    )

                    OutlinedTextField(
                        value = terms,
                        onValueChange = { terms = it },
                        label = {
                            Text(
                                "Terms & Conditions",
                                fontWeight = if (termsFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onFocusChanged { termsFocused = it.isFocused },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF1A202C),
                            fontWeight = if (termsFocused) FontWeight.Bold else FontWeight.Normal
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = validUntil,
                            onValueChange = { validUntil = it },
                            label = {
                                Text(
                                    "Valid Until *",
                                    fontWeight = if (validUntilFocused) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { validUntilFocused = it.isFocused },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = textFieldColors,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xFF1A202C),
                                fontWeight = if (validUntilFocused) FontWeight.Bold else FontWeight.Normal
                            ),
                            isError = validUntil.isBlank()
                        )

                        OutlinedTextField(
                            value = generatedBy,
                            onValueChange = { generatedBy = it },
                            label = {
                                Text(
                                    "Generated By *",
                                    fontWeight = if (generatedByFocused) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { generatedByFocused = it.isFocused },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = textFieldColors,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xFF1A202C),
                                fontWeight = if (generatedByFocused) FontWeight.Bold else FontWeight.Normal
                            ),
                            isError = generatedBy.isBlank()
                        )
                    }

                    // Required fields indicator
                    if (!isFormValid) {
                        Text(
                            text = "* Required fields must be filled",
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // Fixed Action Buttons at bottom
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF6B7280)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                val offerType = when (application.type) {
                                    ApplicationType.INTERNSHIP -> OfferType.INTERN
                                    ApplicationType.FREELANCER -> OfferType.FREELANCER
                                    ApplicationType.FULLTIME -> OfferType.FULLTIME
                                }

                                // Generate the offer content using the template
                                val offerContent = OfferTemplateGenerator.generateOfferContent(
                                    template = selectedTemplate,
                                    application = application,
                                    companyName = companyName,
                                    position = position,
                                    salary = salary,
                                    startDate = startDate,
                                    endDate = if (application.type != ApplicationType.FULLTIME) endDate else null,
                                    location = location,
                                    benefits = benefits,
                                    terms = terms,
                                    validUntil = validUntil,
                                    generatedBy = generatedBy
                                )

                                val description = "We are pleased to offer you the ${
                                    when (application.type) {
                                        ApplicationType.INTERNSHIP -> "internship"
                                        ApplicationType.FREELANCER -> "freelance project"
                                        ApplicationType.FULLTIME -> "full-time position"
                                    }
                                } of $position based on your excellent qualifications and skills in ${application.skills.joinToString(", ")}."

                                val offerLetter = OfferLetter(
                                    id = UUID.randomUUID().toString(),
                                    applicationId = application.id,
                                    candidateName = application.fullName,
                                    candidateEmail = application.email,
                                    companyName = companyName,
                                    position = position,
                                    salary = salary,
                                    startDate = startDate,
                                    endDate = if (application.type != ApplicationType.FULLTIME) endDate else null,
                                    location = location,
                                    description = description,
                                    benefits = benefits,
                                    terms = terms,
                                    validUntil = validUntil,
                                    generatedBy = generatedBy,
                                    type = offerType,
                                    status = OfferStatus.PENDING,
                                    generatedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                )

                                onGenerate(offerLetter)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid) Color(0xFF6366F1) else Color(0xFFD1D5DB),
                                contentColor = if (isFormValid) Color.White else Color(0xFF9CA3AF)
                            ),
                            enabled = isFormValid
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Generate Offer",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}