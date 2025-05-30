package com.example.lread.ui.screens.reader

import com.example.lread.data.model.Book
import com.example.lread.data.model.TextFont
import com.example.lread.data.model.TextTheme
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.TextSpacing

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
    val textSpacing: TextSpacing = TextSpacing.MEDIUM,
    val textTheme: TextTheme = TextTheme.SOFT_WHITE,
    val textFont: TextFont = TextFont.LIBRE_BASKERVILLE,
    val settingsVisible: Boolean = false,
    val topBarVisible: Boolean = true,
    val nextButtonVisible: Boolean = false
) {
    val currentChapterURL = "file:///android_asset/${book.chapters.getOrNull(currentChapter)}"

    val currentStylesScript = """
        document.body.style.fontFamily = '${textFont.value}';
        document.body.style.backgroundColor = '${textTheme.backgroundColor}';
        document.body.style.fontSize = '${textSize.size}px';
        document.body.style.lineHeight = '${textSpacing.size}';
        document.body.style.color = '${textTheme.textColor}'
    """.trimIndent()

    val onLastChapter = currentChapter == book.chapters.size - 1
}
