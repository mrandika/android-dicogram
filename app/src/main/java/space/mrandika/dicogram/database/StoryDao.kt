package space.mrandika.dicogram.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.mrandika.dicogram.data.model.local.StoryItemLocal

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryItemLocal>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, StoryItemLocal>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}