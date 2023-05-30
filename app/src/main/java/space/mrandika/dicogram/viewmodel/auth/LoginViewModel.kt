package space.mrandika.dicogram.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import space.mrandika.dicogram.data.model.remote.LoginResponse
import space.mrandika.dicogram.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    // Form state
    val _isValidated = MutableLiveData<Boolean>()
    val isValidated: LiveData<Boolean> = _isValidated

    // Result
    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse> = _response

    suspend fun login(email: String, password: String) {
        _isLoading.value = true

        repo.login(email, password).collect { result ->
            _isLoading.value = false

            result.onSuccess { response ->
                _response.value = response

                response.user?.token?.let { token -> saveAccessToken(token) }
            }

            result.onFailure {
                _isError.value = true
            }
        }
    }

    private fun saveAccessToken(token: String) {
        viewModelScope.launch {
            repo.saveAccessToken(token)
        }
    }
}