package com.example.mylocationlogapp

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

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