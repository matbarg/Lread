@file:OptIn(ExperimentalFoundationApi::class)

package com.example.lread.ui.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.lread.data.model.Book
import com.example.lread.data.model.getSampleBooks

@Composable
fun LibraryScreen(
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val originalBooks = remember { getSampleBooks() }
    var books by remember { mutableStateOf(originalBooks) }
    var isSortedAlphabetically by remember { mutableStateOf(false) }

    // State to track favorite books by ID
    val favoriteIds = remember { mutableStateListOf<String>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF8BC34A), Color(0xFFFF9800))
                )
            )
    ) {
        Column {
            // 🧹 Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Add book logic */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Book")
                }

                TextButton(onClick = {
                    isSortedAlphabetically = !isSortedAlphabetically
                    books = if (isSortedAlphabetically) {
                        books.sortedBy { it.title }
                    } else {
                        originalBooks
                    }
                }) {
                    Text(if (isSortedAlphabetically) "Sorted" else "Sort Alphabetically")
                }
            }

            // 🖼 Book Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(books) { book ->
                    val isFavorite = favoriteIds.contains(book.id)
                    BookCard(
                        book = book,
                        isFavorite = isFavorite,
                        onClick = { onBookClick(book) },
                        onLongClick = {
                            if (isFavorite) favoriteIds.remove(book.id)
                            else favoriteIds.add(book.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(240.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "file:///android_asset/${book.cover}"),
                contentDescription = book.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            if (isFavorite) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .background(Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("★", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
