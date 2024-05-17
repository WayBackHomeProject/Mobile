package com.ssafy.waybackhome.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destination")
    fun selectAll() : LiveData<List<Destination>>
    @Query("SELECT * FROM destination WHERE id = (:id)")
    fun select(id : Long) : LiveData<Destination>
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item : Destination) : Long
    @Update
    suspend fun update(item : Destination)
    @Delete
    suspend fun delete(item : Destination)
    @Query("DELETE FROM destination WHERE id = (:id)")
    suspend fun delete(id : Long)
}