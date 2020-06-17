package com.example.mylocationlogapp.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.mylocationlogapp.MyBackgroundLocationService
import com.example.mylocationlogapp.R

import com.example.mylocationlogapp.activities.ui.main.SectionsPagerAdapter
import com.example.mylocationlogapp.helper.MySharedPref
import kotlinx.android.synthetic.main.activity_my_main_new_dashboard.*

class MyMainNewDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main_new_dashboard)
        titleToolbar.setTitle("Location Log")
        setSupportActionBar(titleToolbar)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        //val fab: FloatingActionButton = findViewById(R.id.fab)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }

    fun logoutFunction() {
        MyBackgroundLocationService.stopService(this)
        MySharedPref.clearAllPrefs()
        val intent : Intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        val id=item.itemId
        if(id==R.id.logoutItem){
            logoutFunction()
        }
        return super.onOptionsItemSelected(item)
    }
}