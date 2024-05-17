package com.ssafy.waybackhome.data.geo

import android.app.Application
import com.ssafy.waybackhome.ApplicationClass
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoAddressService {
    @GET("geocode")
    suspend fun getAddress(
        /**
         * 검색 주소
         */
        @Query("query") query : String,
        /**
         * ### 검색 중심 좌표
         * - 'lon,lat' 형식으로 입력
         */
        @Query("coordinate") coordinate : String,
        /**
         * ### 검색 결과 필터링 조건
         * - '필터 타입@코드1;코드2;...' 형식으로 입력
         * - HCODE : 행정동 코드
         * - BCODE : 법정동 코드
         *
         * ex ) HCODE@4113554500;4113555000
         */
        @Query("filter") filter : String? = null,
        /**
         * - 기본값 : kor
         * - eng 추가 시 영문 주소 검색 활성화
         */
        @Query("language") language : String? = null,
        /**
         * ### 페이지 번호
         * - 기본값 : 1
         */
        @Query("page") page : Int? = null,
        /**
         * ### 결과 목록 크기
         * - 입력 범위 : 1~100
         * - 기본값 : 10
         */
        @Query("number") number : Int? = null
    ) : AddressResponse

    companion object{
        val api by lazy{
            ApplicationClass.naverAPI.create(GeoAddressService::class.java)
        }
    }
}