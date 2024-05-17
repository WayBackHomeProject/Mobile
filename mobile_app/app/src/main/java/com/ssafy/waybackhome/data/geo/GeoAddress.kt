package com.ssafy.waybackhome.data.geo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class GeoAddress(
    /**
     * 도로명 주소
     */
    val roadAddress : String,
    /**
     * 지번 주소
     */
    val jibunAddress : String,
    /**
     * 영문 주소
     */
    val englishAddress : String,
    val addressElements : List<AddressElement>,
    /**
     * 위도
     */
    @SerializedName("x") val lat : String,
    /**
     * 경도
     */
    @SerializedName("y") val lon : String,
    /**
     * 검색시 위치로부터의 거리 ( 단위 : m )
     */
    val distance : Double
) : Parcelable
