package com.example.lread.ui.screens.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lread.data.model.Book
import androidx.compose.foundation.clickable


@Composable
fun BookScreen(
    navController: NavController,
    book: Book
) {
    var readingProgress by remember { mutableStateOf(0.2f) } // 20% gelesen (später dynamisch machen)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF8BC34A), Color(0xFFFF9800))
                )
            )
            .padding(16.dp)
    ) {
        // 🔙 Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = book.title, style = MaterialTheme.typography.headlineSmall)
                Text(text = book.author, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📈 Reading Progress Bar
        Text(
            text = "Reading Progress: ${(readingProgress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = readingProgress,
            color = Color.Red,
            trackColor = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🖼 Book Cover
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/${book.cover}"),
            contentDescription = "${book.title} Cover",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 📖 Chapters
        Text("Chapters", style = MaterialTheme.typography.titleMedium)

        book.chapters.forEachIndexed { index, _ ->
            Text(
                text = "Chapter ${index + 1}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(Color(0xFFFFECB3), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )
        }

    }
}
