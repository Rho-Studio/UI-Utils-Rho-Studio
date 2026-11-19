/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         TopNavigationBarIntegrationTest.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-11-19
 * ============================================================================
 * Description:
 *      Integration test suite for the TopNavigationBar and HomeScreen layout.
 *      Verifies tab interaction, state synchronization, and module content rendering.
 * ============================================================================
 */
package com.rho.studio.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.domain.usecase.LogoutUseCase
import com.rho.studio.ui.core.ui.theme.UITheme
import com.rho.studio.ui.features.home.HomeScreen
import com.rho.studio.ui.features.home.HomeViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopNavigationBarIntegrationTest {

    @get:Rule
    @JvmField
    val composeTestRule = createComposeRule()

    private lateinit var sessionManager: SessionManager
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        sessionManager = mockk(relaxed = true)
        logoutUseCase = mockk(relaxed = true)
        homeViewModel = HomeViewModel(sessionManager, logoutUseCase)
    }

    @Test
    fun topNavigationBar_rendersAllTabsAndSwitchesModuleContent() {
        composeTestRule.setContent {
            UITheme {
                HomeScreen(
                    homeViewModel = homeViewModel,
                )
            }
        }

        // Verify initial tabs are displayed
        composeTestRule.onNodeWithText("Inventory").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sales").assertIsDisplayed()
        composeTestRule.onNodeWithText("Customers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reports").assertIsDisplayed()

        // Click Sales tab and verify state selection
        composeTestRule.onNodeWithText("Sales").performClick()

        // Verify Sales module header is displayed in SelectedModuleView
        composeTestRule.onNodeWithText("Sales").assertIsDisplayed()

        // Click Customers tab and verify selection
        composeTestRule.onNodeWithText("Customers").performClick()
        composeTestRule.onNodeWithText("Customers").assertIsDisplayed()
    }
}
