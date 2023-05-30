package space.mrandika.dicogram.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import space.mrandika.dicogram.data.model.local.StoryItemLocal

class PagedTestSource : PagingSource<Int, LiveData<List<StoryItemLocal>>>() {

    companion object {
        fun snapshot(items: List<StoryItemLocal>): PagingData<StoryItemLocal> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItemLocal>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItemLocal>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}