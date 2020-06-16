package com.example.mylocationlogapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.mylocationlogapp.activities.LoginActivity
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyLocationModal
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import io.realm.Realm
import java.util.*

class MyBackgroundLocationService : Service() {

    /**This does not work on mi device wwhen killed, best to replace it with job intent service*/

    private val CHANNEL_ID = "ForegroundService"
    private val TAG:String =MyBackgroundLocationService::class.java.toString()

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback

    private lateinit var locationRequest:LocationRequest
    private lateinit var realm: Realm

    private lateinit var latLng:LatLng

    private val LOCATION_UPDATE_INTERVAL :Long=1000*15*60; //15 mins

    companion object {
        /*This companion is like static in java*/
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, MyBackgroundLocationService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, MyBackgroundLocationService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        MyApplicationClass.globalLog(TAG,"oncreate service")
        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    MyApplicationClass.globalLog(TAG,"lat:${location.latitude} lng:${location.longitude}")
                    //todo:insert into db
                    latLng=LatLng(location.latitude,location.longitude)
                    saveTheLocation()
                }
            }
        }

        locationRequest=LocationRequest().setInterval(LOCATION_UPDATE_INTERVAL).setFastestInterval(LOCATION_UPDATE_INTERVAL)

        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //stopSelf();
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    private fun startLocationUpdates() {
        MyApplicationClass.globalLog(TAG,"startingLocationupdates")
        mFusedLocationClient?.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplicationClass.globalLog(TAG,"ondestroy")
        mFusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    fun saveTheLocation() {
        BaseActivity.logd(TAG, "onclick")
        //just check if marker is moved or not
        realm.executeTransactionAsync({
            var lastInsertedId=it.where(MyLocationModal::class.java).max("id")
            /*We are doing this since realm does not have auto increment feature*/
            var nextId : Int ?=null
            if(lastInsertedId==null){
                nextId=1;
                Log.d(TAG,"first insert")
            }
            else{
                nextId=lastInsertedId.toInt()+1
                BaseActivity.logd(TAG, "next insert" + nextId)
            }

            var myLocationInfo= MyLocationModal()
            myLocationInfo.dateTime= Date()
            myLocationInfo.id=nextId
            myLocationInfo.latitude=latLng.latitude
            myLocationInfo.longitude=latLng.longitude
            myLocationInfo.user_id= MySharedPref.getCurrentUserId()

            it.insert(myLocationInfo)

        },{
            //showMessage("Location Saved!")
            MyApplicationClass.globalLog(TAG,"inserted in db")
        },{
            //showMessage("Something went wrong, please try again")
            BaseActivity.loge(TAG, "erro:" + it.message)
        })
    }
}