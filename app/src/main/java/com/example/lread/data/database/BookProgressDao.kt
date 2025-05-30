package com.example.lread.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lread.data.model.BookProgress

@Dao
interface BookProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookProgress(bookProgress: BookProgress)

    @Query("SELECT * FROM book_progress_table WHERE book_id = :bookId")
    suspend fun getBookProgress(bookId: String): BookProgress?

    @Update
    suspend fun updateBookProgress(bookProgress: BookProgress)

    @Query("DELETE FROM book_progress_table WHERE book_id = :bookId")
    suspend fun deleteBookProgress(bookId: String)
}