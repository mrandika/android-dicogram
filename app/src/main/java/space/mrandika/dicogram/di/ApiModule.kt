package space.mrandika.dicogram.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import space.mrandika.dicogram.config.ApiConfig
import space.mrandika.dicogram.service.DicogramService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideDicogramApi(): DicogramService = ApiConfig.getApiService()
}