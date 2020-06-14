package com.example.mylocationlogapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.helper.MySharedPref

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    fun moveToMapCurrentLocation(view: View) {}
    fun moveToLocationList(view: View) {
        val intent : Intent = Intent(this,LocationListActivity::class.java)
        startActivity(intent)
    }

    fun logoutFunction(view: View) {
        MySharedPref.clearAllPrefs()
        val intent : Intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
