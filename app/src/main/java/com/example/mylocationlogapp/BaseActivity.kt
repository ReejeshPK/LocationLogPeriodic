package com.example.mylocationlogapp

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity :  AppCompatActivity(){



    companion object{
        val isInProduction=false;
        fun loge(TAGS:String,message:String){
            if(!isInProduction) {
                Log.e(TAGS, message)
            }
        }
        fun logd(TAGS: String,message: String){
            if(!isInProduction) {
                Log.d(TAGS, message)
            }
        }
    }

}