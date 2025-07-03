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

    // Add this init block inside the InternshipDataManager object
    init {
        addSampleApplications()
    }

    /**
     * Add sample applications for testing and demonstration
     */
    private fun addSampleApplications() {
        val sampleApplications = listOf(
            InternshipApplication(
                id = "sample-001",
                submissionDate = "15/06/2025 14:30:00",
                fullName = "Arjun Sharma",
                dateOfBirth = "15/03/2002",
                yearOfStudy = "3rd Year",
                college = "Indian Institute of Technology Delhi",
                branch = "Computer Science Engineering",
                availableFrom = "01/07/2025",
                availableUntil = "31/08/2025",
                internshipRole = "Android Developer",
                skills = listOf("Kotlin", "Android", "Jetpack Compose", "MVVM", "Room Database"),
                cvFileName = "Arjun_Sharma_CV.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Arjun_Sharma_CV.pdf",
                mobileNumber = "+91 9876543210",
                email = "arjun.sharma@iitd.ac.in",
                linkedinProfile = "https://linkedin.com/in/arjun-sharma-android",
                githubLink = "https://github.com/arjun-android-dev",
                portfolioWebsite = "https://arjunsharma.dev",
                references = "Prof. Rajesh Kumar, CSE Department, IIT Delhi - rajesh.kumar@iitd.ac.in",
                personalStatement = "Passionate Android developer with hands-on experience in Kotlin and Jetpack Compose. I have developed multiple Android applications and contributed to open-source projects.",
                status = ApplicationStatus.SUBMITTED
            ),
            InternshipApplication(
                id = "sample-002",
                submissionDate = "16/06/2025 10:15:00",
                fullName = "Priya Verma",
                dateOfBirth = "28/07/2003",
                yearOfStudy = "2nd Year",
                college = "Delhi Technological University",
                branch = "Information Technology",
                availableFrom = "15/07/2025",
                availableUntil = "15/09/2025",
                internshipRole = "Web Developer",
                skills = listOf("React", "Node.js", "JavaScript", "MongoDB", "Express.js", "HTML/CSS"),
                cvFileName = "Priya_Verma_Resume.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Priya_Verma_Resume.pdf",
                mobileNumber = "+91 8765432109",
                email = "priya.verma@dtu.ac.in",
                linkedinProfile = "https://linkedin.com/in/priya-verma-webdev",
                githubLink = "https://github.com/priya-webdev",
                portfolioWebsite = "https://priyaverma.tech",
                references = "Dr. Anita Singh, IT Department, DTU - anita.singh@dtu.ac.in",
                personalStatement = "Full-stack web developer with strong foundation in MERN stack. I enjoy creating responsive web applications and have experience with modern JavaScript frameworks.",
                status = ApplicationStatus.UNDER_REVIEW,
                reviewedBy = "Tech Team Lead",
                reviewDate = "17/06/2025 09:30:00",
                hrComments = "Good technical skills, proceeding to next round."
            ),
            InternshipApplication(
                id = "sample-003",
                submissionDate = "17/06/2025 16:45:00",
                fullName = "Rohan Gupta",
                dateOfBirth = "12/11/2001",
                yearOfStudy = "4th Year",
                college = "Netaji Subhas University of Technology",
                branch = "Computer Science Engineering",
                availableFrom = "01/08/2025",
                availableUntil = "30/11/2025",
                internshipRole = "Data Science Intern",
                skills = listOf("Python", "Machine Learning", "Pandas", "NumPy", "Scikit-learn", "TensorFlow"),
                cvFileName = "Rohan_Gupta_DataScience_CV.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Rohan_Gupta_DataScience_CV.pdf",
                mobileNumber = "+91 7654321098",
                email = "rohan.gupta@nsut.ac.in",
                linkedinProfile = "https://linkedin.com/in/rohan-gupta-datascience",
                githubLink = "https://github.com/rohan-ml",
                portfolioWebsite = "https://rohangupta-ml.com",
                references = "Prof. Deepika Sharma, CSE Department, NSUT - deepika.sharma@nsut.ac.in",
                personalStatement = "Data science enthusiast with experience in machine learning and statistical analysis. I have worked on several ML projects including predictive modeling and data visualization.",
                status = ApplicationStatus.ACCEPTED,
                reviewedBy = "Data Science Manager",
                reviewDate = "18/06/2025 11:20:00",
                hrComments = "Excellent ML knowledge and project portfolio. Approved for internship."
            ),
            InternshipApplication(
                id = "sample-004",
                submissionDate = "18/06/2025 13:20:00",
                fullName = "Kavya Nair",
                dateOfBirth = "05/09/2002",
                yearOfStudy = "3rd Year",
                college = "Indira Gandhi Delhi Technical University for Women",
                branch = "Electronics and Communication Engineering",
                availableFrom = "10/07/2025",
                availableUntil = "10/10/2025",
                internshipRole = "Digital Marketing Intern",
                skills = listOf("SEO", "Social Media Marketing", "Google Analytics", "Content Writing", "Adobe Creative Suite"),
                cvFileName = "Kavya_Nair_Marketing_Resume.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Kavya_Nair_Marketing_Resume.pdf",
                mobileNumber = "+91 6543210987",
                email = "kavya.nair@igdtuw.ac.in",
                linkedinProfile = "https://linkedin.com/in/kavya-nair-marketing",
                githubLink = "https://github.com/kavya-content",
                portfolioWebsite = "https://kavyanair-portfolio.com",
                references = "Dr. Sunita Rani, ECE Department, IGDTUW - sunita.rani@igdtuw.ac.in",
                personalStatement = "Creative marketing professional with strong analytical skills. I have managed social media campaigns and have experience in content creation and SEO optimization.",
                status = ApplicationStatus.REJECTED,
                reviewedBy = "Marketing Head",
                reviewDate = "19/06/2025 15:45:00",
                hrComments = "Good skills but looking for candidates with more industry experience."
            ),
            InternshipApplication(
                id = "sample-005",
                submissionDate = "19/06/2025 11:30:00",
                fullName = "Vikash Singh",
                dateOfBirth = "20/01/2003",
                yearOfStudy = "2nd Year",
                college = "Jamia Millia Islamia",
                branch = "Computer Science Engineering",
                availableFrom = "25/07/2025",
                availableUntil = "25/12/2025",
                internshipRole = "Mobile App Developer",
                skills = listOf("Flutter", "Dart", "Firebase", "REST APIs", "Git", "UI/UX Design"),
                cvFileName = "Vikash_Singh_Flutter_CV.pdf",
                cvFilePath = "/storage/emulated/0/Documents/Vikash_Singh_Flutter_CV.pdf",
                mobileNumber = "+91 5432109876",
                email = "vikash.singh@jmi.ac.in",
                linkedinProfile = "https://linkedin.com/in/vikash-singh-flutter",
                githubLink = "https://github.com/vikash-flutter",
                portfolioWebsite = "https://vikashsingh-apps.dev",
                references = "Prof. Mohammad Ali, CSE Department, JMI - mohammad.ali@jmi.ac.in",
                personalStatement = "Flutter developer passionate about creating cross-platform mobile applications. I have published apps on Play Store and have experience with Firebase integration.",
                status = ApplicationStatus.OFFER_GENERATED,
                reviewedBy = "Mobile Development Team",
                reviewDate = "20/06/2025 10:15:00",
                hrComments = "Outstanding Flutter skills and published apps. Offer letter generated and sent."
            )
        )

        _applications.addAll(sampleApplications)
        totalSubmissions += sampleApplications.size
    }

    /**
     * Submit a new internship application
     * New applications are added at the beginning of the list to show latest first
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

        // Add new application at the beginning of the list (index 0)
        _applications.add(0, application)
        totalSubmissions++
        return application
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
     * Get applications by status (sorted by submission date - latest first)
     */
    fun getApplicationsByStatus(status: ApplicationStatus): List<InternshipApplication> {
        return _applications.filter { it.status == status }
    }

    /**
     * Get applications by college (sorted by submission date - latest first)
     */
    fun getApplicationsByCollege(college: String): List<InternshipApplication> {
        return _applications.filter { it.college.contains(college, ignoreCase = true) }
    }

    /**
     * Get applications by internship role (sorted by submission date - latest first)
     */
    fun getApplicationsByRole(role: String): List<InternshipApplication> {
        return _applications.filter { it.internshipRole.contains(role, ignoreCase = true) }
    }

    /**
     * Get applications sorted by submission date (latest first)
     */
    fun getApplicationsSortedByDate(): List<InternshipApplication> {
        return _applications.sortedByDescending { application ->
            try {
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(application.submissionDate)
            } catch (e: Exception) {
                Date(0) // Return epoch time if parsing fails
            }
        }
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