package uk.ac.tees.mad.w9628563.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9628563.screens.HomeScreen
import uk.ac.tees.mad.w9628563.screens.LoginScreen
import uk.ac.tees.mad.w9628563.screens.RegisterScreen
import uk.ac.tees.mad.w9628563.screens.SplashScreen

@Composable
fun BookLibraryNavigation() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val firebase = FirebaseAuth.getInstance()
    val currentUser = firebase.currentUser

    val startRoute =
        if (currentUser != null) {
            HomeDestination.navRoute
        } else {
            LoginDestination.navRoute
        }

    NavHost(navController = navController, startDestination = SplashDestination.navRoute) {
        composable(SplashDestination.navRoute) {
            SplashScreen {
                coroutineScope.launch(Dispatchers.Main) {
                    navController.popBackStack()
                    navController.navigate(startRoute)
                }
            }
        }
        composable(HomeDestination.navRoute) {
            HomeScreen()
        }

        composable(LoginDestination.navRoute) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(HomeDestination.navRoute) },
                onSignUpClick = { navController.navigate(RegisterDestination.navRoute) }
            )
        }

        composable(RegisterDestination.navRoute) {
            RegisterScreen(
                onLoginClick = { navController.navigate(LoginDestination.navRoute) },
                onSignUpSuccess = { navController.navigate(HomeDestination.navRoute) },
                onNavigateUp = { navController.navigateUp() })
        }
    }

}