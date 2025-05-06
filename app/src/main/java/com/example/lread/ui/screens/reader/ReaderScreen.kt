package com.example.lread.ui.screens.reader

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.lread.data.model.TextSize
import com.example.lread.ui.theme.LReadTheme
import com.example.lread.utils.ReaderJsBridge
import com.example.lread.utils.ReaderWebViewClient

@Composable
fun ReaderScreen(
    modifier: Modifier = Modifier,
    viewModel: ReaderViewModel = hiltViewModel(),
    bookId: String = "book1"
) {
    viewModel.initializeReaderState()

    val uiState = viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val lifeCycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifeCycleOwner) {
        lifeCycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    val scrolled = remember { mutableStateOf(false) }
    // todo: move colors to viewmodel
    val color1 = Color(0xFF7BA3FF)
    val color2 = Color(0xFF0E5CFD)

    Scaffold() { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(modifier = modifier
                .padding(horizontal = 14.dp)
            ) {
                // rendering the webview needs to wait until the BookProgress was fetched from the db
                if (!viewModel.loadingInProgress.value) {
                    AndroidView(
                        modifier = modifier.fillMaxSize(),
                        factory = {
                            WebView(it).apply {
                                webViewClient =
                                    ReaderWebViewClient(getJsStyles = { uiState.value.currentStylesScript },
                                        getCurrentAnchorId = { uiState.value.currentAnchorId })
                                settings.apply {
                                    javaScriptEnabled = true
                                    //setSupportZoom(false) // todo: das hat kurz funktioniert und dann auf einmal nicht mehr ??????????
                                    setOnScrollChangeListener { _, _, scrollY, _, _ ->
                                        scrolled.value = true
                                    }
                                }

                                // enables the callback to set the current anchorId from inside the webView
                                addJavascriptInterface(ReaderJsBridge { anchorId ->
                                    viewModel.setCurrentAnchorId(anchorId)
                                }, "ReaderBridge")

                                loadUrl(uiState.value.currentChapterURL)
                            }
                        },
                        update = { webView ->
                            if (webView.url != uiState.value.currentChapterURL) { // prevents the webView from reloading if only the css was changed
                                webView.loadUrl(uiState.value.currentChapterURL)
                            } else {
                                webView.evaluateJavascript(uiState.value.currentStylesScript, null)
                            }
                        }
                    )
                } else {
                    Text("Loading...")
                }
            }
            /**
             * Top bar
             * it's not the Scaffolds topBar directly because it acts more as an overlay
             */
            Column(modifier.background(if (!scrolled.value or uiState.value.settingsExpanded) color1 else Color.Transparent)) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    IconButton(
                        modifier = modifier
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color2),
                        onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(modifier = modifier.align(Alignment.Center), visible = !scrolled.value or uiState.value.settingsExpanded, enter = fadeIn(initialAlpha = 0.8f), exit = fadeOut(targetAlpha = 0.4f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = uiState.value.book.title, fontWeight = FontWeight.Bold)
                            Text(text = uiState.value.book.author)
                        }
                    }

                    IconButton(
                        modifier = modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color2),
                        onClick = {
                            viewModel.toggleSettings()
                            scrolled.value = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Settings"
                        )
                    }
                }

                if (uiState.value.settingsExpanded) {
                    Text("Select Chapter")
                    ChapterDropdown(
                        currentChapter = uiState.value.currentChapter,
                        totalChapters = uiState.value.book.chapters.size
                    ) { viewModel.setCurrentChapter(it) }

                    Text("Text Display Settings")
                    TextSizeDropdown(
                        label = uiState.value.textSize.label,
                        setTextSize = { viewModel.setTextSize(it) })
                }
            }

            /**
             * Next chapter button
             */
            Button(
                modifier = modifier.align(Alignment.BottomEnd),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                onClick = {
                    viewModel.goToNextChapter()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Next Chapter")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Chapter"
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsBar(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    textSizeLabel: String,
    setTextSize: (TextSize) -> Unit,
    toggleSettings: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                toggleSettings()
            }) {
            Text("Settings")
        }

        // Makes the settings bar transition in size and fades out child composables on visibility change
        AnimatedVisibility(visible = isExpanded) {
            TextSizeDropdown(label = textSizeLabel) { setTextSize(it) }
        }
    }
}

@Composable
fun ChapterDropdown(
    modifier: Modifier = Modifier,
    currentChapter: Int,
    totalChapters: Int,
    setChapter: (Int) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = modifier) {

        Button(onClick = { expanded.value = !expanded.value }) {
            Text("Chapter ${currentChapter + 1}")
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            // that's the cool kotlin way to loop, alternative would be "for (i in 0 until totalChapters)"
            repeat(totalChapters) { i ->
                DropdownMenuItem(
                    text = { Text("Chapter ${i + 1}") },
                    onClick = {
                        setChapter(i)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun TextSizeDropdown(
    modifier: Modifier = Modifier,
    label: String,
    setTextSize: (TextSize) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Button(onClick = { expanded.value = !expanded.value }) {
        Text("Text size: $label")
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        TextSize.entries.forEach {
            DropdownMenuItem(
                text = { Text(it.label) },
                onClick = {
                    setTextSize(it)
                    expanded.value = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReaderScreenPreview() {
    LReadTheme {
        ReaderScreen()
    }
}