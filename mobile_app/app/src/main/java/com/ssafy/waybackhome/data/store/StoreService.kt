package com.ssafy.waybackhome.data.store

import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreService {
    @GET("convenience_in_radius/")
    suspend fun getStores(
    @Query("lat") lat : Double,
    @Query("lng") lng : Double,
    @Query("radius_km") radius : Double
    ) : List<StoreData>

    companion object{
        val api by lazy{
            ApplicationClass.serverAPI.create(StoreService::class.java)
        }
    }
}