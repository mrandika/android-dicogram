package space.mrandika.dicogram.view.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.mrandika.dicogram.R
import space.mrandika.dicogram.databinding.ActivitySettingBinding
import space.mrandika.dicogram.view.MainActivity
import space.mrandika.dicogram.viewmodel.SettingViewModel

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivitySettingBinding

    private var themeSelection: String? = ""

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.content.themeSetting.setOnClickListener {
            showAlertDialog()
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.content.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.removeAccessToken()
            }

            val intent = Intent(this@SettingActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        /*
        * We need different lifecycleScope as the second collect is not executed.
        * If the reviewer have solution to this, please write in the feedback form. Thanks!
        */
        lifecycleScope.launch {
            viewModel.getAccessToken().collect { token ->
                if (token.isNullOrEmpty()) {
                    binding.content.btnLogout.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getTheme().collect { themeMode ->
                setCheckedItem(themeMode ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun setCheckedItem(value: Int) {
        themeSelection = when (value) {
            AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.theme_light)
            AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.theme_dark)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> getString(R.string.theme_default)
            else -> "Unknown"
        }

        binding.content.themeValueTextView.text = themeSelection
    }

    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@SettingActivity)
        alertDialog.setTitle(getString(R.string.theme))

        val items = arrayOf(
            getString(R.string.theme_light),
            getString(R.string.theme_dark),
            getString(R.string.theme_default)
        )

        val checkedItem = when (themeSelection) {
            getString(R.string.theme_light) -> 0
            getString(R.string.theme_dark) -> 1
            else -> 2
        }

        alertDialog.setSingleChoiceItems(items, checkedItem) { dialog, which ->
            lifecycleScope.launch {
                when (which) {
                    0 -> viewModel.saveTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> viewModel.saveTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> viewModel.saveTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }

            dialog.dismiss()
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alert: AlertDialog = alertDialog.create()
        alert.show()
    }
}