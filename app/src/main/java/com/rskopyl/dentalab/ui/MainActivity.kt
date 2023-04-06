package com.rskopyl.dentalab.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager
            .findFragmentById(R.id.fcv_top_level) as NavHostFragment
    }
    private val navController: NavController
        get() = navHostFragment.navController

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            TOP_LEVEL_DESTINATIONS,
            drawerLayout = binding.root
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment.childFragmentManager.registerFragmentLifecycleCallbacks(
            MenuProviderLifecycleCallback(), false
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.isTitleCentered =
                destination.id == navController.graph.startDestinationId
        }

        setupUi()
    }

    private fun setupUi() = binding.run {
        toolbar.run {
            setupWithNavController(
                navController,
                appBarConfiguration
            )
            setSupportActionBar(toolbar)
        }
        navTopLevel.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) ||
                super.onSupportNavigateUp()
    }

    inner class MenuProviderLifecycleCallback :
        FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentViewCreated(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            view: View,
            savedInstanceState: Bundle?,
        ) {
            if (fragment is MenuProvider) {
                addMenuProvider(fragment, fragment.viewLifecycleOwner)
            }
        }
    }

    private companion object {

        val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.homeFragment,
            R.id.searchFragment,
            R.id.settingsFragment
        )
    }
}