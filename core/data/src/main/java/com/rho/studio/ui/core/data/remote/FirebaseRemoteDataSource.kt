/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         FirebaseRemoteDataSource.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-13
 * ==============================================================================================
 * Description: Firebase remote data source.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.rho.studio.ui.core.domain.model.Credentials
import com.rho.studio.ui.core.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe wrapper around FirebaseAuth SDK.
 */
@Singleton
class FirebaseRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val analyticsDataSource: AnalyticsRemoteDataSource
) {
    suspend fun login(credentials: Credentials): User {
        val result = firebaseAuth.signInWithEmailAndPassword(
            credentials.email,
            credentials.password
        ).await()

        val firebaseUser = result.user ?: throw Exception("Firebase user is null")
        
        analyticsDataSource.logLogin(firebaseUser.uid)

        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: credentials.email,
            name = firebaseUser.displayName ?: extractNameFromEmail(firebaseUser.email ?: credentials.email)
        )
    }

    private fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }
}
