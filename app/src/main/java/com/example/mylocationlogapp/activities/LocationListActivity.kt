package com.example.mylocationlogapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.adapters.MyLocationAdapter
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.listeners.ItemMoveCallbackListener
import com.example.mylocationlogapp.listeners.OnStartDragListener
import com.example.mylocationlogapp.modal.MyLocationModal
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_location_list.*
import kotlinx.android.synthetic.main.content_location_list.*

class LocationListActivity : BaseActivity(), OnStartDragListener {

    private lateinit var realm: Realm
    private val TAG = LocationListActivity::class.java.simpleName

    //private var myLocationList :List<MyLocationModal>?=null

    lateinit var touchHelper: ItemTouchHelper
    lateinit var adapter: MyLocationAdapter

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




    }

    override fun onResume() {
        super.onResume()
        //to refresh
        getLastLocationsForThisUser()
    }

    private fun getLastLocationsForThisUser() {
        //var myLocationList :List<MyLocationModal>?=null
        var myLocationList=realm.where(MyLocationModal::class.java).equalTo("user_id",MySharedPref.getCurrentUserId()).findAll()
        if(myLocationList!=null) {
            var sizeList:Int= myLocationList?.size!!;
            if(sizeList > 0) {

                adapter = MyLocationAdapter(this@LocationListActivity,this@LocationListActivity)
                /**Start of drag and drop*/
                val callback: ItemTouchHelper.Callback = ItemMoveCallbackListener(adapter)
                touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(myLocationRecyclerView)
                /***End of drag and drop*/
                adapter.setList(myLocationList)

                /**Start swipe to delte code*/
                val itemTouchHelperCallback =
                    object :
                        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {

                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            //noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.adapterPosition))
                            adapter.removeThis(viewHolder.adapterPosition)
                            Toast.makeText(
                                this@LocationListActivity,
                                "Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(myLocationRecyclerView)
                /**end of swipe to delte code*/

                noDataText.visibility= View.INVISIBLE
                /*myLocationRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter
                }*/
                myLocationRecyclerView.layoutManager = LinearLayoutManager(this)
                myLocationRecyclerView.adapter=adapter

            }
            else{
                noDataText.visibility= View.VISIBLE
            }
        }
        else{
            noDataText.visibility= View.VISIBLE
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }
}
