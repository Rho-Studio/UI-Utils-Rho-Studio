/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LogoutUseCase.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-18
 * ==============================================================================================
 * Description: Handles the termination of the user session by clearing authentication tokens,
 *              resetting global application state, and ensuring secure cleanup of
 *              persisted identity data.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.usecase

import javax.inject.Inject

/*** Encapsulates the logout business transaction.*/
class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManagerInterface
) : BaseUseCase<Unit, Unit>() {

    override suspend fun execute(parameters: Unit) {
        // Atomic cleanup of session state
        sessionManager.clearSession()
    }
}
