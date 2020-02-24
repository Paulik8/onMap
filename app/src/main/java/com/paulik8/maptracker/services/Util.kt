package com.paulik8.maptracker.services

import com.google.firebase.database.FirebaseDatabase

class Util {

    companion object {
        private var database: FirebaseDatabase? = null

        fun getDatabase(): FirebaseDatabase {
            if (database == null) {
                database = FirebaseDatabase.getInstance()
                database?.setPersistenceEnabled(true)
            }
            return database as FirebaseDatabase
        }

    }

}