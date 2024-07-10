package uk.ac.tees.mad.w9628563.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.w9628563.domain.BookItem
import uk.ac.tees.mad.w9628563.googleBooksApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit
) {
    val books = remember { mutableStateListOf<BookItem>() }

    Scaffold(Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Text("Book Library", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        )
    }) { innerPadding ->
        if (books.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(books) {

                    BookCard(
                        book = it,
                        onClick = {
                            onBookClick(it.id)
                        }
                    )
                }
            }
        }


        LaunchedEffect(Unit) {
            try {
                val response = googleBooksApiService.getBooks("best+subject:fiction ")
                books.clear()
                books.addAll(response.items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookItem,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http", "https")
        if (imageUrl != null) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
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
        Column(Modifier.padding(8.dp)) {
            Text(text = book.volumeInfo.title, maxLines = 2, fontWeight = FontWeight.Medium)
        }
    }
}

