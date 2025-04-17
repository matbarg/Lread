package com.example.lread.data.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val cover: String,
    val chapters: List<String>
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
        )
    )
}