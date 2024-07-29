package uk.ac.tees.mad.w9628563.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9628563.screens.AddBookScreen
import uk.ac.tees.mad.w9628563.screens.BookDetailsScreen
import uk.ac.tees.mad.w9628563.screens.BookListScreen
import uk.ac.tees.mad.w9628563.screens.FavoriteScreen
import uk.ac.tees.mad.w9628563.screens.HomeScreen
import uk.ac.tees.mad.w9628563.screens.LoginScreen
import uk.ac.tees.mad.w9628563.screens.ProfileScreen
import uk.ac.tees.mad.w9628563.screens.RegisterScreen
import uk.ac.tees.mad.w9628563.screens.SplashScreen

@OptIn(ExperimentalSharedTransitionApi::class)
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

    SharedTransitionLayout {
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
                HomeScreen(
                    onBookClick = {
                        navController.navigate("${BookDetailDestination.navRoute}/$it")
                    },
                    onAddBook = {
                        navController.navigate(AddBookDestination.navRoute)
                    },
                    navController = navController
                )
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
                    onSignUpSuccess = { navController.navigate(HomeDestination.navRoute) })
            }

            composable(
                "${BookDetailDestination.navRoute}/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                println("bookId: $bookId")
                BookDetailsScreen(bookId = bookId!!, navController = navController)
            }

            composable(AddBookDestination.navRoute) {
                AddBookScreen(navController = navController)
            }

            composable(BookListDestination.navRoute) {
                BookListScreen(navController = navController)
            }

            composable(ProfileDestination.navRoute) {
                ProfileScreen(navController = navController)
            }

            composable(FavoriteDestination.navRoute) {
                FavoriteScreen(navController = navController)
            }
        }
    }

}