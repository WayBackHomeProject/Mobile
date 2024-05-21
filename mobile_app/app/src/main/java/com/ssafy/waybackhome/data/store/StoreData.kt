package com.ssafy.waybackhome.data.store

import com.google.gson.annotations.SerializedName

data class StoreData(
    val type : String,
    @SerializedName("sr_nm")val name : String,
    @SerializedName("adres")val address : String,
    val detailAddress : String,
    @SerializedName("tel_no")val tel : String?,
    @SerializedName("latitude")val lat : Double,
    @SerializedName("longitude")val lng : Double,
    val sigungu : String,
    val sido : String,
    val umd : String
)
