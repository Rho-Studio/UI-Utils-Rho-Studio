/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         HomeViewModel.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ==============================================================================================
 * Description: ViewModel for the Home feature, managing feature state, session data,
 *              and providing access to available service modules.
 * ==============================================================================================
 */
package com.rho.studio.ui.features.home

import com.rho.studio.ui.core.ui.R
import com.rho.studio.ui.core.ui.base.BaseViewModel
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.features.home.model.ServiceModule
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : BaseViewModel() {

    private val sessionManager = SessionManager.getInstance()
    private val logoutUseCase = LogoutUseCase(sessionManager)

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getCurrentUserSync())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Parametrized services for the Home experience
    private val _services = MutableStateFlow<List<ServiceModule>>(
        listOf(
            ServiceModule("inv", R.string.module_inventory, R.color.rho_red),
            ServiceModule("sales", R.string.module_sales, R.color.rho_strong_gray),
            ServiceModule("cust", R.string.module_customers, R.color.rho_strong_gray),
            ServiceModule("rep", R.string.module_reports, R.color.rho_red)
        )
    )
    val services: StateFlow<List<ServiceModule>> = _services.asStateFlow()

    private val _navigateToService = MutableStateFlow<String?>(null)
    val navigateToService: StateFlow<String?> = _navigateToService.asStateFlow()

    fun onServiceClick(serviceId: String) {
        _navigateToService.value = serviceId
    }

    fun onServiceNavigated() {
        _navigateToService.value = null
    }

    fun logout() {
        launchWithLoading({
            logoutUseCase(Unit)
        })
    }
}
