package space.mrandika.dicogram.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import space.mrandika.dicogram.R
import space.mrandika.dicogram.databinding.ActivityRootBinding
import space.mrandika.dicogram.view.setting.SettingActivity

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.topAppBar.inflateMenu(R.menu.menu)

        binding.topAppBar.setOnMenuItemClickListener {
            val intent = Intent(this@RootActivity, SettingActivity::class.java)
            startActivity(intent)

            return@setOnMenuItemClickListener true
        }

        val navView: BottomNavigationView = binding.bottomNavigationBar

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.home_fragment, R.id.maps_fragment
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}