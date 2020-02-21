package com.paulik8.maptracker.services.location

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.paulik8.maptracker.worker_main.WorkerMainActivity


class LocationService(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var locationTask: Task<Void>? = null

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
        val client: SettingsClient = LocationServices.getSettingsClient(context)
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

    private fun initCallback() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.lastLocation
                Log.i("LocationService", location.toString())
            }

        }
    }

    fun checkPermissions() {
        val permissionAccessCoarseLocationApproved = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (permissionAccessCoarseLocationApproved && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (backgroundLocationPermissionApproved) {
                startLocationUpdates()
            } else {
                ActivityCompat.requestPermissions(context as WorkerMainActivity,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE)
            }
        } else if (!permissionAccessCoarseLocationApproved && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (backgroundLocationPermissionApproved) {
                ActivityCompat.requestPermissions(context as WorkerMainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    context as WorkerMainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE)
            }
        } else if (permissionAccessCoarseLocationApproved && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(context as WorkerMainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_CODE)
        }
    }

    companion object {
        private const val PERMISSION_CODE = 41
    }

}