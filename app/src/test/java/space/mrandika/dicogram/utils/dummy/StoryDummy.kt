package space.mrandika.dicogram.utils.dummy

import space.mrandika.dicogram.data.model.remote.StoryItem
import space.mrandika.dicogram.data.model.remote.StoryResponse

object StoryDummy {

    fun generateDummyEmptyStoriesResponse(location: Double? = null): StoryResponse {
        return StoryResponse(
            error = false,
            message = "success",
            listStory = mutableListOf()
        )
    }

    fun generateDummyStoriesResponse(location: Double? = null): StoryResponse {
        return StoryResponse(
            error = false,
            message = "success",
            listStory = generateDummyStories(location)
        )
    }

    private fun generateDummyStories(location: Double? = null): List<StoryItem> {
        val stories = mutableListOf<StoryItem>()

        for (i in 1 until 5) {
            stories.add(generateDummyStory(i.toString(), location))
        }

        return stories
    }

    private fun generateDummyStory(id: String, location: Double? = null): StoryItem {
        return StoryItem(
            id = id,
            name = "Username",
            description = "Description",
            photoUrl = "https://image.com",
            createdAt = "2002-06-22",
            lon = location,
            lat = location
        )
    }
}