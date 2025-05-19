package com.example.lread.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lread.data.model.getSampleBooks
import com.example.lread.ui.screens.book.BookScreen
import com.example.lread.ui.screens.library.LibraryScreen
import com.example.lread.ui.screens.reader.ReaderScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LibraryScreen.route
    ) {
        // Library Screen
        composable(Screen.LibraryScreen.route) {
            LibraryScreen(
                onBookClick = { book ->
                    //val encodedId = URLEncoder.encode(book.id, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.BookScreen.createRoute(book.id))
                }
            )
        }

        // Book Screen
        composable(
            route = Screen.BookScreen.route,
            arguments = listOf(
                navArgument("bookid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookid")

            val book = getSampleBooks().find { it.id == bookId }

            if (book != null) {
                BookScreen(
                    navController = navController,
                    book = book
                )
            }
        }

        // Reader Screen
        composable(
            route = Screen.ReaderScreen.route,
            arguments = listOf(
                navArgument("bookid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookid")
            val book = getSampleBooks().find { it.id == bookId }

            if (book != null) {
                ReaderScreen(
                    navController = navController,
                    book = book
                )
            }
        }
    }
}
