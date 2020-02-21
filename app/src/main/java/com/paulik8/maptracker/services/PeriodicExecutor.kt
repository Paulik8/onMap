package com.paulik8.maptracker.services

import android.os.Handler
import android.os.Looper


class PeriodicExecutor {

    private var handler = Handler(Looper.getMainLooper())

    private lateinit var task: Runnable

    fun createTask(callback: () -> Unit) {
        task = Runnable {
            callback()
        }
    }

    fun createTask(callback: () -> Unit, delay: Long) {
        task = Runnable {
            callback()
            handler.postDelayed(task, delay)
        }
    }

    fun startTask() {
        task.run()
    }

    fun stopTask() {
        handler.removeCallbacks(task)
    }

}