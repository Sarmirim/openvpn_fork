package de.blinkt.openvpn.boost.net

import android.os.Build
import android.telecom.PhoneAccount
import android.text.TextUtils
import de.blinkt.openvpn.BuildConfig
import de.blinkt.openvpn.boost.utils.PhoneInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class BoostApiClient(tokenManager: TokenManager?) {
    private val mTokenManager: TokenManager
    fun signup(username: String?, password: String?): JSONObject? {
        try {
            val params = JSONObject()
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val os_version = BuildConfig.VERSION_NAME

            params.put("username", username)
            params.put("password", password)
            params.put("manufacturer", manufacturer)
            params.put("model", model)
            params.put("os_version", os_version)

            val response = postRequest("auth/signup", params)
            if (response != null) {
                val data = JSONObject(response)
                val token = data.getString("token")
                mTokenManager.token = token
                return data
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    fun signin(username: String?, password: String?): JSONObject? {
        try {
            val params = JSONObject()
            params.put("username", username)
            params.put("password", password)
            val response = postRequest("auth/signin", params)
            print("RESPONSE: $response")
            if (response != null) {
                val data = JSONObject(response)
                val token = data.getString("token")
                mTokenManager.token = token
                return data
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    fun endpointTask(): JSONObject? {
        try {
            val params = JSONObject()
            params.put("token", "token")
            val response = postRequest("api/v1/online", params)
            print("RESPONSE: $response")
            if (response != null) {
                val data = JSONObject(response)
                val token = data.getString("token")
                mTokenManager.token = token
                return data
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getUrl(endpoint: String): String {
        return BASE_URL + "/" + endpoint
    }

    private fun postRequest(endpoint: String, params: JSONObject?): String? {
        try {
            val url = URL(getUrl(endpoint))
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.setRequestProperty("Accept", "application/json")
            val token = mTokenManager.token
            if (!TextUtils.isEmpty(token)) {
                connection.setRequestProperty("Authorization", "Bearer $token")
            }
            if (params != null) {
                connection.doOutput = true
                val owr = OutputStreamWriter(connection.outputStream)
                owr.write(params.toString())
                owr.flush()
            }
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                val builder = StringBuilder()
                while (bufferedReader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                return builder.toString()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        private const val BASE_URL = "http://192.168.31.173:5000"
        private const val PREF_TOKEN = "auth:token"
    }

    init {
        requireNotNull(tokenManager)
        mTokenManager = tokenManager
    }
}
