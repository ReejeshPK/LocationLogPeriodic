package com.example.mylocationlogapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.BaseActivity
import com.example.mylocationlogapp.MyApplicationClass
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.adapters.MyLocationAdapter
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.listeners.ItemMoveCallbackListener
import com.example.mylocationlogapp.listeners.OnStartDragListener
import com.example.mylocationlogapp.modal.MyLocationModal
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.delete
import kotlinx.android.synthetic.main.activity_location_list.*
import kotlinx.android.synthetic.main.content_location_list.*


class LocationListActivity : BaseActivity(), OnStartDragListener,MyLocationAdapter.MyRecyclerEventsListener {

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
        var myLocationList=realm.where(MyLocationModal::class.java).equalTo("user_id",MySharedPref.getCurrentUserId())
            .sort("manual_order_by",
            Sort.ASCENDING).findAll()
        if(myLocationList!=null) {
            var sizeList:Int= myLocationList?.size!!;
            if(sizeList > 0) {

                adapter = MyLocationAdapter(this@LocationListActivity,this@LocationListActivity,this@LocationListActivity)
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
                            realm.executeTransaction {
                                //it.delete(myLocationList.get(viewHolder.adapterPosition))
                                val idToDelte:Int?=myLocationList.get(viewHolder.adapterPosition)?.id
                                logd(TAG,"idtodelte:"+idToDelte)
                                val result=it.where(MyLocationModal::class.java).equalTo("id",idToDelte).findFirst()
                                result?.deleteFromRealm()
                            }

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



    /**Swap in db and reload*/

    override fun onSwapPerformed(fromPos: Int, toPos: Int,myLocationListLocal:List<MyLocationModal>) {
        //todo: need async task and queing mechanism ?
        //realm.insertOrUpdate(myLocationList)
       /* realm.executeTransactionAsync({
            //it.insertOrUpdate(myLocationList)
            val idFrom=myLocationListLocal.get(fromPos).id
            val dataFrom=myLocationListLocal.get(fromPos)
            val idTo=myLocationListLocal.get(toPos).id
            val dataTo=myLocationListLocal.get(toPos)

            //swap
            val tmpObj=dataFrom
            tmpObj.id=dataFrom.id
            tmpObj.user_id=dataTo.user_id
            tmpObj.latitude=dataTo.latitude
            tmpObj.longitude=dataTo.longitude
            tmpObj.dateTime=dataTo.dateTime

            it.insertOrUpdate(tmpObj)

            //next
            dataTo.user_id=dataFrom.user_id
            dataTo.latitude=dataFrom.latitude
            dataTo.longitude=dataFrom.longitude
            dataTo.dateTime=dataFrom.dateTime

            it.insertOrUpdate(dataTo)

        },{
            logd(TAG,"success")
        },{
            loge(TAG,"err:"+it.message)
        })*/

        val idFrom=myLocationListLocal.get(fromPos).id
        val dataFrom=myLocationListLocal.get(fromPos)
        val idTo=myLocationListLocal.get(toPos).id
        val dataTo=myLocationListLocal.get(toPos)
        val tmpObj=dataFrom

        val tmpOrderById:Int?=myLocationListLocal.get(fromPos).manual_order_by

        realm.executeTransaction {
            dataFrom.manual_order_by=dataTo.manual_order_by
            it.insertOrUpdate(dataFrom)

            dataTo.manual_order_by=tmpOrderById
            it.insertOrUpdate(dataTo)
            logd(TAG,"insert or udpate")
        }

       /* realm.executeTransaction {
            //swap
            //tmpObj.id=dataFrom.id //Primary key field 'id' cannot be changed after object was created.
            dataFrom.user_id=dataTo.user_id //Cannot modify managed objects outside of a write transaction.
            dataFrom.latitude=dataTo.latitude
            dataFrom.longitude=dataTo.longitude
            dataFrom.dateTime=dataTo.dateTime

            realm.insertOrUpdate(tmpObj)

            //next
            dataTo.user_id=tmpObj.user_id
            dataTo.latitude=tmpObj.latitude
            dataTo.longitude=tmpObj.longitude
            dataTo.dateTime=tmpObj.dateTime

            realm.insertOrUpdate(dataTo)
        }*/





    }

    override fun onDestroy() {
        super.onDestroy()
        if (realm != null) { // don't trust old devices
            realm.close()
        }
    }

}
