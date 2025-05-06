package com.example.lread.ui.screens.reader

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lread.data.model.Book
import com.example.lread.data.model.BookProgress
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.getSampleBooks
import com.example.lread.data.repository.BookProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val bookProgressRepository: BookProgressRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState =
        MutableStateFlow(ReaderScreenState())
    val uiState: StateFlow<ReaderScreenState> = _uiState.asStateFlow()

    val loadingInProgress = mutableStateOf(true)

    // progress is saved whenever the app stops (becomes invisible) or when going to the next chapter
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        saveReadingProgress()
    }

    fun initializeReaderState() {
        val book: Book =
            getSampleBooks()[0] // todo: viewmodel is passed the bookId to get the correct book

        viewModelScope.launch {
            val progress = bookProgressRepository.getBookProgress(book.id)

            _uiState.value = ReaderScreenState(
                book = book,
                currentChapter = progress?.currentChapter ?: 0, // default starting chapter
                currentAnchorId = progress?.currentAnchorId
                    ?: "" // default starting anchor (no scroll)
            )

            loadingInProgress.value = false
        }
    }

    fun setTextSize(textSize: TextSize) {
        _uiState.update { it.copy(textSize = textSize) }
    }

    fun setCurrentChapter(chapter: Int) {
        // todo: maybe include check if the parameter isn't greater than the total chapters
        _uiState.update { it.copy(currentChapter = chapter) }
    }

    fun goToNextChapter() {
        val currentChapterIndex = _uiState.value.currentChapter
        val lastChapterIndex = _uiState.value.book.chapters.size - 1

        if (currentChapterIndex < lastChapterIndex) {
            _uiState.update {
                it.copy(currentChapter = currentChapterIndex + 1, currentAnchorId = "")
            }
        } else {
            // end of book
        }

        saveReadingProgress()
    }

    fun setCurrentAnchorId(anchorId: String) {
        println("New anchor id: $anchorId")
        _uiState.update { it.copy(currentAnchorId = anchorId) }
    }

    private fun saveReadingProgress() {
        viewModelScope.launch {
            // insert is fine for simplicity, it either replaces the previous record or creates a new one
            bookProgressRepository.insertBookProgress(
                BookProgress(
                    bookId = _uiState.value.book.id,
                    currentChapter = _uiState.value.currentChapter,
                    currentAnchorId = _uiState.value.currentAnchorId,
                    progress = _uiState.value.currentChapter / _uiState.value.book.chapters.size.toFloat()
                )
            )
        }
    }

    fun toggleSettings() {
        _uiState.update { it.copy(settingsExpanded = !_uiState.value.settingsExpanded) }
    }
}