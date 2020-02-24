package com.paulik8.maptracker.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.paulik8.maptracker.MainApplication
import com.paulik8.maptracker.R
import com.paulik8.maptracker.main.map.MainMapFragment
import com.paulik8.maptracker.services.location.LocationService
import android.content.Context.ACTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Context


class MainWorkerActivity : BaseMainActivity() {

    override var navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.bottom_item_map -> {
                if (currentItem == R.id.bottom_item_map) {
                    return@OnNavigationItemSelectedListener true
                }
                currentItem = R.id.bottom_item_map
                checkFragmentCreated(MainMapFragment.TAG)?.let { fragment ->
                    changeFragment(fragment, MainMapFragment.TAG)
                    return@OnNavigationItemSelectedListener true
                }
                changeFragment(MainMapFragment.newInstance(), MainMapFragment.TAG)
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_item_list -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_item_profile -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val locationService = MainApplication.app.locationService
//        if (!locationService.isActive) {
//            locationService.isActive = true
        if (!isMyServiceRunning(LocationService::class.java)) {
            startService(Intent(this, LocationService::class.java))
        }

//        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        Log.i("onDestroy", "onDestroy")
        super.onDestroy()
    }

}