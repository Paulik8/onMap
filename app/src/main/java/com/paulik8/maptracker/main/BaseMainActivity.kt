package com.paulik8.maptracker.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.paulik8.maptracker.R
import com.paulik8.maptracker.main.map.MainMapFragment
import com.paulik8.maptracker.services.Util
import com.paulik8.maptracker.services.location.LocationService

open class BaseMainActivity : AppCompatActivity(), ValueEventListener {

    private var toolbar: ActionBar? = null
    private lateinit var navigation: BottomNavigationView
    open var currentItem: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val db: FirebaseDatabase  = Util.getDatabase()
    protected lateinit var ref: DatabaseReference

    open lateinit var navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener

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
        ref = db.reference
//        ref.addValueEventListener(this)
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

    protected fun changeFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_container_main, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    protected fun checkFragmentCreated(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    // start ValueEventListener

    override fun onCancelled(error: DatabaseError) {
        Log.e("LocationService", error.toString())
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        Log.i("snapshot", snapshot.value.toString())
    }

    // end


}