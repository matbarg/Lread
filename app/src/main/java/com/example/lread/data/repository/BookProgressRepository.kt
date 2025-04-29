package com.example.lread.data.repository

import com.example.lread.data.database.BookProgressDao
import com.example.lread.data.model.BookProgress
import javax.inject.Inject

class BookProgressRepository @Inject constructor(
    private val bookProgressDao: BookProgressDao
) {
    suspend fun insertBookProgress(bookProgress: BookProgress) = bookProgressDao.insertBookProgress(bookProgress)

    suspend fun getBookProgress(bookId: String) = bookProgressDao.getBookProgress(bookId)

    suspend fun updateBookProgress(bookProgress: BookProgress) = bookProgressDao.updateBookProgress(bookProgress)

    suspend fun deleteBookProgress(bookProgress: BookProgress) = bookProgressDao.deleteBookProgress(bookProgress)
}