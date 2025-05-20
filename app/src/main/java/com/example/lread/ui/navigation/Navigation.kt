package com.example.lread.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lread.data.model.sampleBooks
import com.example.lread.ui.screens.book.BookScreen
import com.example.lread.ui.screens.library.LibraryScreen
import com.example.lread.ui.screens.reader.ReaderScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.LibraryScreen
    ) {
        // Library Screen
        composable<NavRoute.LibraryScreen> {
            LibraryScreen(
                onBookClick = { book ->
                    //val encodedId = URLEncoder.encode(book.id, StandardCharsets.UTF_8.toString())
                    navController.navigate(NavRoute.BookScreen(bookId = book.id))
                }
            )
        }

        // Book Screen
        composable<NavRoute.BookScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.BookScreen>()

            val book = sampleBooks[args.bookId]

            if (book != null) {
                BookScreen(
                    navController = navController,
                    book = book
                )
            }
        }

        // Reader Screen
        composable<NavRoute.ReaderScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.ReaderScreen>()

            ReaderScreen(
                navController = navController,
                bookId = args.bookId
            )
        }
    }
}
