package space.mrandika.dicogram.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.data.model.remote.LoginResponse
import space.mrandika.dicogram.data.model.remote.StoryDetailResponse
import space.mrandika.dicogram.data.model.remote.StoryResponse

interface DicogramService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): GenericAPIResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lon") long: RequestBody?,
        @Part("lat") lat: RequestBody?
    ): GenericAPIResponse

    @Multipart
    @POST("stories/guest")
    suspend fun uploadStoriesAsGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lon") long: RequestBody?,
        @Part("lat") lat: RequestBody?
    ): GenericAPIResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getStory(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): StoryDetailResponse
}