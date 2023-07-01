package com.example.runnningpetproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runnningpetproject.R
import com.example.runnningpetproject.databinding.ActivityMainBinding
import com.example.runnningpetproject.db.RunDao
import com.example.runnningpetproject.utlis.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToTracking(intent)
        val navController = findNavController(R.id.navHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener {
            /* NO OP*/
        }
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when (destination.id){
                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment ->
                    binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTracking(intent)
    }

    fun navigateToTracking(intent: Intent?){
        if (intent?.action == Constants.ACTION_SHOW_FRAGMENT){
            val navController = findNavController(R.id.navHostFragment)
            navController.navigate(R.id.action_global_tracking_fragment)

        }
    }
}