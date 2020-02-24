package com.paulik8.maptracker.services.location

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.paulik8.maptracker.services.Util
import com.paulik8.maptracker.worker_main.WorkerMainActivity
import java.util.*
import kotlin.collections.HashMap


class LocationService : Service(), ValueEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationTask: Task<Void>? = null
    private lateinit var ref: DatabaseReference
    private lateinit var db: FirebaseDatabase

    override fun onCreate() {
        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(this)
        db = Util.getDatabase()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        observeDbState()
        return START_STICKY
    }



    private fun startLocationUpdates() {
        initCallback()
        val minDelay: Long = 3 * 1000 // testTime
        val delay: Long = 5 * 1000 // testTime
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setFastestInterval(minDelay)
            .setInterval(delay)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(applicationContext)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            locationCallback?.let { callback ->
                locationTask = fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
            }
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    Log.e("LocationError", exception.toString())
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

        locationTask?.addOnSuccessListener {
            Log.i("LocationTask", "success")
        }
        locationTask?.addOnFailureListener {
            Log.i("LocationTask", "error")
        }

    }

    fun observeDbState() {
        ref = db.reference
        val adminRef = ref.child("admin").child("isActive")
        adminRef.addValueEventListener(this)
    }

    private fun initCallback() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.lastLocation
                Log.i("LocationService", location.toString())
                location?.let { updateLocation(it) }
            }

        }
    }

    private fun updateLocation(lastLocation: Location) {
        val map = HashMap<String, Any>()
        map["lastLatitude"] = lastLocation.latitude
        map["lastLongitude"] = lastLocation.longitude
        ref.child("users").child(UUID.randomUUID().toString()).setValue(map)
    }

    fun checkPermissions() {
        val permissionAccessCoarseLocationApproved = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (permissionAccessCoarseLocationApproved && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (backgroundLocationPermissionApproved) {
                startLocationUpdates()
            } else {
                ActivityCompat.requestPermissions(applicationContext as WorkerMainActivity,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE)
            }
        } else if (!permissionAccessCoarseLocationApproved && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (backgroundLocationPermissionApproved) {
                ActivityCompat.requestPermissions(applicationContext as WorkerMainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    applicationContext as WorkerMainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE)
            }
        } else if (permissionAccessCoarseLocationApproved && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(applicationContext as WorkerMainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_CODE)
        }
    }

    private fun removeTasks() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    companion object {
        private const val PERMISSION_CODE = 41
    }

    // start ValueEventListener

    override fun onCancelled(error: DatabaseError) {
        Log.e("LocationService", error.toString())
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val data = snapshot.value.toString()
//        val adminStatus = snapshot.child("admin").child("isActive").value.toString()
        Log.i("snapshot", data)
        Toast.makeText(applicationContext, "snapshot changed to $data", Toast.LENGTH_LONG).show()
        if (data == "1") {
            checkPermissions()
        } else if (data == "0") {
            removeTasks()
        }
    }

    // end

}