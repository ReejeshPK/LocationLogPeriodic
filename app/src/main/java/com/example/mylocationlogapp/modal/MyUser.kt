package com.example.mylocationlogapp.modal

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MyUser : RealmObject() {

    @PrimaryKey
    var id: Int? = null
    var userName: String? = null
    var password: String? = null
}