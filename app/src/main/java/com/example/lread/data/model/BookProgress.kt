package com.example.lread.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_progress_table")
data class BookProgress(
    @PrimaryKey @ColumnInfo(name = "book_id")
    val bookId: String,

    @ColumnInfo(name = "current_chapter")
    val currentChapter: Int,

    @ColumnInfo(name = "current_anchor_id")
    val currentAnchorId: String,

    @ColumnInfo(name = "progress")
    val progress: Float
)