package com.ssafy.waybackhome.data.geo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressElement(
    val types : List<String>,
    val longName : String,
    val shortName : String,
    val code : String
) : Parcelable
