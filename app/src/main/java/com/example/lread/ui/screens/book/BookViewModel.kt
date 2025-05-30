package com.example.lread.ui.screens.book

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadBookProgress(_uiState.value.book.id)
    }

    private fun initializeState(bookId: String) {
        // get the Book object from the map
        val book = sampleBooks[bookId]

        if (book != null) {
            _uiState.update {
                it.copy(book = book)
            }
        }

        loadBookProgress(bookId)
    }

    private fun loadBookProgress(bookId: String) {
        viewModelScope.launch {
            val bookProgress = bookProgressRepository.getBookProgress(bookId)

            if (bookProgress != null) {
                _uiState.update {
                    it.copy(
                        progress = bookProgress.progress,
                        chapter = bookProgress.currentChapter,
                        bookIsFirstOpened = false,
                        bookIsFinished = bookProgress.progress == 1f
                    )
                }
            }
        }
    }

    fun deleteBookProgress() {
        viewModelScope.launch {
            bookProgressRepository.deleteBookProgress(uiState.value.book.id)
        }
    }
}