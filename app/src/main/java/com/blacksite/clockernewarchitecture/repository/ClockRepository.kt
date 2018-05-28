package com.blacksite.clockernewarchitecture.repository

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.graphics.BitmapFactory
import android.os.Handler
import com.blacksite.clockernewarchitecture.R
import com.blacksite.clockernewarchitecture.asyncTask.InsertAsyncTask
import com.blacksite.clockernewarchitecture.model.database.AppDatabase
import com.blacksite.clockernewarchitecture.model.database.Clock
import com.blacksite.clockernewarchitecture.model.interfaces.ClockDao
import com.google.firebase.firestore.FirebaseFirestore

import android.util.Log
import com.blacksite.clockernewarchitecture.application.Settings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import android.support.annotation.NonNull
import com.blacksite.clockernewarchitecture.application.Global
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FileDownloadTask
import com.google.android.gms.tasks.OnSuccessListener
import java.io.FileInputStream


class ClockRepository {
    var db = FirebaseFirestore.getInstance()
    // Create a storage reference from our app
    var storage = FirebaseStorage.getInstance()
    // Create a storage reference from our app
    var storageRef = storage.reference

    private var clockDao:ClockDao? = null
//    var clockLiveData: LiveData<List<Clock>> = MutableLiveData()
    constructor(application: Application){
        this.clockDao = AppDatabase.getDatabase(application).clockDao()
    }
    fun getAllClocks(allClocksLiveData: MutableLiveData<List<Clock>>){
        db.collection(Settings.COLLECTION_NAME)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val imageRef = storageRef.child(document.data["image"].toString())
                            val localFile = File.createTempFile("images", "jpg")
                            imageRef.getFile(localFile).addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot> {
                                // Local temp file has been created
                                var b = BitmapFactory.decodeStream(FileInputStream(localFile))
                                Global.absolutePath = Global.saveToInternalStorage(b, document.data["image"].toString())

                                this.insert(Clock((document.data["uid"] as Long).toInt(),
                                        document.data["image"].toString(),
                                        document.data["image"].toString(),
                                        (document.data["number"] as Long?)!!.toInt(),
                                        (document.data["type"] as Long).toInt()))
                                allClocksLiveData.value = clockDao!!.getAll()
                            }).addOnFailureListener(OnFailureListener {
                                // Handle any errors
                            })
                            Log.d("logger", document.id + " => " + document.data)
                        }
                    } else {
                        Log.w("logger", "Error getting documents.", task.exception)
                    }
                }

//        return clockDao!!.getAll()
    }
    fun getFaces():List<Clock>{
        return clockDao!!.getAllByType(Clock.FACE)
    }
    fun getClocks(type:Int, clockListLiveData: MutableLiveData<List<Clock>>){
//        refreshClockList()

//        var handler = Handler()
//        handler.postDelayed({
//        }, 2000)
        var handler2 = Handler()
        handler2.postDelayed({
            clockListLiveData.value = clockDao!!.getAllByType(type)
        }, 5000)


//        refreshClockList()
//        clockListLiveData.value = clockDao!!.getAllByType(type)

//        return this.clockLiveData
    }

    private fun refreshClockList() {
//        this.insert(Clock(1, R.drawable.transparent_512, R.drawable.transparent_512, null, Clock.FACE))
//        this.insert(Clock(2, R.drawable.lion_512, R.drawable.lion_512_w, null, Clock.FACE))
//        this.insert(Clock(3, R.drawable.tiger_512, R.drawable.tiger_512_w, null, Clock.FACE))
//        this.insert(Clock(4, R.drawable.no_dial, R.drawable.no_dial, null, Clock.DIAL))
//        this.insert(Clock(5, R.drawable.dial_1, R.drawable.dial_1, null, Clock.DIAL))
//        this.insert(Clock(6, R.drawable.dial_2, R.drawable.dial_2, null, Clock.DIAL))
//        this.insert(Clock(7, R.drawable.dial_3, R.drawable.dial_3, null, Clock.DIAL))
//        this.insert(Clock(8, R.drawable.hand_1_grey, R.drawable.hand_1_grey, 1, Clock.HAND))
    }

    fun insert(clock: Clock){
        InsertAsyncTask(this.clockDao!!, clock).execute()
    }
}