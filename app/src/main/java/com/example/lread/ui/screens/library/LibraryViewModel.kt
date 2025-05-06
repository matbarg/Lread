package com.example.lread.ui.screens.library

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class LibraryViewModel : ViewModel() {

    // Internal mutable list
    private val _favoriteIds = mutableStateListOf<String>()

    // Public read-only list
    val favoriteIds: List<String> get() = _favoriteIds

    fun toggleFavorite(bookId: String) {
        if (_favoriteIds.contains(bookId)) {
            _favoriteIds.remove(bookId)
        } else {
            _favoriteIds.add(bookId)
        }
    }

    fun isFavorite(bookId: String): Boolean = _favoriteIds.contains(bookId)
}
