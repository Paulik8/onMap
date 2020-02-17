package com.paulik8.maptracker.services.location

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context

class LocationUtility {

    companion object Util {
        fun initJob(context: Context) {
            val serviceComponent = ComponentName(context, LocationService::class.java)
            val builder = JobInfo.Builder(0, serviceComponent)
            val delay: Long = 60 * 1000
            builder.setMinimumLatency(delay)
            builder.setOverrideDeadline(2 * delay)
            val jobScheduler: JobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())
        }
    }

}