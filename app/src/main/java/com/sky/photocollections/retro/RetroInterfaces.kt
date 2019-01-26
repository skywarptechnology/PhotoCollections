package com.sky.photocollections.retro

import com.sky.photocollections.support.PictureData
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by skyalligator on 1/22/19.
 * 12:18 PM
 */
interface PicturesApi {

    @GET("cftPFNNHsi")
    fun getPictures(): Call<List<PictureData>>

}
