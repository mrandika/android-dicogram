package space.mrandika.dicogram.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.mrandika.dicogram.R
import space.mrandika.dicogram.databinding.ActivityMainBinding
import space.mrandika.dicogram.viewmodel.SettingViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivityMainBinding

    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            settingViewModel.getTheme().collect { theme ->
                if (theme != null) {
                    applyTheme(theme)
                }
            }
        }

        // Redirect the user
        redirect()
    }

    private fun applyTheme(theme: Int) {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()

        if (currentTheme != theme) {
            lifecycleScope.launch {
                settingViewModel.saveTheme(theme)
            }
        }
    }

    private fun redirect() {
        lifecycleScope.launch {
            settingViewModel.getAccessToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val intent = Intent(this@MainActivity, RootActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val fragmentManager = supportFragmentManager
                    val welcomeFragment = WelcomeFragment()
                    val fragment =
                        fragmentManager.findFragmentByTag(WelcomeFragment::class.java.simpleName)

                    if (fragment !is WelcomeFragment) {
                        fragmentManager
                            .beginTransaction()
                            .add(
                                R.id.frame_container,
                                welcomeFragment,
                                WelcomeFragment::class.java.simpleName
                            )
                            .commit()
                    }
                }
            }
        }
    }
}