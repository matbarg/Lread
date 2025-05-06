@file:OptIn(ExperimentalFoundationApi::class)

package com.example.lread.ui.screens.library

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.lread.data.model.Book
import com.example.lread.data.model.getSampleBooks

enum class SortType { TITLE, AUTHOR, FAVORITES }

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel(),
    onBookClick: (Book) -> Unit
) {
    val originalBooks = remember { getSampleBooks() }
    var sortType by remember { mutableStateOf(SortType.TITLE) }
    val favoriteIds = viewModel.favoriteIds

    val books = remember(sortType, favoriteIds) {
        when (sortType) {
            SortType.TITLE -> originalBooks.sortedBy { it.title }
            SortType.AUTHOR -> originalBooks.sortedBy { it.author }
            SortType.FAVORITES -> originalBooks.sortedByDescending { favoriteIds.contains(it.id) }
        }
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

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

                Box {
                    TextButton(onClick = { dropdownExpanded = true }) {
                        Text("Sort by ${sortType.name.lowercase().replaceFirstChar { it.uppercase() }} ▼")
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Title") }, onClick = {
                            sortType = SortType.TITLE
                            dropdownExpanded = false
                        })
                        DropdownMenuItem(text = { Text("Author") }, onClick = {
                            sortType = SortType.AUTHOR
                            dropdownExpanded = false
                        })
                        DropdownMenuItem(text = { Text("Favorites") }, onClick = {
                            sortType = SortType.FAVORITES
                            dropdownExpanded = false
                        })
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        isFavorite = favoriteIds.contains(book.id),
                        onClick = { onBookClick(book) },
                        onLongPress = { viewModel.toggleFavorite(book.id) }
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
    onLongPress: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
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
                        .size(26.dp)
                        .background(Color.Red, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("★", fontSize = 16.sp, color = Color.White)
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
