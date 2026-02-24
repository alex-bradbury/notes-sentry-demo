package uk.co.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.co.notes.data.repository.AuthRepository
import uk.co.notes.navigation.Login
import uk.co.notes.navigation.Notes
import uk.co.notes.presentation.screen.LoginScreen
import uk.co.notes.presentation.screen.NotesScreen
import uk.co.notes.ui.theme.NotesTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesApp(authRepository)
                }
            }
        }
    }
}

@Composable
fun NotesApp(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val startDestination = if (authRepository.isLoggedIn()) Notes else Login
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Notes) {
                        popUpTo<Login> { inclusive = true }
                    }
                }
            )
        }
        composable<Notes> {
            NotesScreen(
                onLogout = {
                    authRepository.logout()
                    navController.navigate(Login) {
                        popUpTo<Notes> { inclusive = true }
                    }
                }
            )
        }
    }
}