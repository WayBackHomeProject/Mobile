package com.ssafy.waybackhome.data.alarmbell

import com.google.gson.annotations.SerializedName

data class AlarmBellData(
    val type : String,
    @SerializedName("latitude") val lat : Double,
    @SerializedName("longitude") val lng : Double
)
