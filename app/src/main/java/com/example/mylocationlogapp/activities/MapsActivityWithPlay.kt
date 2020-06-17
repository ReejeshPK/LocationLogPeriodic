package com.example.mylocationlogapp.activities

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
import com.google.android.gms.maps.model.PolylineOptions
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_maps_with_play.*

class MapsActivityWithPlay : BaseActivity(), OnMapReadyCallback {

    lateinit var mMap: GoogleMap

    private var idSelected: Int? = null
    private val TAG: String = MapsActivityWithPlay::class.java.simpleName.toString()

    private lateinit var realm: Realm
    private var selectedLocation: MyLocationModal? = null
    var locationListForThisUser: List<MyLocationModal>? = null

    var isPlaying: Boolean = false
    var polyLineOptions:PolylineOptions?=null

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

        var myAsyncTask = navigateAndPlayTask()
        playButton.setOnClickListener({

            if (!isPlaying) {
                playButton.setImageResource(R.drawable.ic_stop)
                myAsyncTask = navigateAndPlayTask()
                myAsyncTask.execute()
                isPlaying = true;
            } else {
                playButton.setImageResource(R.drawable.ic_play_arrow)
                myAsyncTask.cancel(true)
                isPlaying = false
                //Toast.makeText(this,"Please W",Toast.LENGTH_SHORT).show()
            }

        })


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getThisIdAndUserDetails()
    }

    private fun getThisIdAndUserDetails() {
        selectedLocation = realm.where(MyLocationModal::class.java).equalTo("id", idSelected)
            .equalTo("user_id", MySharedPref.getCurrentUserId()).findFirst()

        /*locationListForThisUser = realm.where(MyLocationModal::class.java)
            .equalTo("user_id", MySharedPref.getCurrentUserId()).findAll()*/

        if (selectedLocation != null) {

            var curloc = selectedLocation!!.latitude?.let {
                selectedLocation!!.longitude?.let { it1 ->
                    LatLng(
                        it,
                        it1
                    )
                }
            }

            mMap.addMarker(MarkerOptions().position(curloc!!).title("Marker in Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curloc, 16.0f))
        }
    }


    inner class navigateAndPlayTask() :
        AsyncTask<Void, LatLng, String>() {
        private lateinit var realm2: Realm
        // List<MyLocationModal>
        override fun onPreExecute() {
            super.onPreExecute()
            runOnUiThread({
                mMap.clear()
                polyLineOptions= PolylineOptions().width(5.0f).color(Color.BLUE).geodesic(true)
            })
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                realm2 = Realm.getDefaultInstance()

                realm2.beginTransaction()
                realm2.commitTransaction()

                var locationListForThis = realm2.where(MyLocationModal::class.java)
                    .equalTo("user_id", MySharedPref.getCurrentUserId()).sort("manual_order_by",
                        Sort.ASCENDING).findAll()
                locationListForThis.forEach({
                    var curLoc =
                        it.latitude?.let { it1 -> it.longitude?.let { it2 -> LatLng(it1, it2) } }
                    onProgressUpdate(curLoc)
                    Thread.sleep(1500)
                })
            } finally {
                if (realm2 != null) {
                    realm2.close();
                }
            }
            return ""
        }

        override fun onProgressUpdate(vararg latlngg: LatLng?) {
            super.onProgressUpdate(*latlngg)
            logd("location:", "" + latlngg)
            runOnUiThread({
                mMap.addMarker(MarkerOptions().position(latlngg[0]!!).title("Marker in Selected Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngg[0], 16.0f))
                polyLineOptions?.add(latlngg[0])
                mMap.addPolyline(polyLineOptions)
            })

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            isPlaying = false
            playButton.setImageResource(R.drawable.ic_play_arrow)
        }

    }

}
