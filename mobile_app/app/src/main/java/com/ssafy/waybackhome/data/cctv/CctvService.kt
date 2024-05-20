package com.ssafy.waybackhome.data.cctv

import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET
import retrofit2.http.Query

interface CctvService {
    /**
     * @param lat 검색 중심 위도
     * @param lng 검색 중심 경도
     * @param radius 검색 반경 (단위:km)
     */
    @GET("cctv_in_radius/")
    suspend fun getCctv(
        @Query("lat") lat : Double,
        @Query("lng") lng : Double,
        @Query("radius_km") radius : Double
    ) : List<CctvData>

    companion object{
        val api  by lazy{
            ApplicationClass.serverAPI.create(CctvService::class.java)
        }
    }
}