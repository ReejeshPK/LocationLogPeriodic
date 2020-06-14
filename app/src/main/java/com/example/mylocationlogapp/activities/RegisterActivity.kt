package com.example.mylocationlogapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mylocationlogapp.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        realm= Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()


        signUpButton.setOnClickListener( View.OnClickListener {
            if(formValid()){
                signUpFunction()
            }
        })

    }

    private fun signUpFunction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun formValid(): Boolean {
        if(!userNameEditTextRegister?.text.toString().isNullOrEmpty()){
            if(!passwordEditTextRegister?.text.toString().isNullOrEmpty()) {
                if (confirmPasswordEditText?.text.toString().equals(passwordEditTextRegister.text.toString())) {

                    return true;

                } else {
                    confirmPasswordEditText?.setError("Passwords Do Not Match")
                }
            }
            else{
                passwordEditTextRegister?.setError("Enter a password")
            }
        }
        else{
            userNameEditTextRegister?.setError("Enter your name here")
        }
        return false;
    }
}
