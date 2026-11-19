/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         TopNavigationBar.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-11-18
 * ============================================================================
 * Description:
 *      A top navigation bar component conforming to the Rho Studio design
 *      system specifications. It renders a branded horizontal bar containing
 *      pill-shaped capsule navigation tabs for switching between feature tabs.
 *
 *      Key Features:
 *          • Branded Container: Styled with Rho Red background and top inset padding.
 *          • Capsule Tabs: Pill-shaped items with adaptive colors for active and
 *            inactive tab states.
 *          • Unidirectional Event Handling: Delegates tab selection events
 *            to caller via callbacks.
 *          • Accessibility & Truncation: Handles label text gracefully with single
 *            line ellipsis handling and touch target sizing.
 * ============================================================================
 */
package com.rho.studio.ui.core.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rho.studio.ui.core.ui.R

/**
 * Encapsulates data required to render a tab item in the [TopNavigationBar].
 *
 * @property id Unique identifier for the navigation destination.
 * @property title Display label for the tab.
 */
data class TopNavItem(
    val id: String,
    val title: String,
)

/**
 * Top navigation bar composable implementing the Rho Studio header navigation pattern.
 *
 * @param items Collection of navigation items to render as capsule tabs.
 * @param selectedItemId ID of the currently selected navigation tab.
 * @param onTabSelected Callback triggered when a tab is tapped.
 * @param modifier Custom layout modifier.
 * @param backgroundColor Background color of the top bar container (defaults to RhoRed).
 */
@Composable
fun TopNavigationBar(
    items: List<TopNavItem>,
    selectedItemId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = colorResource(id = R.color.rho_red)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.id == selectedItemId

            val targetPillColor = if (isSelected) {
                Color.White
            } else {
                colorResource(id = R.color.silver_gray)
            }

            val targetTextColor = if (isSelected) {
                colorResource(id = R.color.rho_red)
            } else {
                colorResource(id = R.color.dark_gray)
            }

            val animatedPillColor by animateColorAsState(
                targetValue = targetPillColor,
                animationSpec = tween(durationMillis = 200),
                label = "pillColorAnimation"
            )

            val animatedTextColor by animateColorAsState(
                targetValue = targetTextColor,
                animationSpec = tween(durationMillis = 200),
                label = "textColorAnimation"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(animatedPillColor)
                    .clickable { onTabSelected(item.id) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.title,
                    color = animatedTextColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
