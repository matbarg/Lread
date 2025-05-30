package com.example.lread.ui.screens.library

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lread.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Internal mutable list
    private val _favoriteIds = mutableStateListOf<String>()

    // Public read-only list
    val favoriteIds: List<String> get() = _favoriteIds

    init {
        loadFavoriteBooksList()
    }

    fun toggleFavorite(bookId: String) {
        if (_favoriteIds.contains(bookId)) {
            _favoriteIds.remove(bookId)
        } else {
            _favoriteIds.add(bookId)
        }

        saveFavoriteBooksList()
    }

    fun isFavorite(bookId: String): Boolean = _favoriteIds.contains(bookId)

    private fun loadFavoriteBooksList() {
        viewModelScope.launch {
            userPreferencesRepository.getUserPreferencesFlow().collect { preferences ->
                _favoriteIds.clear()
                _favoriteIds.addAll(preferences.favBooks)
            }
        }
    }

    private fun saveFavoriteBooksList() {
        viewModelScope.launch {
            userPreferencesRepository.updateFavBooks(_favoriteIds)
        }
    }
}
