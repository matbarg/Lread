package com.example.lread.ui.screens.reader

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lread.ui.theme.LReadTheme

@Composable
fun ReaderScreen(modifier: Modifier = Modifier) {

    val viewModel: ReaderViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState()

    val context = LocalContext.current

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
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = {
                    WebView(it).apply {
                        webViewClient = ReaderWebViewClient { uiState.value.currentJsStyles }
                        settings.apply {
                            javaScriptEnabled = true
                            setSupportZoom(false) // todo: das hat kurz funktioniert und dann auf einmal nicht mehr ??????????
                        }
                        loadUrl(uiState.value.currentChapterURL)
                    }
                },
                update = { webView ->
                    if (webView.url != uiState.value.currentChapterURL) { // prevents the webView from reloading if only the css was changed
                        webView.loadUrl(uiState.value.currentChapterURL)
                    } else {
                        webView.evaluateJavascript(uiState.value.currentJsStyles, null)
                    }
                }
            )

            Button(
                modifier = modifier.align(Alignment.BottomStart),
                onClick = {
                    //viewModel.goToNextChapter()
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

class ReaderWebViewClient(
    private val getJsStyles: () -> String
) : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(getJsStyles(), null)
    }
}

@Preview(showBackground = true)
@Composable
fun ReaderScreenPreview() {
    LReadTheme {
        ReaderScreen()
    }
}