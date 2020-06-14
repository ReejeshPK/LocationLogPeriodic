package com.example.mylocationlogapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.adapters.MyLocationAdapter
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.modal.MyLocationModal
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_location_list.*
import kotlinx.android.synthetic.main.content_location_list.*

class LocationListActivity : BaseActivity() {

    private lateinit var realm: Realm
    private val TAG = LocationListActivity::class.java.simpleName

    private var myLocationList :List<MyLocationModal>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)
        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()

        fab.setOnClickListener { view ->
           /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
            val intent =Intent(this,PIckCurrentLocationMapsActivity::class.java)
            startActivity(intent)
        }



        getLastLocationsForThisUser()
    }

    private fun getLastLocationsForThisUser() {
        myLocationList=realm.where(MyLocationModal::class.java).equalTo("user_id",MySharedPref.getCurrentUserId()).findAll()
        if(myLocationList!=null) {
            var sizeList:Int= myLocationList?.size!!;
            if(sizeList > 0) {
                noDataText.visibility= View.INVISIBLE
                myLocationRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = MyLocationAdapter(myLocationList)
                }

            }
            else{
                noDataText.visibility= View.VISIBLE
            }
        }
        else{
            noDataText.visibility= View.VISIBLE
        }
    }

}
