package com.example.offer_generator.Screens.Internship

import android.net.Uri
import androidx.compose.runtime.*
import com.example.offer_generator.Screens.ApplicationForm.FormData
import java.text.SimpleDateFormat
import java.util.*

// Data class to represent a complete internship application
data class InternshipApplication(
    val id: String = UUID.randomUUID().toString(),
    val submissionDate: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),

    // Personal Information
    val fullName: String,
    val dateOfBirth: String,
    val yearOfStudy: String,
    val college: String,
    val branch: String,

    // Internship Details
    val availableFrom: String,
    val availableUntil: String,
    val internshipRole: String,
    val skills: List<String>,
    val cvFileName: String,
    val cvUri: Uri? = null,
    val cvFilePath: String,

    // Contact Information
    val mobileNumber: String,
    val email: String,
    val linkedinProfile: String,
    val githubLink: String,
    val portfolioWebsite: String,
    val references: String,
    val personalStatement: String,

    // Application Status
    var status: ApplicationStatus,

    // HR Review Information
    val reviewedBy: String? = null,
    val reviewDate: String? = null,
    val hrComments: String? = null
)

enum class ApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ACCEPTED,
    REJECTED,
    WITHDRAWN,
    OFFER_GENERATED  // New status for when offer letter is created
}

// Application Statistics data class
data class ApplicationStatistics(
    val total: Int,
    val submitted: Int,
    val underReview: Int,
    val accepted: Int,
    val rejected: Int,
    val withdrawn: Int,
    val offerGenerated: Int
)

// Singleton object to manage internship applications only
object InternshipDataManager {
    // Mutable list to store all applications
    private val _applications = mutableStateListOf<InternshipApplication>()

    // Read-only access to applications
    val applications: List<InternshipApplication> = _applications

    // Counter for tracking total submissions
    var totalSubmissions by mutableStateOf(0)
        private set

    /**
     * Submit a new internship application
     */
    fun submitApplication(formData: FormData): InternshipApplication {
        val application = InternshipApplication(
            fullName = formData.fullName,
            dateOfBirth = formData.dateOfBirth,
            yearOfStudy = formData.yearOfStudy,
            college = formData.college,
            branch = formData.branch,
            availableFrom = formData.availableFrom,
            availableUntil = formData.availableUntil,
            internshipRole = formData.internshipRole,
            skills = formData.skillsList.toList(),
            cvFileName = formData.cvFileName,
            cvUri = formData.cvUri,
            cvFilePath = formData.cvFilePath,
            mobileNumber = formData.mobileNumber,
            email = formData.email,
            linkedinProfile = formData.linkedinProfile,
            githubLink = formData.githubLink,
            portfolioWebsite = formData.portfolioWebsite,
            references = formData.references,
            personalStatement = formData.personalStatement,
            status = ApplicationStatus.SUBMITTED
        )

        _applications.add(application)
        totalSubmissions++
        return application
    }

    /**
     * Update application status
     */
    fun updateApplicationStatus(
        id: String,
        newStatus: ApplicationStatus,
        reviewedBy: String? = null,
        hrComments: String? = null
    ): Boolean {
        val index = _applications.indexOfFirst { it.id == id }
        return if (index != -1) {
            val currentApp = _applications[index]
            _applications[index] = currentApp.copy(
                status = newStatus,
                reviewedBy = reviewedBy ?: currentApp.reviewedBy,
                reviewDate = if (reviewedBy != null) SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()) else currentApp.reviewDate,
                hrComments = hrComments ?: currentApp.hrComments
            )
            true
        } else {
            false
        }
    }

    /**
     * Get application by ID
     */
    fun getApplicationById(id: String): InternshipApplication? {
        return _applications.find { it.id == id }
    }

    /**
     * Get applications statistics
     */
    fun getApplicationStatistics(): ApplicationStatistics {
        return ApplicationStatistics(
            total = _applications.size,
            submitted = _applications.count { it.status == ApplicationStatus.SUBMITTED },
            underReview = _applications.count { it.status == ApplicationStatus.UNDER_REVIEW },
            accepted = _applications.count { it.status == ApplicationStatus.ACCEPTED },
            rejected = _applications.count { it.status == ApplicationStatus.REJECTED },
            withdrawn = _applications.count { it.status == ApplicationStatus.WITHDRAWN },
            offerGenerated = _applications.count { it.status == ApplicationStatus.OFFER_GENERATED }
        )
    }

    /**
     * Get applications by status
     */
    fun getApplicationsByStatus(status: ApplicationStatus): List<InternshipApplication> {
        return _applications.filter { it.status == status }
    }

    /**
     * Get applications by college
     */
    fun getApplicationsByCollege(college: String): List<InternshipApplication> {
        return _applications.filter { it.college.contains(college, ignoreCase = true) }
    }

    /**
     * Get applications by internship role
     */
    fun getApplicationsByRole(role: String): List<InternshipApplication> {
        return _applications.filter { it.internshipRole.contains(role, ignoreCase = true) }
    }

    /**
     * Delete application by ID
     */
    fun deleteApplication(id: String): Boolean {
        val index = _applications.indexOfFirst { it.id == id }
        return if (index != -1) {
            _applications.removeAt(index)
            totalSubmissions--
            true
        } else {
            false
        }
    }

    /**
     * Clear all applications (for testing purposes)
     */
    fun clearAllApplications() {
        _applications.clear()
        totalSubmissions = 0
    }
}

