package com.example.lread.ui.screens.book

import com.example.lread.data.model.Book

val emptyBook = Book(
    id = "",
    title = "",
    author = "",
    cover = "",
    chapters = emptyList()
)

data class BookScreenState(
    val book: Book = emptyBook,
    val bookIsFirstOpened: Boolean = true,
    val bookIsFinished: Boolean = false,
    val progress: Float,
    val chapter: Int?
)
