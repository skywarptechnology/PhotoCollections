package com.sky.photocollections.support

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.util.Log
import androidx.core.graphics.scale
import com.sky.photocollections.retro.PicturesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by skyalligator on 1/22/19.
 * 12:15 PM
 */
//val packageName = "com.sky.photocollections"
val TAG = "Pictures App"
var BASE_URL = "http://www.json-generator.com/api/json/get/"
var photoDir: String = ""
val CAPTURE_NAME = "Capture_"
val SMALL_IMG_PATH: String get() = "$photoDir/Small_Imgs"
val LARGE_IMG_PATH: String get() = "$photoDir/Large_Imgs"


fun generateName() = "$CAPTURE_NAME${getTimeStamp()}"
fun getTimeStamp() = DateFormat.getDateTimeInstance().format(Date())
fun getSmallImgPath(name: String) = "$SMALL_IMG_PATH/$name"
fun getLargeImgPath(name: String) = "$LARGE_IMG_PATH/$name"

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

/**
 * co-routine implementation for retrofir future object
 */
suspend fun <T> Call<T>.call(): T {
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

suspend fun downloadImageBackground(imgUrl: String): Bitmap {
    return suspendCoroutine { con ->
        thread {
            try {
                val connection = URL(imgUrl).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                con.resume(BitmapFactory.decodeStream(connection.inputStream))
            } catch (e: Exception) {
                con.resumeWithException(e)
            }
        }
    }
}

fun getSmallSize(width: Int, height: Int): Point {
    val w = 500
    val ratio = w.toDouble() / width
    val h = height * ratio
    return Point(w, h.toInt())
}

fun resizeAndWriteImage(bitmap: Bitmap, name: String) {
    val smallSize = getSmallSize(bitmap.width, bitmap.height)
    val smallBitmap = bitmap.scale(smallSize.x, smallSize.y)

    writeBitmapToFile(bitmap, LARGE_IMG_PATH, name)
    writeBitmapToFile(smallBitmap, SMALL_IMG_PATH, name)
}

@Throws(FileNotFoundException::class)
fun writeBitmapToFile(bitmap: Bitmap, folderPath: String, fileName: String): Uri {
    val outputDir = File(folderPath)
    if (!outputDir.exists()) outputDir.mkdirs()
    val outputFile = File(outputDir, fileName)

    FileOutputStream(outputFile).use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
    }
    return Uri.fromFile(outputFile)
}
