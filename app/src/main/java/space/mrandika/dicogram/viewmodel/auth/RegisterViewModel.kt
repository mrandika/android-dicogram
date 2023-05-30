package space.mrandika.dicogram.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
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
    private val _response = MutableLiveData<GenericAPIResponse>()
    val response: LiveData<GenericAPIResponse> = _response

    suspend fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        repo.register(name, email, password).collect { result ->
            _isLoading.value = false

            result.onSuccess { response ->
                _response.value = response
            }

            result.onFailure {
                _isError.value = true
            }
        }
    }
}