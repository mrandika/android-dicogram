package space.mrandika.dicogram.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import space.mrandika.dicogram.data.StoryRemoteMediator
import space.mrandika.dicogram.data.model.local.StoryItemLocal
import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.data.model.remote.StoryDetailResponse
import space.mrandika.dicogram.data.model.remote.StoryResponse
import space.mrandika.dicogram.database.StoryDatabase
import space.mrandika.dicogram.service.DicogramService
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val database: StoryDatabase,
    private val service: DicogramService
) {
    suspend fun upload(
        token: String,
        file: MultipartBody.Part,
        description: String,
        long: String?,
        lat: String?
    ): Flow<Result<GenericAPIResponse>> = flow {
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val longBody = long?.toRequestBody("text/plain".toMediaType())
        val latBody = lat?.toRequestBody("text/plain".toMediaType())

        try {
            val response =
                service.uploadStories("Bearer $token", file, descriptionBody, longBody, latBody)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun uploadAsGuest(
        file: MultipartBody.Part,
        description: String,
        long: String?,
        lat: String?
    ): Flow<Result<GenericAPIResponse>> = flow {
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val longBody = long?.toRequestBody("text/plain".toMediaType())
        val latBody = lat?.toRequestBody("text/plain".toMediaType())

        try {
            val response = service.uploadStoriesAsGuest(file, descriptionBody, longBody, latBody)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    fun get(token: String): Flow<PagingData<StoryItemLocal>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(
                database,
                service,
                "Bearer $token"
            ),
            pagingSourceFactory = {
                database.storyDao().getAllStories()
            }
        ).flow
    }

    suspend fun getOnlyWithLocation(token: String): Flow<Result<StoryResponse>> = flow {
        try {
            val response = service.getStories("Bearer $token", 1, 30, 1)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun getDetail(token: String, id: String): Flow<Result<StoryDetailResponse>> = flow {
        try {
            val response = service.getStory("Bearer $token", id)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}