package com.example.lread.ui.screens.reader

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.navigation.toRoute
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookProgressRepository: BookProgressRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val application: Application // Inject Application context for assets
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState = MutableStateFlow(ReaderScreenState())
    val uiState: StateFlow<ReaderScreenState> = _uiState.asStateFlow()

    val loadingInProgress = mutableStateOf(true)
    private val bookIsFinished = mutableStateOf(false)

    private var mediaPlayer: MediaPlayer? = null // MediaPlayer instance, managed by ViewModel

    init {
        val args = savedStateHandle.toRoute<NavRoute.ReaderScreen>()
        initializeReaderState(args.bookId, args.chapter)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // Optionally pause music when the app goes to background
        // if (_uiState.value.isMusicEnabled) pauseMusic()
        if (!bookIsFinished.value) saveReadingProgress()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        if (!bookIsFinished.value) saveReadingProgress()
    }

    override fun onCleared() {
        super.onCleared()
        stopMusic() // Release MediaPlayer resources when ViewModel is cleared
    }

    private fun initializeReaderState(bookId: String, chapter: Int?) {
        val book = sampleBooks[bookId] ?: run {
            Log.e("ReaderViewModel", "Book with ID $bookId not found in sampleBooks.")
            loadingInProgress.value = false
            return
        }

        if (chapter == null) {
            viewModelScope.launch {
                val progress = bookProgressRepository.getBookProgress(book.id)
                _uiState.value = _uiState.value.copy(
                    book = book,
                    currentChapter = progress?.currentChapter ?: 0,
                    currentAnchorId = progress?.currentAnchorId ?: "para-0"
                )
                loadingInProgress.value = false
            }
        } else {
            _uiState.value = _uiState.value.copy(
                book = book,
                currentChapter = chapter,
                currentAnchorId = "para-0"
            )
            loadingInProgress.value = false
        }

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
        viewModelScope.launch { userPreferencesRepository.updateTextSize(textSize) }
    }

    fun setTextSpacing(textSpacing: TextSpacing) {
        _uiState.update { it.copy(textSpacing = textSpacing) }
        viewModelScope.launch { userPreferencesRepository.updateTextSpacing(textSpacing) }
    }

    fun setTextTheme(textTheme: TextTheme) {
        _uiState.update { it.copy(textTheme = textTheme) }
        viewModelScope.launch { userPreferencesRepository.updateTextTheme(textTheme) }
    }

    fun setTextFont(textFont: TextFont) {
        _uiState.update { it.copy(textFont = textFont) }
        viewModelScope.launch { userPreferencesRepository.updateTextFont(textFont) }
    }

    fun setCurrentChapter(chapter: Int) {
        _uiState.update {
            it.copy(
                currentChapter = chapter,
                currentAnchorId = "para-0",
                nextButtonVisible = false,
                settingsVisible = false
            )
        }
    }

    fun goToNextChapter() {
        val current = _uiState.value.currentChapter
        val last = _uiState.value.book.chapters.size - 1

        if (current < last) {
            _uiState.update {
                it.copy(
                    currentChapter = current + 1,
                    currentAnchorId = "para-0",
                    settingsVisible = false,
                    topBarVisible = true,
                    nextButtonVisible = false
                )
            }
        }
        saveReadingProgress()
    }

    fun setCurrentAnchorId(anchorId: String) {
        _uiState.update { it.copy(currentAnchorId = anchorId) }
    }

    private fun saveReadingProgress() {
        viewModelScope.launch {
            val book = _uiState.value.book
            val progressValue = if (book.chapters.isNotEmpty()) {
                _uiState.value.currentChapter / book.chapters.size.toFloat()
            } else 0f

            bookProgressRepository.insertBookProgress(
                BookProgress(
                    bookId = book.id,
                    currentChapter = _uiState.value.currentChapter,
                    currentAnchorId = _uiState.value.currentAnchorId,
                    progress = progressValue
                )
            )
        }
    }

    fun closeBook() {
        bookIsFinished.value = true
        viewModelScope.launch {
            val book = _uiState.value.book
            bookProgressRepository.insertBookProgress(
                BookProgress(
                    bookId = book.id,
                    currentChapter = book.chapters.size - 1,
                    currentAnchorId = "",
                    progress = 1f
                )
            )
        }
        stopMusic()
    }

    fun toggleSettings() {
        _uiState.update { it.copy(settingsVisible = !it.settingsVisible) }
    }

    fun setTopBarVisible(isVisible: Boolean) {
        _uiState.update { it.copy(topBarVisible = isVisible) }
    }

    fun setNextButtonVisible(isVisible: Boolean) {
        _uiState.update { it.copy(nextButtonVisible = isVisible) }
    }

    // --- Music related functions ---
    fun setMusicEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isMusicEnabled = enabled) }
        if (enabled) {
            // Only play if a track is selected, otherwise select default
            if (_uiState.value.selectedTrack.isBlank()) {
                setSelectedTrack("Lo-fi Vibes") // Or some other default
            } else {
                playSelectedTrack()
            }
        } else {
            pauseMusic() // Use pause for potential resume, stop for full reset
        }
    }

    fun setSelectedTrack(track: String) {
        _uiState.update { it.copy(selectedTrack = track) }
        if (_uiState.value.isMusicEnabled) {
            playSelectedTrack() // Automatically restart music if enabled
        }
    }

    private fun playSelectedTrack() {
        stopMusic() // Always stop before playing a new track or restarting the current one

        // IMPORTANT: Ensure these track names EXACTLY match what's in your UI dropdown!
        // Also, map them to the correct asset file paths.
        val fileName = when (_uiState.value.selectedTrack) {
            "Lo-fi Vibes" -> "music/dreamerx27s-study-lofi-271369.mp3"
            // Corrected mappings based on your UI list and typical asset naming conventions
            "Rainy Calm" -> "music/horror-thriller-2-336734.mp3" // Assuming this was for 'Thriller' music
            "Reading Flow" -> "music/reading-books-312690.mp3" // Assuming this was for 'Ambient' music
            else -> {
                Log.w("ReaderViewModel", "Unknown track selected: ${_uiState.value.selectedTrack}. Playing default.")
                "music/dreamerx27s-study-lofi-271369.mp3" // Fallback to a default if selection is unknown
            }
        }

        try {
            val afd = application.assets.openFd(fileName)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepareAsync() // Prepare asynchronously for smoother UI
                setOnPreparedListener { mp ->
                    mp.start()
                    mp.isLooping = true // Loop the music by default
                    _uiState.update { it.copy(isMusicEnabled = true) } // Update state to playing
                    Log.d("ReaderViewModel", "Started playing: $fileName")
                }
                setOnCompletionListener { mp ->
                    Log.d("ReaderViewModel", "Music track completed: $fileName. Looping: ${mp.isLooping}")
                    // If not looping and no next track, you might want to call stopMusic() here
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("ReaderViewModel", "MediaPlayer error: what=$what, extra=$extra for file: $fileName")
                    stopMusic() // Stop and release resources on error
                    _uiState.update { it.copy(isMusicEnabled = false) } // Update UI state
                    true // Indicate error was handled
                }
            }
            afd.close() // Close the AssetFileDescriptor
        } catch (e: Exception) {
            Log.e("ReaderViewModel", "Failed to play music: $fileName", e)
            _uiState.update { it.copy(isMusicEnabled = false) } // Update UI state on failure
            stopMusic() // Ensure resources are cleaned up
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        _uiState.update { it.copy(isMusicEnabled = false) }
        Log.d("ReaderViewModel", "Music paused.")
    }

    fun stopMusic() {
        mediaPlayer?.apply {
            if (isPlaying) stop() // Only stop if it's currently playing
            release() // Release the MediaPlayer resources
        }
        mediaPlayer = null // Clear the MediaPlayer instance
        _uiState.update { it.copy(isMusicEnabled = false) } // Update UI state
        Log.d("ReaderViewModel", "Music stopped and resources released.")
    }
}