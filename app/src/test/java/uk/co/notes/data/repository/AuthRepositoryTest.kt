package uk.co.notes.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = AuthRepository()
    }

    @Test
    fun `login with valid credentials returns true`() = runTest {
        val result = repository.login("test@example.com", "password")
        assertTrue(result)
        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun `login with empty email returns false`() = runTest {
        val result = repository.login("", "password")
        assertFalse(result)
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `login with empty password returns false`() = runTest {
        val result = repository.login("test@example.com", "")
        assertFalse(result)
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `logout sets isLoggedIn to false`() = runTest {
        repository.login("test@example.com", "password")
        repository.logout()
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `isLoggedIn returns false initially`() {
        assertFalse(repository.isLoggedIn())
    }
}
