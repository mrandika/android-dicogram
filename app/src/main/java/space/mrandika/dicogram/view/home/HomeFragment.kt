package space.mrandika.dicogram.view.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.mrandika.dicogram.R
import space.mrandika.dicogram.data.model.local.StoryItemLocal
import space.mrandika.dicogram.databinding.FragmentHomeBinding
import space.mrandika.dicogram.utils.isConnected
import space.mrandika.dicogram.utils.rotateFile
import space.mrandika.dicogram.utils.uriToFile
import space.mrandika.dicogram.view.adapter.LoadingStateAdapter
import space.mrandika.dicogram.view.adapter.StoriesAdapter
import space.mrandika.dicogram.view.story.CameraActivity
import space.mrandika.dicogram.view.story.DetailActivity
import space.mrandika.dicogram.view.story.UploadActivity
import space.mrandika.dicogram.viewmodel.story.StoriesViewModel
import java.io.File

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: StoriesAdapter

    private val viewModel: StoriesViewModel by activityViewModels()

    private var isAllFabVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: MaterialToolbar? = activity?.findViewById(R.id.topAppBar)
        toolbar?.title = resources.getString(R.string.home)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.isGuest.observe(viewLifecycleOwner) {
            showGuestNotice(it)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) {
            showEmptyNotice(it)
        }

        initRecyclerView()

        if (isConnected(requireActivity())) {
            initFabListener()
        } else {
            binding?.fabNew?.isEnabled = false
        }

        setSecondaryFabVisibility(View.GONE)
        getData()

        binding?.apply {
            rootGuestState.errorGuest.visibility = View.GONE

            // Set error recovery
            rootErrorState.btnErrorRecovery.setOnClickListener {
                getData()
            }
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            viewModel.getAccessToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    viewModel.getStories(token).observe(viewLifecycleOwner) { stories ->
                        setStoriesData(stories)
                    }
                } else {
                    // Remove bottom navigation view
                    val bottomNavView: BottomNavigationView? =
                        activity?.findViewById(R.id.bottom_navigationBar)
                    bottomNavView?.visibility = View.GONE

                    viewModel.toggleGuest(true)
                }
            }
        }
    }

    private fun initFabListener() {
        binding?.apply {
            fabCamera.setOnClickListener {
                setSecondaryFabVisibility(View.GONE)
                isAllFabVisible = false
                startCameraX()
            }

            fabGallery.setOnClickListener {
                setSecondaryFabVisibility(View.GONE)
                isAllFabVisible = false
                startGallery()
            }

            fabNew.setOnClickListener {
                isAllFabVisible = when (isAllFabVisible) {
                    false -> {
                        fabCamera.show()
                        fabGallery.show()
                        setSecondaryFabVisibility(View.VISIBLE)
                        true
                    }

                    true -> {
                        fabCamera.hide()
                        fabGallery.hide()
                        setSecondaryFabVisibility(View.GONE)
                        false
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setSecondaryFabVisibility(v: Int) {
        binding?.apply {
            fabCamera.visibility = v
            fabGallery.visibility = v
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            rvStories.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            rootLoadingState.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
            fabNew.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showError(isError: Boolean) {
        binding?.apply {
            rvStories.visibility = if (isError) View.INVISIBLE else View.VISIBLE
            rootErrorState.errorGeneric.visibility = if (isError) View.VISIBLE else View.GONE
            fabNew.visibility = if (isError) View.GONE else View.VISIBLE
        }
    }

    private fun showGuestNotice(isGuest: Boolean) {
        binding?.apply {
            rvStories.visibility = if (isGuest) View.INVISIBLE else View.VISIBLE
            rootGuestState.errorGuest.visibility = if (isGuest) View.VISIBLE else View.GONE
        }
    }

    private fun showEmptyNotice(isEmpty: Boolean) {
        binding?.apply {
            rvStories.visibility = if (isEmpty) View.INVISIBLE else View.VISIBLE
            rootEmptyState.errorEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun initAdapter() {
        adapter = StoriesAdapter(requireActivity())

        adapter.addLoadStateListener { state ->
            viewModel.toggleLoading(adapter.itemCount == 0 && state.source.refresh is LoadState.Loading)
            viewModel.toggleError(state.source.refresh is LoadState.NotLoading && state.source.refresh is LoadState.Error)
            viewModel.toggleEmpty(adapter.itemCount == 0 && state.source.refresh is LoadState.NotLoading)
        }

        binding?.rvStories?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(id: String) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra("story-id", id)

                startActivity(intent)
            }
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)

        binding?.apply {
            rvStories.layoutManager = layoutManager
            rlStories.setOnRefreshListener {
                rlStories.isRefreshing = false

                // Get new data
                getData()
            }

            rvStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fabNew.isExtended) {
                        fabNew.shrink()
                    } else if (dy < 0 && !fabNew.isExtended) {
                        fabNew.extend()
                    }
                }
            })
        }

        initAdapter()
    }

    private fun setStoriesData(stories: PagingData<StoryItemLocal>) {
        val state = binding?.rvStories?.layoutManager?.onSaveInstanceState()
        adapter.submitData(lifecycle, stories)
        binding?.rvStories?.layoutManager?.onRestoreInstanceState(state)
    }

    private fun startCameraX() {
        val intent = Intent(activity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.gallery_pick))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)

                val intent = Intent(activity, UploadActivity::class.java)
                intent.putExtra("picture", myFile)
                intent.putExtra("isBackCamera", isBackCamera)

                startActivity(intent)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireActivity())

                // Rotate the image as reviewer feedback
                rotateFile(myFile)

                val intent = Intent(activity, UploadActivity::class.java)
                intent.putExtra("picture", myFile)

                startActivity(intent)
            }
        }
    }
}