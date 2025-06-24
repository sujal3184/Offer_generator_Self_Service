// Data Classes for Offer Letter Management

import com.example.offer_generator.Screens.Freelancer.FreelancerDataManager
import com.example.offer_generator.Screens.FulltimeJob.FullTimeDatamanger
import com.example.offer_generator.Screens.Internship.InternshipDataManager
import com.example.offer_generator.Screens.OfferLetters.OfferLetterTemplate
import java.text.SimpleDateFormat
import java.util.*

// Enum for Offer Types
enum class OfferType {
    INTERN, FREELANCER, FULLTIME
}

// Enum for Offer Status - Cleaned up duplicates
enum class OfferStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    EXPIRED,
    WITHDRAWN,
    SIGNED    // Removed duplicate "Unsigned" - PENDING covers unsigned offers
}

// Enhanced SignedOfferDocument data class
data class SignedOfferDocument(
    val fileName: String,
    val filePath: String,
    val uploadDate: String,
    val fileSize: Long,
    val mimeType: String = "application/pdf", // Added for file type validation
    val uploadedBy: String? = null, // Track who uploaded (candidate/HR)
    val documentUrl: String? = null // For cloud storage URLs
)

// Main OfferLetter data class
data class OfferLetter(
    val id: String,
    val applicationId: String,
    val candidateName: String,
    val candidateEmail: String,
    val companyName: String,
    val position: String,
    val salary: String,
    val startDate: String,
    val endDate: String? = null,
    val location: String,
    val description: String,
    val benefits: String,
    val terms: String,
    val validUntil: String,
    val generatedBy: String,
    val generatedDate: String,
    val status: OfferStatus = OfferStatus.PENDING,
    val type: OfferType,
    val acceptedDate: String? = null,
    val rejectedDate: String? = null,
    val attachments: List<String> = emptyList(),
    val signedDocument: SignedOfferDocument? = null,

    // Add template information
    val templateType: OfferLetterTemplate,
    val customTemplateId: String?,
    val customTemplateContent: String?
) {
    // Existing convenience properties...
    val hasSignedDocument: Boolean
        get() = signedDocument != null

    val signedDocumentUrl: String?
        get() = signedDocument?.documentUrl ?: signedDocument?.filePath

    val isAccepted: Boolean
        get() = status == OfferStatus.ACCEPTED

    val isPending: Boolean
        get() = status == OfferStatus.PENDING

    val isSigned: Boolean
        get() = status == OfferStatus.SIGNED || hasSignedDocument
}

// Data class for offer letter statistics
data class OfferLetterStats(
    val totalOffers: Int,
    val pendingOffers: Int,
    val acceptedOffers: Int,
    val rejectedOffers: Int,
    val expiredOffers: Int,
    val withdrawnOffers: Int,
    val signedOffers: Int, // Added for signed offers tracking
    val internOffers: Int,
    val freelancerOffers: Int,
    val fulltimeOffers: Int
)

// Helper function to create a new offer with signed document
fun OfferLetter.withSignedDocument(signedDoc: SignedOfferDocument): OfferLetter {
    return this.copy(
        signedDocument = signedDoc,
        status = if (this.status == OfferStatus.ACCEPTED) OfferStatus.SIGNED else this.status,
    )
}

// Offer Letter Data Manager
object OfferLetterDataManager {
    private val _offerLetters = mutableListOf<OfferLetter>()

    // Public read-only list
    val offerLetters: List<OfferLetter> get() = _offerLetters.toList()

    // Generate Internship Offer Letter
    fun generateInternOfferLetter(
        applicationId: String,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String,
        location: String,
        description: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String,
        templateType: OfferLetterTemplate,
        customTemplateId: String?,
        customTemplateContent: String?
    ): OfferLetter {
        // Find the application to get candidate details
        val application = InternshipDataManager.applications.find { it.id == applicationId }

        val offerLetter = OfferLetter(
            id = UUID.randomUUID().toString(),
            applicationId = applicationId,
            candidateName = application?.fullName ?: "Unknown Candidate",
            candidateEmail = application?.email ?: "unknown@email.com",
            companyName = companyName,
            position = position,
            salary = salary,
            startDate = startDate,
            endDate = endDate,
            location = location,
            description = description,
            benefits = benefits,
            terms = terms,
            validUntil = validUntil,
            generatedBy = generatedBy,
            generatedDate = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date()),
            status = OfferStatus.PENDING,
            type = OfferType.INTERN,
            templateType = templateType,
            customTemplateId = customTemplateId,
            customTemplateContent = customTemplateContent
        )

        _offerLetters.add(offerLetter)
        return offerLetter
    }

    // Generate Freelancer Offer Letter
    fun generateFreelancerOfferLetter(
        applicationId: String,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String,
        location: String,
        description: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String,
        templateType: OfferLetterTemplate,
        customTemplateId: String?,
        customTemplateContent: String?
    ): OfferLetter {
        // Find the application to get candidate details
        val application = FreelancerDataManager.applications.find { it.id == applicationId }

        val offerLetter = OfferLetter(
            id = UUID.randomUUID().toString(),
            applicationId = applicationId,
            candidateName = application?.fullName ?: "Unknown Candidate",
            candidateEmail = application?.email ?: "unknown@email.com",
            companyName = companyName,
            position = position,
            salary = salary,
            startDate = startDate,
            endDate = endDate,
            location = location,
            description = description,
            benefits = benefits,
            terms = terms,
            validUntil = validUntil,
            generatedBy = generatedBy,
            generatedDate = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date()),
            status = OfferStatus.PENDING,
            type = OfferType.FREELANCER,
            templateType = templateType,
            customTemplateId = customTemplateId,
            customTemplateContent = customTemplateContent
        )

        _offerLetters.add(offerLetter)
        return offerLetter
    }

    // Generate Full-Time Offer Letter
    fun generateFullTimeOfferLetter(
        applicationId: String,
        companyName: String,
        position: String,
        salary: String,
        startDate: String,
        endDate: String,
        location: String,
        description: String,
        benefits: String,
        terms: String,
        validUntil: String,
        generatedBy: String,
        templateType: OfferLetterTemplate,
        customTemplateId: String?,
        customTemplateContent: String?
    ): OfferLetter {
        // Find the application to get candidate details
        val application = FullTimeDatamanger.applications.find { it.id == applicationId }

        val offerLetter = OfferLetter(
            id = UUID.randomUUID().toString(),
            applicationId = applicationId,
            candidateName = application?.fullName ?: "Unknown Candidate",
            candidateEmail = application?.email ?: "unknown@email.com",
            companyName = companyName,
            position = position,
            salary = salary,
            startDate = startDate,
            endDate = null, // Full-time positions don't have end dates
            location = location,
            description = description,
            benefits = benefits,
            terms = terms,
            validUntil = validUntil,
            generatedBy = generatedBy,
            generatedDate = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date()),
            status = OfferStatus.PENDING,
            type = OfferType.FULLTIME,
            templateType = templateType,
            customTemplateId = customTemplateId,
            customTemplateContent = customTemplateContent
        )

        _offerLetters.add(offerLetter)
        return offerLetter
    }

    // Get Offer Letter Statistics
    fun getOfferStatistics(): OfferLetterStats {
        return OfferLetterStats(
            totalOffers = _offerLetters.size,
            pendingOffers = _offerLetters.count { it.status == OfferStatus.PENDING },
            acceptedOffers = _offerLetters.count { it.status == OfferStatus.ACCEPTED },
            rejectedOffers = _offerLetters.count { it.status == OfferStatus.REJECTED },
            expiredOffers = _offerLetters.count { it.status == OfferStatus.EXPIRED },
            withdrawnOffers = _offerLetters.count { it.status == OfferStatus.WITHDRAWN },
            internOffers = _offerLetters.count { it.type == OfferType.INTERN },
            freelancerOffers = _offerLetters.count { it.type == OfferType.FREELANCER },
            fulltimeOffers = _offerLetters.count { it.type == OfferType.FULLTIME },
            signedOffers = _offerLetters.count{ it.isSigned}
        )
    }

    // Update Offer Status
    fun updateOfferStatus(
        offerId: String,
        newStatus: OfferStatus,
        rejectionReason: String? = null,
        candidateResponse: String? = null
    ): Boolean {
        val offerIndex = _offerLetters.indexOfFirst { it.id == offerId }

        if (offerIndex == -1) {
            return false // Offer not found
        }

        val currentOffer = _offerLetters[offerIndex]
        val currentDate = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        val updatedOffer = currentOffer.copy(
            status = newStatus,
            acceptedDate = if (newStatus == OfferStatus.ACCEPTED) currentDate else currentOffer.acceptedDate,
            rejectedDate = if (newStatus == OfferStatus.REJECTED) currentDate else currentOffer.rejectedDate,
        )

        _offerLetters[offerIndex] = updatedOffer
        return true // Successfully updated
    }
    // Return all Intern Offer Letters
    fun getInternOfferLetters(): List<OfferLetter> {
        return _offerLetters.filter { it.type == OfferType.INTERN }
    }

    // Return all Freelancer Offer Letters
    fun getFreelancerOfferLetters(): List<OfferLetter> {
        return _offerLetters.filter { it.type == OfferType.FREELANCER }
    }

    // Return all Full-Time Offer Letters
    fun getFullTimeOfferLetters(): List<OfferLetter> {
        return _offerLetters.filter { it.type == OfferType.FULLTIME }
    }

    // Helper function to get offer by ID
    fun getOfferById(offerId: String): OfferLetter? {
        return _offerLetters.find { it.id == offerId }
    }

    // Helper function to get offers by status
    fun getOffersByStatus(status: OfferStatus): List<OfferLetter> {
        return _offerLetters.filter { it.status == status }
    }

    // Helper function to get offers by type
    fun getOffersByType(type: OfferType): List<OfferLetter> {
        return _offerLetters.filter { it.type == type }
    }

    fun isOfferLetterGeneratedForApplication(applicationId: String): Boolean {
        return OfferLetterDataManager.offerLetters.any { it.applicationId == applicationId }
    }
}