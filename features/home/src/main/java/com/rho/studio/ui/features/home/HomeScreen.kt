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
 * Date:         2026-08-06
 * ============================================================================
 * Description:
 *      The primary landing screen of the application, serving as the main
 *      Rho Studio Home screen. It establishes the primary entry point and
 *      experience for user interactions. It orchestrates the display of
 *      the header, available services, and the footer.
 *
 *      Key Features:
 *          • Dynamic Background: Implements a signature vertical gradient
 *            (RhoRed to SilverGray to Black) defining the app's visual identity.
 *          • Multi-ViewModel Architecture: Coordinates state between
 *            HeaderViewModel (navigation/profile) and HomeViewModel (content).
 *          • Modular UI: Composed of reusable building blocks: PageHeader,
 *            ServiceList, and PageFooter.
 *          • Responsive Layout: Uses weighted components to ensure the
 *            ServiceList occupies available vertical space effectively.
 * ============================================================================
 */

package com.rho.studio.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rho.studio.ui.core.ui.common.HeaderViewModel
import com.rho.studio.ui.core.ui.common.PageFooter
import com.rho.studio.ui.core.ui.common.PageHeader
import com.rho.studio.ui.features.home.components.ServiceList
import com.rho.studio.ui.core.ui.theme.Black
import com.rho.studio.ui.core.ui.theme.RhoRed
import com.rho.studio.ui.core.ui.theme.SilverGray

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    headerViewModel: HeaderViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
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

        ServiceList(
            viewModel = homeViewModel,
            modifier = Modifier.weight(1f)
        )
        
        PageFooter(
            onLogoutClick = { homeViewModel.logout() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
