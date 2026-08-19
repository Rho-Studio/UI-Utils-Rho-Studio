/**
 * Rho Studio®
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗®
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         HeaderViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-18
 * ==========================================================================
 * Description:
 *      ViewModel for the reusable PageHeaderFragment.
 *      Decouples the header from feature-specific ViewModels by sourcing
 *      user data directly from the SessionManager and managing the
 *      page-specific title state reactively.
 * ==========================================================================
 */
package com.rho.studio.ui.core.ui.common

import com.rho.studio.ui.core.ui.base.BaseViewModel
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.model.SessionState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HeaderViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : BaseViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.sessionState.collect { state ->
                _currentUser.value = (state as? SessionState.Authenticated)?.user
            }
        }
    }
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    fun setTitle(newTitle: String) { _title.value = newTitle }
}
