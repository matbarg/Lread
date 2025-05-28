package com.example.lread.ui.screens.reader

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.dataStore
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.lread.data.model.Book
import com.example.lread.data.model.BookProgress
import com.example.lread.data.model.TextFont
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.TextSpacing
import com.example.lread.data.model.TextTheme
import com.example.lread.data.model.sampleBooks
import com.example.lread.data.repository.BookProgressRepository
import com.example.lread.data.repository.UserPreferencesRepository
import com.example.lread.ui.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookProgressRepository: BookProgressRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState =
        MutableStateFlow(ReaderScreenState())

    val uiState: StateFlow<ReaderScreenState> = _uiState.asStateFlow()

    val loadingInProgress = mutableStateOf(true)

    init {
        val args = savedStateHandle.toRoute<NavRoute.ReaderScreen>()

        initializeReaderState(args.bookId, args.chapter)
    }

    // progress is saved whenever the app stops (becomes invisible) or when going to the next chapter (in goToNextChapter())
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        saveReadingProgress()
    }

    /**
     * Uses the arguments passed to the ReaderScreen to:
     * access the Book object from the sample books
     * either open the specified chapter or load the BookProgress from the db
     *
     * updates the screen state correspondingly
     */
    private fun initializeReaderState(bookId: String, chapter: Int?) {
        val book = sampleBooks[bookId] ?: return

        if (chapter == null) { // no chapter was passed => restore book progress or start from the beginning
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
        } else { // chapter was passed => initialize state with it
            _uiState.value = ReaderScreenState(
                book = book,
                currentChapter = chapter,
                currentAnchorId = "" // no scroll
            )

            loadingInProgress.value = false
        }

        // restore the text settings
        viewModelScope.launch {
            userPreferencesRepository.getUserPreferencesFlow().collect { preferences ->
                _uiState.update {
                    it.copy(
                        textSize = preferences.textSize,
                        textSpacing = preferences.textSpacing,
                        textTheme = preferences.textTheme,
                        textFont = preferences.textFont
                    )
                }
            }
        }
    }

    fun setTextSize(textSize: TextSize) {
        _uiState.update { it.copy(textSize = textSize) }

        viewModelScope.launch {
            userPreferencesRepository.updateTextSize(textSize)
        }
    }

    fun setTextSpacing(textSpacing: TextSpacing) {
        _uiState.update { it.copy(textSpacing = textSpacing) }

        viewModelScope.launch {
            userPreferencesRepository.updateTextSpacing(textSpacing)
        }
    }

    fun setTextTheme(textTheme: TextTheme) {
        _uiState.update { it.copy(textTheme = textTheme) }

        viewModelScope.launch {
            userPreferencesRepository.updateTextTheme(textTheme)
        }
    }

    fun setTextFont(textFont: TextFont) {
        _uiState.update { it.copy(textFont = textFont) }

        viewModelScope.launch {
            userPreferencesRepository.updateTextFont(textFont)
        }
    }

    fun setCurrentChapter(chapter: Int) {
        _uiState.update {
            it.copy(
                currentChapter = chapter,
                nextButtonVisible = false,
                settingsVisible = false
            )
        }
    }

    fun goToNextChapter() {
        val currentChapterIndex = _uiState.value.currentChapter
        val lastChapterIndex = _uiState.value.book.chapters.size - 1

        if (currentChapterIndex < lastChapterIndex) {
            _uiState.update {
                it.copy(
                    currentChapter = currentChapterIndex + 1,
                    currentAnchorId = "",
                    settingsVisible = false,
                    topBarVisible = true,
                    nextButtonVisible = false
                )
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
        Log.d("", "Settings where toggled")
        _uiState.update { it.copy(settingsVisible = !_uiState.value.settingsVisible) }
    }

    fun setTopBarVisible(isVisible: Boolean) {
        _uiState.update { it.copy(topBarVisible = isVisible) }
    }

    fun setNextButtonVisible(isVisible: Boolean) {
        _uiState.update { it.copy(nextButtonVisible = isVisible) }
    }
}