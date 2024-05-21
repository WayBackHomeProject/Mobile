package com.ssafy.waybackhome.data.police

import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET
import retrofit2.http.Query

interface PoliceStationService {
    @GET("policestation_in_radius")
    suspend fun getPoliceStations(
        @Query("lat") lat : Double,
        @Query("lng") lng : Double,
        @Query("radius_km") radius : Double
    ) : List<PoliceStationData>

    companion object{
        val api by lazy {
            ApplicationClass.serverAPI.create(PoliceStationService::class.java)
        }
    }
}