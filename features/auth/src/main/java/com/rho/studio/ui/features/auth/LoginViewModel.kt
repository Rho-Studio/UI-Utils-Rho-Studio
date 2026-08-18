/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         LoginViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-18
 * ============================================================================
 * Description:
 *      The LoginViewModel manages the state and business logic for the
 *      Authentication screen, utilizing reactive validation and Firebase.
 * ============================================================================
 */
package com.rho.studio.ui.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.rho.studio.ui.core.ui.base.BaseViewModel
import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.Result
import com.rho.studio.ui.core.domain.usecase.LoginUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {
    
    private var loginJob: Job? = null
    private var validationJob: Job? = null

    // ==================== FORM STATE ====================
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    // ==================== UI STATE ====================
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }

    // ==================== ACTIONS ====================
    fun onEmailChanged(email: String) {
        this.email = email
        triggerValidation()
    }

    fun onPasswordChanged(password: String) {
        this.password = password
        triggerValidation()
    }

    private fun triggerValidation() {
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(300.milliseconds)
            validate()
        }
    }

    private fun validate(): Boolean {
        val emailValid = email.isNotBlank() && email.matches(EMAIL_REGEX)

        _emailError.value = when {
            email.isBlank() -> "Email is required"
            !emailValid -> "Please enter a valid email address"
            else -> null
        }

        _passwordError.value = when {
            password.isBlank() -> "Password is required"
            else -> null
        }

        val isValid = emailValid //&& passwordValid
        _isFormValid.value = isValid
        return isValid
    }

    fun onLoginClick() {
        if (isLoading.value) return
        
        if (validate()) {
            performLogin(Credentials(email, password))
        }
    }

    private fun performLogin(credentials: Credentials) {
        loginJob = launchWithLoading(
            block = {
                when (val result = loginUseCase(credentials)) {
                    is Result.Success -> {
                        showToast("Login successful!")
                        clearError()
                    }
                    is Result.Error -> {
                        handleError(result.exception)
                    }
                    Result.Loading -> {}
                }
            }
        )
    }

    fun resetForm() {
        email = ""
        password = ""
        _emailError.value = null
        _passwordError.value = null
        _isFormValid.value = false
        clearError()
        clearToastMessage()
    }

    override fun handleError(e: Exception) {
        val message = e.message ?: "An unknown error occurred"
        val formattedMessage = if (message.startsWith("Login failed")) message else "Login failed: $message"
        super.handleError(Exception(formattedMessage, e.cause))
    }
}