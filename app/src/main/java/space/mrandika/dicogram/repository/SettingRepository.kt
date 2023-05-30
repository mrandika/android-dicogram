package space.mrandika.dicogram.repository

import kotlinx.coroutines.flow.Flow
import space.mrandika.dicogram.prefs.SettingPreferences
import javax.inject.Inject

class SettingRepository @Inject constructor(private val pref: SettingPreferences) {
    suspend fun saveTheme(darkMode: Int) {
        pref.saveThemeSetting(darkMode)
    }

    fun getTheme(): Flow<Int?> = pref.getThemeSetting()
}