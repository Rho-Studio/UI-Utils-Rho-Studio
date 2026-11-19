/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         SelectedModuleView.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-11-19
 * ============================================================================
 * Description:
 *      A container component displaying the signature dark red content area
 *      for the active top navigation tab destination.
 *
 *      Key Features:
 *          • Card Layover Animation: Uses AnimatedContent with custom slide,
 *            scale, and Z-index specs so new module cards seamlessly lay over
 *            the previous one upon tab selection.
 *          • Branded Panel Styling: Implements the dark red panel container
 *            defining the active navigation content view.
 *          • Dynamic Content Integration: Renders active service module details
 *            and grid items based on top navigation selection.
 * ============================================================================
 */
package com.rho.studio.ui.features.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rho.studio.ui.core.ui.theme.RhoDarkRed
import com.rho.studio.ui.features.home.HomeViewModel
import com.rho.studio.ui.features.home.model.ServiceModule

/**
 * Animated card container displaying active module details.
 * Performs a "layover card" animation when [selectedTabId] changes.
 *
 * @param selectedTabId ID of currently selected navigation tab.
 * @param services Available service modules list.
 * @param viewModel HomeViewModel providing service grid logic.
 * @param modifier Layout modifier.
 */
@Composable
fun SelectedModuleView(
    selectedTabId: String,
    services: List<ServiceModule>,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = selectedTabId,
        transitionSpec = {
            (slideInVertically(
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
                initialOffsetY = { fullHeight -> (fullHeight * 0.25f).toInt() },
            ) + fadeIn(
                animationSpec = tween(durationMillis = 380),
            ) + scaleIn(
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
                initialScale = 0.93f,
            )).togetherWith(
                fadeOut(
                    animationSpec = tween(durationMillis = 280),
                ) + scaleOut(
                    animationSpec = tween(durationMillis = 280),
                    targetScale = 0.96f,
                ),
            ).apply {
                targetContentZIndex = 1f
            }
        },
        label = "selectedModuleCardLayover",
        modifier = modifier,
    ) { targetTabId ->
        val activeModule = services.find { it.id == targetTabId } ?: services.firstOrNull()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(RhoDarkRed)
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                activeModule?.let { module ->
                    Text(
                        text = stringResource(id = module.titleRes),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ServiceList(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
