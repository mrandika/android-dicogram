package space.mrandika.dicogram.database

import androidx.room.Database
import androidx.room.RoomDatabase
import space.mrandika.dicogram.data.model.local.RemoteKeys
import space.mrandika.dicogram.data.model.local.StoryItemLocal

@Database(
    entities = [StoryItemLocal::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}