package uk.ac.tees.mad.w9628563.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.ac.tees.mad.w9628563.navigation.BottomNavigationBar
import uk.ac.tees.mad.w9628563.navigation.FavoriteDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
    ) {
        Column(modifier = Modifier.padding(it)) {

            Button(onClick = { navController.navigate(FavoriteDestination.navRoute) }) {
                Text("Favorites")
            }
        }
    }
}