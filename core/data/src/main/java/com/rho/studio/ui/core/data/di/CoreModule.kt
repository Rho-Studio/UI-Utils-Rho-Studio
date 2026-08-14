/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         CoreModule.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-14
 * ==============================================================================================
 * Description: Dagger module responsible for providing core infrastructure dependencies.
 *              It centralizes the injection of data sources, repositories, and cross-cutting
 *              concerns like threading and analytics.
 *
 * Responsibilities:
 * - Binding implementations to domain-level Repository interfaces (Auth and Session).
 * - Providing Singleton instances of Firebase services (Auth, Analytics).
 * - Managing global application context for core-level dependencies.
 * - Defining standard CoroutineDispatchers for background operations.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.di

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.rho.studio.ui.core.data.manager.SessionManager
import com.rho.studio.ui.core.data.repository.SessionRepositoryImpl
import com.rho.studio.ui.core.data.repository.AuthRepositoryImpl
import com.rho.studio.ui.core.domain.repository.SessionRepository
import com.rho.studio.ui.core.domain.repository.AuthRepository
import com.rho.studio.ui.core.domain.usecase.SessionManagerInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSessionManagerInterface(impl: SessionManager): SessionManagerInterface

    companion object {
        private var appContext: Context? = null

        fun init(context: Context) {
            appContext = context.applicationContext
        }

        @Provides
        @Singleton
        fun provideContext(): Context {
            return appContext ?: throw IllegalStateException("CoreModule must be initialized with init(context)")
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @RequiresPermission(allOf = [Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK])
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

        @Provides
        @Singleton
        fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
