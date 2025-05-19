package com.example.lread.ui.navigation

sealed class Screen (val route: String) {
    object LibraryScreen: Screen(route = "library_screen")
    object BookScreen: Screen(route = "book_screen/{bookid}") {
        fun createRoute(bookid: String) = "book_screen/${bookid}"
    }
    object ReaderScreen: Screen(route ="reader_screen/{bookid}") {
        fun createRoute(bookid: String) = "reader_screen/${bookid}"
    }

}