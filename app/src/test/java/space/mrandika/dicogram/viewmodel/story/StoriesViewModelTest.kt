package space.mrandika.dicogram.viewmodel.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import space.mrandika.dicogram.repository.AuthRepository
import space.mrandika.dicogram.repository.StoryRepository
import space.mrandika.dicogram.utils.MainDispatcherRule
import space.mrandika.dicogram.utils.PagedTestSource
import space.mrandika.dicogram.utils.convertToLocal
import space.mrandika.dicogram.utils.dummy.StoryDummy
import space.mrandika.dicogram.utils.getOrAwaitValue
import space.mrandika.dicogram.view.adapter.StoriesAdapter

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
internal class StoriesViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryRepository

    @Mock
    private lateinit var authRepo: AuthRepository

    private val tokenValue = "Bearer AUTHENTICATION_TOKEN"

    @Test
    fun `Stories success and return list of items`(): Unit = runTest {
        val data = convertToLocal(StoryDummy.generateDummyStoriesResponse().listStory)
        val stories = PagedTestSource.snapshot(data)

        `when`(repository.get(tokenValue)).thenReturn(flowOf(stories))

        val viewModel = StoriesViewModel(authRepo, repository)

        val actualStories = viewModel.getStories(tokenValue).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        // Case 1: Verify not null
        Assert.assertNotNull(differ.snapshot())

        // Case 2: Size equals
        Assert.assertEquals(data.size, differ.snapshot().size)

        // Case 3: First data equals
        Assert.assertEquals(data.first(), differ.snapshot().first())
    }

    @Test
    fun `Stories success and return zero item`(): Unit = runTest {
        val data = convertToLocal(StoryDummy.generateDummyEmptyStoriesResponse().listStory)
        val stories = PagedTestSource.snapshot(data)

        val viewModel = StoriesViewModel(authRepo, repository)

        `when`(repository.get(tokenValue)).thenReturn(flowOf(stories))

        val actualStories = viewModel.getStories(tokenValue).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        // Case 4: Size equals to 0
        Assert.assertEquals(data.size, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}