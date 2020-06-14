package com.example.mylocationlogapp.helper

import com.example.mylocationlogapp.MyApplicationClass

object MySharedPref {


    private val TAG="MySharedPref";

    private val PREFERENCES = MyApplicationClass.sharedpreferences
    private val PREF_ISLOGGEDIN = "PREF_ISLOGGEDIN"
    private val PREF_USERID = "PREF_USERID"


    fun setIsLoggedIn(isLoggedIn: Boolean) {
        val editor = PREFERENCES?.edit()
        editor?.putBoolean(PREF_ISLOGGEDIN, isLoggedIn)
        editor?.apply()
    }
    fun setCurrentUserId(currentUserId: Int?) {
        val editor = PREFERENCES?.edit()
        if (currentUserId != null) {
            editor?.putInt(PREF_USERID, currentUserId)
        }
        editor?.apply()
    }

    fun getIsLoggedIn(): Boolean? {
        return PREFERENCES?.getBoolean(PREF_ISLOGGEDIN, false)
    }

    fun getCurrentUserId(): Int? {
        return PREFERENCES?.getInt(PREF_USERID, 0)
    }

    fun clearAllPrefs() {
        PREFERENCES?.edit()?.clear()?.apply()
    }

}