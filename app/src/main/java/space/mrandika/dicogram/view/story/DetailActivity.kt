package space.mrandika.dicogram.view.story

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.mrandika.dicogram.R
import space.mrandika.dicogram.data.model.remote.StoryDetailResponse
import space.mrandika.dicogram.databinding.ActivityDetailBinding
import space.mrandika.dicogram.utils.isConnected
import space.mrandika.dicogram.utils.loadImage
import space.mrandika.dicogram.utils.withDateFormat
import space.mrandika.dicogram.viewmodel.story.StoryViewModel

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivityDetailBinding

    private val viewModel: StoryViewModel by viewModels()

    private var STORY_ID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.detailErrorState.errorGeneric.visibility = View.GONE
        binding.detailLoadingState.pbLoading.visibility = View.GONE
        STORY_ID = intent.getStringExtra("story-id")

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this) {
            showError(it)
        }

        viewModel.response.observe(this) { response ->
            setResponse(response)
        }

        getData()

        binding.detailErrorState.btnErrorRecovery.setOnClickListener {
            getData()
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            STORY_ID?.let { viewModel.getStory(it) }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            storyDetail.storyItem.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE

            detailLoadingState.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            detailErrorState.errorGeneric.visibility = View.GONE
        }
    }

    private fun showError(isError: Boolean) {
        binding.apply {
            storyDetail.storyItem.visibility = if (isError) View.INVISIBLE else View.VISIBLE

            detailErrorState.errorGeneric.visibility = if (isError) View.VISIBLE else View.GONE
            detailLoadingState.pbLoading.visibility = View.GONE

            if (!isConnected(this@DetailActivity)) {
                binding.detailErrorState.errorGenericTitle.text =
                    resources.getString(R.string.connection_failure_title)
                binding.detailErrorState.errorGenericSubtitle.text =
                    resources.getString(R.string.connection_failure_subtitle)
            }
        }
    }

    private fun setResponse(response: StoryDetailResponse) {
        binding.apply {
            with(response.story) {
                storyDetail.textUsername.text = this?.name
                storyDetail.textUsernameCaption.text = this?.name
                storyDetail.textCaption.text = this?.description
                storyDetail.textDate.text = this?.createdAt?.withDateFormat()
            }

            storyDetail.imageStory.loadImage(this@DetailActivity, response.story?.photoUrl)
        }
    }
}