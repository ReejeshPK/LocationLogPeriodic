package com.example.mylocationlogapp.fragments

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mylocationlogapp.BaseActivity

import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.activities.MapsActivityWithPlay
import com.example.mylocationlogapp.helper.MyConstants
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyLocationModal
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_maps_with_play.*


class MyMapsFragment : BaseFragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mMap: GoogleMap

    private var idSelected: Int? = null
    private val TAG: String = MyMapsFragment::class.java.simpleName.toString()

    private lateinit var realm: Realm
    private var selectedLocation: MyLocationModal? = null
    var locationListForThisUser: List<MyLocationModal>? = null

    var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      /*  arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View =  inflater.inflate(R.layout.fragment_my_maps, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val playButton:FloatingActionButton=view.findViewById(R.id.playButton)

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if (intent != null) {
            //idSelected = intent.getIntExtra(MyConstants.INTENT_EXTRA_LOCATION_POS, 0)

            logd(TAG, "idseld:" + idSelected)

       // }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): MyMapsFragment {
            return MyMapsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mMap = googleMap
        }
        getThisIdAndUserDetails(false)
    }

    private fun getThisIdAndUserDetails(idPassed: Boolean) {
        if(idPassed) {
            selectedLocation = realm.where(MyLocationModal::class.java).equalTo("id", idSelected)
                .equalTo("user_id", MySharedPref.getCurrentUserId()).findFirst()
        }
        else{
            selectedLocation = realm.where(MyLocationModal::class.java)
                .equalTo("user_id", MySharedPref.getCurrentUserId()).findFirst()
        }

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
        private var locationsFound:Boolean=false
        // List<MyLocationModal>
        override fun onPreExecute() {
            super.onPreExecute()
            logd(TAG,"onPreExecute")
            activity?.runOnUiThread({
                mMap.clear()
            })
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                logd(TAG,"doInBackground")
                realm2 = Realm.getDefaultInstance()

                realm2.beginTransaction()
                realm2.commitTransaction()

                var locationListForThis = realm2.where(MyLocationModal::class.java)
                    .equalTo("user_id", MySharedPref.getCurrentUserId()).sort("manual_order_by",
                        Sort.ASCENDING).findAll()
                locationListForThis.forEach({
                    locationsFound=true
                    var curLoc =
                        it.latitude?.let { it1 -> it.longitude?.let { it2 -> LatLng(it1, it2) } }
                    onProgressUpdate(curLoc)
                    Thread.sleep(1500)
                })
            }catch (e:Exception){
                loge(TAG,"Error:"+e.message)
            }

            finally {
                logd(TAG,"finally")
                if (realm2 != null) {
                    realm2.close();
                }
            }
            return ""
        }

        override fun onProgressUpdate(vararg latlngg: LatLng?) {
            super.onProgressUpdate(*latlngg)
            logd(TAG, "latlng" + latlngg.size)
            activity?.runOnUiThread({
                mMap.addMarker(MarkerOptions().position(latlngg[0]!!).title("Marker in Selected Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngg[0], 16.0f))
            })
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            isPlaying = false
            playButton.setImageResource(R.drawable.ic_play_arrow)
            if(!locationsFound){
                Toast.makeText(context,"No Data Found",Toast.LENGTH_SHORT).show()
            }
        }

    }
}
