package com.angellira.app_1_eduardo.preferences


import android.content.Context

const val USER_PREFERENCES = "USER_PREFERENCES"

class PreferencesManager (context: Context){
    private val sharedPreferences =
        context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

    var isLogged: Boolean
        get() = sharedPreferences.getBoolean(IS_LOGGED, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_LOGGED, value).apply()

    var userName: String
        get() = sharedPreferences.getString(USER_NAME,"").toString()
        set(value) = sharedPreferences.edit().putString(USER_NAME, value).apply()

    var image: String
        get() = sharedPreferences.getString(IMAGE,"").toString()
        set(value) = sharedPreferences.edit().putString(IMAGE, value).apply()

    companion object {
        private const val IS_LOGGED = "logou"
        private const val USER_NAME = "name"
        private const val IMAGE = "image"
    }

}