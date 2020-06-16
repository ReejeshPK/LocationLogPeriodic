package com.example.mylocationlogapp.listeners

import androidx.recyclerview.widget.RecyclerView

interface OnStartDragListener {
    /** Credits: https://androidwave.com/drag-and-drop-recyclerview-item-android/ */
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}