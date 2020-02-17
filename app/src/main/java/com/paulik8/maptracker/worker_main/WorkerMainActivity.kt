package com.paulik8.maptracker.worker_main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.google.firebase.database.*
import com.paulik8.maptracker.R
import android.content.ComponentName
import android.app.ActivityManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.paulik8.maptracker.services.Helper
import java.util.*


class WorkerMainActivity: AppCompatActivity(), ValueEventListener {

    private lateinit var db: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var button: Button
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.worker_main_activity)
        button = findViewById(R.id.worker_main_button)
        button.text = "Push"
        db = FirebaseDatabase.getInstance()
        db.setPersistenceEnabled(true)
        createFirebaseToken()
    }

    override fun onStart() {
        super.onStart()
        token = UUID.randomUUID().toString()
        Log.i("version", android.os.Build.VERSION.SDK_INT.toString())
        myRef = db.reference
        myRef.child("orders").child("$token").setValue("aaa")
        addListener()
        subscribeToFirebaseTopic()
    }

    private fun addListener() {
        myRef.addValueEventListener(this)
        button.setOnClickListener {
            myRef.child("orders").child("user1").setValue("bbb")
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
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.notification_token, token)
                Log.d("token", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
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

    @SuppressLint("LongLogTag")
    override fun onDataChange(data: DataSnapshot) {
        val str = data.child("orders").value
        Log.i("WorkerMainActivity:onDataChange", str.toString())
    }

    //end ValueEventListener

}