package com.example.lread.ui.screens.reader

data class ReaderScreenState(
    val currentChapter: String,
    val currentPage: String = "",
    val fontSize: Int = 100, // percent
    val lineSpacing: Int = 100 // percent
)
