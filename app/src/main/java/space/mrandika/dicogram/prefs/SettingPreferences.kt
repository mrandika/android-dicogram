package space.mrandika.dicogram.prefs

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = intPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    suspend fun saveThemeSetting(darkMode: Int) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = darkMode
        }
    }
}