package com.example.mylocationlogapp.activities

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        /*Note: Initialisation of Realm only has to be done once so as to use it throughout the application.*/
        val config = RealmConfiguration.Builder().build() //this is default
        Realm.setDefaultConfiguration(config)
    }

}