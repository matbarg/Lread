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
    val textTheme: TextTheme = TextTheme.SOFT_WHITE, // Requires TextTheme.SOFT_WHITE
    val textFont: TextFont = TextFont.LIBRE_BASKERVILLE, // Requires TextFont.LIBRE_BASKERVILLE
    val settingsVisible: Boolean = false,
    val topBarVisible: Boolean = true,
    val nextButtonVisible: Boolean = false,

    // 🎵 Music playback options
    val isMusicEnabled: Boolean = false,
    val selectedTrack: String = "Lo-fi Vibes" // Ensure this default matches a display name in your UI dropdown
) {
    val currentChapterURL: String
        get() = "file:///android_asset/${book.chapters.getOrNull(currentChapter) ?: ""}" // Added null-check

    val currentStylesScript: String
        get() = """
            javascript:(function() {
                document.body.style.fontFamily = '${textFont.value}'; // Requires textFont.value
                document.body.style.backgroundColor = '${textTheme.backgroundColor}'; // Requires textTheme.backgroundColor
                document.body.style.fontSize = '${textSize.size}px'; // Requires textSize.size
                document.body.style.lineHeight = '${textSpacing.size}'; // Requires textSpacing.size
                document.body.style.color = '${textTheme.textColor}'; // Requires textTheme.textColor
            })();
        """.trimIndent()

    val onLastChapter: Boolean
        get() = currentChapter == book.chapters.size - 1
}