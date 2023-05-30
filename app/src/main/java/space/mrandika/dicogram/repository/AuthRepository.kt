package space.mrandika.dicogram.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.data.model.remote.LoginResponse
import space.mrandika.dicogram.prefs.TokenPreferences
import space.mrandika.dicogram.service.DicogramService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val service: DicogramService,
    private val pref: TokenPreferences
) {
    // Auth call
    suspend fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = service.login(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Result<GenericAPIResponse>> = flow {
        try {
            val response = service.register(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // Token-related
    suspend fun saveAccessToken(token: String) {
        pref.saveAccessToken(token)
    }

    suspend fun removeAccessToken() {
        pref.removeAccessToken()
    }

    fun getAccessToken(): Flow<String?> = pref.getAccessToken()
}