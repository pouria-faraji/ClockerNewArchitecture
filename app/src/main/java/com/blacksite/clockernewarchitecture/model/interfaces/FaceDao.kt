//package com.blacksite.clockernewarchitecture.model.interfaces
//
//import android.arch.lifecycle.LiveData
//import android.arch.lifecycle.MutableLiveData
//import android.arch.persistence.room.Dao
//import android.arch.persistence.room.Insert
//import android.arch.persistence.room.OnConflictStrategy.REPLACE
//import android.arch.persistence.room.Query
//import com.blacksite.clockernewarchitecture.model.database.Face
//
//@Dao
//interface FaceDao {
//    @Query("SELECT * FROM face")
//    fun getAllAsLiveData(): LiveData<List<Face>>
//
//    @Query("SELECT * FROM face")
//    fun getAll(): List<Face>
//
//    @Query("SELECT * FROM face WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Face>
//
//    @Insert(onConflict = REPLACE)
//    fun insert(face: Face)
//}