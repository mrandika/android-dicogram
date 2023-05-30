package space.mrandika.dicogram.utils

import space.mrandika.dicogram.data.model.local.StoryItemLocal
import space.mrandika.dicogram.data.model.remote.StoryItem

fun mapToLocal(remote: StoryItem): StoryItemLocal {
    return StoryItemLocal(
        id = remote.id,
        photoUrl = remote.photoUrl,
        createdAt = remote.createdAt,
        name = remote.name,
        description = remote.description,
        lon = remote.lon,
        lat = remote.lat
    )
}

fun convertToLocal(stories: List<StoryItem>): List<StoryItemLocal> {
    val localStories = mutableListOf<StoryItemLocal>()

    stories.forEach { story ->
        val storyLocal = mapToLocal(story)
        localStories.add(storyLocal)
    }

    return localStories
}