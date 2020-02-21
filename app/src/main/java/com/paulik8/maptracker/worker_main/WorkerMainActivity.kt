package com.paulik8.maptracker.worker_main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.google.firebase.database.*
import com.paulik8.maptracker.R
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.HashMap


class WorkerMainActivity: AppCompatActivity(), ValueEventListener {

    private var db: FirebaseDatabase? = null
    private lateinit var myRef: DatabaseReference
    private lateinit var button: Button
    private var userId: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.worker_main_activity)
        button = findViewById(R.id.worker_main_button)
        button.text = "Push"
        userId = UUID.randomUUID().toString()
        // err
        if (db == null) {
            db = FirebaseDatabase.getInstance()
            db?.setPersistenceEnabled(true)
        }
        createFirebaseToken()
    }

    override fun onStart() {
        super.onStart()
        Log.i("version", android.os.Build.VERSION.SDK_INT.toString())
        db?.let {
            myRef = it.reference
        }
        // ?
        addListener()
        subscribeToFirebaseTopic()
    }

    private fun addListener() {
        myRef.addValueEventListener(this)

        button.setOnClickListener {
            Log.i("push", "push")
            val updateData = HashMap<String, Any>()
            token?.let {
                updateData["deviceId"] = it
            }
            myRef.child("users").child("$userId").setValue(updateData)
//            LocationService(this).checkPermissions()
        }
    }

    private fun removeListener() {
        myRef.removeEventListener(this)
    }

    private fun createFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("token", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                token = task.result?.token
                addTokenToDb()
                // Log and toast
                val msg = getString(R.string.notification_token, token)
                Log.d("token", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }

    private fun addTokenToDb() {
        val data = HashMap<String, Any>()
        data["deviceId"] = token + "1"
        myRef.child("users").child("$userId").setValue(data)
    }

    private fun subscribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("test")
            .addOnCompleteListener { task ->
                //var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                   // msg = getString(R.string.msg_subscribe_failed)
                }
                Log.d("subscribe", "subscribe")
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }


    override fun onStop() {
        super.onStop()
        removeListener()
    }

    //start ValueEventListener

    override fun onCancelled(error: DatabaseError) {
    }

    override fun onDataChange(data: DataSnapshot) {
        val str = data.child("users").child("$userId").child("deviceId").value
        Log.i("WorkerMainActivity:onDataChange", str.toString())
    }

    //end ValueEventListener

}