package com.ssafy.waybackhome.data.geo

data class AddressResponse(
    val status : String,
    val meta : ResponseMetaData,
    val addresses : List<GeoAddress>,
    val errorMessage : String
)
