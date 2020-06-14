package com.example.mylocationlogapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.BuildConfig
import com.example.mylocationlogapp.MyBackgroundLocationService
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyUser
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main_login.*

class LoginActivity : BaseActivity() {

    private lateinit var realm: Realm
    private val TAG: String = LoginActivity::class.simpleName.toString()
    private val REQUEST_PERMISSIONS_REQUEST_CODE:Int=102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()

        loginBtn.setOnClickListener(View.OnClickListener {
            if (validFields()) {
                //check the location permission also
                if(checkPermissions()) {
                    loginFunction()
                }
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
            MyBackgroundLocationService.startService(this,"Service Start")
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


    //permissions
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            logd(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    startLocationPermissionRequest()
                })

        } else {
            logd(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest()
        }
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@LoginActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        logd(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                logd(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //getLastLocation()
                loginFunction()
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Toast.makeText(this, resources.getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }
}
