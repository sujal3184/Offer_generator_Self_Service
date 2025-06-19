package com.example.offer_generator.Screens.ApplicationForm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.offer_generator.R
import com.example.offer_generator.ViewModels.WhoLoginViewModel

@Composable
fun ReviewAndSubmitStep(
    formData: FormData,
    onEditStep: (Int) -> Unit,
    viewModel: WhoLoginViewModel
) {
    val purpleColor = colorResource(id = R.color.purple)
    val userType = viewModel.getCurrentUserType()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = getReviewTitle(userType),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = purpleColor
        )

        // Personal Info Review
        ReviewSection(
            title = "Personal Information",
            onEdit = { onEditStep(0) }
        ) {
            ReviewItem("Full Name", formData.fullName)
            ReviewItem("Date of Birth", formData.dateOfBirth)

            // User type specific personal info
            when (userType) {
                "intern" -> {
                    ReviewItem("Year of Study", formData.yearOfStudy)
                    ReviewItem("College", formData.college)
                    ReviewItem("Branch", formData.branch)
                }
                "freelancer" -> {
                    ReviewItem("Years of Experience", formData.yearsOfExperience)
                    ReviewItem("Current Location", formData.currentLocation)
                    ReviewItem("Availability Status", formData.availabilityStatus)
                }
                "fulltime" -> {
                    ReviewItem("Current Location", formData.currentLocation)
                    ReviewItem("College", formData.college)
                    ReviewItem("Branch", formData.branch)
                    ReviewItem("Graduation Year", formData.graduationYear)
                    ReviewItem("CGPA/Percentage", formData.cgpaPercentage)
//                    if (formData.currentCompany?.isNotEmpty() == true) {
//                        ReviewItem("Current Company", formData.currentCompany)
//                    }
                    ReviewItem("Years of Experience", formData.yearsOfExperience)
                }
            }
        }

        // Work/Application Details Review
        ReviewSection(
            title = getWorkDetailsSectionTitle(userType),
            onEdit = { onEditStep(1) }
        ) {

            ReviewItem(getAvailabilityFromLabel(userType), formData.availableFrom)

            // Show availability until for interns and freelancers
            if (userType != "fulltime" && formData.availableUntil.isNotEmpty()) {
                ReviewItem(getAvailabilityUntilLabel(userType), formData.availableUntil)
            }

            when(userType){
                "intern" -> ReviewItem("Internship Role", formData.internshipRole)
                "freelancer" -> ReviewItem("Service Category", formData.internshipRole)
                "fulltime" -> ReviewItem("Position", formData.internshipRole)
            }
            ReviewItem(getSkillsLabel(userType), formData.skillsList.joinToString(", "))
            when(userType){
                "intern" -> ReviewItem("CV/Resume", formData.cvFileName)
                "fulltime" -> ReviewItem("Resume", formData.cvFileName)
            }
        }

        // Contact Info Review
        ReviewSection(
            title = "Contact Information",
            onEdit = { onEditStep(2) }
        ) {
            ReviewItem("Mobile Number", formData.mobileNumber)
            ReviewItem("Email", formData.email)

            // LinkedIn - required for freelancers and full-time
            if (formData.linkedinProfile.isNotEmpty()) {
                ReviewItem("LinkedIn Profile", formData.linkedinProfile)
            }

            // GitHub - required for freelancers and full-time
            if (formData.githubLink.isNotEmpty()) {
                ReviewItem("GitHub Link", formData.githubLink)
            }

            // Portfolio - required for freelancers
            if (formData.portfolioWebsite.isNotEmpty()) {
                ReviewItem("Portfolio Website", formData.portfolioWebsite)
            }

            // References - required for full-time

            if(formData.references.isNotEmpty()){
                ReviewItem("References", formData.references)
            }
            if(formData.clientReferences.isNotEmpty()){
                ReviewItem("Client References", formData.clientReferences)
            }

            // Personal Statement/Summary/Cover Letter
            if (formData.personalStatement.isNotEmpty()) {
                ReviewItem("personal statement",formData.personalStatement)
            }
            if(formData.professionalSummary.isNotEmpty()){
                ReviewItem("Professional Summary",formData.professionalSummary)
            }

//            // Additional full-time field from contact info
//            if (userType == "fulltime" && formData.currentCompany?.isNotEmpty() == true) {
//                ReviewItem("Current/Previous Company", formData.currentCompany)
//            }
        }
    }
}

@Composable
fun ReviewSection(
    title: String,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA),
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                TextButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = colorResource(R.color.Lightpurple)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", color = colorResource(R.color.Lightpurple))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ReviewItem(label: String, value: String?) {
    if (!value.isNullOrEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        ) {
            Text(
                text = "$label: ",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(120.dp),
                color = Color(0xFF374151)
            )
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
        }
    }
}

// Helper functions for dynamic labels based on user type
private fun getReviewTitle(userType: String?): String {
    return when (userType) {
        "intern" -> "Review Your Internship Application"
        "freelancer" -> "Review Your Freelance Profile"
        "fulltime" -> "Review Your Job Application"
        else -> "Review Your Application"
    }
}

private fun getWorkDetailsSectionTitle(userType: String?): String {
    return when (userType) {
        "intern" -> "Internship Details"
        "freelancer" -> "Freelance Details"
        "fulltime" -> "Work Details"
        else -> "Work Details"
    }
}

private fun getAvailabilityFromLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "Available From"
        "freelancer" -> "Project Start Date"
        "fulltime" -> "Available to Join From"
        else -> "Available From"
    }
}

private fun getAvailabilityUntilLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "Available Until"
        "freelancer" -> "Project End Date"
        else -> "Available Until"
    }
}

private fun getRoleLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "Internship Role"
        "freelancer" -> "Service Category"
        "fulltime" -> "Position"
        else -> "Role"
    }
}

private fun getSkillsLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "Core Skills"
        "freelancer" -> "Professional Skills"
        "fulltime" -> "Technical Skills"
        else -> "Skills"
    }
}

private fun getFileUploadLabel(userType: String?): String {
    return when (userType) {
        "intern" -> "CV/Resume"
        "freelancer" -> "Portfolio/Resume"
        "fulltime" -> "Resume"
        else -> "CV"
    }
}