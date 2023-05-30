package space.mrandika.dicogram.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import space.mrandika.dicogram.R
import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.databinding.ActivityUploadBinding
import space.mrandika.dicogram.utils.loadBitmap
import space.mrandika.dicogram.utils.reduceFileImage
import space.mrandika.dicogram.utils.rotateFile
import space.mrandika.dicogram.view.RootActivity
import space.mrandika.dicogram.viewmodel.story.CreateStoryViewModel
import java.io.File

@AndroidEntryPoint
class UploadActivity : AppCompatActivity(), LocationListener {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivityUploadBinding
    private lateinit var locationManager: LocationManager

    private val locationPermissionCode = 2
    private var PICTURE: File? = null
    private var IS_BACK_CAMERA: Boolean = false
    private var long: String? = null
    private var lat: String? = null

    private val viewModel: CreateStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this) {
            showError()
        }

        viewModel.response.observe(this) { response ->
            setResponse(response)
        }

        binding.btnPost.setOnClickListener {
            postStory()
        }

        binding.uploadLoading.pbLoading.visibility = View.GONE
        binding.btnPost.isEnabled = false

        binding.etCaption.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.btnPost.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })

        binding.checkboxLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
            } else {
                long = null
                lat = null
            }
        }

        getFile()
    }

    override fun onLocationChanged(location: Location) {
        long = location.longitude.toString()
        lat = location.latitude.toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty()) {
            return
        }

        if (requestCode == locationPermissionCode) {
            // grantResults with index 0: FINE_LOCATION
            // grantResults with index 1: COARSE_LOCATION
            if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                binding.checkboxLocation.isChecked = false
                Toast.makeText(
                    this,
                    resources.getString(R.string.location_permission_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                getLocation()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnPost.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            uploadLoading.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showError() {
        Toast.makeText(this, resources.getString(R.string.post_upload_failed), Toast.LENGTH_LONG)
            .show()
    }

    private fun setResponse(response: GenericAPIResponse) {
        if (response.error == true) {
            Toast.makeText(
                this,
                resources.getString(R.string.post_upload_failed),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.post_upload_success),
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, RootActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }
    }

    private fun getFile() {
        PICTURE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("picture", File::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("picture")
        } as? File

        IS_BACK_CAMERA = intent.getBooleanExtra("isBackCamera", true)

        PICTURE?.let { file ->
            rotateFile(file, IS_BACK_CAMERA)

            binding.imgPreview.loadBitmap(this, file.path)
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions: MutableList<String> = ArrayList()

            val fineLocation =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarseLocation =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (fineLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                locationPermissionCode
            )

            return
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this)
    }

    private fun postStory() {
        if (PICTURE != null) {
            val includeLocation = binding.checkboxLocation.isChecked
            val file = reduceFileImage(PICTURE as File)

            val description = binding.etCaption.text.toString()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

            lifecycleScope.launch {
                if (includeLocation) {
                    viewModel.addNew(body, description, long, lat)
                } else {
                    viewModel.addNew(body, description, null, null)
                }
            }
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.upload_image_empty_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}