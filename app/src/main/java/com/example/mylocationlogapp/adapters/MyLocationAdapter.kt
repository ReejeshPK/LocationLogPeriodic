package com.example.mylocationlogapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.activities.MapsActivityWithPlay
import com.example.mylocationlogapp.helper.MyConstants
import com.example.mylocationlogapp.modal.MyLocationModal
import java.text.SimpleDateFormat

class MyLocationAdapter(val myLocationList: List<MyLocationModal>?,val context :Context) :
    RecyclerView.Adapter<MyLocationAdapter.MyViewHolder>() {

    val simpleDateFormat= SimpleDateFormat(MyConstants.MY_DATE_FORMAT,MyConstants.MYGLOBAL_LOCALE)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var myLocationText: TextView? = null
        var dateTime: TextView? = null
        var myLocationLayout: ConstraintLayout? = null

        init {
            myLocationText = itemView.findViewById(R.id.latitudeAndLongitude)
            myLocationLayout = itemView.findViewById(R.id.myLocationLayoutItem)
            dateTime = itemView.findViewById(R.id.dateTime)
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
    }
}