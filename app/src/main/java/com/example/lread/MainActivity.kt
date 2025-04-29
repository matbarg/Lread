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
import com.example.lread.data.model.getSampleBooks
import com.example.lread.ui.screens.book.BookScreen
import com.example.lread.ui.screens.library.LibraryScreen
import com.example.lread.ui.theme.LReadTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
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
                                    val encodedId = URLEncoder.encode(book.id, StandardCharsets.UTF_8.toString())
                                    navController.navigate("book/$encodedId")
                                }
                            )
                        }

                        composable(
                            route = "book/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val encodedId = backStackEntry.arguments?.getString("id") ?: ""
                            val id = URLDecoder.decode(encodedId, StandardCharsets.UTF_8.toString())

                            val book = getSampleBooks().find { it.id == id }

                            if (book != null) {
                                BookScreen(
                                    navController = navController,
                                    book = book
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
