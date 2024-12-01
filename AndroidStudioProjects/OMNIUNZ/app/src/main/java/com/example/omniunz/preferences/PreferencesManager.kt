package com.angellira.app_1_eduardo.preferences


import android.content.Context

const val USER_PREFERENCES = "USER_PREFERENCES"

class PreferencesManager (context: Context){
    private val sharedPreferences =
        context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

    var isLogged: Boolean
        get() = sharedPreferences.getBoolean(IS_LOGGED, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_LOGGED, value).apply()

    var userEmail: String
        get() = sharedPreferences.getString(USER_EMAIL,"").toString()
        set(value) = sharedPreferences.edit().putString(USER_EMAIL, value).apply()

    var userName: String
        get() = sharedPreferences.getString(USER_NAME,"").toString()
        set(value) = sharedPreferences.edit().putString(USER_NAME, value).apply()

    var userImage: String
        get() = sharedPreferences.getString(USER_IMAGE,"").toString()
        set(value) = sharedPreferences.edit().putString(USER_IMAGE, value).apply()


    var userUid: String
        get() = sharedPreferences.getString(USER_UID,"").toString()
        set(value) = sharedPreferences.edit().putString(USER_UID, value).apply()

    var userMeta: Int?
        get() = sharedPreferences.getInt(USER_META,0)
        set(value) = sharedPreferences.edit().putInt(USER_META, value!!).apply()

    var CaloriaDiaAtual: Int?
        get() = sharedPreferences.getInt(CALORIA,0)
        set(value) = sharedPreferences.edit().putInt(CALORIA, value!!).apply()



    var image: String
        get() = sharedPreferences.getString(IMAGE,"").toString()
        set(value) = sharedPreferences.edit().putString(IMAGE, value).apply()

    companion object {
        private const val IS_LOGGED = "logou"
        private const val USER_EMAIL = "email"
        private const val USER_NAME = "name"
        private const val USER_IMAGE = "userimage"
        private const val USER_META = "usermeta"
        private const val CALORIA = "caloria"
        private const val USER_UID = "userid"
        private const val IMAGE = "image"
    }

}