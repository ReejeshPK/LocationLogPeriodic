package com.example.mylocationlogapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R

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

    fun logoutFunction(view: View) {}
}
