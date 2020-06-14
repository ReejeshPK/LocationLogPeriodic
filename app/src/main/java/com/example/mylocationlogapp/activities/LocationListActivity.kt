package com.example.mylocationlogapp.activities

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.mylocationlogapp.R

import kotlinx.android.synthetic.main.activity_location_list.*

class LocationListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
           /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
        }
    }

}
