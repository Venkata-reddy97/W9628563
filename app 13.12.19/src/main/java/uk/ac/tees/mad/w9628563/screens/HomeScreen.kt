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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9628563.database.FavoriteBook
import uk.ac.tees.mad.w9628563.domain.BookItem
import uk.ac.tees.mad.w9628563.googleBooksApiService
import uk.ac.tees.mad.w9628563.navigation.BottomNavigationBar
import uk.ac.tees.mad.w9628563.viewmodels.FavoriteBookViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    onAddBook: () -> Unit,
    navController: NavHostController,
    favoriteBookViewModel: FavoriteBookViewModel = hiltViewModel()
) {
    val books = remember { mutableStateListOf<BookItem>() }
    var searchValue by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Book Library", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBook
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }) { innerPadding ->
        if (books.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OutlinedTextField(
                    value = searchValue,
                    onValueChange = { searchValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    placeholder = {
                        Text(text = "Books, authors, category ...")
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            focusManager.clearFocus()
                            scope.launch {
                                val response = if (searchValue.isEmpty())
                                    googleBooksApiService.getBooks("best+subject:fiction ")
                                else googleBooksApiService.getBooks(searchValue)
                                books.clear()
                                books.addAll(response.items)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                        scope.launch {
                            val response = if (searchValue.isEmpty())
                                googleBooksApiService.getBooks("best+subject:fiction ")
                            else googleBooksApiService.getBooks(searchValue)
                            books.clear()
                            books.addAll(response.items)
                        }
                    })
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(books) {

                        BookCard(book = it, onClick = {
                            onBookClick(it.id)
                        }, favoriteBookViewModel = favoriteBookViewModel)
                    }
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
    onClick: () -> Unit,
    favoriteBookViewModel: FavoriteBookViewModel = viewModel()
) {
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(book.id) {
        isFavorite = favoriteBookViewModel.isFavorite(book.id)
    }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {

            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http", "https")
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
                                title = book.volumeInfo.title,
                                thumbnail = book.volumeInfo.imageLinks?.thumbnail
                            )
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red.copy(alpha = 0.8f) else Color.Black
                )
            }
        }
        Column(Modifier.padding(8.dp)) {
            Text(text = book.volumeInfo.title, maxLines = 2, fontWeight = FontWeight.Medium)
        }
    }
}

