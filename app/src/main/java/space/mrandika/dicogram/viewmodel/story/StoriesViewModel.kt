package space.mrandika.dicogram.viewmodel.story

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import space.mrandika.dicogram.data.model.local.StoryItemLocal
import space.mrandika.dicogram.data.model.remote.StoryResponse
import space.mrandika.dicogram.repository.AuthRepository
import space.mrandika.dicogram.repository.StoryRepository
import javax.inject.Inject

@HiltViewModel
class StoriesViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val repo: StoryRepository
) : ViewModel() {
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    private val _isGuest = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError
    val isGuest: LiveData<Boolean> = _isGuest

    // Empty state
    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    // Response
    private val _response = MutableLiveData<StoryResponse>()
    val response: LiveData<StoryResponse> = _response

    fun getStories(token: String): LiveData<PagingData<StoryItemLocal>> =
        repo.get(token).cachedIn(viewModelScope).asLiveData()

    suspend fun getStoriesWithLocation() {
        getAccessToken().collect { token ->
            if (!token.isNullOrEmpty()) {
                repo.getOnlyWithLocation(token).collect { result ->
                    result.onSuccess { response ->
                        _response.value = response
                    }

                    result.onFailure {
                        _isError.value = true
                    }
                }
            } else {
                _isError.value = true
            }
        }
    }

    fun getAccessToken(): Flow<String?> = authRepo.getAccessToken()

    fun toggleLoading(v: Boolean) {
        _isLoading.value = v
    }

    fun toggleError(v: Boolean) {
        _isError.value = v
    }

    fun toggleGuest(v: Boolean) {
        _isGuest.value = v
    }

    fun toggleEmpty(v: Boolean) {
        _isEmpty.value = v
    }
}