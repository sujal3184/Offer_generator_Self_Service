package com.example.offer_generator.Screens.OfferLetters

import OfferLetter
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.R
import com.example.offer_generator.Screens.Internship.*
import com.example.offer_generator.Screens.Freelancer.*
import com.example.offer_generator.Screens.FulltimeJob.FullTimeDatamanger
import kotlinx.coroutines.delay
import java.util.*

// Enhanced enum for offer letter templates with more professional options
enum class OfferLetterTemplate(val displayName: String, val description: String) {
    EXECUTIVE("Executive & Formal", "Traditional corporate style for senior positions"),
    CORPORATE("Corporate Professional", "Standard professional business format"),
    TECH("Tech-Focused", "Developer-friendly with technical elements"),
    CUSTOM("Custom Template", "User-defined custom template")
}

// Template configuration data class
data class TemplateConfig(
    val id: String,
    val name: String,
    val template: OfferLetterTemplate,
    val headerStyle: String = "standard",
    val includeCompanyLogo: Boolean = true,
    val includeSignatureSection: Boolean = true,
    val customFields: Map<String, String> = emptyMap(),
    val createdBy: String,
    val createdDate: Date = Date(),
    val isActive: Boolean = true
)

// Template variable placeholders
object TemplateVariables {
    const val COMPANY_NAME = "{{COMPANY_NAME}}"
    const val CANDIDATE_NAME = "{{CANDIDATE_NAME}}"
    const val POSITION = "{{POSITION}}"
    const val SALARY = "{{SALARY}}"
    const val START_DATE = "{{START_DATE}}"
    const val END_DATE = "{{END_DATE}}"
    const val LOCATION = "{{LOCATION}}"
    const val BENEFITS = "{{BENEFITS}}"
    const val TERMS = "{{TERMS}}"
    const val VALID_UNTIL = "{{VALID_UNTIL}}"
    const val GENERATED_BY = "{{GENERATED_BY}}"
    const val CURRENT_DATE = "{{CURRENT_DATE}}"
    const val SKILLS = "{{SKILLS}}"
    const val POSITION_TYPE = "{{POSITION_TYPE}}"
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

enum class ApplicationType {
    INTERNSHIP, FREELANCER, FULLTIME
}

class OfferTemplateManager {
    private val customTemplates = mutableMapOf<String, String>()
    private val templateConfigs = mutableMapOf<String, TemplateConfig>()


    // Add new custom template
    fun addCustomTemplate(
        templateId: String,
        templateName: String,
        templateContent: String,
        createdBy: String,
        customFields: Map<String, String> = emptyMap()
    ): Boolean {
        return try {
            val config = TemplateConfig(
                id = templateId,
                name = templateName,
                template = OfferLetterTemplate.CUSTOM,
                customFields = customFields,
                createdBy = createdBy
            )

            customTemplates[templateId] = templateContent
            templateConfigs[templateId] = config
            true
        } catch (e: Exception) {
            false
        }
    }

    // Get all available templates
    fun getAllTemplates(): List<TemplateConfig> {
        val builtInTemplates = OfferLetterTemplate.values()
            .filter { it != OfferLetterTemplate.CUSTOM }
            .map { template ->
                TemplateConfig(
                    id = template.name,
                    name = template.displayName,
                    template = template,
                    createdBy = "System",
                    createdDate = Date()
                )
            }

        return builtInTemplates + templateConfigs.values
        }

    // Get custom template content by ID (ADD THIS METHOD)
    fun getCustomTemplateContent(templateId: String): String? {
        return customTemplates[templateId]
    }


    // Delete template
    fun deleteTemplate(templateId: String): Boolean {
        return if (customTemplates.containsKey(templateId)) {
            customTemplates.remove(templateId)
            templateConfigs.remove(templateId)
            true
        } else false
    }

    // Get sample template for HR to customize
    fun getSampleTemplate(): String {
        return """
            |${TemplateVariables.COMPANY_NAME}
            |${TemplateVariables.CURRENT_DATE}
            |
            |Dear ${TemplateVariables.CANDIDATE_NAME},
            |
            |OFFER OF EMPLOYMENT - ${TemplateVariables.POSITION}
            |
            |We are delighted to offer you the position of ${TemplateVariables.POSITION} at ${TemplateVariables.COMPANY_NAME}.
            |
            |POSITION DETAILS:
            |Position: ${TemplateVariables.POSITION}
            |Type: ${TemplateVariables.POSITION_TYPE}
            |Compensation: ${TemplateVariables.SALARY}
            |Start Date: ${TemplateVariables.START_DATE}
            |Location: ${TemplateVariables.LOCATION}
            |
            |KEY SKILLS: ${TemplateVariables.SKILLS}
            |
            |BENEFITS PACKAGE:
            |${TemplateVariables.BENEFITS}
            |
            |TERMS AND CONDITIONS:
            |${TemplateVariables.TERMS}
            |
            |This offer is valid until ${TemplateVariables.VALID_UNTIL}.
            |
            |We look forward to your positive response.
            |
            |Sincerely,
            |${TemplateVariables.GENERATED_BY}
            |${TemplateVariables.COMPANY_NAME}
            |
            |ACCEPTANCE:
            |I accept this offer of employment.
            |
            |Candidate Signature: _________________ Date: _________________
            |${TemplateVariables.CANDIDATE_NAME}
        """.trimMargin()
    }
}

// Enhanced Template Generator with professional formatting
object OfferTemplateGenerator {
    private val templateManager = OfferTemplateManager()

    // Updated generateOfferContent method
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
        generatedBy: String,
        customTemplateId: String? = null,
        customTemplateContent: String? = null
    ): String {

        return when (template) {
            OfferLetterTemplate.EXECUTIVE -> {
                generateExecutiveTemplate(
                    application, companyName, position, salary, startDate, endDate,
                    location, benefits, terms, validUntil, generatedBy
                )
            }
            OfferLetterTemplate.CORPORATE -> {
                generateCorporateTemplate(
                    application, companyName, position, salary, startDate, endDate,
                    location, benefits, terms, validUntil, generatedBy
                )
            }
            OfferLetterTemplate.TECH -> {
                generateTechTemplate(
                    application, companyName, position, salary, startDate, endDate,
                    location, benefits, terms, validUntil, generatedBy
                )
            }
            OfferLetterTemplate.CUSTOM -> {
                generateCustomTemplate(
                    templateId = customTemplateId,
                    customContent = customTemplateContent,
                    application = application,
                    companyName = companyName,
                    position = position,
                    salary = salary,
                    startDate = startDate,
                    endDate = endDate,
                    location = location,
                    benefits = benefits,
                    terms = terms,
                    validUntil = validUntil,
                    generatedBy = generatedBy
                )
            }
        }
    }


    private fun generateCustomTemplate(
        templateId: String?,
        customContent: String?,
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

        // Use the passed customContent first, then try to get from template manager, then fallback to sample
        val template = customContent
            ?: templateId?.let {
                val storedTemplate = templateManager.getCustomTemplateContent(it)
                storedTemplate
            }
            ?: run {
                templateManager.getSampleTemplate()
            }


        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val positionType = getPositionType(application.type)

        // Replace template variables with actual values
        return template
            .replace(TemplateVariables.COMPANY_NAME, companyName)
            .replace(TemplateVariables.CANDIDATE_NAME, application.fullName)
            .replace(TemplateVariables.POSITION, position)
            .replace(TemplateVariables.SALARY, salary)
            .replace(TemplateVariables.START_DATE, startDate)
            .replace(TemplateVariables.END_DATE, endDate ?: "")
            .replace(TemplateVariables.LOCATION, location)
            .replace(TemplateVariables.BENEFITS, benefits)
            .replace(TemplateVariables.TERMS, terms)
            .replace(TemplateVariables.VALID_UNTIL, validUntil)
            .replace(TemplateVariables.GENERATED_BY, generatedBy)
            .replace(TemplateVariables.CURRENT_DATE, currentDate)
            .replace(TemplateVariables.SKILLS, application.skills.joinToString(", "))
            .replace(TemplateVariables.POSITION_TYPE, positionType)
    }

    private fun generateExecutiveTemplate(
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
        val positionType = getPositionType(application.type)

        return """
        |$companyName
        |EXECUTIVE OFFER LETTER
        |
        |Date: $currentDate | Ref: EXE-${application.id.takeLast(4).uppercase()}
        |
        |${application.fullName} | ${application.email}
        |
        |Dear ${application.fullName.split(" ").last()},
        |
        |We are pleased to offer you the position of $position ($positionType) at $companyName.
        |
        |POSITION DETAILS:
        |Title: $position | Type: $positionType | Location: $location
        |Compensation: $salary | Start Date: $startDate${if (endDate != null && application.type != ApplicationType.FULLTIME) " | End Date: $endDate" else ""}
        |
        |QUALIFICATIONS: Your expertise in ${application.skills.take(4).joinToString(", ")} and experience in ${application.role} make you an ideal candidate.
        |
        |BENEFITS PACKAGE: $benefits
        |
        |TERMS: $terms
        |
        |CONDITIONS: This offer is contingent upon background verification and remains valid until $validUntil.
        |
        |Please sign below to accept this offer.
        |
        |Sincerely,
        |$generatedBy, $companyName
        |
        |ACCEPTANCE:
        |I, ${application.fullName}, accept this offer.
        |
        |Signature: _________________________ Date: _____________
    """.trimMargin()
    }

    private fun generateCorporateTemplate(
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
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val positionType = getPositionType(application.type)

        return """
        |$companyName - Human Resources Department
        |
        |Date: $currentDate | Ref: HR/OFF/${application.id.takeLast(6).uppercase()}
        |
        |${application.fullName} | ${application.email}
        |
        |EMPLOYMENT OFFER - $position
        |
        |We offer you employment as $position ($positionType) at $companyName.
        |
        |EMPLOYMENT DETAILS:
        |Position: $position | Department: ${application.branch ?: "As Assigned"}
        |Salary: $salary | Start: $startDate${if (endDate != null && application.type != ApplicationType.FULLTIME) " | End: $endDate" else ""}
        |Location: $location | Type: $positionType
        |
        |ROLE: ${application.role} utilizing skills in ${application.skills.take(4).joinToString(", ")}.
        |
        |COMPENSATION & BENEFITS:
        |Base Salary: $salary
        |Additional Benefits: $benefits
        |
        |TERMS: $terms
        |
        |CONDITIONS: Subject to background checks, qualification verification, and company policy compliance.
        |
        |VALIDITY: This offer expires on $validUntil.
        |
        |Yours sincerely,
        |$generatedBy | HR Manager | $companyName
        |
        |ACCEPTANCE:
        |I accept the employment offer as outlined above.
        |
        |Signature: _________________ Date: _________ Name: ${application.fullName}
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
        val positionType = getPositionType(application.type)

        return """
        |// $companyName - Technical Offer Letter v2.0
        |// Generated: $currentDate | Status: pending_acceptance
        |
        |const offer = {
        |  candidate: "${application.fullName}" // ${application.email}
        |  position: "$position",
        |  type: "$positionType",
        |  department: "${application.branch ?: "Engineering"}",
        |  startDate: "$startDate",${if (endDate != null && application.type != ApplicationType.FULLTIME) "\n|  endDate: \"$endDate\"," else ""}
        |  location: "$location",
        |  salary: "$salary",
        |  skills: [${application.skills.take(5).joinToString(", ") { "\"$it\"" }}]
        |};
        |
        |// 🚀 MISSION: ${application.role}
        |// 💻 TECH STACK: ${application.skills.take(4).joinToString(" | ")}
        |
        |// 💰 PACKAGE
        |const compensation = {
        |  base: "$salary",
        |  benefits: "$benefits"
        |};
        |
        |// 📋 TERMS
        |/* $terms */
        |
        |// ⏰ EXPIRES: $validUntil
        |
        |// 🎯 TO ACCEPT:
        |// 1. Review terms 2. Sign below 3. Email confirmation 4. Join team chat
        |
        |console.log("Offer by: $generatedBy | $companyName");
        |
        |// ACCEPTANCE BLOCK
        |function acceptOffer() {
        |  return {
        |    name: "${application.fullName}",
        |    signature: "________________________",
        |    date: "___________",
        |    accepted: true
        |  };
        |}
        |
        |// Execute: acceptOffer();
    """.trimMargin()
    }

    private fun getPositionType(type: ApplicationType): String {
        return when (type) {
            ApplicationType.INTERNSHIP -> "Internship Position"
            ApplicationType.FREELANCER -> "Contract Position"
            ApplicationType.FULLTIME -> "Full-Time Employment"
        }
    }

    // Get template manager instance for HR operations
    fun getTemplateManager(): OfferTemplateManager = templateManager
}

@Composable
fun OfferLetterGenerator(
    navController: NavController,
    whoLoginViewModel: WhoLoginViewModel,
    applicationId: String? = null,
    applicationType: String? = null // "internship", "freelancer", "fulltime"
) {
    // Initialize template manager
    val templateManager = remember { OfferTemplateGenerator.getTemplateManager() }

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
                }  // already implemented
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
                }  // already implemented
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
                }     // already implemented
                else -> null
            }
        }
    }

    var isVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showTemplateManager by remember { mutableStateOf(false) }
    var generatedOffer by remember { mutableStateOf<OfferLetter?>(null) }

    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        isVisible = true
        delay(100)
        showContent = true
        // Show the generation dialog only if we have a valid application
        if (application != null) {
            showDialog = true
        }
    }

    AppNavigationDrawer(
        navController = navController,
        currentScreen = "Offer Letter",
        onBackPressed = {
            navController.navigateUp()
        },
        whoLoginViewModel = whoLoginViewModel
    ) {
        when {
            // Show template manager if requested
            showTemplateManager -> {
                TemplateManagerDialog(
                    templateManager = templateManager,
                    whoLoginViewModel = whoLoginViewModel,
                    onDismiss = { showTemplateManager = false }
                )
            }
            // Show generation dialog first
            application != null && showDialog -> {
                EnhancedOfferGenerationDialog(
                    application = application,
                    templateManager = templateManager,
                    onDismiss = {
                        showDialog = false
                        navController.navigateUp()
                    },
                    onManageTemplates = {
                        showTemplateManager = true
                    },
                    onGenerate = { offerLetter, selectedTemplate, customTemplateId, customTemplateContent ->
                        // Generate using the new enhanced system
                        val offerContent = OfferTemplateGenerator.generateOfferContent(
                            template = selectedTemplate,
                            application = application,
                            companyName = offerLetter.companyName,
                            position = offerLetter.position,
                            salary = offerLetter.salary,
                            startDate = offerLetter.startDate,
                            endDate = offerLetter.endDate,
                            location = offerLetter.location,
                            benefits = offerLetter.benefits,
                            terms = offerLetter.terms,
                            validUntil = offerLetter.validUntil,
                            generatedBy = offerLetter.generatedBy,
                            customTemplateId = customTemplateId,
                            customTemplateContent = customTemplateContent // PASS THIS PARAMETER
                        )

                        // Create offer letter with generated content using the correct data manager functions
                        val generatedOfferLetter = when (application.type) {
                            ApplicationType.INTERNSHIP -> {
                                OfferLetterDataManager.generateInternOfferLetter(
                                    applicationId = application.id,
                                    companyName = offerLetter.companyName,
                                    position = offerLetter.position,
                                    salary = offerLetter.salary,
                                    startDate = offerLetter.startDate,
                                    endDate = offerLetter.endDate ?: "",
                                    location = offerLetter.location,
                                    description = offerContent,
                                    benefits = offerLetter.benefits,
                                    terms = offerLetter.terms,
                                    validUntil = offerLetter.validUntil,
                                    generatedBy = offerLetter.generatedBy,
                                    templateType = selectedTemplate,
                                    customTemplateId = customTemplateId,
                                    customTemplateContent = customTemplateContent
                                )
                            }
                            ApplicationType.FREELANCER -> {
                                OfferLetterDataManager.generateFreelancerOfferLetter(
                                    applicationId = application.id,
                                    companyName = offerLetter.companyName,
                                    position = offerLetter.position,
                                    salary = offerLetter.salary,
                                    startDate = offerLetter.startDate,
                                    endDate = offerLetter.endDate ?: "",
                                    location = offerLetter.location,
                                    description = offerContent,
                                    benefits = offerLetter.benefits,
                                    terms = offerLetter.terms,
                                    validUntil = offerLetter.validUntil,
                                    generatedBy = offerLetter.generatedBy,
                                    templateType = selectedTemplate,
                                    customTemplateId = customTemplateId,
                                    customTemplateContent = customTemplateContent
                                )
                            }
                            ApplicationType.FULLTIME -> {
                                OfferLetterDataManager.generateFullTimeOfferLetter(
                                    applicationId = application.id,
                                    companyName = offerLetter.companyName,
                                    position = offerLetter.position,
                                    salary = offerLetter.salary,
                                    startDate = offerLetter.startDate,
                                    endDate = "", // Full-time doesn't need end date
                                    location = offerLetter.location,
                                    description = offerContent,
                                    benefits = offerLetter.benefits,
                                    terms = offerLetter.terms,
                                    validUntil = offerLetter.validUntil,
                                    generatedBy = offerLetter.generatedBy,
                                    templateType = selectedTemplate,
                                    customTemplateId = customTemplateId,
                                    customTemplateContent = customTemplateContent
                                )
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
fun EnhancedOfferGenerationDialog(
    application: GenericApplication,
    templateManager: OfferTemplateManager,
    onDismiss: () -> Unit,
    onManageTemplates: () -> Unit,
    onGenerate: (OfferLetter, OfferLetterTemplate, String?, String?) -> Unit // Added customTemplateContent parameter
) {
    var companyName by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(application.role) }
    var salary by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(application.availableFrom) }
    var endDate by remember { mutableStateOf(application.availableUntil) }
    var location by remember { mutableStateOf("") }
    var benefits by remember { mutableStateOf("Comprehensive health insurance, flexible work arrangements, generous paid time off, professional development opportunities, competitive salary with bonuses.") }
    var terms by remember { mutableStateOf("By using this service, you agree to comply with all applicable laws and provide accurate information.") }
    var validUntil by remember { mutableStateOf(application.availableUntil) }
    var generatedBy by remember { mutableStateOf("HR") }

    // Template selection
    var selectedTemplate by remember { mutableStateOf<OfferLetterTemplate?>(null) }
    var customTemplateId by remember { mutableStateOf<String?>(null) }
    var customTemplateContent by remember { mutableStateOf<String?>(null) } // Add this line
    var availableTemplates by remember { mutableStateOf(templateManager.getAllTemplates()) }
    var showTemplatePreview by remember { mutableStateOf(false) }

    // Step control
    var currentStep by remember { mutableStateOf(1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF3B82F6)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
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
                                text = "Generate Professional Offer Letter",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (currentStep == 1) "Step 1: Choose Template" else "Step 2: Fill Details",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Row {
                            if (currentStep == 2) {
                                IconButton(onClick = onManageTemplates) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Manage Templates",
                                        tint = Color.White
                                    )
                                }
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
                }

                // Progress indicator
                LinearProgressIndicator(
                    progress = if (currentStep == 1) 0.5f else 1.0f,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFF1F5F9)
                )

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    when (currentStep) {
                        1 -> {
                            // Template Selection Step
                            TemplateScreenContainer(
                                availableTemplates = availableTemplates,
                                selectedTemplate = selectedTemplate,
                                customTemplateId = customTemplateId,
                                customTemplateContent = customTemplateContent, // Pass the content
                                onTemplateSelect = { template ->
                                    selectedTemplate = template
                                    if (template != OfferLetterTemplate.CUSTOM) {
                                        customTemplateId = null
                                        customTemplateContent = null // Clear custom content
                                    }
                                },
                                onCustomTemplateSelect = { templateId ->
                                    customTemplateId = templateId
                                    selectedTemplate = OfferLetterTemplate.CUSTOM
                                },
                                onCustomTemplateContentChange = { content -> // New callback
                                    customTemplateContent = content
                                },
                                onTemplatesRefresh = {
                                    // Refresh the templates list when new custom template is added
                                    availableTemplates = OfferTemplateGenerator.getTemplateManager().getAllTemplates()
                                },
                                onNext = {
                                    if (selectedTemplate != null) {
                                        currentStep = 2
                                    }
                                }
                            )
                        }
                        2 -> {
                            // Form Fields Step
                            endDate?.let {
                                validUntil?.let { it1 ->
                                    FormFieldsStep(
                                        application = application,
                                        selectedTemplate = selectedTemplate!!,
                                        companyName = companyName,
                                        position = position,
                                        salary = salary,
                                        startDate = startDate,
                                        endDate = it,
                                        location = location,
                                        benefits = benefits,
                                        terms = terms,
                                        validUntil = it1,
                                        generatedBy = generatedBy,
                                        onCompanyNameChange = { companyName = it },
                                        onPositionChange = { position = it },
                                        onSalaryChange = { salary = it },
                                        onStartDateChange = { startDate = it },
                                        onEndDateChange = { endDate = it },
                                        onLocationChange = { location = it },
                                        onBenefitsChange = { benefits = it },
                                        onTermsChange = { terms = it },
                                        onValidUntilChange = { validUntil = it },
                                        onGeneratedByChange = { generatedBy = it },
                                        onBack = { currentStep = 1 },
                                        onGenerate = {
                                            val tempOfferLetter = OfferLetter(
                                                id = "",
                                                applicationId = application.id,
                                                candidateName = application.fullName,
                                                candidateEmail = application.email,
                                                companyName = companyName,
                                                position = position,
                                                salary = salary,
                                                startDate = startDate,
                                                endDate = endDate.takeIf { it?.isNotEmpty() ?: false },
                                                location = location,
                                                description = "",
                                                benefits = benefits,
                                                terms = terms,
                                                validUntil = validUntil!!,
                                                generatedBy = generatedBy,
                                                generatedDate = System.currentTimeMillis().toString(),
                                                type = when (application.type) {
                                                    ApplicationType.INTERNSHIP -> OfferType.INTERN
                                                    ApplicationType.FREELANCER -> OfferType.FREELANCER
                                                    ApplicationType.FULLTIME -> OfferType.FULLTIME
                                                },
                                                templateType = selectedTemplate!!,
                                                customTemplateId = customTemplateId,
                                                customTemplateContent = customTemplateContent
                                            )
                                            // Pass both customTemplateId and customTemplateContent
                                            onGenerate(tempOfferLetter, selectedTemplate!!, customTemplateId, customTemplateContent)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Fixed TemplateScreenContainer with proper custom template handling
@Composable
fun TemplateScreenContainer(
    availableTemplates: List<TemplateConfig>,
    selectedTemplate: OfferLetterTemplate?,
    customTemplateId: String?,
    customTemplateContent: String? = null,
    onTemplateSelect: (OfferLetterTemplate) -> Unit,
    onCustomTemplateSelect: (String) -> Unit,
    onCustomTemplateContentChange: ((String) -> Unit)? = null,
    onTemplatesRefresh: () -> Unit, // ADD THIS CALLBACK
    onNext: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<TemplateScreens>(TemplateScreens.Selection) }
    var showTemplatePreview by remember { mutableStateOf(false) }
    var localCustomTemplateContent by remember { mutableStateOf(customTemplateContent) }

    // Update local state when external state changes
    LaunchedEffect(customTemplateContent) {
        localCustomTemplateContent = customTemplateContent
    }

    when (currentScreen) {
        is TemplateScreens.Selection -> {
            TemplateSelectionStep(
                availableTemplates = availableTemplates,
                selectedTemplate = selectedTemplate,
                customTemplateId = customTemplateId,
                customTemplateContent = localCustomTemplateContent,
                showTemplatePreview = showTemplatePreview,
                onTemplateSelect = onTemplateSelect,
                onCustomTemplateSelect = onCustomTemplateSelect,
                onTogglePreview = { showTemplatePreview = !showTemplatePreview },
                onShowPreview = { template ->
                    template?.let {
                        currentScreen = TemplateScreens.Preview(it, customTemplateId, localCustomTemplateContent)
                    }
                },
                onEditCustomTemplate = {
                    currentScreen = TemplateScreens.CustomEditor
                },
                onNext = onNext
            )
        }

        is TemplateScreens.Preview -> {
            TemplatePreviewScreen(
                template = (currentScreen as TemplateScreens.Preview).template,
                customTemplateId = (currentScreen as TemplateScreens.Preview).customTemplateId,
                customTemplateContent = (currentScreen as TemplateScreens.Preview).customContent,
                onBack = { currentScreen = TemplateScreens.Selection }
            )
        }

        is TemplateScreens.CustomEditor -> {
            CustomTemplateEditorScreen(
                initialTemplate = localCustomTemplateContent ?: OfferTemplateGenerator.getTemplateManager().getSampleTemplate(),
                onBack = { currentScreen = TemplateScreens.Selection },
                onConfirm = { content ->
                    // Generate a unique template ID
                    val newTemplateId = "custom_${System.currentTimeMillis()}"

                    // Save to template manager FIRST
                    val saveSuccess = OfferTemplateGenerator.getTemplateManager().addCustomTemplate(
                        templateId = newTemplateId,
                        templateName = "My Custom Template",
                        templateContent = content,
                        createdBy = "User"
                    )

                    if (saveSuccess) {
                        // Update local state
                        localCustomTemplateContent = content
                        onCustomTemplateContentChange?.invoke(content)

                        // CRITICAL: Set both the template type and the custom template ID
                        onTemplateSelect(OfferLetterTemplate.CUSTOM)
                        onCustomTemplateSelect(newTemplateId)

                        // FIXED: Use callback to refresh templates in parent component
                        onTemplatesRefresh()

                        currentScreen = TemplateScreens.Selection
                    } else {
                        // Handle error - maybe show a toast or error message
                        Log.e("TemplateManager", "Failed to save custom template")
                    }
                }
            )
        }
    }

}

// Updated TemplateSelectionStep to show custom template content
@Composable
fun TemplateSelectionStep(
    availableTemplates: List<TemplateConfig>,
    selectedTemplate: OfferLetterTemplate?,
    customTemplateId: String?,
    customTemplateContent: String? = null, // Add this parameter
    showTemplatePreview: Boolean,
    onTemplateSelect: (OfferLetterTemplate) -> Unit,
    onCustomTemplateSelect: (String) -> Unit,
    onTogglePreview: () -> Unit,
    onShowPreview: (OfferLetterTemplate?) -> Unit,
    onEditCustomTemplate: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Template Selection Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Template Style",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            TextButton(
                onClick = {
                    if (selectedTemplate != null) {
                        onShowPreview(selectedTemplate)
                    }
                },
                enabled = selectedTemplate != null,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF6366F1),
                    disabledContentColor = Color(0xFF94A3B8)
                )
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Preview", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Template Grid
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(OfferLetterTemplate.values().toList()) { template ->
                ProfessionalTemplateCard(
                    template = template,
                    isSelected = selectedTemplate == template,
                    availableCustomTemplates = availableTemplates.filter { it.template == OfferLetterTemplate.CUSTOM },
                    customTemplateContent = if (template == OfferLetterTemplate.CUSTOM) customTemplateContent else null,
                    showPreview = showTemplatePreview,
                    onSelect = { onTemplateSelect(template) },
                    onSelectCustom = onCustomTemplateSelect,
                    onEditCustom = if (template == OfferLetterTemplate.CUSTOM) onEditCustomTemplate else null
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Next Button
        Button(
            onClick = onNext,
            enabled = selectedTemplate != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1),
                disabledContainerColor = Color(0xFFE2E8F0)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continue to Form",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

// Updated ProfessionalTemplateCard to show custom content status
@Composable
fun ProfessionalTemplateCard(
    template: OfferLetterTemplate,
    isSelected: Boolean,
    availableCustomTemplates: List<TemplateConfig>,
    customTemplateContent: String? = null, // Add this parameter
    showPreview: Boolean,
    onSelect: () -> Unit,
    onSelectCustom: (String) -> Unit,
    onEditCustom: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF6366F1).copy(alpha = 0.1f)
            else
                Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
        ),
        onClick = onSelect,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF6366F1)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    when (template) {
                        OfferLetterTemplate.EXECUTIVE -> Icons.Default.Business
                        OfferLetterTemplate.CORPORATE -> Icons.Default.CorporateFare
                        OfferLetterTemplate.TECH -> Icons.Default.Code
                        OfferLetterTemplate.CUSTOM -> Icons.Default.Edit
                    },
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF6366F1) else Color(0xFF64748B),
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )

                    // Show custom template status
                    if (template == OfferLetterTemplate.CUSTOM) {
                        Text(
                            text = if (customTemplateContent.isNullOrEmpty()) "No custom template created" else "Custom template ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (customTemplateContent.isNullOrEmpty()) Color(0xFFEF4444) else Color(0xFF10B981),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Edit button for custom template - always show
            if (template == OfferLetterTemplate.CUSTOM && onEditCustom != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onEditCustom,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (customTemplateContent.isNullOrEmpty()) "Create Template" else "Edit Template",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (showPreview) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (template) {
                            OfferLetterTemplate.EXECUTIVE -> "Sophisticated, formal layout perfect for C-level and senior executive positions with premium styling"
                            OfferLetterTemplate.CORPORATE -> "Professional business format suitable for most corporate roles with clean, traditional design"
                            OfferLetterTemplate.TECH -> "Developer-friendly format with code-style elements and tech-focused terminology"
                            OfferLetterTemplate.CUSTOM -> if (customTemplateContent.isNullOrEmpty())
                                "Create your custom template by clicking 'Create Template' button above"
                            else "Your custom-designed template with personalized branding and styling"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Custom templates dropdown (if any exist)
            if (template == OfferLetterTemplate.CUSTOM && availableCustomTemplates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                availableCustomTemplates.forEach { customTemplate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { onSelectCustom(customTemplate.id) }
                    ) {
                        Text(
                            text = "• ${customTemplate.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

// Fixed generateDemoOfferLetter function
private fun generateDemoOfferLetter(
    template: OfferLetterTemplate,
    customTemplateId: String? = null,
    customTemplateContent: String? = null
): String {
    val demoApplication = GenericApplication(
        id = "DEMO123",
        fullName = "Sarah Johnson",
        email = "sarah.johnson@email.com",
        role = "Full Stack Development",
        branch = "Engineering",
        skills = listOf("React", "Node.js", "Python", "AWS", "Docker", "MongoDB"),
        availableFrom = "2025-07-01",
        availableUntil = "",
        type = ApplicationType.FULLTIME
    )

    return when (template) {
        OfferLetterTemplate.CUSTOM -> {
            if (customTemplateContent != null && customTemplateContent.isNotEmpty()) {
                // Use the actual custom template content and replace placeholders
                replaceCustomTemplatePlaceholders(customTemplateContent, demoApplication)
            } else {
                "No custom template content available. Please create a custom template first."
            }
        }
        else -> {
            // Use the built-in template generator for other templates
            OfferTemplateGenerator.generateOfferContent(
                template = template,
                application = demoApplication,
                companyName = "TechCorp Solutions",
                position = "Senior Software Developer",
                salary = "$85,000 per annum",
                startDate = "July 1st, 2025",
                endDate = null,
                location = "Remote / San Francisco, CA",
                benefits = "Health Insurance, Dental Coverage, 401(k) Matching, Flexible PTO, Remote Work Options, Professional Development Budget",
                terms = "This is a full-time position. You will be expected to maintain confidentiality of company information and comply with all company policies. Standard probationary period of 90 days applies.",
                validUntil = "June 30th, 2025",
                generatedBy = "Alex Smith, HR Manager",
                customTemplateId = customTemplateId
            )
        }
    }
}

// Helper function to replace placeholders in custom template
private fun replaceCustomTemplatePlaceholders(
    templateContent: String,
    demoApplication: GenericApplication
): String {
    return templateContent
        .replace("{CANDIDATE_NAME}", demoApplication.fullName)
        .replace("{POSITION}", "Senior Software Developer")
        .replace("{COMPANY_NAME}", "TechCorp Solutions")
        .replace("{DATE}", "June 22, 2025")
        .replace("{DEPARTMENT}", "Engineering")
        .replace("{MANAGER_NAME}", "Alex Smith")
        .replace("{EMPLOYMENT_TYPE}", "Full-time")
        .replace("{START_DATE}", "July 1st, 2025")
        .replace("{SALARY}", "$85,000 per annum")
        .replace("{BENEFITS}", "Health Insurance, Dental Coverage, 401(k) Matching, Flexible PTO")
        .replace("{LOCATION}", "Remote / San Francisco, CA")
        .replace("{TERMS}", "This is a full-time position. Standard probationary period of 90 days applies.")
        .replace("{VALID_UNTIL}", "June 30th, 2025")
        .replace("{GENERATED_BY}", "Alex Smith, HR Manager")
        .replace("{SKILLS}", demoApplication.skills.joinToString(", "))
}

// Custom Template Editor Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTemplateEditorScreen(
    initialTemplate: String = "",
    onBack: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var templateContent by remember {
        mutableStateOf(
            if (initialTemplate.isEmpty()) {
                generateDefaultCustomTemplate()
            } else {
                initialTemplate
            }
        )
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Edit Custom Template",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF1E293B)
            )
        )

        // Editor Content
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Customize your offer letter template:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Use placeholders like {CANDIDATE_NAME}, {POSITION}, {SALARY}, {START_DATE}, etc.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = templateContent,
                    onValueChange = { templateContent = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black // Black text color
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = Color.Black, // Black text when focused
                        unfocusedTextColor = Color.Black, // Black text when unfocused
                        cursorColor = Color(0xFF6366F1) // Optional: matching cursor color
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Bottom Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF64748B)
                ),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { onConfirm(templateContent) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Confirm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

// Generate default professional template for custom editing
private fun generateDefaultCustomTemplate(): String {
    return """
[COMPANY LETTERHEAD]

{DATE}

Dear {CANDIDATE_NAME},

We are pleased to extend an offer of employment for the position of {POSITION} at {COMPANY_NAME}.

POSITION DETAILS:
• Position: {POSITION}
• Department: {DEPARTMENT}
• Reporting to: {MANAGER_NAME}
• Employment Type: {EMPLOYMENT_TYPE}
• Start Date: {START_DATE}

COMPENSATION & BENEFITS:
• Annual Salary: {SALARY}
• Benefits Package: {BENEFITS}
• Location: {LOCATION}

TERMS & CONDITIONS:
{TERMS}

This offer is valid until {VALID_UNTIL}. Please confirm your acceptance by signing and returning this letter.

We look forward to welcoming you to our team!

Sincerely,

{GENERATED_BY}
{COMPANY_NAME}

_________________________
Candidate Signature                    Date

_________________________
{CANDIDATE_NAME}
    """.trimIndent()
}

// Template Preview Screen (Updated)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePreviewScreen(
    template: OfferLetterTemplate,
    customTemplateId: String? = null,
    customTemplateContent: String? = null,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "${template.displayName} Preview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF1E293B)
            )
        )

        // Preview Content
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Preview Label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "PREVIEW MODE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Generated Offer Letter Content
                SelectionContainer {
                    Text(
                        text = generateDemoOfferLetter(template, customTemplateId, customTemplateContent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1E293B),
                        lineHeight = 20.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}


// Navigation State Management (Updated)
sealed class TemplateScreens {
    object Selection : TemplateScreens()
    data class Preview(val template: OfferLetterTemplate, val customTemplateId: String? = null, val customContent: String? = null) : TemplateScreens()
    object CustomEditor : TemplateScreens()
}


@Composable
fun FormFieldsStep(
    application: GenericApplication,
    selectedTemplate: OfferLetterTemplate,
    companyName: String,
    position: String,
    salary: String,
    startDate: String,
    endDate: String,
    location: String,
    benefits: String,
    terms: String,
    validUntil: String,
    generatedBy: String,
    onCompanyNameChange: (String) -> Unit,
    onPositionChange: (String) -> Unit,
    onSalaryChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onBenefitsChange: (String) -> Unit,
    onTermsChange: (String) -> Unit,
    onValidUntilChange: (String) -> Unit,
    onGeneratedByChange: (String) -> Unit,
    onBack: () -> Unit,
    onGenerate: () -> Unit
) {

    var showDatePicker by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Selected Template Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6366F1).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    when (selectedTemplate) {
                        OfferLetterTemplate.EXECUTIVE -> Icons.Default.Business
                        OfferLetterTemplate.CORPORATE -> Icons.Default.CorporateFare
                        OfferLetterTemplate.TECH -> Icons.Default.Code
                        OfferLetterTemplate.CUSTOM -> Icons.Default.Edit
                    },
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Selected: ${selectedTemplate.displayName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = selectedTemplate.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onBack,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF6366F1)
                    ),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        "Change",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Company Name - Full Width
            item {
                ProfessionalTextField(
                    value = companyName,
                    onValueChange = onCompanyNameChange,
                    label = "Company Name",
                    leadingIcon = Icons.Default.Business,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Position - Full Width
            item {
                ProfessionalTextField(
                    value = position,
                    onValueChange = onPositionChange,
                    label = "Position",
                    leadingIcon = Icons.Default.Work,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Salary - Full Width
            item {
                ProfessionalTextField(
                    value = salary,
                    onValueChange = onSalaryChange,
                    label = "Salary/Compensation",
                    leadingIcon = Icons.Default.AttachMoney,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Work Location - Full Width
            item {
                ProfessionalTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = "Work Location",
                    leadingIcon = Icons.Default.LocationOn,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Date Fields - Half Width
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatePickerField(
                        value = startDate,
                        onValueChange = onStartDateChange,
                        label = "Start Date*",
                        placeholder = "Select start date",
                        showDatePicker = showDatePicker,
                        onShowDatePickerChange = { showDatePicker = it },
                        modifier = Modifier.weight(1f) // Apply weight here
                    )
                    if (application.type != ApplicationType.FULLTIME) {
                        DatePickerField(
                            value = endDate,
                            onValueChange = onEndDateChange,
                            label = "End Date",
                            placeholder = "Select end date",
                            showDatePicker = showDatePicker,
                            onShowDatePickerChange = { showDatePicker = it },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Benefits & Perks - Full Width
            item {
                ProfessionalTextField(
                    value = benefits,
                    onValueChange = onBenefitsChange,
                    label = "Benefits & Perks",
                    leadingIcon = Icons.Default.CardGiftcard,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            // Terms & Conditions - Full Width
            item {
                ProfessionalTextField(
                    value = terms,
                    onValueChange = onTermsChange,
                    label = "Terms & Conditions",
                    leadingIcon = Icons.Default.Assignment,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }


            // Valid Until and Generated By - Half Width
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (application.type != ApplicationType.FULLTIME) {
                        DatePickerField(
                            value = validUntil,
                            onValueChange = onValidUntilChange,
                            label = "Valid Until",
                            placeholder = "",
                            showDatePicker = showDatePicker,
                            onShowDatePickerChange = { showDatePicker = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    ProfessionalTextField(
                        value = generatedBy,
                        onValueChange = onGeneratedByChange,
                        label = "Generated By",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(0.9f).height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6366F1)
                ),
                border = BorderStroke(1.dp, Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = onGenerate,
                enabled = companyName.isNotEmpty() && position.isNotEmpty() &&
                        salary.isNotEmpty() && startDate.isNotEmpty() &&
                        validUntil.isNotEmpty() && generatedBy.isNotEmpty(),
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1),
                    disabledContainerColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFF94A3B8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Create, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Generate",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ProfessionalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = Color(0xFF6366F1)
            )
        },
        modifier = modifier,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color(0xFF6366F1),
            focusedBorderColor = Color(0xFF6366F1),
            focusedLabelColor = Color(0xFF6366F1),
            unfocusedBorderColor = Color(0xFFCBD5E1),
            unfocusedLabelColor = Color(0xFF64748B),
            focusedTextColor = Color(0xFF1E293B),
            unfocusedTextColor = Color(0xFF374151)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { }, // Read-only, changed via date picker
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = { showPicker = true }
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select date",
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            // trailingIcon removed - no more arrow icon
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedLabelColor = Color(0xFF6366F1),
                unfocusedLabelColor = Color(0xFF64748B),
                cursorColor = Color(0xFF6366F1)
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF1E293B)
            )
        )

        if (showPicker) {
            DatePickerDialog(
                onDateSelected = { selectedDate ->
                    onValueChange(selectedDate)
                    showPicker = false
                },
                onDismiss = { showPicker = false }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(selectedDate)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF059669) // Emerald green for confirm
                )
            ) {
                Text("OK", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF6B7280) // Cool gray for cancel
                )
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White // White background
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                // Selected date styling
                selectedDayContainerColor = Color(0xFF3B82F6), // Bright blue
                selectedDayContentColor = Color.White,

                // Today's date styling
                todayDateBorderColor = Color(0xFF3B82F6),
                todayContentColor = Color(0xFF3B82F6),

                // General text colors
                containerColor = Color.White,
                titleContentColor = Color(0xFF111827), // Dark gray/black for title
                headlineContentColor = Color(0xFF111827), // Dark gray/black for headline
                weekdayContentColor = Color(0xFF374151), // Medium gray for weekdays
                dayContentColor = Color(0xFF111827), // Dark gray/black for regular days
                disabledDayContentColor = Color(0xFF9CA3AF), // Light gray for disabled days
                yearContentColor = Color(0xFF111827), // Dark gray/black for years
                disabledYearContentColor = Color(0xFF9CA3AF), // Light gray for disabled years
                currentYearContentColor = Color(0xFF3B82F6), // Blue for current year
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Color(0xFF3B82F6),

                // Navigation colors
                dayInSelectionRangeContainerColor = Color(0xFFDBEAFE), // Light blue for range
                dayInSelectionRangeContentColor = Color(0xFF1E40AF), // Darker blue for range text

                // Divider color
                dividerColor = Color(0xFFE5E7EB) // Light gray divider
            )
        )
    }
}


@Composable
fun TemplateManagerDialog(
    templateManager: OfferTemplateManager,
    whoLoginViewModel: WhoLoginViewModel,
    onDismiss: () -> Unit
) {
    var showAddTemplate by remember { mutableStateOf(false) }
    var templates by remember { mutableStateOf(templateManager.getAllTemplates()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Template Manager",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        Button(
                            onClick = { showAddTemplate = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Template")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Templates List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(templates) { template ->
                        TemplateManagerItem(
                            template = template,
                            onEdit = {
                                // Handle edit
                            },
                            onDelete = { templateId ->
                                templateManager.deleteTemplate(templateId)
                                templates = templateManager.getAllTemplates()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddTemplate) {
        AddTemplateDialog(
            templateManager = templateManager,
            whoLoginViewModel = whoLoginViewModel,
            onDismiss = { showAddTemplate = false },
            onTemplateAdded = {
                templates = templateManager.getAllTemplates()
                showAddTemplate = false
            }
        )
    }
}

@Composable
fun TemplateManagerItem(
    template: TemplateConfig,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Created by: ${template.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type: ${template.template.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (template.template == OfferLetterTemplate.CUSTOM) {
                IconButton(onClick = { onEdit(template.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(template.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddTemplateDialog(
    templateManager: OfferTemplateManager,
    whoLoginViewModel: WhoLoginViewModel,
    onDismiss: () -> Unit,
    onTemplateAdded: () -> Unit
) {
    var templateName by remember { mutableStateOf("") }
    var templateContent by remember { mutableStateOf(templateManager.getSampleTemplate()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Custom Template",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = templateName,
                    onValueChange = { templateName = it },
                    label = { Text("Template Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Template Content (Use variables like {{COMPANY_NAME}}, {{CANDIDATE_NAME}}, etc.)",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = templateContent,
                    onValueChange = { templateContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeholder = { Text("Enter your template content here...") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val success = templateManager.addCustomTemplate(
                                templateId = "custom_${System.currentTimeMillis()}",
                                templateName = templateName,
                                templateContent = templateContent,
                                createdBy = "HR"
                            )
                            if (success) {
                                onTemplateAdded()
                            }
                        },
                        enabled = templateName.isNotEmpty() && templateContent.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Template")
                    }
                }
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
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF00C853),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Offer Letter Generated Successfully",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 24.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "The offer letter has been generated and delivered to the candidate. They will receive a notification and can review the details in their dashboard.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 0.15.sp
                    ),
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Professional offer details card
                offer?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFAFAFA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Header with position and company
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = when (it.type) {
                                        OfferType.INTERN -> Icons.Default.School
                                        OfferType.FREELANCER -> Icons.Default.Work
                                        OfferType.FULLTIME -> Icons.Default.Business
                                    },
                                    contentDescription = null,
                                    tint = Color(0xFF6200EA),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${it.position}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1A1A1A)
                                )
                            }

                            Text(
                                text = it.companyName,
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(start = 26.dp)
                            )

                            Divider(
                                color = Color(0xFFE5E5E5),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Compensation details
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Compensation: ${it.salary}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Validity period
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Valid until: ${it.validUntil}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333)
                                )
                            }

                            // Offer type badge
                            Surface(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .wrapContentWidth()
                            ) {
                                Text(
                                    text = when (it.type) {
                                        OfferType.INTERN -> "Internship"
                                        OfferType.FREELANCER -> "Freelance"
                                        OfferType.FULLTIME -> "Full-time"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "The candidate will be notified via email and can respond to this offer through their candidate portal.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 0.25.sp
                    ),
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            if (!whoLoginViewModel.isHrLoggedIn.value) {
                // Candidate view - show both buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewOffers,
                        border = BorderStroke(1.5.dp, colorResource(id = R.color.purple)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp)
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View All Offers",
                                color = colorResource(id = R.color.purple),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                letterSpacing = 0.25.sp
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.purple)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 6.dp
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp)
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                letterSpacing = 0.25.sp
                            )
                        }
                    }
                }
            } else {
                // HR view - single button
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.purple)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 6.dp
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 140.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Close",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(20.dp)
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
