/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginUseCase.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-18
 * ==============================================================================================
 * Description: Orchestrates the authentication process by validating user credentials,
 *              interacting with the AuthRepository to verify identity, and updating the
 *              global application session state upon successful login.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.usecase

import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.repository.AuthRepository
import javax.inject.Inject

/*** Encapsulates the login business transaction.*/
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManagerInterface
) : BaseUseCase<Credentials, User>() {

    override suspend fun execute(parameters: Credentials): User {
        // 1. Authentication performed by Repository
        // Note: parameters (Credentials) are already validated via Value Objects
        val user = authRepository.login(parameters)

        // 2. Update global session state
        sessionManager.updateSession(user)

        return user
    }
}
