/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         UserComponent.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-12
 * ==============================================================================================
 * Description: Dagger component defining the scope for authenticated user sessions.
 *
 *              This component acts as the Single Source of Truth (SSOT) for the user's session
 *              lifecycle, managing the injection of dependencies that require a valid user
 *              context. It bridges core data layers with feature-specific modules, ensuring
 *              that sensitive user data is scoped correctly and cleared upon logout.
 *
 *              Scope: @UserScope
 * ==============================================================================================
 */
package com.rho.studio.ui.di

import com.rho.studio.ui.core.data.di.CoreComponent
import com.rho.studio.ui.core.ui.di.DaggerViewModelFactory
import com.rho.studio.ui.core.ui.di.UserScope
import com.rho.studio.ui.features.home.di.HomeModule
import dagger.Component

@UserScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [HomeModule::class, com.rho.studio.ui.core.ui.di.UIModule::class]
)
interface UserComponent {
    fun viewModelFactory(): DaggerViewModelFactory
}
