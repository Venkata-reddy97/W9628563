package uk.ac.tees.mad.w9628563.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.w9628563.domain.BookItem
import uk.ac.tees.mad.w9628563.googleBooksApiService

@Composable
fun BookDetailsScreen(bookId: String, navController: NavHostController) {
    var book by remember { mutableStateOf<BookItem?>(null) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        try {
            val response = googleBooksApiService.getBookById(bookId)
            book = response
            println(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (book != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.height(300.dp)) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .zIndex(10f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                val imageUrl = book!!.volumeInfo.imageLinks?.thumbnail?.replace("http", "https")
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
                            .fillMaxWidth()
                    ) {
                        Text("No Image Available", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = book!!.volumeInfo.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Authors: ${book!!.volumeInfo.authors?.joinToString(", ")}" ?: "Unknown Author",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = book!!.volumeInfo.description ?: "No Description Available",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { isDescriptionExpanded = !isDescriptionExpanded },
                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Justify
                )
                if (isDescriptionExpanded) {
                    Text(
                        text = "Show Less",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .clickable { isDescriptionExpanded = false }
                            .fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                } else {
                    Text(
                        text = "Show More",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clickable { isDescriptionExpanded = true }
                            .fillMaxWidth(),
                        textAlign = TextAlign.End

                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                book!!.volumeInfo.categories?.let {
                    it.forEach {
                        Card(modifier = Modifier.padding(6.dp)) {
                            TextButton(onClick = { /*TODO*/ }) {
                                Text(text = it)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text(
                        text = "More info",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Publisher",
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                            Text(text = "${book!!.volumeInfo.publisher}", fontSize = 16.sp)
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Publish Date",
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                            Text(text = "${book!!.volumeInfo.publishedDate}", fontSize = 16.sp)
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Page Count",
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                            Text(text = "${book!!.volumeInfo.pageCount}", fontSize = 16.sp)
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Language",
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                            Text(text = "${book!!.volumeInfo.language}", fontSize = 16.sp)
                        }

                    }

                }
                val uriHandler = LocalUriHandler.current
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { uriHandler.openUri(book!!.volumeInfo.previewLink) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Open the book",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}