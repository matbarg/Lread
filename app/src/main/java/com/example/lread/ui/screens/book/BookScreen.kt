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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.draw.shadow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.lread.ui.navigation.NavRoute
import com.example.lread.ui.theme.lreadBlue
import com.example.lread.ui.theme.lreadLightBlue
import com.example.lread.ui.theme.lreadPurpleTranslucent
import com.example.lread.ui.theme.lreadPurple


@Composable
fun BookScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BookViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    val lifeCycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifeCycleOwner) {
        lifeCycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lreadLightBlue, lreadPurpleTranslucent)
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
                Text(
                    text = uiState.value.book.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(text = uiState.value.book.author, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📈 Reading Progress Bar
        Text(
            text = "Reading Progress: ${(uiState.value.progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = uiState.value.progress,
            color = lreadPurple,
            trackColor = Color.LightGray,
            modifier = Modifier
                .height(25.dp)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(100))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🖼 Book Cover
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/${uiState.value.book.cover}"),
            contentDescription = "${uiState.value.book.title} Cover",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 📖 Chapters
        Text("Chapters", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = modifier.height(250.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(uiState.value.book.chapters.size) { index ->
                Button(
                    modifier = modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 12.dp),
                    colors = ButtonColors(
                        containerColor = if (uiState.value.chapter != index) lreadBlue else lreadPurple,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Green,
                        disabledContentColor = Color.Yellow
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        navController.navigate(
                            NavRoute.ReaderScreen(
                                bookId = uiState.value.book.id,
                                chapter = index
                            )
                        )
                    }) {
                    Text(
                        text = "Chapter ${index + 1}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        Spacer(modifier = modifier.height(30.dp))

        Button(
            modifier = modifier
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                .align(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
            colors = ButtonColors(
                containerColor = lreadBlue,
                contentColor = Color.White,
                disabledContainerColor = Color.Green,
                disabledContentColor = Color.Yellow
            ),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                if (uiState.value.bookIsFinished) viewModel.deleteBookProgress() // previous book progress is deleted when "Read again" is clicked
                navController.navigate(NavRoute.ReaderScreen(bookId = uiState.value.book.id))
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val msg =
                    if (uiState.value.bookIsFirstOpened) "Start reading" else if (uiState.value.bookIsFinished) "Read again" else "Continue reading"

                Text(msg)
                Icon(
                    imageVector = if (uiState.value.bookIsFinished) Icons.Default.Refresh else Icons.Default.KeyboardArrowRight,
                    contentDescription = msg
                )
            }
        }

    }
}
