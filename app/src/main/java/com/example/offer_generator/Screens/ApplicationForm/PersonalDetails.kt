package com.example.offer_generator.Screens.ApplicationForm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.offer_generator.ViewModels.WhoLoginViewModel

@Composable
fun PersonalInfoStep(formData: FormData, viewModel: WhoLoginViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var collegeExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }
    var graduationYearExpanded by remember { mutableStateOf(false) }

    val yearOptions = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "Final Year")
    val graduationYearOptions = listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028")
    val collegeOptions = listOf(
        "Indian Institute of Technology, Delhi",
        "Indian Institute of Technology, Bombay",
        "Indian Institute of Technology, Madras",
        "Indian Institute of Technology, Kanpur",
        "Indian Institute of Technology, Kharagpur",
        "Indian Institute of Technology, Roorkee",
        "Indian Institute of Technology, Guwahati",
        "National Institute of Technology, Trichy",
        "National Institute of Technology, Warangal",
        "National Institute of Technology, Surathkal",
        "Birla Institute of Technology and Science, Pilani",
        "Delhi Technological University",
        "College of Engineering, Pune",
        "Other"
    )
    val branchOptions = listOf(
        "Computer Science and Engineering",
        "Electronics and Communication Engineering",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering",
        "Chemical Engineering",
        "Information Technology",
        "BioTechnology",
        "Aerospace Engineering",
        "Instrumentation Engineering",
        "Metallurgical Engineering",
        "Production Engineering",
        "Industrial Engineering",
        "Other"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Full Name - Common for all user types
        FormTextField(
            value = formData.fullName,
            onValueChange = { formData.fullName = it },
            label = "Full Name *",
            placeholder = "Enter your full name",
        )

        // Date of Birth - Common for all user types
        DatePickerField(
            value = formData.dateOfBirth,
            onValueChange = { formData.dateOfBirth = it },
            label = "Date of Birth *",
            placeholder = "Select your date of birth",
            showDatePicker = showDatePicker,
            onShowDatePickerChange = { showDatePicker = it }
        )

        // Freelancer specific fields
        if (viewModel.getCurrentUserType() == "freelancer") {
            EnhancedFormTextField(
                value = formData.yearsOfExperience ?: "",
                onValueChange = { formData.yearsOfExperience = it },
                label = "Years of Experience *",
                placeholder =  "e.g., 2.5",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                validator = { years -> years.isNotEmpty()
                },
                errorMessage = "Years of experience is required",
                isRequired = viewModel.isFreeLancerLoggedIn.value
            )

            FormTextField(
                value = formData.currentLocation,
                onValueChange = { formData.currentLocation = it },
                label = "Current Location *",
                placeholder = "e.g. Bangalore, Karnataka",
            )

            FormTextField(
                value = formData.availabilityStatus,
                onValueChange = { formData.availabilityStatus = it },
                label = "Availability Status *",
                placeholder = "e.g. Available in 4 weeks or 2 months",
            )
        }

        // Intern specific fields
        if (viewModel.getCurrentUserType() == "intern") {
            // Year of Study Dropdown
            DropdownField(
                value = formData.yearOfStudy,
                onValueChange = { formData.yearOfStudy = it },
                label = "Year of Study *",
                placeholder = "Select year",
                options = yearOptions,
                expanded = yearExpanded,
                onExpandedChange = { yearExpanded = it }
            )

            // College/University Dropdown
            DropdownField(
                value = formData.college,
                onValueChange = { formData.college = it },
                label = "College / University *",
                placeholder = "Select your institution",
                options = collegeOptions,
                expanded = collegeExpanded,
                onExpandedChange = { collegeExpanded = it }
            )

            // Branch Dropdown
            DropdownField(
                value = formData.branch,
                onValueChange = { formData.branch = it },
                label = "Branch / Major *",
                placeholder = "Select your branch",
                options = branchOptions,
                expanded = branchExpanded,
                onExpandedChange = { branchExpanded = it }
            )
        }

        // Full-time employee specific fields
        if (viewModel.getCurrentUserType() == "fulltime") {
            // College/University Dropdown
            DropdownField(
                value = formData.college,
                onValueChange = { formData.college = it },
                label = "College / University *",
                placeholder = "Select your institution",
                options = collegeOptions,
                expanded = collegeExpanded,
                onExpandedChange = { collegeExpanded = it }
            )

            // Branch Dropdown
            DropdownField(
                value = formData.branch,
                onValueChange = { formData.branch = it },
                label = "Branch / Major *",
                placeholder = "Select your branch",
                options = branchOptions,
                expanded = branchExpanded,
                onExpandedChange = { branchExpanded = it }
            )
            // Graduation year Dropdown
            DropdownField(
                value = formData.graduationYear,
                onValueChange = { formData.graduationYear = it },
                label = "Graduation Year*",
                placeholder = "Select your graduation year",
                options = graduationYearOptions,
                expanded = graduationYearExpanded,
                onExpandedChange = { graduationYearExpanded = it }
            )


            FormTextField(
                value = formData.cgpaPercentage,
                onValueChange = { formData.cgpaPercentage = it },
                label = "Overall Percentage/CGPA *",
                placeholder = "e.g. 8.95 or 95%",
            )

            EnhancedFormTextField(
                value = formData.yearsOfExperience ?: "",
                onValueChange = { formData.yearsOfExperience = it },
                label = "Years of Experience *",
                placeholder =  "e.g., 2.5",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                validator = { years -> years.isNotEmpty()
                },
                errorMessage = "Years of experience is required",
                isRequired = viewModel.isFulltimeEmployeeLoggedIn.value
            )

            FormTextField(
                value = formData.previousCompany,
                onValueChange = { formData.previousCompany = it },
                label = "Previous Company (if any)",
                placeholder = "e.g. Google, Microsoft, Startup Name",
            )
        }
    }
}