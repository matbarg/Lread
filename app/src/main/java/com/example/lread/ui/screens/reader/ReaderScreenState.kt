package com.example.lread.ui.screens.reader

import com.example.lread.data.model.Book

val emptyBook = Book(
    id = "",
    title = "",
    author = "",
    cover = "",
    chapters = emptyList()
)

data class ReaderScreenState(
    val book: Book = emptyBook,
    val currentChapter: Int = 0, // starting chapter
    val currentAnchorId: String = "para-0", // starting anchor/paragraph
    val fontSize: Int = 20, // in pixel
    val lineSpacing: Int = 100, // percent
) {
    val currentChapterURL = "file:///android_asset/${book.chapters.getOrNull(currentChapter)}"

    val currentStylesScript = """
        document.body.style.fontSize = '${fontSize}px';
    """.trimIndent()
}
