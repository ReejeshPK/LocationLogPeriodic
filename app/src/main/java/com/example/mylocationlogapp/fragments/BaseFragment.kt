package com.example.mylocationlogapp.fragments

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.mylocationlogapp.MyApplicationClass

open class BaseFragment: Fragment() {
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