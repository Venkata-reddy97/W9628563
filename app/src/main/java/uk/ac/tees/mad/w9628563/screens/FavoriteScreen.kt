package uk.ac.tees.mad.w9628563.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.w9628563.database.FavoriteBook
import uk.ac.tees.mad.w9628563.viewmodels.FavoriteBookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavHostController,
    favoriteBookViewModel: FavoriteBookViewModel = hiltViewModel()
) {
    val favoriteBooks by favoriteBookViewModel.favoriteBooks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Books") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favoriteBooks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorite books found")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteBooks) { favoriteBook ->
                    FavoriteBookCard(
                        book = favoriteBook,
                        onClick = {
                            navController.navigate("bookDetails/${favoriteBook.id}")
                        },
                        favoriteBookViewModel = favoriteBookViewModel
                    )
                }
            }
        }
    }
}


@Composable
fun FavoriteBookCard(
    book: FavoriteBook,
    onClick: () -> Unit,
    favoriteBookViewModel: FavoriteBookViewModel = viewModel()
) {
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(book.id) {
        isFavorite = favoriteBookViewModel.isFavorite(book.id)
    }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val imageUrl = book.thumbnail?.replace("http", "https")
            if (imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .background(Color.Gray)
                        .fillMaxWidth()
                ) {
                    Text("No Image Available", modifier = Modifier.align(Alignment.Center))
                }
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f)),
                onClick = {
                    if (isFavorite) {
                        favoriteBookViewModel.removeFavoriteBook(book.id)
                    } else {
                        favoriteBookViewModel.addFavoriteBook(
                            FavoriteBook(
                                id = book.id,
                                title = book.title,
                                thumbnail = book.thumbnail
                            )
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red.copy(alpha = 0.8f) else Color.Gray
                )
            }
        }
        Column(Modifier.padding(8.dp)) {
            Text(text = book.title, maxLines = 2, fontWeight = FontWeight.Medium)

        }
    }
}