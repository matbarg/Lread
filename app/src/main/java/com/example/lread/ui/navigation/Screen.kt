package com.example.lread.ui.navigation

import kotlinx.serialization.Serializable

/**
 * This is a newer approach to routing which uses a serialization library.
 * It makes passing arguments more robust and shorter.
 * Explanation video: https://www.youtube.com/watch?v=AIC_OFQ1r3k
 */
sealed class NavRoute {
    @Serializable
    data object LibraryScreen : NavRoute()

    @Serializable
    data class BookScreen(val bookId: String) : NavRoute()

    @Serializable
    data class ReaderScreen(val bookId: String, val chapter: Int? = null) : NavRoute()
}

/* old code:
sealed class Screen (val route: String) {
    object LibraryScreen: Screen(route = "library_screen")
    object BookScreen: Screen(route = "book_screen/{bookid}") {
        fun createRoute(bookid: String) = "book_screen/${bookid}"
    }
    object ReaderScreen: Screen(route ="reader_screen/{bookid}") {
        fun createRoute(bookid: String) = "reader_screen/${bookid}"
    }

}
 */