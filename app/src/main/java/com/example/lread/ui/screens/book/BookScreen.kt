package com.example.lread.ui.screens.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lread.ui.screens.reader.ReaderScreen
import com.example.lread.ui.theme.LReadTheme

@Composable
fun BookScreen(
    navController: NavController,
    title: String,
    author: String,
    coverResId: Int? = null
) {
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
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column {
                Text(text = title, style = MaterialTheme.typography.headlineSmall)
                Text(text = author, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🖼 Book Cover
        coverResId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "$title Cover",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp) // Bigger cover
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 📊 Progress
        Text("Progress", style = MaterialTheme.typography.bodySmall)
        LinearProgressIndicator(
            progress = 0.2f,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📖 Chapters
        val chapters = listOf("Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4", "Chapter 5")
        chapters.forEachIndexed { index, chapter ->
            Button(
                onClick = { /* TODO: Navigate to chapter reader */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (index == 0) Color(0xFF1976D2) else Color(0xFF90CAF9)
                )
            ) {
                Text(text = chapter)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Continue reading */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Continue reading")
        }
    }
}