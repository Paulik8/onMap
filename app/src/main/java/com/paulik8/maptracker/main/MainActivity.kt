package com.paulik8.maptracker.main

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.paulik8.maptracker.R
import com.paulik8.maptracker.main.map.MainMapFragment

class MainActivity : AppCompatActivity() {

    private var toolbar: ActionBar? = null
    private lateinit var navigation: BottomNavigationView
    private var currentItem: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
//    private lateinit var bottomSheetLayout: LinearLayout


    private val navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
        setContentView(R.layout.activity_example)

        toolbar = supportActionBar
        navigation = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        navigation.selectedItemId = R.id.bottom_item_map
        val bottomSheetView: NestedScrollView = findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

//        bottomSheetBehavior.peekHeight = navigation.height + 90
        initialize()
    }

    private fun initialize() {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

        }
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun changeFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_container_main, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    private fun checkFragmentCreated(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }
}