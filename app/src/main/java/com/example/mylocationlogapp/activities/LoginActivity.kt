package com.example.mylocationlogapp.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mylocationlogapp.BaseActivity

import com.example.mylocationlogapp.MyBackgroundLocationService
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyUser
import com.google.android.material.snackbar.Snackbar
import io.realm.BuildConfig
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
            loginBasic()
        })

        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                loginBasic()
                true
            } else {
                false
            }
        }

        if(MySharedPref.getIsLoggedIn()!!){
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginBasic() {
        if (validFields()) {
            //check the location permission also
            if(checkPermissions()) {
                if(checkGps()) {
                    loginFunction()
                }else{
                    checkGPSEnable()
                }
            }
            else{
                requestPermissions()
            }
        }
    }

    private fun loginFunction() {

        var loginInfo: MyUser? = null
        var loginInfoUserName: MyUser? = null

        loginInfoUserName =realm.where(MyUser::class.java).equalTo("userName", userNameEditText.text.toString()).findFirst()

        loginInfo =realm.where(MyUser::class.java).equalTo("userName", userNameEditText.text.toString())
            .equalTo("password", passwordEditText.text.toString()).findFirst()

        if((loginInfoUserName!=null)&&(loginInfo==null)) {
            //means passworkd is wrong
            Toast.makeText(this,"Kindly Check your Credentials",Toast.LENGTH_SHORT).show()
        }
        else if(loginInfo==null){
            //mens user not found
            Toast.makeText(this,"Kindly Register to Login",Toast.LENGTH_SHORT).show()
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

    var userAskAgain:Boolean=false;
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale&& !userAskAgain) {
            logd(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbarForLoc(R.string.permission_rationale, android.R.string.ok,
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
    private fun showSnackbarForLoc(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
      /*  Toast.makeText(this, resources.getString(mainTextStringId), Toast.LENGTH_LONG).show()*/

        val parentLayout = findViewById<View>(R.id.myLoginLayout)

        hideKeyboard(parentLayout)
        val snackBar = Snackbar.make(parentLayout, resources.getString(mainTextStringId), Snackbar.LENGTH_LONG)
            .setAction("Request Permission") {
                userAskAgain=true;
                requestPermissions()
            }
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView = snackBar.view
        /*val textView =
            snackBarView.findViewById<TextView>(androidx.R.id.snackbar_text)
        textView.setTextColor(Color.RED)*/
        snackBar.show()
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {

        /*  Toast.makeText(this, resources.getString(mainTextStringId), Toast.LENGTH_LONG).show()*/
        val parentLayout = findViewById<View>(R.id.myLoginLayout)
        hideKeyboard(parentLayout)
        val snackBar = Snackbar.make(parentLayout, resources.getString(mainTextStringId), Snackbar.LENGTH_LONG)
            .setAction("Grant Permission") {

                requestPermissions()
            }
        snackBar.setActionTextColor(Color.GREEN)
        val snackBarView = snackBar.view
        /*val textView =
            snackBarView.findViewById<TextView>(androidx.R.id.snackbar_text)
        textView.setTextColor(Color.RED)*/
        snackBar.show()
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun checkGps():Boolean{
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        else{
            return true;
        }
    }

    private fun checkGPSEnable() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Your GPS seems to be disabled, kindly enable it, set it to High Accuracy")
            .setCancelable(false)
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id
                ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.show()
    }
}
