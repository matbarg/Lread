package com.example.lread.ui.screens.reader

import androidx.compose.ui.unit.dp
import com.example.lread.data.model.Book
import com.example.lread.data.model.TextSize

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
    val textSize: TextSize = TextSize.MEDIUM,
    val lineSpacing: Int = 100, // percent
    val settingsExpanded: Boolean = false
) {
    val currentChapterURL = "file:///android_asset/${book.chapters.getOrNull(currentChapter)}"

    val currentStylesScript = """
        document.body.style.fontSize = '${textSize.size}px';
    """.trimIndent()

    val bottomBarHeight = if (settingsExpanded) 50.dp else 400.dp
}
