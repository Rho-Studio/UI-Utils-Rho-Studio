/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         AppComponent.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-14
 * ==============================================================================================
 * Description: Root Dagger component for the application.
 *              Responsible for bridging the core data layers with the UI layer and
 *              managing the application-wide dependency graph.
 * ==============================================================================================
 */
package com.rho.studio.ui.di

import com.rho.studio.ui.MainActivity
import com.rho.studio.ui.core.data.di.CoreComponent
import com.rho.studio.ui.core.ui.di.DaggerViewModelFactory
import com.rho.studio.ui.features.auth.di.AuthModule
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope

@AppScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [AuthModule::class, com.rho.studio.ui.core.ui.di.UIModule::class]
)
interface AppComponent {
    fun inject(activity: MainActivity)
    
    // Exposed for UserComponent
    fun coreComponent(): CoreComponent
    
    fun viewModelFactory(): DaggerViewModelFactory
}
