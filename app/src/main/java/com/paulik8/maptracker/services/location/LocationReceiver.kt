package com.paulik8.maptracker.services.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationReceiver: BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { LocationUtility.initJob(it) }
    }

}