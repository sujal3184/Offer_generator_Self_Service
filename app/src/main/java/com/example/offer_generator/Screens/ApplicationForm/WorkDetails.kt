package com.example.offer_generator.Screens.ApplicationForm

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.offer_generator.R
import com.example.offer_generator.Screens.HR.EmploymentType
import com.example.offer_generator.ViewModels.JobOpeningsViewModel
import com.example.offer_generator.ViewModels.WhoLoginViewModel
import java.io.File

@Composable
fun WorkDetailsStep(formData: FormData, viewModel: WhoLoginViewModel,jobViewModel: JobOpeningsViewModel) {
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showUntilDatePicker by remember { mutableStateOf(false) }
    var showJoinDatePicker by remember { mutableStateOf(false) }
    var roleExpanded by remember { mutableStateOf(false) }
    var experienceExpanded by remember { mutableStateOf(false) }
    var newSkill by remember { mutableStateOf("") }

    val userType = viewModel.getCurrentUserType()
    val context = LocalContext.current

    // Determine employment type based on login state
    val currentEmploymentType = when {
        viewModel.isUserLoggedIn.value -> EmploymentType.INTERN
        viewModel.isFreeLancerLoggedIn.value -> EmploymentType.FREELANCER
        viewModel.isFulltimeEmployeeLoggedIn.value -> EmploymentType.FULL_TIME // Assuming you have this
        else -> null
    }

    // Get available role options based on current employment type
    val roleOptions = if (currentEmploymentType != null) {
        jobViewModel.getAvailableRolesForEmploymentType(currentEmploymentType)
    } else {
        emptyList()
    }

    val experienceOptions = listOf(
        "0-1 years",
        "1-2 years",
        "2-3 years",
        "3-5 years",
        "5-7 years",
        "7-10 years",
        "10+ years"
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "file_${System.currentTimeMillis()}.pdf"

            // Save the actual file
            val savedPath = saveFileToInternalStorage(context, it, fileName)
            if (savedPath != null) {
                // Set the appropriate fields based on user type
                when (userType) {
                    "intern" -> {
                        formData.cvFileName = fileName
                        formData.cvFilePath = savedPath
                        formData.cvUri = null
                    }
                    "fulltime" -> {
                        formData.cvFileName = fileName
                        formData.cvFilePath = savedPath
                        formData.cvUri = null
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (userType) {
            "intern" -> {
                InternFields(
                    formData = formData,
                    roleOptions = roleOptions,
                    showFromDatePicker = showFromDatePicker,
                    onShowFromDatePickerChange = { showFromDatePicker = it },
                    showUntilDatePicker = showUntilDatePicker,
                    onShowUntilDatePickerChange = { showUntilDatePicker = it },
                    roleExpanded = roleExpanded,
                    onRoleExpandedChange = { roleExpanded = it },
                    newSkill = newSkill,
                    onNewSkillChange = { newSkill = it },
                    onFileSelect = { filePickerLauncher.launch("application/pdf") },
                    userType = userType
                )
            }
            "freelancer" -> {
                FreelancerFields(
                    formData = formData,
                    roleOptions = roleOptions,
                    experienceOptions = experienceOptions,
                    showFromDatePicker = showFromDatePicker,
                    onShowFromDatePickerChange = { showFromDatePicker = it },
                    showUntilDatePicker = showUntilDatePicker,
                    onShowUntilDatePickerChange = { showUntilDatePicker = it },
                    roleExpanded = roleExpanded,
                    onRoleExpandedChange = { roleExpanded = it },
                    experienceExpanded = experienceExpanded,
                    onExperienceExpandedChange = { experienceExpanded = it },
                    newSkill = newSkill,
                    onNewSkillChange = { newSkill = it },
                    onFileSelect = { filePickerLauncher.launch("application/pdf") },
                    userType = userType
                )
            }
            "fulltime" -> {
                FullTimeFields(
                    formData = formData,
                    roleOptions = roleOptions,
                    experienceOptions = experienceOptions,
                    showJoinDatePicker = showJoinDatePicker,
                    onShowJoinDatePickerChange = { showJoinDatePicker = it },
                    roleExpanded = roleExpanded,
                    onRoleExpandedChange = { roleExpanded = it },
                    experienceExpanded = experienceExpanded,
                    onExperienceExpandedChange = { experienceExpanded = it },
                    newSkill = newSkill,
                    onNewSkillChange = { newSkill = it },
                    onFileSelect = { filePickerLauncher.launch("application/pdf") },
                    userType = userType
                )
            }
        }
    }
}

@Composable
fun InternFields(
    formData: FormData,
    roleOptions: List<String>,
    showFromDatePicker: Boolean,
    onShowFromDatePickerChange: (Boolean) -> Unit,
    showUntilDatePicker: Boolean,
    onShowUntilDatePickerChange: (Boolean) -> Unit,
    roleExpanded: Boolean,
    onRoleExpandedChange: (Boolean) -> Unit,
    newSkill: String,
    onNewSkillChange: (String) -> Unit,
    onFileSelect: () -> Unit,
    userType: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Available From Date
        DatePickerField(
            value = formData.availableFrom,
            onValueChange = { formData.availableFrom = it },
            label = "Available From *",
            placeholder = "Select start date",
            showDatePicker = showFromDatePicker,
            onShowDatePickerChange = onShowFromDatePickerChange,
            disablePastDates = true
        )


        // Available Until Date
        DatePickerField(
            value = formData.availableUntil,
            onValueChange = { formData.availableUntil = it },
            label = "Available Until *",
            placeholder = "Select end date",
            showDatePicker = showUntilDatePicker,
            onShowDatePickerChange = onShowUntilDatePickerChange,
            disablePastDates = true
        )

        // Internship Role
        DropdownField(
            value = formData.internshipRole,
            onValueChange = { formData.internshipRole = it },
            label = "Internship Role Applying For *",
            placeholder = "Select a role",
            options = roleOptions,
            expanded = roleExpanded,
            onExpandedChange = onRoleExpandedChange
        )


        // Core Skills
        SkillsField(
            skills = formData.skillsList,
            onSkillsChange = { formData.skillsList = it },
            newSkill = newSkill,
            onNewSkillChange = onNewSkillChange,
            userType = userType
        )

        // Upload CV/Resume
        FileUploadField(
            fileName = formData.cvFileName,
            onFileSelect = onFileSelect,
            userType = userType
        )
    }
}

@Composable
fun FreelancerFields(
    formData: FormData,
    roleOptions: List<String>,
    experienceOptions: List<String>,
    showFromDatePicker: Boolean,
    onShowFromDatePickerChange: (Boolean) -> Unit,
    showUntilDatePicker: Boolean,
    onShowUntilDatePickerChange: (Boolean) -> Unit,
    roleExpanded: Boolean,
    onRoleExpandedChange: (Boolean) -> Unit,
    experienceExpanded: Boolean,
    onExperienceExpandedChange: (Boolean) -> Unit,
    newSkill: String,
    onNewSkillChange: (String) -> Unit,
    onFileSelect: () -> Unit,
    userType: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Project Start Date
        DatePickerField(
            value = formData.projectStartDate,
            onValueChange = { formData.projectStartDate = it },
            label = "Project Start Date *",
            placeholder = "Select start date",
            showDatePicker = showFromDatePicker,
            onShowDatePickerChange = onShowFromDatePickerChange,
            disablePastDates = true
        )

        // Project End Date
        DatePickerField(
            value = formData.projectEndDate,
            onValueChange = { formData.projectEndDate = it },
            label = "Project End Date (if applicable)",
            placeholder = "Select end date",
            showDatePicker = showUntilDatePicker,
            onShowDatePickerChange = onShowUntilDatePickerChange,
            disablePastDates = true
        )

        // Service Category
        DropdownField(
            value = formData.serviceCategory,
            onValueChange = { formData.serviceCategory = it },
            label = "Freelance Service Category *",
            placeholder = "Select your service category",
            options = roleOptions,
            expanded = roleExpanded,
            onExpandedChange = onRoleExpandedChange
        )


        // Hourly Rate
        FormTextField(
            value = formData.hourlyRate ?: "",
            onValueChange = { formData.hourlyRate = it },
            label = "Hourly Rate (₹)",
            placeholder = "e.g., 500, 1000",
        )

        // Professional Skills
        SkillsField(
            skills = formData.skillsList,
            onSkillsChange = { formData.skillsList = it },
            newSkill = newSkill,
            onNewSkillChange = onNewSkillChange,
            userType = userType
        )

    }
}

@Composable
fun FullTimeFields(
    formData: FormData,
    roleOptions: List<String>,
    experienceOptions: List<String>,
    showJoinDatePicker: Boolean,
    onShowJoinDatePickerChange: (Boolean) -> Unit,
    roleExpanded: Boolean,
    onRoleExpandedChange: (Boolean) -> Unit,
    experienceExpanded: Boolean,
    onExperienceExpandedChange: (Boolean) -> Unit,
    newSkill: String,
    onNewSkillChange: (String) -> Unit,
    onFileSelect: () -> Unit,
    userType: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Join Date
        DatePickerField(
            value = formData.joinDate,
            onValueChange = { formData.joinDate = it },
            label = "Available to Join From *",
            placeholder = "Select joining date",
            showDatePicker = showJoinDatePicker,
            onShowDatePickerChange = onShowJoinDatePickerChange,
            disablePastDates = true
        )

        // Job Role
        DropdownField(
            value = formData.jobRole,
            onValueChange = { formData.jobRole = it },
            label = "Position Applying For *",
            placeholder = "Select a position",
            options = roleOptions,
            expanded = roleExpanded,
            onExpandedChange = onRoleExpandedChange
        )


        // Expected Salary
        FormTextField(
            value = formData.expectedSalary ?: "",
            onValueChange = { formData.expectedSalary = it },
            label = "Expected Annual Salary (₹)",
            placeholder = "e.g., 600000, 1200000",
        )

        // Notice Period
        OutlinedTextField(
            value = formData.noticePeriod ?: "",
            onValueChange = { formData.noticePeriod = it },
            label = { Text("Notice Period") },
            placeholder = { Text("e.g., Immediate, 30 days, 60 days") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Black.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Black.copy(alpha = 0.3f)
            )
        )

        // Technical Skills
        SkillsField(
            skills = formData.skillsList,
            onSkillsChange = { formData.skillsList = it },
            newSkill = newSkill,
            onNewSkillChange = onNewSkillChange,
            userType = userType
        )

        // Upload Resume
        FileUploadField(
            fileName = formData.cvFileName,
            onFileSelect = onFileSelect,
            userType = userType
        )
    }
}

@Composable
fun SkillsField(
    skills: MutableList<String>,
    onSkillsChange: (MutableList<String>) -> Unit,
    newSkill: String,
    onNewSkillChange: (String) -> Unit,
    userType: String?
) {
    val purpleColor = colorResource(id = R.color.purple)

    Column {
        Text(
            text = getSkillsLabel(userType),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = newSkill,
                onValueChange = onNewSkillChange,
                placeholder = {
                    Text(
                        text = getSkillsPlaceholder(userType),
                        color = Color.DarkGray
                    )
                },
                modifier = Modifier.weight(1f),
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

            Button(
                onClick = {
                    if (newSkill.isNotBlank() && !skills.contains(newSkill)) {
                        val updatedSkills = skills.toMutableList()
                        updatedSkills.add(newSkill)
                        onSkillsChange(updatedSkills)
                        onNewSkillChange("")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = purpleColor, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Skill")
            }
        }

        if (skills.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(skills) { skill ->
                    FilterChip(
                        onClick = {
                            val updatedSkills = skills.toMutableList()
                            updatedSkills.remove(skill)
                            onSkillsChange(updatedSkills)
                        },
                        label = { Text(skill) },
                        selected = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FileUploadField(
    fileName: String,
    onFileSelect: () -> Unit,
    userType: String?
) {
    Column {
        Text(
            text = getFileUploadLabel(userType),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = onFileSelect,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Upload", tint = colorResource(R.color.blue))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (fileName.isEmpty()) "Choose PDF file" else fileName,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
        }
    }
}

// Helper functions for dynamic labels based on user type
private fun getSkillsLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "Core Skills and Strengths *"
        "freelancer" -> "Professional Skills and Expertise *"
        "fulltime" -> "Technical Skills and Competencies *"
        else -> "Skills *"
    }
}

private fun getSkillsPlaceholder(userType: String?): String {
    return when (userType) {
        "intern" -> "Add your skills (e.g., JavaScript, Python)"
        "freelancer" -> "Add your expertise (e.g., React.js, Node.js)"
        "fulltime" -> "Add your technical skills (e.g., AWS, Docker)"
        else -> "Add your skills"
    }
}

private fun getFileUploadLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "Upload CV/Resume *"
        "freelancer" -> "Upload Portfolio/Resume *"
        "fulltime" -> "Upload Resume *"
        else -> "Upload CV *"
    }
}

private fun saveFileToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val sanitizedFileName = "${System.currentTimeMillis()}_${fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")}"
        val file = File(context.filesDir, sanitizedFileName)

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}