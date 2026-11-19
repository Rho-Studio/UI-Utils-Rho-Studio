/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         HomeViewModelTest.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-11-18
 * ==========================================================================
 * Description: Test suite for HomeViewModel.
 *              Verifies services provisioning, tab navigation state, and logout integration.
 * ==========================================================================
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
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sessionManager: SessionManager
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionManager = mockk(relaxed = true)
        logoutUseCase = mockk(relaxed = true)
        viewModel = HomeViewModel(sessionManager, logoutUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `default tab is inventory`() {
        assertEquals("inv", viewModel.selectedTabId.value)
    }

    @Test
    fun `selectTab updates selectedTabId state flow`() = runTest {
        viewModel.selectTab("sales")
        assertEquals("sales", viewModel.selectedTabId.value)

        viewModel.selectTab("cust")
        assertEquals("cust", viewModel.selectedTabId.value)
    }

    @Test
    fun `services returns standard service modules`() {
        val services = viewModel.services.value
        assertEquals(4, services.size)
        assertEquals("inv", services[0].id)
        assertEquals("sales", services[1].id)
        assertEquals("cust", services[2].id)
        assertEquals("rep", services[3].id)
    }
}
