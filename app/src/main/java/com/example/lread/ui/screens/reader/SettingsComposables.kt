package com.example.lread.ui.screens.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lread.data.model.TextSetting
import com.example.lread.ui.theme.lreadBlue
import com.example.lread.ui.theme.lreadPurple
import kotlin.enums.EnumEntries

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
                    containerColor = if (i != currentChapter) lreadBlue else lreadPurple,
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
fun <T> TextSettingDropdown(
    modifier: Modifier = Modifier,
    buttonText: String,
    currentValueText: String,
    items: EnumEntries<T>,
    setOption: (T) -> Unit
) where T : Enum<T>, T : TextSetting {
    val expanded = remember { mutableStateOf(false) }

    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonColors(
            containerColor = lreadBlue,
            contentColor = Color.White,
            disabledContainerColor = Color.Green,
            disabledContentColor = Color.Yellow
        ),
        onClick = { expanded.value = !expanded.value }
    ) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(buttonText)
            Text(text = currentValueText, fontWeight = FontWeight.Bold)
        }
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