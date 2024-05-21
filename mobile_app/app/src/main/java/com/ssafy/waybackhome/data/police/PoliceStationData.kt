package com.ssafy.waybackhome.data.police

import com.google.gson.annotations.SerializedName

data class PoliceStationData(
    val number : Int,
    val sido : String,
    val policestation : String,
    val department : String,
    val type : String,
    @SerializedName("tel_no") val tel : String,
    val address : String,
    @SerializedName("latitude") val lat : Double,
    @SerializedName("longitude") val lng : Double
)
