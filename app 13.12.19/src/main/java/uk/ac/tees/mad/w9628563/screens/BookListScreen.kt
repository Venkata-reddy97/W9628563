package uk.ac.tees.mad.w9628563.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.w9628563.navigation.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavHostController) {
    val books = remember { mutableStateOf<List<BookItem>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fetchBooksFromFirestore { fetchedBooks ->
            books.value = fetchedBooks
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Books") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                if (books.value.isEmpty()) {
                    Text(text = "No books added.")
                }
            }
            items(books.value) { book ->
                BookItemView(book, context)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BookItemView(book: BookItem, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Author: ${book.author}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            book.coverUrl?.let { coverUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(coverUrl)
                        .crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { downloadPdf(book.pdfUrl, context) }) {
                Text("Download PDF")
            }
        }
    }
}

fun downloadPdf(pdfUrl: String, context: Context) {
    val request = DownloadManager.Request(Uri.parse(pdfUrl))
        .setTitle("Downloading PDF")
        .setDescription("Downloading ${Uri.parse(pdfUrl).lastPathSegment}")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}

fun fetchBooksFromFirestore(onBooksFetched: (List<BookItem>) -> Unit) {
    val firestore = Firebase.firestore
    firestore.collection("books")
        .get()
        .addOnSuccessListener { result ->
            val books = result.map { document ->
                BookItem(
                    title = document.getString("title") ?: "",
                    author = document.getString("author") ?: "",
                    coverUrl = document.getString("coverUrl") ?: "",
                    pdfUrl = document.getString("pdfUrl") ?: ""
                )
            }
            onBooksFetched(books)
        }
        .addOnFailureListener { exception ->
            Log.w("BookListScreen", "Error getting documents: ", exception)
        }
}

data class BookItem(
    val title: String,
    val author: String,
    val coverUrl: String?,
    val pdfUrl: String
)