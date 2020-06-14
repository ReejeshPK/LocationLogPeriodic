package com.example.mylocationlogapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun moveToRegister(view: View) {
        val intent : Intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }
}
