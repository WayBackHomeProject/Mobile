package com.ssafy.waybackhome.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room

class LocalRepository private constructor(context: Context){
    private val database : LocalDatabase = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        "local.db"
    ).build()

    private val destinationDao = database.destinationDao()

    fun selectAllDestination() : LiveData<List<Destination>>{
        return destinationDao.selectAll()
    }
    fun selectDestination(id : Long) : LiveData<Destination>{
        return destinationDao.select(id)
    }
    suspend fun insertDestination(item : Destination) : Long{
        return destinationDao.insert(item)
    }
    suspend fun updateDestination(item : Destination){
        destinationDao.update(item)
    }
    suspend fun deleteDestination(item : Destination){
        destinationDao.delete(item)
    }
    suspend fun deleteDestination(id : Long){
        destinationDao.delete(id)
    }

    companion object{
        private var _instance : LocalRepository? = null
        val instance get() = _instance!!
        fun initialize(context: Context){
            if(_instance == null) _instance = LocalRepository(context)
        }
    }
}