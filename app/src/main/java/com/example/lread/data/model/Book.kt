package com.example.lread.data.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val cover: String,
    val chapters: List<String>,
    val isFavorite: Boolean = false // ➕ HINZUGEFÜGT
)


fun getSampleBooks(): List<Book> {
    return listOf(
        Book(
            id = "book1",
            title = "Later",
            author = "Stephen King",
            cover = "later/cover.jpeg",
            chapters = listOf(
                "later/part0011.xhtml",
                "later/part0012.xhtml",
                "later/part0013.xhtml",
                "later/part0014.xhtml",
                "later/part0015.xhtml"
            )
        ),
        Book(
            id = "book2",
            title = "Animal Farm",
            author = "George Orwell",
            cover = "animal-farm/bookcover-generated.jpg",
            chapters = listOf(
                "animal-farm/ch01.xhtml",
                "animal-farm/ch02.xhtml",
                "animal-farm/ch03.xhtml",
                "animal-farm/ch04.xhtml",
                "animal-farm/ch05.xhtml"
            )
        ),
        Book(
            id = "book3",
            title = "Die Verwandlung",
            author = "Franz Kafka",
            cover = "metamorphosis/epubbooks-cover.jpg",
            chapters = listOf(
                "metamorphosis/ch01.xhtml",
                "metamorphosis/ch02.xhtml",
                "metamorphosis/ch03.xhtml",
            )
        ),
        Book(
            id = "book4",
            title = "Spurlos verschwunden",
            author = "Paul Pilkington",
            cover = "spurlos/book-1-longgone_spurlos-verschwunden.jpg",
            chapters = listOf(
                "spurlos/part-001-chapter-001.xhtml",
                "spurlos/part-001-chapter-002.xhtml",
                "spurlos/part-001-chapter-003.xhtml",
                "spurlos/part-001-chapter-004.xhtml",
                "spurlos/part-001-chapter-005.xhtml"
            )
        )
    )
}