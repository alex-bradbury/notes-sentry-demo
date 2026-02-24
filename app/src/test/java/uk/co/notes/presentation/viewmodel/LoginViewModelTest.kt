package uk.co.notes.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.co.notes.data.repository.AuthRepository

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isLoggedIn)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `login with valid credentials updates state correctly`() = runTest {
        coEvery { authRepository.login("test@example.com", "password") } returns true

        viewModel.uiState.test {
            awaitItem()
            viewModel.login("test@example.com", "password")
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isLoggedIn)
            assertNull(successState.errorMessage)
        }
    }

    @Test
    fun `login with invalid credentials shows error`() = runTest {
        coEvery { authRepository.login("test@example.com", "wrong") } returns false

        viewModel.uiState.test {
            awaitItem()
            viewModel.login("test@example.com", "wrong")
            
            awaitItem()
            
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.isLoggedIn)
            assertEquals("Invalid credentials", errorState.errorMessage)
        }
    }

    @Test
    fun `clearError removes error message`() = runTest {
        coEvery { authRepository.login("test@example.com", "wrong") } returns false

        viewModel.uiState.test {
            awaitItem()
            viewModel.login("test@example.com", "wrong")
            awaitItem()
            awaitItem()
            
            viewModel.clearError()
            
            val clearedState = awaitItem()
            assertNull(clearedState.errorMessage)
        }
    }
}
