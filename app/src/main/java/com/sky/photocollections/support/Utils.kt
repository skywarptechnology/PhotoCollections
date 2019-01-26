package com.sky.photocollections.support

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.sky.photocollections.retro.PicturesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by skyalligator on 1/22/19.
 * 12:15 PM
 */

val TAG = "Pictures App"
var BASE_URL = "http://www.json-generator.com/api/json/get/"

/**
 * general log for application
 */
fun log(txt: String) {
    Log.d(TAG, txt)
}

/**
 * convenient func for printing object even its null reference
 */
fun Any?.lg() {
    Log.d(TAG, this?.toString() ?: " null object :P")
}

/**
 * retrofit lazy initialization variable
 */
val retrofitIns: Retrofit by lazy {

    Retrofit.Builder().apply {
        baseUrl(BASE_URL)
        addConverterFactory(GsonConverterFactory.create())
    }.build()
}

/**
 * location api by lazy initialization
 */
val picturesApi: PicturesApi by lazy {
    retrofitIns.create(PicturesApi::class.java)
}


fun Activity.shareAddressViaOtherApps(subject: String, message: String) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
    startActivity(Intent.createChooser(sharingIntent, "Share Address"))
}

/**
 * co-routine implementation for retrofir future object
 */
suspend fun <T> Call<T>.callApi(): T? {
    return suspendCoroutine { continuation ->

        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val data = response.body()
                if (data != null)
                    continuation.resume(data)
                else
                    continuation.resumeWithException(NullPointerException("Null data Object"))
            }
        })
    }
}

/**
 * util func for ignoring an exception thrown by supplied inline lambda
 */
inline fun ignoreEx(call: () -> Unit) {
    try {
        call()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * this is support util func for CoroutineScope.launch
 * while ignoreing exception
 *
 * @param fnc provide callback crossinline fnc for coroutine launch function
 */
inline fun CoroutineScope.launchWithoutEx(crossinline fnc: suspend () -> Unit) {
    launch {
        try {
            fnc()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
