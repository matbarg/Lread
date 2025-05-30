package com.example.lread.data.model

interface TextSetting {
    val label: String
}

// Size parameter represents the font size in pixel inside the webview
enum class TextSize(override val label: String, val size: Int) : TextSetting {
    SMALL("Small", 15),
    MEDIUM("Medium", 18),
    LARGE("Large", 21)
}

enum class TextSpacing(override val label: String, val size: Float) : TextSetting {
    SMALL("Small", 1.2f),
    MEDIUM("Medium", 1.5f),
    LARGE("Large", 1.8f)
}

enum class TextTheme(
    override val label: String,
    val textColor: String,
    val backgroundColor: String
) : TextSetting {
    SOFT_WHITE("Soft White", "#373737", "#ffffff"),
    BASIC_WHITE("Basic White", "#000000", "#ffffff"),
    SOFT_BLACK("Soft Black", "#ffffff", "#282828"),
    BASIC_BLACK("Basic Black", "#ffffff", "#000000"),
    HIGH_CONTRAST("High Contrast", "#ffff00", "#000000"),
    PAPER("Paper", "#191919", "#faecda"),
    NIGHT_BLUE("Night Blue", "#d9ebfc", "#2b2d4f"),
    DAY_BLUE("Day Blue", "#2b2d4f", "#d9ebfc"),
    GREY("Grey", "#444444", "#dddddd")
}

enum class TextFont(override val label: String, val value: String) : TextSetting {
    LIBRE_BASKERVILLE("Libre Baskerville", "LibreBaskerville"),
    LATO("Lato", "Lato"),
    SANCHEZ("Sanchez", "Sanchez")
}