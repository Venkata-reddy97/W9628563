package uk.ac.tees.mad.w9628563

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.w9628563.navigation.BookLibraryNavigation
import uk.ac.tees.mad.w9628563.ui.theme.BookLibraryTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            BookLibraryTheme {
                BookLibraryNavigation()
            }
        }
    }
}
