//package com.blacksite.clockernewarchitecture.repository
//
//import android.app.Application
//import android.arch.lifecycle.LiveData
//import android.arch.lifecycle.MutableLiveData
//import android.arch.persistence.room.Insert
//import android.arch.persistence.room.Room
//import android.os.AsyncTask
//import com.blacksite.clockernewarchitecture.R
//import com.blacksite.clockernewarchitecture.asyncTask.InsertAsyncTask
//import com.blacksite.clockernewarchitecture.model.database.AppDatabase
//import com.blacksite.clockernewarchitecture.model.database.Face
//import com.blacksite.clockernewarchitecture.model.interfaces.FaceDao
//
//class FaceRepository {
//    private var faceDao: FaceDao? = null
//
//    var facesLiveData:LiveData<List<Face>>
//    var faces: List<Face>? = null
//
//    constructor(application: Application){
//        this.faceDao = AppDatabase.getDatabase(application).userDao()
//        facesLiveData = faceDao!!.getAllAsLiveData()
//        AsyncTask.execute({faces = faceDao!!.getAll()})
//    }
//
//    fun loadFaces():List<Face>{
//        return faces!!
//    }
//    fun loadFacesLiveData():LiveData<List<Face>>{
//        refreshFaceList()
////        return this.faceDao!!.getAll()
//        return this.facesLiveData
//    }
//
//    fun insert(face: Face){
//        InsertAsyncTask(this.faceDao!!, face).execute()
//    }
//    private fun refreshFaceList() {
//        this.insert(Face(1, R.drawable.lion_512, R.drawable.lion_512_w))
//        this.insert(Face(2, R.drawable.tiger_512, R.drawable.tiger_512_w))
////        InsertAsyncTask(this.faceDao!!, Face(1, R.drawable.lion_512, R.drawable.lion_512_w)).execute()
////        InsertAsyncTask(this.faceDao!!, Face(2, R.drawable.tiger_512, R.drawable.tiger_512_w)).execute()
////        this.faceDao!!.insert(Face(1, "Image1", "image2"))
////        this.faceDao!!.insert(Face(2, "Image3", "image4"))
//        this.facesLiveData = faceDao!!.getAllAsLiveData()
//    }
//}