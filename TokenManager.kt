package de.blinkt.openvpn.boost.net

import android.content.Context
import android.content.SharedPreferences

class TokenManager(private val mContext: Context) {
    val preferences: SharedPreferences = mContext.getSharedPreferences("boost_proxy", Context.MODE_PRIVATE)

    var token: String?
        get() = preferences.getString(PREF_TOKEN, null)
        set(token) = preferences.edit().putString(PREF_TOKEN, token).apply()
//        get() = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PREF_TOKEN, null)
//        set(token) {
//            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(PREF_TOKEN, token).apply()
//        }

    fun clearToken() {
        preferences.edit().remove(PREF_TOKEN).apply()
//        PreferenceManager.getDefaultSharedPreferences(mContext).edit().remove(PREF_TOKEN).apply()
    }

    companion object {
        private const val PREF_TOKEN = "auth:token"
    }
}
