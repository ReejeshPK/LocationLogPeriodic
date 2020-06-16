package com.example.mylocationlogapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.activities.MapsActivityWithPlay
import com.example.mylocationlogapp.helper.MyConstants
import com.example.mylocationlogapp.listeners.ItemMoveCallbackListener
import com.example.mylocationlogapp.listeners.OnStartDragListener
import com.example.mylocationlogapp.modal.MyLocationModal
import java.text.SimpleDateFormat
import java.util.*

class MyLocationAdapter(val context :Context,
                        private val startDragListener: OnStartDragListener) :
    RecyclerView.Adapter<MyLocationAdapter.MyViewHolder>(), ItemMoveCallbackListener.Listener {

    private var myLocationList = emptyList<MyLocationModal>().toMutableList() //done for drag and drop requires this

    fun setList(myLocationListFromActivity: List<MyLocationModal>) {
        myLocationList.addAll(myLocationListFromActivity)
    }

    val simpleDateFormat= SimpleDateFormat(MyConstants.MY_DATE_FORMAT,MyConstants.MYGLOBAL_LOCALE)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var myLocationText: TextView? = null
        var dateTime: TextView? = null
        var myLocationLayout: ConstraintLayout? = null
        var myDragIcon: ImageView? = null

        init {
            myLocationText = itemView.findViewById(R.id.latitudeAndLongitude)
            myLocationLayout = itemView.findViewById(R.id.myLocationLayoutItem)
            dateTime = itemView.findViewById(R.id.dateTime)
            myDragIcon = itemView.findViewById(R.id.myDragIcon)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_location_item, parent, false);
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (myLocationList != null) {
            return myLocationList.size
        } else {
            return 0
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var oneLocation = myLocationList?.get(position)
        holder.myLocationText?.text =
            "Lat: ${oneLocation?.latitude}\nLng: ${oneLocation?.longitude}"

        holder.dateTime?.setText(simpleDateFormat.format(oneLocation?.dateTime))

        holder.myLocationLayout?.setOnClickListener({
                val intent = Intent(context,MapsActivityWithPlay::class.java)
            if (oneLocation != null) {
                intent.putExtra(MyConstants.INTENT_EXTRA_LOCATION_POS,oneLocation.id)
            }
            context.startActivity(intent)
        })

        holder.myDragIcon?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                this.startDragListener.onStartDrag(holder)
            }
            return@setOnTouchListener true
        }
    }


    fun removeThis(adapterPosition: Int) {
        myLocationList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(myLocationList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(myLocationList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
    override fun onRowSelected(itemViewHolder: MyViewHolder) {
    }
    override fun onRowClear(itemViewHolder: MyViewHolder) {
    }
}