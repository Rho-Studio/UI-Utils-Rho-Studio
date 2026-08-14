/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         CoreComponent.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-12
 * ==============================================================================================
 * Description: Dagger component responsible for providing core infrastructure dependencies,
 *              including session management, authentication repositories, and application context.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.di

import android.content.Context
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.domain.repository.SessionRepository
import com.rho.studio.ui.core.domain.repository.AuthRepository
import com.rho.studio.ui.core.domain.usecase.SessionManagerInterface
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent {
    
    fun context(): Context
    fun sessionRepository(): SessionRepository
    fun sessionManager(): SessionManager
    fun sessionManagerInterface(): SessionManagerInterface
    fun authRepository(): AuthRepository
}
