package com.paulik8.maptracker

import android.app.Application
import android.util.Log
import com.paulik8.maptracker.services.Util
import com.paulik8.maptracker.services.location.LocationService

class MainApplication : Application() {

//    lateinit var locationService: LocationService

    companion object {
        lateinit var app: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("onCreate", "onCreate")
        app = this
//        locationService = LocationService()
        Util.getDatabase()
    }

    override fun onTerminate() {
        Log.i("onTerminate", "onTerminate")
        super.onTerminate()

    }

}