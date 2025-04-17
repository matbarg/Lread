package com.example.lread.ui.screens.reader

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReaderViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderScreenState(currentChapter = "file:///android_asset/Later/part0000.xhtml"))
    val uiState: StateFlow<ReaderScreenState> = _uiState.asStateFlow()

    fun injectCss(webView: WebView) {
        val css = """
            body { font-size: ${_uiState.value.fontSize}% }
        """.trimIndent()

        val js = """
            javascript:(function() {
                var style = document.createElement('style');
                style.innerHTML = `$css`;
                document.head.appendChild(style);
            })()
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    fun setFontSize(size: Int) {
        _uiState.update { it.copy(fontSize = size) }
    }

    fun goToNextChapter() {

    }
}