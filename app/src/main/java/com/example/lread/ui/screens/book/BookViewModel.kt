package com.example.lread.ui.screens.book

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.lread.data.model.sampleBooks
import com.example.lread.data.repository.BookProgressRepository
import com.example.lread.ui.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookProgressRepository: BookProgressRepository
) : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(BookScreenState(progress = 0f, chapter = null))

    val uiState: StateFlow<BookScreenState> = _uiState.asStateFlow()

    init {
        val args = savedStateHandle.toRoute<NavRoute.BookScreen>()
        initializeState(args.bookId)
    }

    private fun initializeState(bookId: String) {
        // get the Book object from the map
        val book = sampleBooks[bookId]

        if (book != null) {
            _uiState.update {
                it.copy(book = book)
            }
        }

        // get the BookProgress from the db
        viewModelScope.launch {
            val bookProgress = bookProgressRepository.getBookProgress(bookId)

            if (bookProgress != null) {
                _uiState.update {
                    it.copy(progress = bookProgress.progress, chapter = bookProgress.currentChapter)
                }
            }
        }
    }
}