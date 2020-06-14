package com.example.mylocationlogapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mylocationlogapp.R
import com.example.mylocationlogapp.modal.MyLocationModal

class MyLocationAdapter(val myLocationList: List<MyLocationModal>?) :
    RecyclerView.Adapter<MyLocationAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var myLocationText: TextView? = null

        init {
            myLocationText = itemView.findViewById(R.id.latitudeAndLongitude);
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
    }
}