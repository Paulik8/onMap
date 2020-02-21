package com.paulik8.maptracker.main

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.paulik8.maptracker.R
import java.util.*


class MainMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, PlaceSelectionListener {

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private lateinit var mMap: GoogleMap
    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        button = findViewById(R.id.fab)
//        button.setOnClickListener {
//            loadPlace()
//        }
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US)
            val client = Places.createClient(this)
        }
        val autoCompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autoCompleteFragment.setOnPlaceSelectedListener(this)
        createIntent()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun createIntent() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                    Log.i("onMap", "Place: " + place?.name + ", " + place?.id);
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                    Log.i("onMap", status?.statusMessage)
                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    private fun createFirebaseToken() {
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w("token", "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
//                val msg = getString(R.string.notification_token, token)
//                Log.d("token", msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            })
//    }

    //start OnMyLocationButtonClickListener

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(applicationContext, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    //end OnMyLocationButtonClickListener

    //start OnMyLocationClickListener

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(applicationContext, "Current location:\n" + location, Toast.LENGTH_LONG).show()
    }

    //end OnMyLocationClickListener

    //start PlaceSelectionListener

    override fun onPlaceSelected(p0: Place) {
        Toast.makeText(applicationContext,""+p0.name+p0.latLng,Toast.LENGTH_LONG).show();
    }

    override fun onError(p0: Status) {
        Toast.makeText(applicationContext, "" + p0.toString(), Toast.LENGTH_LONG).show()
    }

    //end PlaceSelectionListener

}
