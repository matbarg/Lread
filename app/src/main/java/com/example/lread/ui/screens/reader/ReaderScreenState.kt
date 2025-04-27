package com.example.lread.ui.screens.reader

import com.example.lread.data.model.Book

data class ReaderScreenState(
    val book: Book,
    val currentChapter: Int,
    val currentPage: String = "",
    val fontSize: Int = 25, // in pixel
    val lineSpacing: Int = 100, // percent
    val currentAnchorId: String = "para-30"
) {
    val currentChapterURL = "file:///android_asset/${book.chapters.getOrNull(currentChapter)}"

    val currentStylesScript = """
        document.body.style.fontSize = '${fontSize}px';
    """.trimIndent()
}
