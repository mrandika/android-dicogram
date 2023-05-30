package space.mrandika.dicogram.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import space.mrandika.dicogram.prefs.SettingPreferences
import space.mrandika.dicogram.prefs.TokenPreferences
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dicogram")

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun providesSettingPreferences(dataStore: DataStore<Preferences>): SettingPreferences =
        SettingPreferences(dataStore)

    @Provides
    @Singleton
    fun provideTokenPreferences(dataStore: DataStore<Preferences>): TokenPreferences =
        TokenPreferences(dataStore)
}