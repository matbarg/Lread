package com.example.lread.utils

import android.webkit.JavascriptInterface

class ReaderJsBridge(
    private val onAnchorChange: (String) -> Unit
) {
    @JavascriptInterface
    fun reportVisibleAnchor(id: String) {
        onAnchorChange(id)
    }
}