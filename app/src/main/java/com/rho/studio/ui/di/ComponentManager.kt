/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         ComponentManager.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-14
 * ==============================================================================================
 * Description: Centralized manager for the Dagger component hierarchy.
 *              Handles the lifecycle of global (App) and scoped (User) components.
 *              Enables session-based dependency injection by providing mechanisms to
 *              initialize and tear down the UserComponent upon login/logout.
 * ==============================================================================================
 */
package com.rho.studio.ui.di

import android.content.Context
import com.rho.studio.ui.core.data.di.CoreComponent
import com.rho.studio.ui.core.data.di.CoreModule
import com.rho.studio.ui.core.data.di.DaggerCoreComponent

object ComponentManager {

    private lateinit var coreComponent: CoreComponent
    private lateinit var appComponent: AppComponent
    private var userComponent: UserComponent? = null

    fun init(context: Context) {
        CoreModule.init(context)
        coreComponent = DaggerCoreComponent.builder()
            .build()
            
        appComponent = DaggerAppComponent.builder()
            .coreComponent(coreComponent)
            .build()
    }

    fun getAppComponent(): AppComponent = appComponent

    fun createUserComponent(): UserComponent {
        if (userComponent == null) {
            userComponent = DaggerUserComponent.builder()
                .coreComponent(coreComponent)
                .build()
        }
        return userComponent!!
    }

    fun destroyUserComponent() {
        userComponent = null
    }

    fun getUserComponent(): UserComponent? = userComponent
}
