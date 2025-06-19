package com.example.offer_generator.Screens.Freelancer

import android.net.Uri
import androidx.compose.runtime.*
import com.example.offer_generator.Screens.ApplicationForm.FormData
import java.text.SimpleDateFormat
import java.util.*

// Data class to represent a complete freelancer application
data class FreelancerApplication(
    val id: String = UUID.randomUUID().toString(),
    val submissionDate: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),

    // Personal Information
    val fullName: String,
    val dateOfBirth: String,
    val yearsOfExperience: String,
    val currentLocation: String,
    val availabilityStatus: String,

    // Project Details
    val projectStartDate: String,
    val projectEndDate: String?, // Optional for freelancers
    val serviceCategory: String,
    val skills: List<String>,
    val portfolioWebsite: String, // Required for freelancers

    // Contact Information
    val mobileNumber: String,
    val email: String,
    val linkedinProfile: String, // Required for freelancers
    val githubLink: String, // Required for freelancers
    val clientReferences: String, // Optional
    val professionalSummary: String, // Required for freelancers

    // Application Status
    var status: FreelancerApplicationStatus,

    // HR Review Information
    val reviewedBy: String? = null,
    val reviewDate: String? = null,
    val hrComments: String? = null,

    // Freelancer specific fields
    val hourlyRate: String? = null,
    val preferredProjectDuration: String? = null,
    val workingTimeZone: String? = null
)

enum class FreelancerApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ACCEPTED,
    REJECTED,
    WITHDRAWN,
    OFFER_GENERATED  // New status for when offer letter is created
}

// Application Statistics data class
data class FreelancerApplicationStatistics(
    val total: Int,
    val submitted: Int,
    val underReview: Int,
    val accepted: Int,
    val rejected: Int,
    val withdrawn: Int,
    val offerGenerated: Int
)

// Singleton object to manage freelancer applications only
object FreelancerDataManager {
    // Mutable list to store all applications
    private val _applications = mutableStateListOf<FreelancerApplication>()

    // Read-only access to applications
    val applications: List<FreelancerApplication> = _applications

    // Counter for tracking total submissions
    var totalSubmissions by mutableStateOf(0)
        private set

    // Flag to track if sample data has been initialized
    private var sampleDataInitialized = false

    /**
     * Submit a new freelancer application
     */
    fun submitApplication(formData: FormData): FreelancerApplication {
        val application = FreelancerApplication(
            fullName = formData.fullName,
            dateOfBirth = formData.dateOfBirth,
            yearsOfExperience = formData.yearsOfExperience ?: "",
            currentLocation = formData.currentLocation,
            availabilityStatus = formData.availabilityStatus,
            projectStartDate = formData.projectStartDate,
            projectEndDate = formData.projectEndDate.takeIf { it.isNotEmpty() },
            serviceCategory = formData.serviceCategory, // Using same field as role
            skills = formData.skillsList.toList(),
            hourlyRate = formData.hourlyRate,
            mobileNumber = formData.mobileNumber,
            email = formData.email,
            linkedinProfile = formData.linkedinProfile,
            githubLink = formData.githubLink,
            portfolioWebsite = formData.portfolioWebsite,
            clientReferences = formData.clientReferences,
            professionalSummary = formData.professionalSummary,
            status = FreelancerApplicationStatus.SUBMITTED
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
        newStatus: FreelancerApplicationStatus,
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
     * Update freelancer specific details
     */
    fun updateFreelancerDetails(
        id: String,
        hourlyRate: String? = null,
        preferredProjectDuration: String? = null,
        workingTimeZone: String? = null
    ): Boolean {
        val index = _applications.indexOfFirst { it.id == id }
        return if (index != -1) {
            val currentApp = _applications[index]
            _applications[index] = currentApp.copy(
                hourlyRate = hourlyRate ?: currentApp.hourlyRate,
                preferredProjectDuration = preferredProjectDuration ?: currentApp.preferredProjectDuration,
                workingTimeZone = workingTimeZone ?: currentApp.workingTimeZone
            )
            true
        } else {
            false
        }
    }

    /**
     * Get application by ID
     */
    fun getApplicationById(id: String): FreelancerApplication? {
        return _applications.find { it.id == id }
    }

    /**
     * Get applications statistics
     */
    fun getApplicationStatistics(): FreelancerApplicationStatistics {
        return FreelancerApplicationStatistics(
            total = _applications.size,
            submitted = _applications.count { it.status == FreelancerApplicationStatus.SUBMITTED },
            underReview = _applications.count { it.status == FreelancerApplicationStatus.UNDER_REVIEW },
            accepted = _applications.count { it.status == FreelancerApplicationStatus.ACCEPTED },
            rejected = _applications.count { it.status == FreelancerApplicationStatus.REJECTED },
            withdrawn = _applications.count { it.status == FreelancerApplicationStatus.WITHDRAWN },
            offerGenerated = _applications.count { it.status == FreelancerApplicationStatus.OFFER_GENERATED }
        )
    }

    /**
     * Get applications by status
     */
    fun getApplicationsByStatus(status: FreelancerApplicationStatus): List<FreelancerApplication> {
        return _applications.filter { it.status == status }
    }

    /**
     * Get applications by service category
     */
    fun getApplicationsByServiceCategory(category: String): List<FreelancerApplication> {
        return _applications.filter { it.serviceCategory.contains(category, ignoreCase = true) }
    }

    /**
     * Get applications by location
     */
    fun getApplicationsByLocation(location: String): List<FreelancerApplication> {
        return _applications.filter { it.currentLocation.contains(location, ignoreCase = true) }
    }

    /**
     * Get applications by skills
     */
    fun getApplicationsBySkill(skill: String): List<FreelancerApplication> {
        return _applications.filter { application ->
            application.skills.any { it.contains(skill, ignoreCase = true) }
        }
    }

    /**
     * Get applications by experience range
     */
    fun getApplicationsByExperienceRange(minYears: Float, maxYears: Float): List<FreelancerApplication> {
        return _applications.filter { application ->
            try {
                val experience = application.yearsOfExperience.toFloatOrNull() ?: 0f
                experience in minYears..maxYears
            } catch (e: Exception) {
                false
            }
        }
    }

//    /**
//     * Get active projects (contracts signed or project active)
//     */
//    fun getActiveProjects(): List<FreelancerApplication> {
//        return _applications.filter {
//            it.status == FreelancerApplicationStatus.CONTRACT_SIGNED ||
//                    it.status == FreelancerApplicationStatus.PROJECT_ACTIVE
//        }
//    }
//
//    /**
//     * Get completed projects
//     */
//    fun getCompletedProjects(): List<FreelancerApplication> {
//        return _applications.filter {
//            it.status == FreelancerApplicationStatus.PROJECT_COMPLETED ||
//                    it.status == FreelancerApplicationStatus.PAYMENT_COMPLETED
//        }
//    }
//
//    /**
//     * Get applications pending payment
//     */
//    fun getApplicationsPendingPayment(): List<FreelancerApplication> {
//        return _applications.filter { it.status == FreelancerApplicationStatus.PAYMENT_PENDING }
//    }

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
     * Search applications by multiple criteria
     */
    fun searchApplications(
        query: String,
        status: FreelancerApplicationStatus? = null,
        serviceCategory: String? = null,
        minExperience: Float? = null,
        maxExperience: Float? = null
    ): List<FreelancerApplication> {
        return _applications.filter { application ->
            val matchesQuery = query.isEmpty() ||
                    application.fullName.contains(query, ignoreCase = true) ||
                    application.email.contains(query, ignoreCase = true) ||
                    application.serviceCategory.contains(query, ignoreCase = true) ||
                    application.skills.any { it.contains(query, ignoreCase = true) }

            val matchesStatus = status == null || application.status == status

            val matchesCategory = serviceCategory == null ||
                    application.serviceCategory.contains(serviceCategory, ignoreCase = true)

            val matchesExperience = if (minExperience != null || maxExperience != null) {
                try {
                    val experience = application.yearsOfExperience.toFloatOrNull() ?: 0f
                    (minExperience == null || experience >= minExperience) &&
                            (maxExperience == null || experience <= maxExperience)
                } catch (e: Exception) {
                    false
                }
            } else {
                true
            }

            matchesQuery && matchesStatus && matchesCategory && matchesExperience
        }
    }

    /**
     * Clear all applications (for testing purposes)
     */
    fun clearAllApplications() {
        _applications.clear()
        totalSubmissions = 0
        sampleDataInitialized = false
    }

    /**
     * Reset to sample data only
     */
    fun resetToSampleData() {
        clearAllApplications()
    }

    /**
     * Get applications submitted in last N days
     */
    fun getRecentApplications(days: Int): List<FreelancerApplication> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val cutoffDate = calendar.time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        return _applications.filter { application ->
            try {
                val submissionDate = dateFormat.parse(application.submissionDate)
                submissionDate?.after(cutoffDate) == true
            } catch (e: Exception) {
                false
            }
        }
    }
}