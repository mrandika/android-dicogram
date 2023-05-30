package space.mrandika.dicogram.viewmodel.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import space.mrandika.dicogram.data.model.remote.StoryDetailResponse
import space.mrandika.dicogram.repository.AuthRepository
import space.mrandika.dicogram.repository.StoryRepository
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
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
    private val _response = MutableLiveData<StoryDetailResponse>()
    val response: LiveData<StoryDetailResponse> = _response

    suspend fun getStory(id: String) {
        _isLoading.value = true

        getAccessToken().collect { token ->
            if (token != null) {
                repo.getDetail(token, id).collect { result ->
                    _isLoading.value = false

                    result.onSuccess { response ->
                        _response.value = response
                    }

                    result.onFailure {
                        _isError.value = true
                    }
                }
            } else {
                _isLoading.value = false
                _isError.value = true
            }
        }
    }

    private fun getAccessToken(): Flow<String?> = authRepo.getAccessToken()
}