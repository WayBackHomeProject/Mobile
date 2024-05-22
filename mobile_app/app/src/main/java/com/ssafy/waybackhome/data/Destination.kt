package com.ssafy.waybackhome.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "destination")
data class Destination(
    /**
     * ID
     */
    @PrimaryKey(autoGenerate = true) val id : Long,
    /**
     * 목적지 이름
     */
    val name : String,
    /**
     * 주소 : 지번
     */
    val address : String,
    /**
     * 주소 : 도로명
     */
    val road : String,
    /**
     * 위도
     */
    val lat : Double,
    /**
     * 경도
     */
    val lng : Double,
) : Parcelable{
    constructor(
        address : String,
        road : String,
        lat: Double,
        lng: Double
    ) : this(0, "", address, road, lat, lng)

    @Ignore
    @IgnoredOnParcel
    val location = LatLng(this.lat, this.lng)
}
