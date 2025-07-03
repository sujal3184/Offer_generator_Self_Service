package com.example.offer_generator.Screens.ApplicationForm

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import com.example.offer_generator.Navigation.Screen
import com.example.offer_generator.R
import com.example.offer_generator.Screens.Freelancer.FreelancerDataManager
import com.example.offer_generator.Screens.Internship.InternshipDataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.offer_generator.ProfessionalTheme
import com.example.offer_generator.Screens.FulltimeJob.FullTimeDatamanger
import com.example.offer_generator.Screens.Internship.resetForm
import com.example.offer_generator.ViewModels.JobOpeningsViewModel
import com.example.offer_generator.common.AppNavigationDrawer
import com.example.offer_generator.common.bottomBar

@Composable
fun ApplicationScreen(navController: NavController, whoLoginViewModel : WhoLoginViewModel,jobViewModel: JobOpeningsViewModel) {

    var currentStep by remember { mutableStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Form states
    val formData = remember { FormData() }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // Floating animation for background elements
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    AppNavigationDrawer(
        navController = navController,
        currentScreen = Screen.ApplicationForm.route,
        onBackPressed = { navController.popBackStack()},
        whoLoginViewModel = whoLoginViewModel
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                ,
                verticalArrangement = Arrangement.spacedBy(44.dp)
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    ProfessionalTheme.Purple,
                                    colorResource(R.color.topbarbackgound),
                                    colorResource(R.color.Lightpurple),
                                    ProfessionalTheme.Purple
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Dynamic header text based on user type
                        val userType = whoLoginViewModel.getCurrentUserType()
                        val headerText = when (userType) {
                            "intern" -> "Internship Application"
                            "freelancer" -> "Freelancer Application"
                            "fulltime" -> "Job Application"
                            else -> "Application Form"
                        }

                        Text(
                            headerText,
                            style = androidx.compose.material.MaterialTheme.typography.h4,
                            color = Color.White
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                ) {

                    ApplicationForm(
                        currentStep = currentStep,
                        onStepChange = { currentStep = it },
                        formData = formData,
                        snackbarHostState = snackbarHostState,
                        navController = navController,
                        viewModel = whoLoginViewModel,
                        jobViewModel = jobViewModel
                    )
                }
                bottomBar(navController , whoLoginViewModel)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

        }
    }
}

@Composable
fun ApplicationForm(
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    formData: FormData,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    viewModel: WhoLoginViewModel,
    jobViewModel: JobOpeningsViewModel
) {
    val userType = viewModel.getCurrentUserType()

    // Dynamic steps based on user type
    val steps = when (userType) {
        "intern" -> listOf("Personal Info", "Internship Details", "Contact Info", "Review & Submit")
        "freelancer" -> listOf("Personal Info", "Service Details", "Contact Info", "Review & Submit")
        "fulltime" -> listOf("Personal Info", "Job Details", "Contact Info", "Review & Submit")
        else -> listOf("Personal Info", "Details", "Contact Info", "Review & Submit")
    }

    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val purpleColor = colorResource(id = R.color.purple)

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            val headerText = when (userType) {
                "intern" -> "Internship Application Form"
                "freelancer" -> "Freelancer Application Form"
                "fulltime" -> "Job Application Form"
                else -> "Application Form"
            }

            Text(
                text = headerText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = purpleColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val descriptionText = when (userType) {
                "intern" -> "Fill out the form below to apply for an internship. All fields marked with * are required."
                "freelancer" -> "Fill out the form below to apply as a freelancer. All fields marked with * are required."
                "fulltime" -> "Fill out the form below to apply for a full-time position. All fields marked with * are required."
                else -> "Fill out the form below to submit your application. All fields marked with * are required."
            }

            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step Indicator
            StepIndicator(
                steps = steps,
                currentStep = currentStep
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Content
            when (currentStep) {
                0 -> PersonalInfoStep(formData = formData, viewModel)
                1 -> WorkDetailsStep(formData = formData, viewModel,jobViewModel)
                2 -> ContactInfoStep(formData = formData, viewModel)
                3 -> ReviewAndSubmitStep(
                    formData = formData,
                    onEditStep = onStepChange,
                    viewModel
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Buttons
            NavigationButtons(
                currentStep = currentStep,
                totalSteps = steps.size,
                onPrevious = { onStepChange(currentStep - 1) },
                onNext = {
                    if (currentStep < 3) {
                        onStepChange(currentStep + 1)
                    } else {
                        // Submit form and store locally
                        isSubmitting = true
                        scope.launch {
                            try {
                                // Submit the application to local storage
                                // Submit the application based on user type
                                val submittedApplication = when(viewModel.getCurrentUserType()) {
                                    "intern" -> {
                                        InternshipDataManager.submitApplication(formData)
                                    }
                                    "freelancer" -> {
                                        FreelancerDataManager.submitApplication(formData)
                                    }
                                    "fulltime" -> {
                                        FullTimeDatamanger.submitApplication(formData)
                                    }
                                    else -> {
                                        throw IllegalStateException("Invalid user type for application submission")
                                    }
                                }

                                // Success feedback
                                isSubmitting = false
                                val successMessage = when (userType) {
                                    "intern" -> "Internship application submitted successfully!"
                                    "freelancer" -> "Freelancer application submitted successfully!"
                                    "fulltime" -> "Job application submitted successfully!"
                                    else -> "Application submitted successfully!"
                                }

                                snackbarHostState.showSnackbar(
                                    "Submitted Successfully"
                                )

                                // Simulate submission delay
                                delay(200)

                                // Optional: Reset form after successful submission
                                resetForm(formData)

                                // Optional: Navigate to a success screen or applications list
                                when(userType){
                                    "intern" -> navController.navigate(Screen.CandidateDashboard.route)
                                    "freelancer" -> navController.navigate(Screen.FlDashboard.route)
                                    "fulltime" -> navController.navigate(Screen.FullTimejobDashboard.route)
                                }

                            } catch (e: Exception) {
                                isSubmitting = false
                                snackbarHostState.showSnackbar("Submission failed: ${e.message}")
                            }
                        }
                    }
                },
                isSubmitting = isSubmitting,
                isStepValid = isStepValid(currentStep, formData, userType),
                userType = userType
            )
        }
    }
}

@Composable
fun StepIndicator(
    steps: List<String>,
    currentStep: Int
) {
    val purpleColor = colorResource(id = R.color.purple)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (index < currentStep) Color.Green else if(index==currentStep) purpleColor else Color.LightGray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < currentStep) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (index <= currentStep) purpleColor else Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// Helper Composables and Classes

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    maxLines: Int = 1,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    showError: Boolean = false, // Only show error after user interaction
    errorMessage: String = ""
) {
    var isFocused by remember { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }

    // Show error only if field has been focused and then lost focus, or if showError is explicitly true
    val shouldShowError = showError && (hasBeenFocused || isError)

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.DarkGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (focusState.isFocused) {
                        hasBeenFocused = true
                    }
                },
            shape = RoundedCornerShape(8.dp),
            maxLines = maxLines,
            isError = shouldShowError,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = if (shouldShowError) Color.Red else Color.Black.copy(alpha = 0.5f),
                unfocusedBorderColor = if (shouldShowError) Color.Red else Color.Black.copy(alpha = 0.3f),
                focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                cursorColor = Color.DarkGray,
                selectionColors = TextSelectionColors(
                    handleColor = Color.DarkGray,
                    backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
                ),
                errorTextColor = Color.Black,
                errorContainerColor = Color.White,
                errorBorderColor = Color.Red,
            )
        )

        // Show error message if there's an error and field has been interacted with
        if (shouldShowError && errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit,
    disablePastDates: Boolean = false
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column{
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                IconButton(onClick = { onShowDatePickerChange(true) }) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select Date",
                        tint = Color.Black
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePickerChange(true) }
                .background(Color.White),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Black.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f)
            )
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (disablePastDates) {
                System.currentTimeMillis()
            } else null,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return if (disablePastDates) {
                        utcTimeMillis >= System.currentTimeMillis() - 86400000
                    } else {
                        true
                    }
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { onShowDatePickerChange(false) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        onValueChange(dateFormatter.format(date))
                    }
                    onShowDatePickerChange(false)
                }) {
                    Text("OK", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDatePickerChange(false) }) {
                    Text("Cancel", color = Color.Black)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Color.DarkGray
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Black.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                    focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationButtons(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isSubmitting: Boolean,
    isStepValid: Boolean,
    userType: String?
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (currentStep == 0) Arrangement.End else Arrangement.SpaceBetween
    ) {
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.height(48.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.white),
                    contentColor = colorResource(R.color.Lightpurple)
                )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Previous")
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.Lightpurple),
                contentColor = colorResource(R.color.white)
            ),
            enabled = isStepValid && !isSubmitting
        ) {
            if (isSubmitting && currentStep == totalSteps - 1) {
                Text("Submitting...")
            } else {
                val buttonText = if (currentStep == totalSteps - 1) {
                    when (userType) {
                        "intern" -> "Submit"
                        "freelancer" -> "Submit"
                        "fulltime" -> "Submit"
                        else -> "Submit"
                    }
                } else {
                    "Next"
                }

                Text(text = buttonText)
                if (currentStep < totalSteps - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }
}


// Data Classes and Validation Functions

class FormData {
    // Personal info for intern and full time
    var fullName by mutableStateOf("")
    var dateOfBirth by mutableStateOf("")
    var college by mutableStateOf("")
    var branch by mutableStateOf("")

    // Intern specific fields
    var yearOfStudy by mutableStateOf("")

    // Freelancer specific fields
    var yearsOfExperience by mutableStateOf("")
    var currentLocation by mutableStateOf("")
    var availabilityStatus by mutableStateOf("")

    // Full-time specific fields
    var graduationYear by mutableStateOf("")
    var cgpaPercentage by mutableStateOf("")
    var previousCompany by mutableStateOf("")

    // Work details

    var skillsList by mutableStateOf(mutableListOf<String>())
    // intern fields
    var availableFrom by mutableStateOf("")
    var availableUntil by mutableStateOf("")
    var internshipRole by mutableStateOf("")
    var cvFileName by mutableStateOf("")
    var cvUri by mutableStateOf<Uri?>(null)
    var cvFilePath by mutableStateOf("")

    // Freelancer specific
    var projectStartDate by mutableStateOf("")
    var projectEndDate by mutableStateOf("")
    var serviceCategory by mutableStateOf("")
    var hourlyRate by mutableStateOf("")

    // full time
    var joinDate by mutableStateOf("")
    var jobRole by mutableStateOf("")
    var expectedSalary by mutableStateOf("")
    var noticePeriod by mutableStateOf("")

    // Contact information fields
    var mobileNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var linkedinProfile by mutableStateOf("")
    var githubLink by mutableStateOf("")
    var portfolioWebsite by mutableStateOf("")
    var references by mutableStateOf("")
    var personalStatement by mutableStateOf("")

    // Freelancer specific contact fields
    var clientReferences by mutableStateOf("")
    var professionalSummary by mutableStateOf("")

    // full time role
    var professionalReferences by mutableStateOf("")
    var coverLetter by mutableStateOf("")
}

// Enhanced data class with better type safety
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val field: String? = null
)

// Complete Enhanced validation functions for all user types

fun isStepValid(step: Int, formData: FormData, userType: String?): Boolean {
    return validateStep(step, formData, userType).isValid
}

fun validateStep(step: Int, formData: FormData, userType: String?): ValidationResult {
    return when (step) {
        0 -> validatePersonalInfo(formData, userType)
        1 -> validateWorkDetails(formData, userType)
        2 -> validateContactInfo(formData, userType)
        3 -> ValidationResult(true) // Review step
        else -> ValidationResult(false, "Invalid step")
    }
}

private fun validatePersonalInfo(formData: FormData, userType: String?): ValidationResult {
    if (formData.fullName.isBlank()) {
        return ValidationResult(false, "Full name is required", "fullName")
    }

    if (formData.dateOfBirth.isBlank()) {
        return ValidationResult(false, "Date of birth is required", "dateOfBirth")
    }

    // Validate date of birth format and logic
    val dobValidation = validateDateOfBirth(formData.dateOfBirth)
    if (!dobValidation.isValid) {
        return dobValidation.copy(field = "dateOfBirth")
    }

    return when (userType) {
        "intern" -> {
            when {
                formData.yearOfStudy.isBlank() -> ValidationResult(false, "Year of study is required", "yearOfStudy")
                formData.college.isBlank() -> ValidationResult(false, "College name is required", "college")
                formData.branch.isBlank() -> ValidationResult(false, "Branch/Department is required", "branch")
                else -> ValidationResult(true)
            }
        }
        "freelancer" -> {
            when {
                formData.yearsOfExperience.isBlank() -> ValidationResult(false, "Years of experience is required", "yearsOfExperience")
                !validateYearsOfExperience(formData.yearsOfExperience).isValid -> validateYearsOfExperience(formData.yearsOfExperience).copy(field = "yearsOfExperience")
                formData.currentLocation.isBlank() -> ValidationResult(false, "Current location is required", "currentLocation")
                formData.availabilityStatus.isBlank() -> ValidationResult(false, "Availability status is required", "availabilityStatus")
                else -> ValidationResult(true)
            }
        }
        "fulltime" -> {
            when {
                formData.college.isBlank() -> ValidationResult(false, "College name is required", "college")
                formData.branch.isBlank() -> ValidationResult(false, "Branch/Department is required", "branch")
                formData.cgpaPercentage.isBlank() -> ValidationResult(false, "CGPA/Percentage is required", "cgpaPercentage")
                !validateCGPAOrPercentage(formData.cgpaPercentage).isValid -> validateCGPAOrPercentage(formData.cgpaPercentage).copy(field = "cgpaPercentage")
                formData.yearsOfExperience.isBlank() -> ValidationResult(false, "Years of experience is required", "yearsOfExperience")
                !validateYearsOfExperience(formData.yearsOfExperience).isValid -> validateYearsOfExperience(formData.yearsOfExperience).copy(field = "yearsOfExperience")
                else -> ValidationResult(true)
            }
        }
        else -> ValidationResult(true)
    }
}

private fun validateWorkDetails(formData: FormData, userType: String?): ValidationResult {
    return when (userType) {
        "intern" -> {
            when {
                formData.availableFrom.isBlank() -> ValidationResult(false, "Start date is required", "availableFrom")
                formData.availableUntil.isBlank() -> ValidationResult(false, "End date is required for internships", "availableUntil")
                !validateAvailabilityDates(formData.availableFrom, formData.availableUntil, userType).isValid ->
                    validateAvailabilityDates(formData.availableFrom, formData.availableUntil, userType)
                formData.internshipRole.isBlank() -> ValidationResult(false, "Role selection is required", "internshipRole")
                formData.skillsList.isEmpty() -> ValidationResult(false, "At least one skill is required", "skillsList")
                formData.cvFileName.isBlank() -> ValidationResult(false, "CV/Resume upload is required", "cvFileName")
                else -> ValidationResult(true)
            }
        }
        "freelancer" -> {
            when {
                formData.projectStartDate.isBlank() -> ValidationResult(false, "Project start date is required", "projectStartDate")
                formData.serviceCategory.isBlank() -> ValidationResult(false, "Service category is required", "serviceCategory")
                formData.skillsList.isEmpty() -> ValidationResult(false, "At least one skill is required", "skillsList")
                formData.hourlyRate.isBlank() -> ValidationResult(false, "Hourly rate is required", "hourlyRate")
                !validateHourlyRate(formData.hourlyRate).isValid -> validateHourlyRate(formData.hourlyRate).copy(field = "hourlyRate")
                else -> ValidationResult(true)
            }
        }
        "fulltime" -> {
            when {
                formData.joinDate.isBlank() -> ValidationResult(false, "Join date is required", "joinDate")
                formData.jobRole.isBlank() -> ValidationResult(false, "Job role is required", "jobRole")
                formData.expectedSalary.isBlank() -> ValidationResult(false, "Expected salary is required", "expectedSalary")
                !validateSalary(formData.expectedSalary).isValid -> validateSalary(formData.expectedSalary).copy(field = "expectedSalary")
                formData.skillsList.isEmpty() -> ValidationResult(false, "At least one skill is required", "skillsList")
                formData.cvFileName.isBlank() -> ValidationResult(false, "CV/Resume upload is required", "cvFileName")
                else -> ValidationResult(true)
            }
        }
        else -> ValidationResult(false, "Invalid user type")
    }
}

private fun validateContactInfo(formData: FormData, userType: String?): ValidationResult {
    // Basic validations for all user types
    when {
        formData.mobileNumber.isBlank() -> return ValidationResult(false, "Mobile number is required", "mobileNumber")
        !isValidPhoneNumberRegex(formData.mobileNumber) -> return ValidationResult(false, "Invalid mobile number format", "mobileNumber")
        formData.email.isBlank() -> return ValidationResult(false, "Email address is required", "email")
        !isValidEmail(formData.email) -> return ValidationResult(false, "Invalid email address format", "email")
    }

    return when (userType) {
        "intern" -> {
            // For interns, social profiles are optional but must be valid if provided
            when {
                formData.linkedinProfile.isNotEmpty() && !isValidLinkedInUrl(formData.linkedinProfile) ->
                    ValidationResult(false, "Invalid LinkedIn profile URL", "linkedinProfile")
                formData.githubLink.isNotEmpty() && !isValidGithubUrl(formData.githubLink) ->
                    ValidationResult(false, "Invalid GitHub profile URL", "githubLink")
                formData.portfolioWebsite.isNotEmpty() && !isValidWebsiteUrl(formData.portfolioWebsite) ->
                    ValidationResult(false, "Invalid portfolio website URL", "portfolioWebsite")
                else -> ValidationResult(true)
            }
        }
        "freelancer" -> {
            // For freelancers, professional profiles are mandatory
            when {
                formData.linkedinProfile.isBlank() -> ValidationResult(false, "LinkedIn profile is required for freelancers", "linkedinProfile")
                !isValidLinkedInUrl(formData.linkedinProfile) -> ValidationResult(false, "Invalid LinkedIn profile URL", "linkedinProfile")
                formData.githubLink.isBlank() -> ValidationResult(false, "GitHub profile is required for freelancers", "githubLink")
                !isValidGithubUrl(formData.githubLink) -> ValidationResult(false, "Invalid GitHub profile URL", "githubLink")
                formData.portfolioWebsite.isBlank() -> ValidationResult(false, "Portfolio website is required for freelancers", "portfolioWebsite")
                !isValidWebsiteUrl(formData.portfolioWebsite) -> ValidationResult(false, "Invalid portfolio website URL", "portfolioWebsite")
                formData.professionalSummary.isBlank() -> ValidationResult(false, "Professional summary is required for freelancers", "professionalSummary")
                formData.professionalSummary.length < 50 -> ValidationResult(false, "Professional summary must be at least 50 characters long", "professionalSummary")
                else -> ValidationResult(true)
            }
        }
        "fulltime" -> {
            // For fulltime, LinkedIn is mandatory, others are optional but validated
            when {
                formData.linkedinProfile.isBlank() -> ValidationResult(false, "LinkedIn profile is required for full-time positions", "linkedinProfile")
                !isValidLinkedInUrl(formData.linkedinProfile) -> ValidationResult(false, "Invalid LinkedIn profile URL", "linkedinProfile")
                formData.coverLetter.isBlank() -> ValidationResult(false, "Personal statement is required for full-time positions", "personalStatement")
                formData.coverLetter.length < 100 -> ValidationResult(false, "Personal statement must be at least 100 characters long", "personalStatement")
                formData.professionalReferences.isBlank() -> ValidationResult(false, "Professional references are required for full-time positions", "professionalReferences")
                formData.professionalReferences.length < 20 -> ValidationResult(false, "Please provide detailed reference information", "professionalReferences")
                else -> ValidationResult(true)
            }
        }
        else -> ValidationResult(true)
    }
}

// Enhanced validation helper functions
fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidLinkedInUrl(url: String): Boolean {
    if (url.isBlank()) return false
    val normalizedUrl = url.lowercase().trim()
    return normalizedUrl.contains("linkedin.com/in/") || normalizedUrl.contains("linkedin.com/pub/")
}

fun isValidGithubUrl(url: String): Boolean {
    if (url.isBlank()) return false
    val normalizedUrl = url.lowercase().trim()
    return normalizedUrl.contains("github.com/") && !normalizedUrl.endsWith("github.com/")
}

fun isValidWebsiteUrl(url: String): Boolean {
    if (url.isBlank()) return false

    // Check if it starts with http:// or https://
    return url.startsWith("http://") || url.startsWith("https://")
}

fun isValidPhoneNumberRegex(phone: String): Boolean {
    if (phone.isBlank()) return false
    // Supports multiple formats: +91 XXXXXXXXXX, +91XXXXXXXXXX, XXXXXXXXXX
    val patterns = listOf(
        "^\\+91 \\d{10}$".toRegex(),
        "^\\+91\\d{10}$".toRegex(),
        "^\\d{10}$".toRegex()
    )
    return patterns.any { it.matches(phone.trim()) }
}

// Additional validation utilities
fun validateCGPA(cgpa: String): ValidationResult {
    if (cgpa.isBlank()) return ValidationResult(false, "CGPA is required")

    return try {
        val cgpaValue = cgpa.toDouble()
        when {
            cgpaValue < 0 -> ValidationResult(false, "CGPA cannot be negative")
            cgpaValue > 10 -> ValidationResult(false, "CGPA cannot be greater than 10")
            else -> ValidationResult(true)
        }
    } catch (e: NumberFormatException) {
        ValidationResult(false, "Invalid CGPA format")
    }
}

fun validatePercentage(percentage: String): ValidationResult {
    if (percentage.isBlank()) return ValidationResult(false, "Percentage is required")

    return try {
        val percentageValue = percentage.toDouble()
        when {
            percentageValue < 0 -> ValidationResult(false, "Percentage cannot be negative")
            percentageValue > 100 -> ValidationResult(false, "Percentage cannot be greater than 100")
            else -> ValidationResult(true)
        }
    } catch (e: NumberFormatException) {
        ValidationResult(false, "Invalid percentage format")
    }
}

// Combined CGPA/Percentage validation
fun validateCGPAOrPercentage(value: String): ValidationResult {
    if (value.isBlank()) return ValidationResult(false, "CGPA/Percentage is required")

    return try {
        val numericValue = value.toDouble()
        when {
            numericValue < 0 -> ValidationResult(false, "Value cannot be negative")
            numericValue <= 10 -> validateCGPA(value) // Assume CGPA if <= 10
            numericValue <= 100 -> validatePercentage(value) // Assume percentage if <= 100
            else -> ValidationResult(false, "Value must be either CGPA (0-10) or Percentage (0-100)")
        }
    } catch (e: NumberFormatException) {
        ValidationResult(false, "Invalid number format")
    }
}

fun validateYearsOfExperience(years: String): ValidationResult {
    if (years.isBlank()) return ValidationResult(false, "Years of experience is required")

    return try {
        val yearsValue = years.toInt()
        when {
            yearsValue < 0 -> ValidationResult(false, "Years of experience cannot be negative")
            yearsValue > 50 -> ValidationResult(false, "Years of experience seems unrealistic")
            else -> ValidationResult(true)
        }
    } catch (e: NumberFormatException) {
        ValidationResult(false, "Invalid years format")
    }
}

fun validateHourlyRate(rate: String): ValidationResult {
    if (rate.isBlank()) return ValidationResult(false, "Hourly rate is required")

    return try {
        val rateValue = rate.toDouble()
        when {
            rateValue < 0 -> ValidationResult(false, "Hourly rate cannot be negative")
            rateValue > 10000 -> ValidationResult(false, "Hourly rate seems unrealistic")
            else -> ValidationResult(true)
        }
    } catch (e: NumberFormatException) {
        ValidationResult(false, "Invalid rate format")
    }
}

fun validateSalary(salary: String): ValidationResult {
    if (salary.isBlank()) return ValidationResult(false, "Expected salary is required")

    return try {
        val salaryValue = salary.toDouble()
        when {
            salaryValue < 0 -> ValidationResult(false, "Salary cannot be negative")
            salaryValue > 100000000 -> ValidationResult(false, "Salary amount seems unrealistic")
            else -> ValidationResult(true)
        }
    } catch (e: NumberFormatException) {
        ValidationResult(false, "Invalid salary format")
    }
}

// Date validation utilities
fun validateDateOfBirth(dob: String): ValidationResult {
    if (dob.isBlank()) return ValidationResult(false, "Date of birth is required")

    return try {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormatter.isLenient = false
        val birthDate = dateFormatter.parse(dob)
        val currentDate = Date()
        val calendar = Calendar.getInstance()

        if (birthDate != null) {
            calendar.time = birthDate
            val birthYear = calendar.get(Calendar.YEAR)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val age = currentYear - birthYear

            when {
                birthDate.after(currentDate) -> ValidationResult(false, "Date of birth cannot be in the future")
                age < 16 -> ValidationResult(false, "Minimum age requirement is 16 years")
                age > 100 -> ValidationResult(false, "Please verify the date of birth")
                else -> ValidationResult(true)
            }
        } else {
            ValidationResult(false, "Invalid date format")
        }
    } catch (e: Exception) {
        ValidationResult(false, "Invalid date format. Please use DD/MM/YYYY")
    }
}

fun validateAvailabilityDates(startDate: String, endDate: String, userType: String?): ValidationResult {
    if (startDate.isBlank()) return ValidationResult(false, "Start date is required")

    return try {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormatter.isLenient = false
        val start = dateFormatter.parse(startDate)
        val currentDate = Date()

        if (start != null) {
            // Allow start date to be today or in the future
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Allow today

            if (start.before(calendar.time)) {
                return ValidationResult(false, "Start date cannot be in the past")
            }

            // For interns, end date is required
            if (userType == "intern") {
                if (endDate.isBlank()) return ValidationResult(false, "End date is required for internships")

                val end = dateFormatter.parse(endDate)
                if (end != null) {
                    if (end.before(start) || end.equals(start)) {
                        return ValidationResult(false, "End date must be after start date")
                    }

                    // Calculate duration
                    val diffInMillis = end.time - start.time
                    val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

                    if (diffInDays < 30) {
                        return ValidationResult(false, "Internship duration should be at least 1 month")
                    }
                    if (diffInDays > 365) {
                        return ValidationResult(false, "Internship duration cannot exceed 1 year")
                    }
                }
            }

            ValidationResult(true)
        } else {
            ValidationResult(false, "Invalid start date format")
        }
    } catch (e: Exception) {
        ValidationResult(false, "Invalid date format. Please use DD/MM/YYYY")
    }
}

// Project dates validation for freelancers
fun validateProjectDates(startDate: String, endDate: String): ValidationResult {
    if (startDate.isBlank()) return ValidationResult(false, "Project start date is required")

    return try {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormatter.isLenient = false
        val start = dateFormatter.parse(startDate)
        val end = dateFormatter.parse(endDate)

        if (start != null && end != null) {
            when {
                end.before(start) || end.equals(start) -> ValidationResult(false, "Project end date must be after start date")
                else -> {
                    // Calculate duration
                    val diffInMillis = end.time - start.time
                    val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

                    if (diffInDays < 7) {
                        ValidationResult(false, "Project duration should be at least 1 week")
                    } else {
                        ValidationResult(true)
                    }
                }
            }
        } else {
            ValidationResult(false, "Invalid date format")
        }
    } catch (e: Exception) {
        ValidationResult(false, "Invalid date format. Please use DD/MM/YYYY")
    }
}