package com.ssafy.waybackhome.data.cctv

import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET
import retrofit2.http.Query

interface CctvService {
    @GET("data_api/coordinates_in_radius/")
    suspend fun getCctv(
        @Query("lat") lat : Double,
        @Query("lng") lng : Double,
        @Query("radius_km") radius : Double
    ) : CctvData

    companion object{
        val api  by lazy{
            ApplicationClass.serverAPI.create(CctvService::class.java)
        }
    }
}