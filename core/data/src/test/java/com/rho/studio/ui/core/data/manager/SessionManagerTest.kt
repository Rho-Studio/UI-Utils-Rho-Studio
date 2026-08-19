package com.rho.studio.ui.core.data.manager

import com.rho.studio.ui.core.domain.model.SessionState
import com.rho.studio.ui.core.domain.model.User
import com.rho.studio.ui.core.domain.repository.SessionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SessionManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SessionRepository
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads saved session - authenticated`() = runTest {
        val user = User("1", "test@rho.studio", "Test User")
        coEvery { repository.getUser() } returns user

        sessionManager = SessionManager(repository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = sessionManager.sessionState.value
        assertTrue(state is SessionState.Authenticated)
        assertEquals(user, (state as SessionState.Authenticated).user)
    }

    @Test
    fun `init loads saved session - guest`() = runTest {
        coEvery { repository.getUser() } returns null

        sessionManager = SessionManager(repository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(sessionManager.sessionState.value is SessionState.Guest)
    }

    @Test
    fun `updateSession updates state and saves to repository`() = runTest {
        coEvery { repository.getUser() } returns null
        sessionManager = SessionManager(repository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val user = User("2", "new@rho.studio", "New User")
        sessionManager.updateSession(user)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = sessionManager.sessionState.value
        assertTrue(state is SessionState.Authenticated)
        assertEquals(user, (state as SessionState.Authenticated).user)
        coVerify { repository.saveUser(user) }
    }

    @Test
    fun `clearSession updates state to Guest and clears repository`() = runTest {
        val user = User("1", "test@rho.studio", "Test User")
        coEvery { repository.getUser() } returns user
        sessionManager = SessionManager(repository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        sessionManager.clearSession()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(sessionManager.sessionState.value is SessionState.Guest)
        coVerify { repository.clearSession() }
    }
}
