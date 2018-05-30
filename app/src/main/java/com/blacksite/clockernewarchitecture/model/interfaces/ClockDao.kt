package com.blacksite.clockernewarchitecture.model.interfaces

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.blacksite.clockernewarchitecture.model.database.Clock

@Dao
interface ClockDao {
    @Query("SELECT * FROM clock WHERE type=:type ORDER BY uid ASC")
    fun getAllByType(type:Int): List<Clock>

    @Query("SELECT * FROM clock ORDER BY uid ASC")
    fun getAll(): List<Clock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clock: Clock)
}