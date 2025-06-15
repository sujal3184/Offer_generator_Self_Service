package com.example.offer_generator.ViewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.offer_generator.Screens.Freelancer.FreelancerApplication
import com.example.offer_generator.Screens.Freelancer.FreelancerApplicationStatus
import com.example.offer_generator.Screens.Internship.ApplicationStatus
import com.example.offer_generator.Screens.Internship.InternshipApplication
import java.text.SimpleDateFormat
import java.util.*

class ApplicationViewModel : ViewModel() {

    // Private mutable lists
    private val _internshipApplications = mutableListOf<InternshipApplication>()
    private val _freelancerApplications = mutableListOf<FreelancerApplication>()

    // StateFlow for reactive UI updates
    private val _internshipApplicationsFlow = MutableStateFlow<List<InternshipApplication>>(emptyList())
    val internshipApplicationsFlow: StateFlow<List<InternshipApplication>> = _internshipApplicationsFlow.asStateFlow()

    private val _freelancerApplicationsFlow = MutableStateFlow<List<FreelancerApplication>>(emptyList())
    val freelancerApplicationsFlow: StateFlow<List<FreelancerApplication>> = _freelancerApplicationsFlow.asStateFlow()

    // Public read-only access
    val internshipApplications: List<InternshipApplication> get() = _internshipApplications.toList()
    val freelancerApplications: List<FreelancerApplication> get() = _freelancerApplications.toList()

    // ===== INTERNSHIP APPLICATION METHODS =====

    /**
     * Add a new internship application
     */
    fun addInternshipApplication(application: InternshipApplication) {
        viewModelScope.launch {
            _internshipApplications.add(application)
            _internshipApplicationsFlow.value = _internshipApplications.toList()
        }
    }

    /**
     * Remove an internship application by ID
     */
    fun removeInternshipApplication(applicationId: String): Boolean {
        return viewModelScope.run {
            val removed = _internshipApplications.removeAll { it.id == applicationId }
            if (removed) {
                _internshipApplicationsFlow.value = _internshipApplications.toList()
            }
            removed
        }
    }

    /**
     * Update an existing internship application
     */
    fun updateInternshipApplication(updatedApplication: InternshipApplication): Boolean {
        viewModelScope.launch {
            val index = _internshipApplications.indexOfFirst { it.id == updatedApplication.id }
            if (index != -1) {
                _internshipApplications[index] = updatedApplication
                _internshipApplicationsFlow.value = _internshipApplications.toList()
            }
        }
        return _internshipApplications.any { it.id == updatedApplication.id }
    }

    /**
     * Get internship application by ID
     */
    fun getInternshipApplicationById(applicationId: String): InternshipApplication? {
        return _internshipApplications.find { it.id == applicationId }
    }

    /**
     * Update internship application status
     */
    fun updateInternshipApplicationStatus(
        applicationId: String,
        newStatus: ApplicationStatus,
        reviewedBy: String? = null,
        hrComments: String? = null
    ): Boolean {
        viewModelScope.launch {
            val index = _internshipApplications.indexOfFirst { it.id == applicationId }
            if (index != -1) {
                val currentApp = _internshipApplications[index]
                val reviewDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                _internshipApplications[index] = currentApp.copy(
                    status = newStatus,
                    reviewedBy = reviewedBy ?: currentApp.reviewedBy,
                    reviewDate = reviewDate,
                    hrComments = hrComments ?: currentApp.hrComments
                )
                _internshipApplicationsFlow.value = _internshipApplications.toList()
            }
        }
        return _internshipApplications.any { it.id == applicationId }
    }

    // ===== FREELANCER APPLICATION METHODS =====

    /**
     * Add a new freelancer application
     */
    fun addFreelancerApplication(application: FreelancerApplication) {
        viewModelScope.launch {
            _freelancerApplications.add(application)
            _freelancerApplicationsFlow.value = _freelancerApplications.toList()
        }
    }

    /**
     * Remove a freelancer application by ID
     */
    fun removeFreelancerApplication(applicationId: String): Boolean {
        return viewModelScope.run {
            val removed = _freelancerApplications.removeAll { it.id == applicationId }
            if (removed) {
                _freelancerApplicationsFlow.value = _freelancerApplications.toList()
            }
            removed
        }
    }

    /**
     * Update an existing freelancer application
     */
    fun updateFreelancerApplication(updatedApplication: FreelancerApplication): Boolean {
        viewModelScope.launch {
            val index = _freelancerApplications.indexOfFirst { it.id == updatedApplication.id }
            if (index != -1) {
                _freelancerApplications[index] = updatedApplication
                _freelancerApplicationsFlow.value = _freelancerApplications.toList()
            }
        }
        return _freelancerApplications.any { it.id == updatedApplication.id }
    }

    /**
     * Get freelancer application by ID
     */
    fun getFreelancerApplicationById(applicationId: String): FreelancerApplication? {
        return _freelancerApplications.find { it.id == applicationId }
    }

    /**
     * Update freelancer application status
     */
    fun updateFreelancerApplicationStatus(
        applicationId: String,
        newStatus: FreelancerApplicationStatus,
        reviewedBy: String? = null,
        hrComments: String? = null
    ): Boolean {
        viewModelScope.launch {
            val index = _freelancerApplications.indexOfFirst { it.id == applicationId }
            if (index != -1) {
                val currentApp = _freelancerApplications[index]
                val reviewDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                _freelancerApplications[index] = currentApp.copy(
                    status = newStatus,
                    reviewedBy = reviewedBy ?: currentApp.reviewedBy,
                    reviewDate = reviewDate,
                    hrComments = hrComments ?: currentApp.hrComments
                )
                _freelancerApplicationsFlow.value = _freelancerApplications.toList()
            }
        }
        return _freelancerApplications.any { it.id == applicationId }
    }

    // ===== UTILITY METHODS =====

    /**
     * Get all applications count
     */
    fun getTotalApplicationsCount(): Int {
        return _internshipApplications.size + _freelancerApplications.size
    }

    /**
     * Get internship applications by status
     */
    fun getInternshipApplicationsByStatus(status: ApplicationStatus): List<InternshipApplication> {
        return _internshipApplications.filter { it.status == status }
    }

    /**
     * Get freelancer applications by status
     */
    fun getFreelancerApplicationsByStatus(status: FreelancerApplicationStatus): List<FreelancerApplication> {
        return _freelancerApplications.filter { it.status == status }
    }

    /**
     * Search internship applications by name or email
     */
    fun searchInternshipApplications(query: String): List<InternshipApplication> {
        return _internshipApplications.filter {
            it.fullName.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.college.contains(query, ignoreCase = true)
        }
    }

    /**
     * Search freelancer applications by name or email
     */
    fun searchFreelancerApplications(query: String): List<FreelancerApplication> {
        return _freelancerApplications.filter {
            it.fullName.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
        }
    }

    /**
     * Clear all applications
     */
    fun clearAllApplications() {
        viewModelScope.launch {
            _internshipApplications.clear()
            _freelancerApplications.clear()
            _internshipApplicationsFlow.value = emptyList()
            _freelancerApplicationsFlow.value = emptyList()
        }
    }

    /**
     * Get applications submitted today
     */
    fun getTodayApplicationsCount(): Pair<Int, Int> {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val internshipToday = _internshipApplications.count { it.submissionDate.startsWith(today) }
        val freelancerToday = _freelancerApplications.count { it.submissionDate.startsWith(today) }
        return Pair(internshipToday, freelancerToday)
    }

    /**
     * Sort internship applications by submission date (newest first)
     */
    fun sortInternshipApplicationsByDate(): List<InternshipApplication> {
        return _internshipApplications.sortedByDescending { it.submissionDate }
    }

    /**
     * Sort freelancer applications by submission date (newest first)
     */
    fun sortFreelancerApplicationsByDate(): List<FreelancerApplication> {
        return _freelancerApplications.sortedByDescending { it.submissionDate }
    }
}