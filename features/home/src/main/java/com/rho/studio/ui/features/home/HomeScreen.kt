/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         HomeScreen.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-11-19
 * ============================================================================
 * Description:
 *      The primary landing screen of the application, serving as the main
 *      Rho Studio Home screen. It establishes the primary entry point and
 *      experience for user interactions. It orchestrates the display of
 *      the top navigation bar, header, selected module container, and footer.
 *
 *      Key Features:
 *          • Top Navigation Integration: Renders the branded TopNavigationBar
 *            with capsule tabs corresponding to available feature modules.
 *          • Dynamic Background: Implements a signature vertical gradient
 *            (RhoRed to SilverGray to Black) defining the app's visual identity.
 *          • Multi-ViewModel Architecture: Coordinates state between
 *            HeaderViewModel (navigation/profile) and HomeViewModel (content).
 *          • Modular UI: Composed of reusable building blocks: TopNavigationBar,
 *            PageHeader, SelectedModuleView, and PageFooter.
 *          • Responsive Layout: Uses weighted components to ensure the active
 *            module container occupies available vertical space effectively.
 * ============================================================================
 */

package com.rho.studio.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rho.studio.ui.core.ui.common.HeaderViewModel
import com.rho.studio.ui.core.ui.common.PageFooter
import com.rho.studio.ui.core.ui.common.PageHeader
import com.rho.studio.ui.core.ui.common.TopNavigationBar
import com.rho.studio.ui.core.ui.common.TopNavItem
import com.rho.studio.ui.features.home.components.SelectedModuleView
import com.rho.studio.ui.core.ui.theme.Black
import com.rho.studio.ui.core.ui.theme.RhoRed
import com.rho.studio.ui.core.ui.theme.SilverGray

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    headerViewModel: HeaderViewModel = viewModel(),
) {
    val services by homeViewModel.services.collectAsState()
    val selectedTabId by homeViewModel.selectedTabId.collectAsState()

    val navItems = services.map { service ->
        TopNavItem(
            id = service.id,
            title = stringResource(id = service.titleRes),
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(RhoRed, SilverGray, Black)
                )
            )
    ) {
        PageHeader(
            viewModel = headerViewModel,
            modifier = Modifier.fillMaxWidth()
        )

        TopNavigationBar(
            items = navItems,
            selectedItemId = selectedTabId,
            onTabSelected = { homeViewModel.selectTab(it) },
            modifier = Modifier.fillMaxWidth()
        )

        SelectedModuleView(
            selectedTabId = selectedTabId,
            services = services,
            viewModel = homeViewModel,
            modifier = Modifier.weight(1f)
        )

        PageFooter(
            onLogoutClick = { homeViewModel.logout() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
