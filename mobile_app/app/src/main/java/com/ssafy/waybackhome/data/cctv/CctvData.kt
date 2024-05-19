package com.ssafy.waybackhome.data.cctv

import com.google.gson.annotations.SerializedName

data class CctvData(
    @SerializedName("area_code")val areaCode : String,
    val number : Int,
    @SerializedName("managing_agency")val managingAgency : String,
    @SerializedName("address_road")val roadAddress : String,
    @SerializedName("address_jibun")val jibunAddress : String?,
    val purpose : String,
    @SerializedName("camera_count") val cameraCount : Int,
    val resolution : Int,
    @SerializedName("direction_info") val directionInfo : String?,
    @SerializedName("retention_period") val retentionPeriod : Int,
    @SerializedName("installation_date") val installationDate : String?,
    @SerializedName("managing_agency_phone") val managingAgencyPhone : String,
    val latitude : Double,
    val longitude : Double,
    @SerializedName("data_standard_date") val dataStandardDate : String
)
