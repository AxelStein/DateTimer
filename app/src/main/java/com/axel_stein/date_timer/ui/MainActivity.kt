package com.axel_stein.date_timer.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var settings: AppSettings

    @Inject
    fun inject(settings: AppSettings) {
        this.settings = settings
    }

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        NavigationUI.setupActionBarWithNavController(this, navController)

        binding.adProposal.setOnClickListener {
            settings.setAdProposalVisible(false)
            binding.adProposal.isVisible = false
            openSettings()
        }

        if (settings.isAdsEnabled()) {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
            binding.adView.isVisible = true
            binding.adDivider.isVisible = true
            binding.adProposal.isVisible = settings.isAdProposalVisible()
        } else {
            binding.adProposal.isVisible = false
            binding.adView.isVisible = false
            binding.adDivider.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> openSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSettings() {
        navController.navigate(R.id.action_open_preferences)
    }
}