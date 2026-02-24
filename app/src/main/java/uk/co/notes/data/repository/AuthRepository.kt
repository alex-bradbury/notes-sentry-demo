package uk.co.notes.data.repository

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private var isLoggedIn = false

    suspend fun login(email: String, password: String): Boolean {
        delay(1000)
        isLoggedIn = email.isNotEmpty() && password.isNotEmpty()
        return isLoggedIn
    }

    fun isLoggedIn(): Boolean = isLoggedIn

    fun logout() {
        isLoggedIn = false
    }
}