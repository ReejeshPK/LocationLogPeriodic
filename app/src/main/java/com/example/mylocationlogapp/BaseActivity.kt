package com.example.mylocationlogapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.startActivity

open class BaseActivity :  AppCompatActivity(){




    companion object{

        fun loge(TAGS:String,message:String){
            if(!MyApplicationClass.isInProduction) {
                Log.e(TAGS, message)
            }
        }
        fun logd(TAGS: String,message: String){
            if(!MyApplicationClass.isInProduction) {
                Log.d(TAGS, message)
            }
        }


    }

}