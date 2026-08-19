package com.rho.studio.ui.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class DaggerGraphTest {

    @Before
    fun setUp() {
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
    }

    @Test
    fun `verify Dagger graph initialization`() {
        val mockContext = mockk<Context>(relaxed = true)
        
        // Initialize ComponentManager
        ComponentManager.init(mockContext)
        
        val appComponent = ComponentManager.getAppComponent()
        assertNotNull(appComponent)
        assertNotNull(appComponent.viewModelFactory())
        
        // Verify CoreComponent exposures
        val coreComponent = appComponent.coreComponent()
        assertNotNull(coreComponent)
        assertNotNull(coreComponent.sessionManager())
        assertNotNull(coreComponent.authRepository())
    }

    @Test
    fun `verify UserComponent lifecycle`() {
        val mockContext = mockk<Context>(relaxed = true)
        ComponentManager.init(mockContext)
        
        val userComponent = ComponentManager.createUserComponent()
        assertNotNull(userComponent)
        assertNotNull(userComponent.viewModelFactory())
        
        ComponentManager.destroyUserComponent()
        // Note: ComponentManager.getUserComponent() would return null now
    }
}
