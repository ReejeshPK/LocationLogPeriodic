package com.example.mylocationlogapp.modal

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class MyLocationModal : RealmObject() {
    @PrimaryKey
    var id: Int? = null
    var user_id: Int? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var dateTime: Date? = null
    var manual_order_by: Int?=null
}