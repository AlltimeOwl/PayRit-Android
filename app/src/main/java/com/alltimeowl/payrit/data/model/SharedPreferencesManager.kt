package com.alltimeowl.payrit.data.model

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private lateinit var sharedPref: SharedPreferences

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    }

    fun saveUserToken(accessToken: String?, refreshToken: String?, firebaseToken: String) {
        val editor = sharedPref.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.putString("firebaseToken", firebaseToken)
        editor.apply()
    }

    fun saveUserInfo(name: String?, phoneNumber: String?, email: String?) {
        val editor = sharedPref.edit()
        editor.putString("name", name)
        editor.putString("phoneNumber", phoneNumber)
        editor.putString("email", email)
        editor.apply()
    }


    fun getAccessToken(): String {
        return sharedPref.getString("accessToken", "") ?: ""
    }

    fun getRefreshToken(): String {
        return sharedPref.getString("refreshToken", "") ?: ""
    }

    fun getFirebaseToken(): String {
        return sharedPref.getString("firebaseToken", "") ?: ""
    }

    fun getUserName(): String {
        return sharedPref.getString("name", "") ?: ""
    }

    fun getUserPhoneNumber(): String {
        return sharedPref.getString("phoneNumber", "") ?: ""
    }

    fun getUserEmail(): String {
        return sharedPref.getString("email", "") ?: ""
    }

    fun clearUserInfo() {
        val editor = sharedPref.edit()
        editor.remove("accessToken")
        editor.remove("refreshToken")
        editor.remove("name")
        editor.remove("phoneNumber")
        editor.remove("firebaseToken")
        editor.remove("email")
        editor.apply()
    }
}