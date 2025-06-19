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

    init {
        // Add sample application for testing
        addSampleApplication()
    }

    /**
     * Add a sample application for testing purposes
     */
    private fun addSampleApplication() {
        val sampleApplication = InternshipApplication(
            id = "sample-001",
            submissionDate = "15/06/2025 14:30:00",
            fullName = "Priya Sharma",
            dateOfBirth = "15/03/2003",
            yearOfStudy = "3rd Year",
            college = "Indian Institute of Technology Delhi",
            branch = "Computer Science Engineering",
            availableFrom = "01/07/2025",
            availableUntil = "31/08/2025",
            internshipRole = "Android Developer",
            skills = listOf("Kotlin", "Java", "Android Studio", "Firebase", "Git", "Jetpack Compose"),
            cvFileName = "Priya_Sharma_Resume.pdf",
            cvUri = null,
            cvFilePath = "/storage/emulated/0/Documents/Priya_Sharma_Resume.pdf",
            mobileNumber = "+91 9876543210",
            email = "priya.sharma@iitd.ac.in",
            linkedinProfile = "https://linkedin.com/in/priya-sharma-dev",
            githubLink = "https://github.com/priyasharma-dev",
            portfolioWebsite = "https://priyasharma.dev",
            references = "Dr. Rajesh Kumar, Professor, CSE Department, IIT Delhi - rajesh.kumar@iitd.ac.in",
            personalStatement = "I am a passionate computer science student with strong foundation in mobile app development. I have experience building Android applications using Kotlin and Jetpack Compose. I'm eager to contribute to innovative projects and learn from industry professionals during this internship.",
            status = ApplicationStatus.SUBMITTED
        )

        _applications.add(sampleApplication)
        totalSubmissions++
    }

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

    /**
     * Add multiple sample applications for comprehensive testing
     */
    fun addMoreSampleApplications() {
        val sampleApplications = listOf(
            InternshipApplication(
                id = "sample-002",
                submissionDate = "16/06/2025 10:15:00",
                fullName = "Rahul Gupta",
                dateOfBirth = "22/08/2002",
                yearOfStudy = "4th Year",
                college = "Delhi Technological University",
                branch = "Information Technology",
                availableFrom = "20/06/2025",
                availableUntil = "20/08/2025",
                internshipRole = "Backend Developer",
                skills = listOf("Java", "Spring Boot", "MySQL", "REST APIs", "Docker"),
                cvFileName = "Rahul_Gupta_CV.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Rahul_Gupta_CV.pdf",
                mobileNumber = "+91 8765432109",
                email = "rahul.gupta@dtu.ac.in",
                linkedinProfile = "https://linkedin.com/in/rahul-gupta-backend",
                githubLink = "https://github.com/rahulgupta-backend",
                portfolioWebsite = "https://rahulgupta.tech",
                references = "Prof. Anita Singh, IT Department, DTU - anita.singh@dtu.ac.in",
                personalStatement = "Backend development enthusiast with experience in Java and Spring Boot. I have worked on several projects involving REST API development and database management.",
                status = ApplicationStatus.UNDER_REVIEW,
                reviewedBy = "HR Team",
                reviewDate = "17/06/2025 09:30:00",
                hrComments = "Strong technical background, scheduled for technical interview."
            ),
            InternshipApplication(
                id = "sample-003",
                submissionDate = "17/06/2025 16:45:00",
                fullName = "Sneha Patel",
                dateOfBirth = "10/12/2003",
                yearOfStudy = "2nd Year",
                college = "Netaji Subhas University of Technology",
                branch = "Electronics and Communication",
                availableFrom = "01/08/2025",
                availableUntil = "30/09/2025",
                internshipRole = "UI/UX Designer",
                skills = listOf("Figma", "Adobe XD", "Sketch", "Prototyping", "User Research"),
                cvFileName = "Sneha_Patel_Portfolio.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Sneha_Patel_Portfolio.pdf",
                mobileNumber = "+91 7654321098",
                email = "sneha.patel@nsut.ac.in",
                linkedinProfile = "https://linkedin.com/in/sneha-patel-ux",
                githubLink = "https://github.com/sneha-design",
                portfolioWebsite = "https://snehapatel.design",
                references = "Dr. Meera Sharma, ECE Department, NSUT - meera.sharma@nsut.ac.in",
                personalStatement = "Creative designer passionate about creating intuitive user experiences. I have designed several mobile app interfaces and conducted user research for various projects.",
                status = ApplicationStatus.ACCEPTED,
                reviewedBy = "Design Team Lead",
                reviewDate = "18/06/2025 11:20:00",
                hrComments = "Excellent portfolio, great fit for our design team. Offer letter to be generated."
            )
        )

        _applications.addAll(sampleApplications)
        totalSubmissions += sampleApplications.size
    }
}