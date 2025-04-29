package com.example.lread.ui.screens.reader

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
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

    Scaffold(
        topBar = {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(10.dp),
            ) {
                IconButton(modifier = modifier.align(Alignment.CenterStart), onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Column(
                    modifier = modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.value.book.title, fontWeight = FontWeight.Bold)
                    Text(text = uiState.value.book.author)
                }
                Button(modifier = modifier.align(Alignment.CenterEnd), onClick = {}) {
                    Text(text = "Chapter ${uiState.value.currentChapter + 1}")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
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
                                setSupportZoom(false) // todo: das hat kurz funktioniert und dann auf einmal nicht mehr ??????????
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

            Button(
                modifier = modifier.align(Alignment.BottomStart),
                onClick = {
                    viewModel.setFontSize(25)
                }) {
                Text("Text size")
            }
            Button(
                modifier = modifier.align(Alignment.BottomEnd),
                onClick = {
                    viewModel.goToNextChapter()
                }) {
                Text("Next chapter")
            }
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