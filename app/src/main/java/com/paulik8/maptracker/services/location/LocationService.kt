package com.paulik8.maptracker.services.location

import android.app.job.JobParameters
import android.app.job.JobService

class LocationService: JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}