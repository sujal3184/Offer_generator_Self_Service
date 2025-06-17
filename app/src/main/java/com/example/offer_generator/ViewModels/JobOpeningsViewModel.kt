package com.example.offer_generator.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.offer_generator.Screens.HR.EmploymentType
import com.example.offer_generator.Screens.HR.JobOpeningsRepository

data class PostedJob(
    val id: String = java.util.UUID.randomUUID().toString(),
    val domain: String,
    val role: String,
    val employmentType: EmploymentType,
    val count: Int,
    val postedDate: Long = System.currentTimeMillis(),
    val status: JobStatus = JobStatus.ACTIVE
)

enum class JobStatus {
    ACTIVE, INACTIVE, FILLED
}

// Singleton PostedJobsRepository
object PostedJobsRepository {
    private val _postedJobs = mutableStateOf<List<PostedJob>>(emptyList())
    val postedJobs: State<List<PostedJob>> = _postedJobs

    fun postJobs(jobsToPost: List<PostedJob>) {
        val currentJobs = _postedJobs.value.toMutableList()
        currentJobs.addAll(jobsToPost)
        _postedJobs.value = currentJobs
    }

    fun clearPostedJobs() {
        _postedJobs.value = emptyList()
    }

    fun getActiveJobs(): List<PostedJob> {
        return _postedJobs.value.filter { it.status == JobStatus.ACTIVE }
    }

    fun updateJobStatus(jobId: String, newStatus: JobStatus) {
        val updatedJobs = _postedJobs.value.map { job ->
            if (job.id == jobId) job.copy(status = newStatus) else job
        }
        _postedJobs.value = updatedJobs
    }
}

// Enhanced ViewModel - now uses singleton PostedJobsRepository
class JobOpeningsViewModel : ViewModel() {
    private val _repository = JobOpeningsRepository()
    val repository: JobOpeningsRepository = _repository

    // Use singleton posted jobs repository
    val postedJobsRepository: PostedJobsRepository = PostedJobsRepository

    // Get total count for a specific employment type
    fun getTotalForEmploymentType(employmentType: EmploymentType): Int {
        return repository.getTotalForEmploymentType(employmentType)
    }

    // Get domain total for a specific employment type
    fun getDomainTotalForEmploymentType(domain: String, employmentType: EmploymentType): Int {
        return repository.getDomainTotalForEmploymentType(domain, employmentType)
    }

    // Get role count for a specific employment type
    fun getRoleCountForEmploymentType(domain: String, role: String, employmentType: EmploymentType): Int {
        return repository.getRoleCountForEmploymentType(domain, role, employmentType)
    }

    // Update job count for a specific employment type
    fun updateJobCountForEmploymentType(domain: String, role: String, employmentType: EmploymentType, action: String) {
        repository.updateJobCountForEmploymentType(domain, role, employmentType, action)
    }

    // Get all employment types with their counts for a specific role
    fun getEmploymentTypesForRole(domain: String, role: String): Map<EmploymentType, Int> {
        return repository.getEmploymentTypesForRole(domain, role)
    }

    // Post all jobs with counts > 0 and clear the template counts
    fun postAllJobs(): List<PostedJob> {
        val jobsToPost = mutableListOf<PostedJob>()
        val currentJobData = repository.getJobOpeningsData()

        currentJobData.forEach { (domainKey, domain) ->
            domain.roles.forEach { (roleName, employmentTypes) ->
                employmentTypes.forEach { (employmentTypeKey, count) ->
                    if (count > 0) {
                        val employmentType = EmploymentType.values().find { it.name == employmentTypeKey }
                        if (employmentType != null) {
                            jobsToPost.add(
                                PostedJob(
                                    domain = domain.name,
                                    role = roleName,
                                    employmentType = employmentType,
                                    count = count
                                )
                            )
                        }
                    }
                }
            }
        }

        // Post the jobs to singleton repository
        postedJobsRepository.postJobs(jobsToPost)

        // Optional: Clear the template counts after posting
        // Uncomment if you want to reset counts to 0 after posting
        // clearAllTemplateCounts()

        return jobsToPost
    }

    // Optional: Method to clear all template counts after posting
    private fun clearAllTemplateCounts() {
        val currentJobData = repository.getJobOpeningsData()
        currentJobData.forEach { (domainKey, domain) ->
            domain.roles.forEach { (roleName, employmentTypes) ->
                employmentTypes.keys.forEach { employmentTypeKey ->
                    val employmentType = EmploymentType.values().find { it.name == employmentTypeKey }
                    if (employmentType != null) {
                        // Set count to 0 for each employment type
                        repository.updateJobCountForEmploymentType(domainKey, roleName, employmentType, "clear")
                    }
                }
            }
        }
    }

    // Get posted jobs count for UI display
    fun getPostedJobsCount(): Int {
        return postedJobsRepository.postedJobs.value.filter { it.status == JobStatus.ACTIVE }.sumOf { it.count }
    }

    // Get posted jobs for a specific employment type
    fun getPostedJobsForEmploymentType(employmentType: EmploymentType): List<PostedJob> {
        return postedJobsRepository.getActiveJobs().filter { it.employmentType == employmentType }
    }
    //    Get available roles for a specific employment type
    fun getAvailableRolesForEmploymentType(employmentType: EmploymentType): List<String> {
        val availableRoles = mutableListOf<String>()
        val currentJobData = repository.getJobOpeningsData()

        currentJobData.forEach { (domainKey, domain) ->
            domain.roles.forEach { (roleName, employmentTypes) ->
                val count = employmentTypes[employmentType.name] ?: 0
                if (count > 0) {
                    // Map internal role names to display names based on employment type
                    val displayName = mapRoleToDisplayName(roleName, employmentType)
                    if (!availableRoles.contains(displayName)) {
                        availableRoles.add(displayName)
                    }
                }
            }
        }

        return availableRoles.sorted()
    }

    // Helper function to map internal role names to user-friendly display names
    private fun mapRoleToDisplayName(internalRoleName: String, employmentType: EmploymentType): String {
        return when (employmentType) {
            EmploymentType.INTERN -> when (internalRoleName) {
                "AI/ML" -> "AI / ML / DL / GenAI"
                "Frontend" -> "Frontend Development"
                "Backend" -> "Backend Development"
                "Fullstack" -> "Fullstack Development"
                "Business Strategy" -> "Business and Strategy Research"
                "Business Analyst" -> "Business Analyst"
                "UI/UX" -> "UX/UI Design"
                "Marketing" -> "Marketing"
                "HR" -> "HR"
                "Legal" -> "Legal"
                "Project Management" -> "Project Management"
                "Blockchain" -> "Data Analysis" // or map to appropriate intern role
                "Web3" -> "Mobile App Development" // or map to appropriate intern role
                "DevSecOps" -> "Quality Assurance" // or map to appropriate intern role
                "Product Management" -> "Content Writing" // or map to appropriate intern role
                else -> internalRoleName
            }

            EmploymentType.FREELANCER -> when (internalRoleName) {
                "AI/ML" -> "AI / ML / DL / GenAI Consultant"
                "Blockchain" -> "Blockchain Developer"
                "Web3" -> "Web3 Developer"
                "Frontend" -> "Frontend Developer"
                "Backend" -> "Backend Developer"
                "Fullstack" -> "Fullstack Developer"
                "Business Strategy" -> "Business Consultant"
                "Product Management" -> "Product Manager"
                "DevSecOps" -> "DevSecOps Engineer"
                "UI/UX" -> "UX/UI Designer"
                "Marketing" -> "Digital Marketing Specialist"
                "Business Analyst" -> "Content Creator" // or map appropriately
                "HR" -> "Technical Writer" // or map appropriately
                "Legal" -> "Data Analyst" // or map appropriately
                "Project Management" -> "Mobile App Developer" // or map appropriately
                "Chartered Accountant" -> "Graphic Designer" // or map appropriately
                "Company Secretary" -> "SEO Specialist" // or map appropriately
                else -> internalRoleName
            }

            EmploymentType.FULL_TIME -> when (internalRoleName) {
                "AI/ML" -> "AI / ML / DL / GenAI Engineer"
                "Blockchain" -> "Blockchain Developer"
                "Web3" -> "Web3 Engineer"
                "Frontend" -> "Senior Frontend Developer"
                "Backend" -> "Senior Backend Developer"
                "Fullstack" -> "Fullstack Engineer"
                "Business Analyst" -> "Business Analyst"
                "Product Management" -> "Product Manager"
                "Project Management" -> "Project Manager"
                "DevSecOps" -> "DevSecOps Engineer"
                "UI/UX" -> "UX/UI Designer"
                "Marketing" -> "Marketing Manager"
                "Legal" -> "Legal Counsel"
                "Chartered Accountant" -> "Chartered Accountant"
                "Company Secretary" -> "Company Secretary"
                "HR" -> "HR Manager"
                "Business Strategy" -> "Team Lead" // or map appropriately
                else -> internalRoleName
            }
        }
    }

    // Reverse mapping function to get internal role name from display name
    fun getInternalRoleName(displayName: String, employmentType: EmploymentType): String? {
        val currentJobData = repository.getJobOpeningsData()

        currentJobData.forEach { (domainKey, domain) ->
            domain.roles.forEach { (roleName, employmentTypes) ->
                val count = employmentTypes[employmentType.name] ?: 0
                if (count > 0) {
                    val mappedDisplayName = mapRoleToDisplayName(roleName, employmentType)
                    if (mappedDisplayName == displayName) {
                        return roleName
                    }
                }
            }
        }
        return null
    }
}

