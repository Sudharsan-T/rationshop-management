package com.example.meh

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.meh.databinding.ActivityMainBinding

/**
 * The main container for the application.
 * Manages the navigation host and toolbar visibility.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup View Binding to access layout elements
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Find the navigation controller responsible for swapping fragments
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        
        // Define top-level destinations (screens without a back button in the toolbar)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.LoginFragment, R.id.DashboardFragment))
        
        // Connect the toolbar with the navigation controller
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Hide or show the toolbar based on which screen the user is currently viewing
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.LoginFragment || 
                destination.id == R.id.RegisterFragment || 
                destination.id == R.id.DashboardFragment) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles the "Up" button (back arrow) in the toolbar.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}