/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ============================================================================
 * File:         ServiceItem.kt (composable UI)
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-06
 * ============================================================================
 * Description:
 *      A modular UI component representing an individual service entry
 *      within the Home screen grid. It encapsulates the visual style
 *      and interaction logic for a single ServiceModule.
 *
 *      Key Features:
 *          • Adaptive Styling: Dynamically sets its background color based
 *            on the ServiceModule's resource definitions.
 *          • Geometric Design: Features a fixed 1:1 aspect ratio and rounded
 *            corners to maintain UI consistency across the service grid.
 *          • Localized Content: Automatically resolves and displays title
 *            strings from Android resource IDs.
 *          • Feedback: Built on Material 3 Button semantics to provide
 *            standard touch feedback and accessibility support.
 *          • Scalable Grid Integration: Designed to be used within LazyVerticalGrid
 *            for responsive feature layouts.
 * ============================================================================
 */
package com.rho.studio.ui.features.home.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rho.studio.ui.features.home.model.ServiceModule

@Composable
fun ServiceItem(
    service: ServiceModule,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(service.id) },
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = service.backgroundColor)
        )
    ) {
        Text(
            text = stringResource(id = service.titleRes),
            color = Color.White
        )
    }
}
