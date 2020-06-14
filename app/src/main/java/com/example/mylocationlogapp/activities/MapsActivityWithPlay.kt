package com.example.mylocationlogapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.helper.MyConstants
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyLocationModal

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_maps_with_play.*

class MapsActivityWithPlay : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var idSelected: Int? = null
    private val TAG: String = MapsActivityWithPlay::class.java.simpleName.toString()

    private lateinit var realm: Realm
    private var selectedLocation: MyLocationModal ?=null
    private var locationListForThisUser:List<MyLocationModal>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_with_play)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()

        if (intent != null) {
            idSelected = intent.getIntExtra(MyConstants.INTENT_EXTRA_LOCATION_POS, 0)
            logd(TAG, "idseld:" + idSelected)

        }

        playButton.setOnClickListener({

        })


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getThisIdAndUserDetails()
    }

    private fun getThisIdAndUserDetails() {
       selectedLocation= realm.where(MyLocationModal::class.java).equalTo("id",idSelected)
            .equalTo("user_id",MySharedPref.getCurrentUserId()).findFirst()

        locationListForThisUser=realm.where(MyLocationModal::class.java).equalTo("user_id",MySharedPref.getCurrentUserId()).findAll()

        if(selectedLocation!=null) {

            var curloc = selectedLocation!!.latitude?.let { selectedLocation!!.longitude?.let { it1 ->
                LatLng(it,
                    it1
                )
            } }

            mMap.addMarker(MarkerOptions().position(curloc!!).title("Marker in Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curloc, 16.0f))
        }
    }



}
