package com.example.offer_generator.ViewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class WhoLoginViewModel : ViewModel() {
    // Actual login states (only set after successful login/signup)
    private val _isUserLoggedIn = mutableStateOf(false)
    val isUserLoggedIn: MutableState<Boolean> = _isUserLoggedIn

    private val _isHrLoggedIn = mutableStateOf(false)
    val isHrLoggedIn: MutableState<Boolean> = _isHrLoggedIn

    private val _isFreeLancerLoggedIn = mutableStateOf(false)
    val isFreeLancerLoggedIn: MutableState<Boolean> = _isFreeLancerLoggedIn

    private val _isFulltimeEmployeeLoggedIn = mutableStateOf(false)
    val isFulltimeEmployeeLoggedIn: MutableState<Boolean> = _isFulltimeEmployeeLoggedIn

    private val _isAdminLoggedIn = mutableStateOf(false)
    val isAdminLoggedIn: MutableState<Boolean> = _isAdminLoggedIn

    // Selected role state (for guest users who haven't logged in yet)
    private val _selectedRole = mutableStateOf<String?>(null)
    val selectedRole: MutableState<String?> = _selectedRole

    // Guest mode flag
    private val _isGuestMode = mutableStateOf(false)
    val isGuestMode: MutableState<Boolean> = _isGuestMode

    // Function to set selected role for guest users
    fun setSelectedRole(role: String) {
        _selectedRole.value = role
        _isGuestMode.value = true
        // Don't set any login states here
    }

    // Function to clear selected role (when user goes back to role selection)
    fun clearSelectedRole() {
        _selectedRole.value = null
        _isGuestMode.value = false
    }

    // Functions to set actual login states (only called after successful login/signup)
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isUserLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            _isGuestMode.value = false // Exit guest mode
        }
    }

    fun setHrLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isHrLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            _isGuestMode.value = false // Exit guest mode
        }
    }

    fun setFreeLancerLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isFreeLancerLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            _isGuestMode.value = false // Exit guest mode
        }
    }

    fun setFulltimeEmployeeLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isFulltimeEmployeeLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            _isGuestMode.value = false // Exit guest mode
        }
    }

    fun setAdminLoggedIn(isLoggedIn: Boolean) {
        resetLoginStatus() // Reset all other states
        _isAdminLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            _isGuestMode.value = false // Exit guest mode
        }
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
        _selectedRole.value = null
        _isGuestMode.value = false
    }

    // Helper function to get current logged-in user type
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

    // Helper function to get current role (either logged-in or selected as guest)
    fun getCurrentRole(): String? {
        return getCurrentUserType() ?: _selectedRole.value
    }


    // Helper function to check if any user is actually logged in
    fun isAnyUserLoggedIn(): Boolean {
        return _isUserLoggedIn.value ||
                _isHrLoggedIn.value ||
                _isFreeLancerLoggedIn.value ||
                _isFulltimeEmployeeLoggedIn.value ||
                _isAdminLoggedIn.value
    }


    // Function to login user with their previously selected role
    fun loginWithSelectedRole() {
        val role = _selectedRole.value
        when (role) {
            "intern" -> setUserLoggedIn(true)
            "fulltime" -> setFulltimeEmployeeLoggedIn(true)
            "freelancer" -> setFreeLancerLoggedIn(true)
            "hr" -> setHrLoggedIn(true)
            "admin" -> setAdminLoggedIn(true)
        }
        // Clear selected role as user is now logged in
        _selectedRole.value = null
    }
}