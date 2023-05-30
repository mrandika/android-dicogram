package space.mrandika.dicogram.viewmodel.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.repository.AuthRepository
import space.mrandika.dicogram.repository.StoryRepository
import javax.inject.Inject

@HiltViewModel
class CreateStoryViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val repo: StoryRepository
) : ViewModel() {
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    // Result
    private val _response = MutableLiveData<GenericAPIResponse>()
    val response: LiveData<GenericAPIResponse> = _response

    suspend fun addNew(file: MultipartBody.Part, description: String, long: String?, lat: String?) {
        _isLoading.value = true

        getAccessToken().collect { token ->
            if (!token.isNullOrEmpty()) {
                repo.upload(token, file, description, long, lat).collect { result ->
                    _isLoading.value = false

                    result.onSuccess { response ->
                        _response.value = response
                    }

                    result.onFailure {
                        _isError.value = true
                    }
                }
            } else {
                addNewAsGuest(file, description, long, lat)
            }
        }
    }

    private suspend fun addNewAsGuest(
        file: MultipartBody.Part,
        description: String,
        long: String?,
        lat: String?
    ) {
        _isLoading.value = true

        repo.uploadAsGuest(file, description, long, lat).collect { result ->
            _isLoading.value = false

            result.onSuccess { response ->
                _response.value = response
            }

            result.onFailure {
                _isError.value = true
            }
        }
    }

    private fun getAccessToken(): Flow<String?> = authRepo.getAccessToken()
}