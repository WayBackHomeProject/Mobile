package com.ssafy.waybackhome.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Destination::class], version = 1)
abstract class LocalDatabase : RoomDatabase(){
    abstract fun destinationDao() : DestinationDao
}