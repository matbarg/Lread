package com.example.lread.ui.screens.library

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class LibraryViewModel : ViewModel() {
    val favoriteIds = mutableStateListOf<String>()

    fun toggleFavorite(bookId: String) {
        if (favoriteIds.contains(bookId)) {
            favoriteIds.remove(bookId)
        } else {
            favoriteIds.add(bookId)
        }
    }

    fun isFavorite(bookId: String): Boolean {
        return favoriteIds.contains(bookId)
    }
}
