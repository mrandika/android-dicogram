package space.mrandika.dicogram.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import space.mrandika.dicogram.repository.AuthRepository
import space.mrandika.dicogram.repository.SettingRepository
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val repo: SettingRepository
) : ViewModel() {

    fun getAccessToken(): Flow<String?> = authRepo.getAccessToken()

    fun getTheme() = repo.getTheme()

    suspend fun removeAccessToken() {
        authRepo.removeAccessToken()
    }

    suspend fun saveTheme(darkMode: Int) {
        repo.saveTheme(darkMode)

        AppCompatDelegate.setDefaultNightMode(darkMode)
    }
}