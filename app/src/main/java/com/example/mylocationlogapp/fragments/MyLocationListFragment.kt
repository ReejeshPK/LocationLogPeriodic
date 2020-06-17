package com.example.mylocationlogapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.BaseActivity

import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.activities.LocationListActivity
import com.example.mylocationlogapp.activities.PIckCurrentLocationMapsActivity
import com.example.mylocationlogapp.adapters.MyLocationAdapter
import com.example.mylocationlogapp.helper.MySharedPref
import com.example.mylocationlogapp.listeners.ItemMoveCallbackListener
import com.example.mylocationlogapp.listeners.OnStartDragListener
import com.example.mylocationlogapp.modal.MyLocationModal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.content_location_list.*


class MyLocationListFragment : BaseFragment(),OnStartDragListener,MyLocationAdapter.MyRecyclerEventsListener {

    private lateinit var realm: Realm
    private val TAG = LocationListActivity::class.java.simpleName

    //private var myLocationList :List<MyLocationModal>?=null

    lateinit var touchHelper: ItemTouchHelper
    lateinit var adapter: MyLocationAdapter



    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_my_location_list, container, false)

       val fab:FloatingActionButton=view.findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()*/
            val intent = Intent(context, PIckCurrentLocationMapsActivity::class.java)
            startActivity(intent)

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        realm.commitTransaction()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): MyLocationListFragment {
            return MyLocationListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        //to refresh
        getLastLocationsForThisUser()
    }

    private fun getLastLocationsForThisUser() {
        //var myLocationList :List<MyLocationModal>?=null
        var myLocationList=realm.where(MyLocationModal::class.java).equalTo("user_id", MySharedPref.getCurrentUserId())
            .sort("manual_order_by",
                Sort.ASCENDING).findAll()
        if(myLocationList!=null) {
            var sizeList:Int= myLocationList?.size!!;
            if(sizeList > 0) {

                adapter = MyLocationAdapter(context,this@MyLocationListFragment,this@MyLocationListFragment)
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
                                BaseActivity.logd(TAG, "idtodelte:" + idToDelte)
                                val result=it.where(MyLocationModal::class.java).equalTo("id",idToDelte).findFirst()
                                result?.deleteFromRealm()
                            }

                            adapter.removeThis(viewHolder.adapterPosition)
                            Toast.makeText(
                                context,
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
                myLocationRecyclerView.layoutManager = LinearLayoutManager(context)
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

        val dataFrom=myLocationListLocal.get(fromPos)
        // val idTo=myLocationListLocal.get(toPos).id
        val dataTo=myLocationListLocal.get(toPos)
        // val tmpObj=dataFrom

        val tmpOrderById:Int?=myLocationListLocal.get(fromPos).manual_order_by

        realm.executeTransaction {
            dataFrom.manual_order_by=dataTo.manual_order_by
            it.insertOrUpdate(dataFrom)

            dataTo.manual_order_by=tmpOrderById
            it.insertOrUpdate(dataTo)
            BaseActivity.logd(TAG, "insert or udpate")
        }



    }


}
