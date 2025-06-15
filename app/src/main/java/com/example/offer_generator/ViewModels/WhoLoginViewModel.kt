package com.example.offer_generator.ViewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class WhoLoginViewModel : ViewModel() {
    private val _isUserLoggedIn = mutableStateOf(false)
    val isUserLoggedIn: MutableState<Boolean> = _isUserLoggedIn

    private val _isHrLoggedIn = mutableStateOf(false)
    val isHrLoggedIn: MutableState<Boolean> = _isHrLoggedIn

    private val _isFreeLancerLoggedIn = mutableStateOf(false)
    val isFreeLancerLoggedIn: MutableState<Boolean> = _isFreeLancerLoggedIn

    private val _isFulltimeEmployeeLoggedIn = mutableStateOf(false)
    val isFulltimeEmployeeLoggedIn: MutableState<Boolean> = _isFulltimeEmployeeLoggedIn

    // Added admin state
    private val _isAdminLoggedIn = mutableStateOf(false)
    val isAdminLoggedIn: MutableState<Boolean> = _isAdminLoggedIn

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isUserLoggedIn.value = isLoggedIn
    }

    fun setHrLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isHrLoggedIn.value = isLoggedIn
    }

    fun setFreeLancerLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isFreeLancerLoggedIn.value = isLoggedIn
    }

    fun setFulltimeEmployeeLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isFulltimeEmployeeLoggedIn.value = isLoggedIn
    }

    fun setAdminLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isAdminLoggedIn.value = isLoggedIn
    }

    private fun resetLoginStatus() {
        _isUserLoggedIn.value = false
        _isHrLoggedIn.value = false
        _isFreeLancerLoggedIn.value = false
        _isFulltimeEmployeeLoggedIn.value = false
        _isAdminLoggedIn.value = false
    }

    fun logoutAll() {
        resetLoginStatus()
    }

    // Helper function to get current user type
    fun getCurrentUserType(): String? {
        return when {
            _isUserLoggedIn.value -> "intern"
            _isFulltimeEmployeeLoggedIn.value -> "fulltime"
            _isFreeLancerLoggedIn.value -> "freelancer"
            _isHrLoggedIn.value -> "hr"
            _isAdminLoggedIn.value -> "admin"
            else -> null
        }
    }

    // Helper function to get user type display name
    fun getCurrentUserTypeDisplayName(): String {
        return when (getCurrentUserType()) {
            "intern" -> "Intern"
            "fulltime" -> "Full-time Employee"
            "freelancer" -> "Freelancer"
            "hr" -> "HR"
            "admin" -> "Admin"
            else -> "Guest"
        }
    }

    // Helper function to check if any user is logged in
    fun isAnyUserLoggedIn(): Boolean {
        return _isUserLoggedIn.value ||
                _isHrLoggedIn.value ||
                _isFreeLancerLoggedIn.value ||
                _isFulltimeEmployeeLoggedIn.value ||
                _isAdminLoggedIn.value
    }

    // Helper function to check if current user can apply for positions
    fun canApplyForPositions(): Boolean {
        return getCurrentUserType() in listOf("intern", "freelancer", "fulltime")
    }

    // Helper function to check if current user is HR or admin
    fun isHrOrAdmin(): Boolean {
        return getCurrentUserType() in listOf("hr", "admin")
    }
}