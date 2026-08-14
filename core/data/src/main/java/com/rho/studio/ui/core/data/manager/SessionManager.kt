/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         SessionManager.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-12
 * ==============================================================================================
 * Description: Orchestrator for authentication state and session lifecycle.
 *              Acts as the Single Source of Truth (SSOT) for the user's session.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.manager

import com.rho.studio.ui.core.domain.model.SessionState
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.repository.SessionRepository
import com.rho.studio.ui.core.domain.usecase.SessionManagerInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * # SessionManager
 * Implementation of SessionManagerInterface and SSOT for session state.
 */
@Singleton
class SessionManager @Inject constructor(
    private val repository: SessionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SessionManagerInterface {

    private val sessionScope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Uninitialized)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadSavedSession()
    }

    override fun updateSession(user: User) {
        _sessionState.value = SessionState.Authenticated(user)
        saveUserSession(user)
    }

    override fun clearSession() {
        _sessionState.value = SessionState.Guest
        clearUserSession()
    }

    override fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun loadSavedSession() {
        _sessionState.value = SessionState.Checking
        sessionScope.launch {
            try {
                val user = repository.getUser()
                if (user != null) {
                    _sessionState.value = SessionState.Authenticated(user)
                } else {
                    _sessionState.value = SessionState.Guest
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Guest
                clearUserSession()
            }
        }
    }

    private fun saveUserSession(user: User) {
        sessionScope.launch {
            try {
                repository.saveUser(user)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private fun clearUserSession() {
        sessionScope.launch {
            try {
                repository.clearSession()
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun isAuthenticated(): Boolean = _sessionState.value is SessionState.Authenticated
    fun getCurrentUser(): User? = (_sessionState.value as? SessionState.Authenticated)?.user
    fun isSessionChecked(): Boolean = _sessionState.value !is SessionState.Uninitialized && _sessionState.value !is SessionState.Checking
    
    fun clearError() {
        _error.value = null
    }

    fun cleanup() {
        sessionScope.cancel()
    }
}
