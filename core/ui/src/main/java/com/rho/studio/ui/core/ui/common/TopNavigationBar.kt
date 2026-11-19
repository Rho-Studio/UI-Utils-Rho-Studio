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
 * Date:         2026-11-19
 * ============================================================================
 * Description:
 *      A top navigation bar component conforming to the Rho Studio design
 *      system specifications. It renders a frosted glassmorphism floating bar
 *      containing pill-shaped capsule navigation tabs.
 *
 *      Key Features:
 *          • Glassmorphism Container: Translucent tinted background with specular
 *            border highlights and floating drop shadow.
 *          • Glass Capsule Tabs: Translucent pill items with specular edges for
 *            inactive tabs and crisp highlight styling for the active tab.
 *          • Unidirectional Event Handling: Delegates tab selection events
 *            to caller via callbacks.
 *          • Accessibility & Truncation: Single-line ellipsis text handling.
 * ============================================================================
 */
package com.rho.studio.ui.core.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
 * Frosted glassmorphism top navigation bar composable implementing the Rho Studio header navigation.
 *
 * @param items Collection of navigation items to render as capsule tabs.
 * @param selectedItemId ID of the currently selected navigation tab.
 * @param onTabSelected Callback triggered when a tab is tapped.
 * @param modifier Custom layout modifier.
 * @param backgroundColor Base background color for the translucent glass bar (defaults to RhoRed).
 */
@Composable
fun TopNavigationBar(
    items: List<TopNavItem>,
    selectedItemId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = colorResource(id = R.color.rho_red),
) {
    val glassShape = RoundedCornerShape(24.dp)
    val glassContainerColor = backgroundColor.copy(alpha = 0.55f)
    val glassBorderColor = Color.White.copy(alpha = 0.40f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(elevation = 8.dp, shape = glassShape, clip = false)
            .clip(glassShape)
            .background(glassContainerColor)
            .border(width = 1.dp, color = glassBorderColor, shape = glassShape)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val isSelected = item.id == selectedItemId

                val targetPillColor = if (isSelected) {
                    Color.White.copy(alpha = 0.95f)
                } else {
                    Color.White.copy(alpha = 0.22f)
                }

                val targetPillBorderColor = if (isSelected) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.30f)
                }

                val targetTextColor = if (isSelected) {
                    colorResource(id = R.color.rho_red)
                } else {
                    Color.White
                }

                val animatedPillColor by animateColorAsState(
                    targetValue = targetPillColor,
                    animationSpec = tween(durationMillis = 200),
                    label = "pillColorAnimation",
                )

                val animatedPillBorderColor by animateColorAsState(
                    targetValue = targetPillBorderColor,
                    animationSpec = tween(durationMillis = 200),
                    label = "pillBorderAnimation",
                )

                val animatedTextColor by animateColorAsState(
                    targetValue = targetTextColor,
                    animationSpec = tween(durationMillis = 200),
                    label = "textColorAnimation",
                )

                val pillShape = RoundedCornerShape(50)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(pillShape)
                        .background(animatedPillColor)
                        .border(width = 1.dp, color = animatedPillBorderColor, shape = pillShape)
                        .clickable { onTabSelected(item.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.title,
                        color = animatedTextColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }
        }
    }
}
