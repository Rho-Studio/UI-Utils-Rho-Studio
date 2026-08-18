/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         MainActivity.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-17
 * ==========================================================================
 * Description:
 *      The primary entry point for the RHO Studio application, migrated to 
 *      pure Jetpack Compose.
 *      Screen Assembly: Built LoginScreen and HomeScreen to unify the components.
 *      Main Entry Point: Migrated MainActivity to ComponentActivity.
 *      Navigation: Implemented NavHost for routing based on SessionManager state.
 *      Dagger Scoping: Orchestrates UserComponent lifecycle, ensuring data cleanup
 *      on logout via ComponentManager.
 *      State Management: Uses StateFlow with collectAsState() for Compose compatibility.
 * ==========================================================================
 */
package com.rho.studio.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.domain.model.SessionState
import com.rho.studio.ui.core.ui.common.HeaderViewModel
import com.rho.studio.ui.features.auth.LoginScreen
import com.rho.studio.ui.features.auth.LoginViewModel
import com.rho.studio.ui.features.home.HomeScreen
import com.rho.studio.ui.features.home.HomeViewModel
import com.rho.studio.ui.core.ui.theme.UITheme
import com.rho.studio.ui.di.ComponentManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    /**
     * # Dependency Injection Integration
     * Field injection of the SessionManager SSOT. This removes manual singleton access
     * and ensures the class is provisioned by the Dagger CoreComponent.
     */
    @Inject
    lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bootstrapping: Connects the Activity to the Dagger dependency graph.
        ComponentManager.getAppComponent().inject(this)
        
        /**
         * # Reactive Error Feedback
         * Globally collects infrastructure errors and displays them as Toasts, 
         * regardless of the current navigation destination.
         */
        lifecycleScope.launch {
            sessionManager.error.collect { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                    sessionManager.clearError()
                }
            }
        }

        setContent {
            UITheme {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()
        
        /**
         * # Sealed State Management
         * Uses a sealed class (SessionState) instead of simple booleans to prevent
         * illegal UI states and ensure the UI is always a reflection of the session truth.
         */
        val sessionState by sessionManager.sessionState.collectAsState()
        val isLoading by sessionManager.isLoading.collectAsState()

        val isSessionChecked = sessionState !is SessionState.Uninitialized && sessionState !is SessionState.Checking
        val isAuthenticated = sessionState is SessionState.Authenticated

        if (!isSessionChecked) {
            LoadingScreen()
            return
        }

        /**
         * # Reactive ViewModel Provisioning
         * Utilizes Compose-native viewModel() pattern to avoid race conditions.
         * LoginViewModel is sourced from the persistent AppScope.
         */
        val appFactory = ComponentManager.getAppComponent().viewModelFactory()
        val loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = appFactory)

        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                loginViewModel.resetForm()
                
                /**
                 * # Secure Session Isolation
                 * Atomically destroys the authenticated dependency graph on logout,
                 * ensuring PII (Personally Identifiable Information) is binary-purged from memory.
                 */
                ComponentManager.destroyUserComponent()
                
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) "home" else "login"
            ) {
                composable("login") {
                    LoginScreen(viewModel = loginViewModel)
                }
                composable("home") {
                    /**
                     * # Tiered DI Scoping
                     * Home and Header ViewModels are provided by the dynamic UserComponent,
                     * which only exists while the user is actively authenticated.
                     */
                    val userFactory = ComponentManager.createUserComponent().viewModelFactory()
                    val homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = userFactory)
                    val headerViewModel: HeaderViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = userFactory)
                    
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        headerViewModel = headerViewModel
                    )
                }
            }

            if (isLoading) {
                LoadingOverlay()
            }
        }
    }

    @Composable
    private fun LoadingScreen() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    @Composable
    private fun LoadingOverlay() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.cleanup()
    }
}