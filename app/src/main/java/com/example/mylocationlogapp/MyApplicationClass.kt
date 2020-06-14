package com.example.mylocationlogapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        /*Note: Initialisation of Realm only has to be done once so as to use it throughout the application.*/
        val config = RealmConfiguration.Builder().build() //this is default
        Realm.setDefaultConfiguration(config)
        sharedpreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    companion object{
        var sharedpreferences: SharedPreferences? = null
    }


}