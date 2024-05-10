package com.alltimeowl.payrit.data.model

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesPromiseManager {
    private lateinit var sharedPref: SharedPreferences

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences("Promises", Context.MODE_PRIVATE)
    }

    fun savePromiseId(promiseId: String) {
        val editor = sharedPref.edit()
        editor.putBoolean(promiseId, true) // promiseId를 key로 사용하고, 처리됐음을 나타내는 true를 저장
        editor.apply()
    }

    fun isPromiseIdProcessed(promiseId: String): Boolean {
        return sharedPref.getBoolean(promiseId, false) // promiseId가 처리됐다면 true, 아니면 false 반환
    }

    fun clearPromises() {
        sharedPref.edit().clear().apply()
    }
}