package com.example.mylocationlogapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyUser
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main_login.*

class LoginActivity : BaseActivity() {

    private lateinit var realm: Realm
    private val TAG: String = LoginActivity::class.simpleName.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()

        loginBtn.setOnClickListener(View.OnClickListener {
            if (validFields()) {
                loginFunction()
            }
        })

        if(MySharedPref.getIsLoggedIn()!!){
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginFunction() {

        var loginInfo: MyUser? = null

        loginInfo =realm.where(MyUser::class.java).equalTo("userName", userNameEditText.text.toString())
            .equalTo("password", passwordEditText.text.toString()).findFirst()

        if(loginInfo==null){
            //mens user not found
            Toast.makeText(this,"User Not Found",Toast.LENGTH_SHORT).show()
        }
        else{
            //goto dashboard
            logd(TAG,"id:"+loginInfo.id)
            Toast.makeText(this,"Login was successfull",Toast.LENGTH_SHORT).show()
            MySharedPref.setCurrentUserId(loginInfo.id)
            MySharedPref.setIsLoggedIn(true)
            val intent =Intent(this,DashboardActivity::class.java)
            startActivity(intent)
        }

       /*
       //we cannot access the object afterwards in this technique
       /*n: This Realm instance has already been closed, making it unusable.*/
       realm.executeTransactionAsync({
            loginInfo =
                it.where(MyUser::class.java).equalTo("userName", userNameEditText.text.toString())
                    .equalTo("password", passwordEditText.text.toString()).findFirst()
        }, {
            //onsuccess
            if(loginInfo==null){
                //mens user not found
                Toast.makeText(this,"User Not Found",Toast.LENGTH_SHORT).show()
            }
            else{
                //goto dashboard
                logd(TAG,"id:"+loginInfo?.id)
            }
        }, {
            //onerror
            loge(TAG, "Err:" + it.message)
        })*/

    }

    private fun validFields(): Boolean {
        if (!userNameEditText?.text.toString().isNullOrEmpty()) {

            if (!passwordEditText?.text.toString().isNullOrEmpty()) {
                return true;
            } else {
                passwordEditText?.setError("Enter Your Password")
            }

        } else {
            userNameEditText?.setError("Enter Your User Name")
        }
        return false;
    }

    fun moveToRegister(view: View) {
        val intent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
