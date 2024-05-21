package com.ssafy.waybackhome.data.alarmbell

import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET
import retrofit2.http.Query

interface AlarmBellService {
    @GET("alarmbell_in_radius")
    suspend fun getAlarmBells(
        @Query("lat") lat : Double,
        @Query("lng") lng : Double,
        @Query("radius_km") radius : Double
    ) : List<AlarmBellData>

    companion object{
        val api by lazy {
            ApplicationClass.serverAPI.create(AlarmBellService::class.java)
        }
    }
}