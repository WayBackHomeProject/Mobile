package com.ssafy.waybackhome.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
){

}
