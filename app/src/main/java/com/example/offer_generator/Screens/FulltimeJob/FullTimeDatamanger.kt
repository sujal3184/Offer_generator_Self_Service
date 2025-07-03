package com.example.offer_generator.Screens.FulltimeJob


import android.net.Uri
import androidx.compose.runtime.*
import com.example.offer_generator.Screens.ApplicationForm.FormData
import java.text.SimpleDateFormat
import java.util.*

// Data class to represent a complete freelancer application
data class FullTimeApplication(
    val id: String = UUID.randomUUID().toString(),
    val submissionDate: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),

    // Personal Information
    val fullName: String,
    val dateOfBirth: String,
    val college : String,
    val branch : String,
    val graduationYear: String,
    val cgpaPercentage: String,
    val yearOfExperience: String ?= null,
    val previousCompany: String?,

    // Project Details
    val availableFrom: String,
    val jobRole: String,
    val skills: List<String>,
    val expectedSalary : String,
    val noticePeriod : String,
    val CVfilename: String,
    val CVuri: Uri? = null,
    val CVfilepath: String,

    // Contact Information
    val mobileNumber: String,
    val email: String,
    val linkedinProfile: String, // Required for freelancers
    val githubLink: String, // Required for freelancers
    val portfolioWebsite: String, // Required for freelancers
    val ProfessionalReferences: String, // Optional
    val coverLetter: String, // Required for freelancers

    // Application Status
    var status: FullTimeApplicationStatus,

    // HR Review Information
    val reviewedBy: String? = null,
    val reviewDate: String? = null,
    val hrComments: String? = null,

)

enum class FullTimeApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ACCEPTED,
    REJECTED,
    WITHDRAWN,
    OFFER_GENERATED  // New status for when offer letter is created
}

// Application Statistics data class
data class FullTimeApplicationStatistics(
    val total: Int,
    val submitted: Int,
    val underReview: Int,
    val accepted: Int,
    val rejected: Int,
    val withdrawn: Int,
    val offerGenerated: Int
)

// Singleton object to manage freelancer applications only
object FullTimeDatamanger {
    // Mutable list to store all applications
    private val _applications = mutableStateListOf<FullTimeApplication>()

    // Read-only access to applications
    val applications: List<FullTimeApplication> = _applications

    // Counter for tracking total submissions
    var totalSubmissions by mutableStateOf(0)
        private set

    /**
     * Submit a new freelancer application
     */
    fun submitApplication(formData: FormData): FullTimeApplication {
        val application = FullTimeApplication(
            fullName = formData.fullName,
            dateOfBirth = formData.dateOfBirth,
            college = formData.college,
            branch = formData.branch,
            graduationYear = formData.graduationYear ?: "",
            cgpaPercentage = formData.cgpaPercentage,
            previousCompany = formData.previousCompany.takeIf { it.isNotEmpty() },
            availableFrom = formData.joinDate,
            expectedSalary = formData.expectedSalary,
            noticePeriod = formData.noticePeriod,
            jobRole = formData.jobRole,
            skills = formData.skillsList.toList(),
            CVuri = formData.cvUri,
            CVfilename = formData.cvFileName,
            CVfilepath = formData.cvFilePath,
            mobileNumber = formData.mobileNumber,
            email = formData.email,
            linkedinProfile = formData.linkedinProfile,
            githubLink = formData.githubLink,
            portfolioWebsite = formData.portfolioWebsite,
            ProfessionalReferences = formData.professionalReferences,
            coverLetter = formData.coverLetter,
            status = FullTimeApplicationStatus.SUBMITTED
        )

        _applications.add(0,application)
        totalSubmissions++
        return application
    }

    /**
     * Update application status
     */
    fun updateApplicationStatus(
        id: String,
        newStatus: FullTimeApplicationStatus,
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
    fun getApplicationById(id: String): FullTimeApplication? {
        return _applications.find { it.id == id }
    }

    /**
     * Get applications statistics
     */
    fun getApplicationStatistics(): FullTimeApplicationStatistics {
        return FullTimeApplicationStatistics(
            total = _applications.size,
            submitted = _applications.count { it.status == FullTimeApplicationStatus.SUBMITTED },
            underReview = _applications.count { it.status == FullTimeApplicationStatus.UNDER_REVIEW },
            accepted = _applications.count { it.status == FullTimeApplicationStatus.ACCEPTED },
            rejected = _applications.count { it.status == FullTimeApplicationStatus.REJECTED },
            withdrawn = _applications.count { it.status == FullTimeApplicationStatus.WITHDRAWN },
            offerGenerated = _applications.count { it.status == FullTimeApplicationStatus.OFFER_GENERATED }
        )
    }

    /**
     * Get applications by status
     */
    fun getApplicationsByStatus(status: FullTimeApplicationStatus): List<FullTimeApplication> {
        return _applications.filter { it.status == status }
    }

    /**
     * Get applications by service category
     */
    fun getApplicationsByServiceCategory(jobRole: String): List<FullTimeApplication> {
        return _applications.filter { it.jobRole.contains(jobRole, ignoreCase = true) }
    }


    /**
     * Get applications by skills
     */
    fun getApplicationsBySkill(skill: String): List<FullTimeApplication> {
        return _applications.filter { application ->
            application.skills.any { it.contains(skill, ignoreCase = true) }
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
        status: FullTimeApplicationStatus? = null,
        serviceCategory: String? = null,
    ): List<FullTimeApplication> {
        return _applications.filter { application ->
            val matchesQuery = query.isEmpty() ||
                    application.fullName.contains(query, ignoreCase = true) ||
                    application.email.contains(query, ignoreCase = true) ||
                    application.jobRole.contains(query, ignoreCase = true) ||
                    application.skills.any { it.contains(query, ignoreCase = true) }

            val matchesStatus = status == null || application.status == status

            val matchesCategory = serviceCategory == null ||
                    application.jobRole.contains(serviceCategory, ignoreCase = true)


            matchesQuery && matchesStatus && matchesCategory
        }
    }

    /**
     * Clear all applications (for testing purposes)
     */
    fun clearAllApplications() {
        _applications.clear()
        totalSubmissions = 0
    }

    /**
     * Get applications submitted in last N days
     */
    fun getRecentApplications(days: Int): List<FullTimeApplication> {
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