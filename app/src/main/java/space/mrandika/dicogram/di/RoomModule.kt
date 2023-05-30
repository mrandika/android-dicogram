package space.mrandika.dicogram.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import space.mrandika.dicogram.database.RemoteKeysDao
import space.mrandika.dicogram.database.StoryDao
import space.mrandika.dicogram.database.StoryDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    fun provideStoryDao(database: StoryDatabase): StoryDao = database.storyDao()

    @Provides
    fun provideRemoteKeysDao(database: StoryDatabase): RemoteKeysDao =
        database.remoteKeysDao()

    @Provides
    @Singleton
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "dicogram_story"
        ).build()
    }
}