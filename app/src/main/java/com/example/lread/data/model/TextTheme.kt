package com.example.lread.data.model

enum class TextTheme(val label: String, val textColor: String, val backgroundColor: String) {
    SOFT_WHITE("Soft White", "#373737", "#ffffff"),
    BASIC_WHITE("Basic White", "#000000", "#ffffff"),
    SOFT_BLACK("Soft Black", "#ffffff", "#282828"),
    BASIC_BLACK("Basic Black", "#ffffff", "#000000"),
    HIGH_CONTRAST("High Contrast", "#ffff00", "#000000"),
    PAPER("Paper", "#191919", "#f5eec4"),
    SEPIA("Sepia", "#5b4636", "#f4ecd8"),
    NIGHT_BLUE("Night Blue", "#d9ebfc", "#2b2d4f"),
    DAY_BLUE("Day Blue", "#2b2d4f", "#d9ebfc"),
    GREY("Grey", "#444444", "#dddddd")
}