package space.mrandika.dicogram.view.maps

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.mrandika.dicogram.R
import space.mrandika.dicogram.data.model.remote.StoryResponse
import space.mrandika.dicogram.databinding.FragmentMapsBinding
import space.mrandika.dicogram.utils.isConnected
import space.mrandika.dicogram.viewmodel.story.StoriesViewModel

@AndroidEntryPoint
class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    private val boundsBuilder = LatLngBounds.Builder()

    private val viewModel: StoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: MaterialToolbar? = activity?.findViewById(R.id.topAppBar)
        toolbar?.title = resources.getString(R.string.maps)

        if (isConnected(requireActivity())) {
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(callback)
        } else {
            Toast.makeText(
                activity,
                resources.getString(R.string.connection_failure_title),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        // Set UI
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Set map style
        setMapStyle()

        viewModel.response.observe(viewLifecycleOwner) { response ->
            addStoriesMarker(response)
        }

        mMap.setOnMapLoadedCallback {
            // Get data on map load
            getData()
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            viewModel.getStoriesWithLocation()
        }
    }

    private fun setMapStyle() {
        try {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireActivity(),
                            R.raw.map_style_night
                        )
                    )
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireActivity(),
                            R.raw.map_style
                        )
                    )
                }
            }
        } catch (exception: Resources.NotFoundException) {
            exception.printStackTrace()
        }
    }

    private fun addStoriesMarker(response: StoryResponse) {
        val stories = response.listStory

        mMap.clear()

        stories.forEach { story ->
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(story.name).snippet(story.description)
                )

                boundsBuilder.include(latLng)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }
}