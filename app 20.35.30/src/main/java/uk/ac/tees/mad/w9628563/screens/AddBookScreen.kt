package uk.ac.tees.mad.w9628563.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddBookScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcherCover = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Handle the captured image
        coverUri = bitmap?.let { getImageUri(context, it) }
    }

    val launcherPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Handle the selected PDF
        pdfUri = uri
    }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Book") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = {
                                if (cameraPermissionState.status.isGranted) {
                                    launcherCover.launch(null)
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Capture Cover")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        coverUri?.let {
                            Text("Cover Preview:")
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "PDF Preview"
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {

                        OutlinedButton(
                            onClick = { launcherPdf.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload PDF")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        pdfUri?.let {
                            Text("PDF Preview:")

                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null
                            )

                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty() && author.isNotEmpty() && coverUri != null && pdfUri != null) {
                            uploadBookToFirestore(title, author, coverUri!!, pdfUri!!, context) {
                                navController.popBackStack()
                            }
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Book")
                }
            }
        }
    }
}

fun getImageUri(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path)
}

fun uploadBookToFirestore(
    title: String,
    author: String,
    coverUri: Uri,
    pdfUri: Uri,
    context: Context,
    onSuccessfulUpload: () -> Unit = {}
) {
    val storage = Firebase.storage
    val firestore = Firebase.firestore

    val coverRef = storage.reference.child("covers/${UUID.randomUUID()}.jpg")
    val pdfRef = storage.reference.child("pdfs/${UUID.randomUUID()}.pdf")

    coverRef.putFile(coverUri)
        .addOnSuccessListener { coverTask ->
            coverRef.downloadUrl.addOnSuccessListener { coverUrl ->
                pdfRef.putFile(pdfUri)
                    .addOnSuccessListener { pdfTask ->
                        pdfRef.downloadUrl.addOnSuccessListener { pdfUrl ->
                            val book = hashMapOf(
                                "title" to title,
                                "author" to author,
                                "coverUrl" to coverUrl.toString(),
                                "pdfUrl" to pdfUrl.toString()
                            )
                            firestore.collection("books")
                                .add(book)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Book added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onSuccessfulUpload.invoke()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Failed to add book",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed to add pdf",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Failed to add cover",
                Toast.LENGTH_SHORT
            ).show()
        }
}