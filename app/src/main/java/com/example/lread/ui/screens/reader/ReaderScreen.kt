package com.example.lread.ui.screens.reader

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.lread.ui.theme.LReadTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(modifier: Modifier = Modifier) {

    val viewModel: ReaderViewModel

    val context = LocalContext.current
    //val url = "file:///android_asset/later/part0000.xhtml"
    val url = "file:///android_asset/animal-farm/ch01.xhtml"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reader") },
                actions = {}
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text("Hello")
            AndroidView(
                factory = {
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(url)
                    }
                },
                update = { webView ->

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