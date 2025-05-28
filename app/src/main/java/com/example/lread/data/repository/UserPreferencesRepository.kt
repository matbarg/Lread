package com.example.lread.data.repository

import android.content.Context
import androidx.compose.foundation.MarqueeSpacing
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lread.data.model.TextFont
import com.example.lread.data.model.TextSize
import com.example.lread.data.model.TextSpacing
import com.example.lread.data.model.TextTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException
import javax.inject.Inject

data class UserPreferences(
    val textSize: TextSize,
    val textSpacing: TextSpacing,
    val textTheme: TextTheme,
    val textFont: TextFont
)

/* not needed after automatic datastore dependency injection
private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
*/

/**
 * Preferences data store only stores primitive values,
 * so the enums have to be represented as strings
 */
object PreferenceKeys {
    val TEXT_SIZE = stringPreferencesKey("text_size")
    val TEXT_SPACING = stringPreferencesKey("text_spacing")
    val TEXT_THEME = stringPreferencesKey("text_theme")
    val TEXT_FONT = stringPreferencesKey("text_font")
}

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                // the data returned hast to be mapped into a UserPreferences object and the keys into the corresponding enums
                UserPreferences(
                    textSize = preferences[PreferenceKeys.TEXT_SIZE]?.let { name ->
                        TextSize.valueOf(name)
                    } ?: TextSize.MEDIUM,
                    textSpacing = preferences[PreferenceKeys.TEXT_SPACING]?.let { name ->
                        TextSpacing.valueOf(name)
                    } ?: TextSpacing.MEDIUM,
                    textTheme = preferences[PreferenceKeys.TEXT_THEME]?.let { name ->
                        TextTheme.valueOf(name)
                    } ?: TextTheme.SOFT_BLACK,
                    textFont = preferences[PreferenceKeys.TEXT_FONT]?.let { name ->
                        TextFont.valueOf(name)
                    } ?: TextFont.LIBRE_BASKERVILLE
                )
            }
    }

    suspend fun updateTextSize(textSize: TextSize) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEXT_SIZE] = textSize.name
        }
    }

    suspend fun updateTextSpacing(textSpacing: TextSpacing) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEXT_SPACING] = textSpacing.name
        }
    }

    suspend fun updateTextTheme(textTheme: TextTheme) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEXT_THEME] = textTheme.name
        }
    }

    suspend fun updateTextFont(textFont: TextFont) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEXT_FONT] = textFont.name
        }
    }
}