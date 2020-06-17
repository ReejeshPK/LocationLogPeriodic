package com.example.mylocationlogapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.MyApplicationClass
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.activities.MapsActivityWithPlay
import com.example.mylocationlogapp.helper.MyConstants
import com.example.mylocationlogapp.listeners.ItemMoveCallbackListener
import com.example.mylocationlogapp.listeners.OnStartDragListener
import com.example.mylocationlogapp.modal.MyLocationModal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MyLocationAdapter(
    val context: Context,
    private val startDragListener: OnStartDragListener,
    private val myRecyclerEventsListener: MyRecyclerEventsListener
) :
    RecyclerView.Adapter<MyLocationAdapter.MyViewHolder>(), ItemMoveCallbackListener.Listener {

    val df=DecimalFormat("#.#######")

    interface MyRecyclerEventsListener {
        fun onSwapPerformed(fromPos: Int, toPos: Int,myLocationList:List<MyLocationModal>)
    }

    private var myLocationList =
        emptyList<MyLocationModal>().toMutableList() //done for drag and drop requires this

    fun setList(myLocationListFromActivity: List<MyLocationModal>) {
        myLocationList.addAll(myLocationListFromActivity)
    }

    val simpleDateFormat = SimpleDateFormat(MyConstants.MY_DATE_FORMAT, MyConstants.MYGLOBAL_LOCALE)

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
        /*holder.myLocationText?.text =
            "Lat: ${oneLocation?.latitude}\nLng: ${oneLocation?.longitude}"*/
        var laStr=df.format(oneLocation.latitude)
        var lngStr=df.format(oneLocation.longitude)
        holder.myLocationText?.text =
            "id:${oneLocation?.id}Lat: ${laStr}\nLng: ${lngStr}"

        holder.dateTime?.setText(simpleDateFormat.format(oneLocation?.dateTime))

        holder.myLocationLayout?.setOnClickListener({
            val intent = Intent(context, MapsActivityWithPlay::class.java)
            if (oneLocation != null) {
                intent.putExtra(MyConstants.INTENT_EXTRA_LOCATION_POS, oneLocation.id)
            }
            context.startActivity(intent)
        })

        holder.myDragIcon?.setOnTouchListener { _, event ->
            MyApplicationClass.globalLog("MyLocationAdapter","action:"+event.action)
            if (event.action == MotionEvent.ACTION_DOWN) {
                MyApplicationClass.globalLog("MyLocationAdapter", "onTouchDown")
                this.startDragListener.onStartDrag(holder)
                return@setOnTouchListener true
            }/* else if ((event.action == MotionEvent.ACTION_UP)||(event.action==ACTION_CANCEL)) {
                //user released the touch
                //todo: better place to update the list - events not registering correctly, only action_cancel is called
                MyApplicationClass.globalLog("MyLocationAdapter", "onTouchUp")
                return@setOnTouchListener true
            }*/
            return@setOnTouchListener true
        }
    }


    fun removeThis(adapterPosition: Int) {
        myLocationList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        MyApplicationClass.globalLog(
            "MyLocationAdapter",
            "onRowMoved:" + fromPosition + " to:" + toPosition
        )
        //todo swap in db also - doing it here will be inefficient, since this is called lot of times so
        //todo do it when user releases his touch - ti also does not work properly, so doing here itself
        myRecyclerEventsListener.onSwapPerformed(fromPosition, toPosition,myLocationList)

        //todo analyze why for loop, we could have done wihout it too, i think it is becasue it keeps moving -yes, for each movement, we are swapping up or down
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