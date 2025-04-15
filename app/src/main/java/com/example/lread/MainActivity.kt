package com.example.lread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lread.ui.screens.book.BookScreen
import com.example.lread.ui.screens.library.LibraryScreen
import com.example.lread.ui.theme.LReadTheme
import com.example.lread.R
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LReadTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "library",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("library") {
                            LibraryScreen(
                                onBookClick = { book ->
                                    val encodedTitle = URLEncoder.encode(book.title, StandardCharsets.UTF_8.toString())
                                    val encodedAuthor = URLEncoder.encode(book.author, StandardCharsets.UTF_8.toString())
                                    navController.navigate("book/$encodedTitle/$encodedAuthor")
                                }
                            )
                        }

                        composable(
                            route = "book/{title}/{author}",
                            arguments = listOf(
                                navArgument("title") { type = NavType.StringType },
                                navArgument("author") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val encodedTitle = backStackEntry.arguments?.getString("title") ?: ""
                            val encodedAuthor = backStackEntry.arguments?.getString("author") ?: ""
                            val title = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
                            val author = URLDecoder.decode(encodedAuthor, StandardCharsets.UTF_8.toString())

                            // 🖼 For now, pass a default cover or map dynamically if needed
                            BookScreen(
                                navController = navController,
                                title = title,
                                author = author,
                                coverResId = R.drawable.later_by_stephen_king
                            )
                        }
                    }
                }
            }
        }
    }
}
