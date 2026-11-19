/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         HomeScreenIntegrationTest.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-11-19
 * ============================================================================
 * Description:
 *      Integration test for Home feature navigation state synchronization.
 *      Verifies tab selection, service module lookup, and state flow interactions.
 * ============================================================================
 */
package com.rho.studio.ui.features.home

import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.domain.usecase.LogoutUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeScreenIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sessionManager: SessionManager
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionManager = mockk(relaxed = true)
        logoutUseCase = mockk(relaxed = true)
        homeViewModel = HomeViewModel(sessionManager, logoutUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify tab navigation integration across all service modules`() = runTest {
        val services = homeViewModel.services.value
        assertEquals(4, services.size)

        // Verify default tab
        assertEquals("inv", homeViewModel.selectedTabId.value)

        // Cycle through all tabs and verify state selection
        services.forEach { service ->
            homeViewModel.selectTab(service.id)
            assertEquals(service.id, homeViewModel.selectedTabId.value)

            val activeModule = services.find { it.id == homeViewModel.selectedTabId.value }
            assertNotNull(activeModule)
            assertEquals(service.titleRes, activeModule?.titleRes)
        }
    }
}
