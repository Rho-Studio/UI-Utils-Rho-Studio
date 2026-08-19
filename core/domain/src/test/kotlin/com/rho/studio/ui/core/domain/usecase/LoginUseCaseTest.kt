/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         LoginUseCaseTest.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-12
 * ==============================================================================================
 * Description: Check expected behavior of [LoginUseCase] with simplified credentials.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.usecase

import com.rho.studio.ui.core.domain.model.*
import com.rho.studio.ui.core.domain.repository.AuthRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManagerInterface

    @Before
    fun setUp() {
        authRepository = mockk()
        sessionManager = mockk(relaxed = true)
        loginUseCase = LoginUseCase(authRepository, sessionManager)
    }

    @Test
    fun `login success calls repository and updates session`() = runBlocking {
        val credentials = Credentials("test@rho.studio", "password123")
        val user = User("user_123", "test@rho.studio", "Test User")
        
        coEvery { authRepository.login(credentials) } returns user
        
        val result = loginUseCase(credentials)
        
        assertTrue(result is Result.Success)
        assertEquals(user, (result as Result.Success).data)
        coVerify { sessionManager.updateSession(user) }
    }

    @Test
    fun `login failure returns error result`() = runBlocking {
        val credentials = Credentials("test@rho.studio", "password123")
        val exceptionMessage = "Auth failed"
        val exception = RuntimeException(exceptionMessage)
        
        coEvery { authRepository.login(credentials) } throws exception
        
        val result = loginUseCase(credentials)
        
        assertTrue(result is Result.Error)
        assertEquals(exceptionMessage, (result as Result.Error).exception.message)
        coVerify(exactly = 0) { sessionManager.updateSession(any()) }
    }

    @Test
    fun `logout success calls session clear`() = runBlocking {
        val logoutUseCase = LogoutUseCase(sessionManager)
        logoutUseCase(Unit)
        coVerify { sessionManager.clearSession() }
    }
}
