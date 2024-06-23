package uk.ac.tees.mad.w9628563.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9628563.screens.HomeScreen
import uk.ac.tees.mad.w9628563.screens.SplashScreen

@Composable
fun BookLibraryNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = SplashDestination.navRoute) {
        composable(SplashDestination.navRoute){
            SplashScreen {
                coroutineScope.launch (Dispatchers.Main){
                    navController.popBackStack()
                    navController.navigate(HomeDestination.navRoute)
                }
            }
        }
        composable(HomeDestination.navRoute){
            HomeScreen()
        }
    }

}