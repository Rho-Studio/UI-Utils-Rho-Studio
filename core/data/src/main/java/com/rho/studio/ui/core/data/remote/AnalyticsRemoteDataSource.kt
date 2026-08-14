/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         AnalyticsRemoteDataSource.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-13
 * ==============================================================================================
 * Description: Firebase analytics data layer for decoupled event logging.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.remote

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper around FirebaseAnalytics for decoupled event logging.
 */
@Singleton
class AnalyticsRemoteDataSource @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logLogin(userId: String) {
        Log.d("RHO_TELEMETRY", "Logging login event for user: $userId")
        // Set the user identity for all future events in the session
        firebaseAnalytics.setUserId(userId)
        
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "email_password")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }
}
