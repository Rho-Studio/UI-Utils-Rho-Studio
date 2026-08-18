/**
 * Rho Studio®
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==========================================================================
 * File:         SessionState.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-18
 * ==========================================================================
 * Description:
 *      Defines the states of a user session,
 *      to manage authentication and guest access.
 * ==========================================================================
 */
package com.rho.studio.ui.core.domain.model

/**
 * Sealed class representing the possible states of a user session.
 */
sealed class SessionState {
    object Uninitialized : SessionState()
    object Checking : SessionState()
    data class Authenticated(val user: User) : SessionState()
    object Guest : SessionState()
}
