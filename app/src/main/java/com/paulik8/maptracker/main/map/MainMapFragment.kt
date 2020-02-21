package com.paulik8.maptracker.main.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.paulik8.maptracker.R
import java.util.*

class MainMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
    PlaceSelectionListener {

    companion object {
        const val TAG = "MainMapFragmentTAG"
        fun newInstance(): Fragment {
            val fragment = MainMapFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var mMap: GoogleMap? = null
    private lateinit var supportMapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_map, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { act ->
            supportMapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)

            if (!Places.isInitialized()) {
                val activityContext = act.applicationContext
                Places.initialize(activityContext, getString(R.string.google_maps_key), Locale.US)
                val client = Places.createClient(activityContext)
            }
            val autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment
            autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
            autoCompleteFragment.setOnPlaceSelectedListener(this)
        }
    }

    // start OnMapReadyCallback

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.let { map ->
            map.isMyLocationEnabled = true
            map.setOnMyLocationButtonClickListener(this)
            map.setOnMyLocationClickListener(this)
            // Add a marker in Sydney and move the camera
            val sydney = LatLng(-34.0, 151.0)
            map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

    }

    // end

    // start GoogleMap.OnMyLocationClickListener

    override fun onMyLocationClick(location: Location) {
    }

    // end

    // start GoogleMap.OnMyLocationButtonClickListener

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    // end

    // start PlaceSelectionListener

    override fun onPlaceSelected(place: Place) {
    }

    override fun onError(status: Status) {
    }

    // end

}