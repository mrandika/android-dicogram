package space.mrandika.dicogram.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import space.mrandika.dicogram.data.model.local.StoryItemLocal
import space.mrandika.dicogram.databinding.ItemStoryBinding
import space.mrandika.dicogram.utils.loadImage
import space.mrandika.dicogram.utils.withDateFormat

class StoriesAdapter(private val context: Context) :
    PagingDataAdapter<StoryItemLocal, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = getItem(position)

        if (data != null) {
            viewHolder.bind(data)
        }
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItemLocal) {
            binding.apply {
                textUsername.text = story.name
                textUsernameCaption.text = story.name
                textCaption.text = story.description
                textDate.text = story.createdAt.withDateFormat()

                // Prevent resize after scrolling
                imageStory.layout(0, 0, 0, 0)

                imageStory.loadImage(context, story.photoUrl)

                itemView.setOnClickListener {
                    story.id.let { id ->
                        onItemClickCallback.onItemClicked(id)
                    }
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(id: String)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItemLocal>() {
            override fun areItemsTheSame(
                oldItem: StoryItemLocal,
                newItem: StoryItemLocal
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryItemLocal,
                newItem: StoryItemLocal
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}