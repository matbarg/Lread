package com.example.lread.ui.screens.reader

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import com.example.lread.data.model.getSampleBooks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReaderViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderScreenState(book = getSampleBooks()[0], currentChapter = 0))
    val uiState: StateFlow<ReaderScreenState> = _uiState.asStateFlow()

    fun setFontSize(size: Int) {
        _uiState.update { it.copy(fontSize = size) }
    }

    fun goToNextChapter() {
        val currentChapterIndex = _uiState.value.currentChapter
        val lastChapterIndex = _uiState.value.book.chapters.size - 1

        if (currentChapterIndex < lastChapterIndex) {
            _uiState.update {
                it.copy(currentChapter = currentChapterIndex + 1)
            }
        } else {
            // end of book
        }
    }
}