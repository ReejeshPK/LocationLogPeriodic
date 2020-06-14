package com.example.mylocationlogapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.modal.MyUser
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    private lateinit var realm : Realm
    private val TAG : String = RegisterActivity::class.simpleName.toString()

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

        realm.executeTransactionAsync( {
            var lastInsertedId=it.where(MyUser::class.java).max("id")
            /*We are doing this since realm does not have auto increment feature*/
            var nextId : Int ?=null
            if(lastInsertedId==null){
                nextId=1;
                Log.d(TAG,"first insert")
            }
            else{
                nextId=lastInsertedId.toInt()+1
                logd(TAG,"next insert"+nextId)
            }
            var myuser = MyUser()
            myuser.id=nextId
            myuser.userName=userNameEditTextRegister.text.toString()
            myuser.password=passwordEditTextRegister.text.toString()

            it.insert(myuser)
        },{
            //on success
            Toast.makeText(this,"Registered Successfully",Toast.LENGTH_SHORT).show()
            finish()
        },{
            //onerror
            Toast.makeText(this,"Sorry, something went wrong, please try again",Toast.LENGTH_SHORT).show()
            loge(TAG,"error:"+it.message)
        })


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
