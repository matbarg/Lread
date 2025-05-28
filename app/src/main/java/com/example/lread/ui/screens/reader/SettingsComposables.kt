package com.example.lread.ui.screens.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lread.data.model.TextFont
import com.example.lread.data.model.TextSetting
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.TextSpacing
import com.example.lread.data.model.TextTheme
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

@Composable
fun ChapterButtonRow(
    modifier: Modifier = Modifier,
    currentChapter: Int,
    totalChapters: Int,
    setChapter: (Int) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(totalChapters) { i ->
            Button(
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 5.dp, horizontal = 12.dp),
                colors = ButtonColors(
                    containerColor = if (i != currentChapter) Color.Blue else Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Green,
                    disabledContentColor = Color.Yellow
                ),
                onClick = { setChapter(i) }) {
                Text("Chapter ${i + 1}")
            }
        }
    }
}

/**
 * One generic function for all TextSettings (for that to work they need implement the interface TextSetting)
 */

@Composable
fun <T> TextSettingDropdown(modifier: Modifier = Modifier, label: String, items : EnumEntries<T>, setOption: (T) -> Unit) where T : Enum<T>, T : TextSetting {
    val expanded = remember { mutableStateOf(false) }

    Button(onClick = { expanded.value = !expanded.value }) {
        Text("Text font: $label")
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {

        items.forEach {
            DropdownMenuItem(
                text = { Text(it.label) },
                onClick = {
                    setOption(it)
                    expanded.value = false
                }
            )
        }
    }
}