package com.ssafy.waybackhome.data.tip

import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET

interface TipService {
    @GET("safe_tip/")
    suspend fun getTip() : Tip

    companion object{
        val api by lazy {
            ApplicationClass.serverAPI.create(TipService::class.java)
        }
    }
}